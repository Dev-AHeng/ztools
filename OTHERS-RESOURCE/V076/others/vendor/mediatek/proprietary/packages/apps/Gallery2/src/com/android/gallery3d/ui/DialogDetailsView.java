/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.gallery3d.R;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.DetailsHelper.DetailsSource;
import com.android.gallery3d.ui.DetailsHelper.DetailsViewContainer;
import com.android.gallery3d.ui.DetailsHelper.ResolutionResolvingListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map.Entry;
import android.util.DisplayMetrics;
import android.os.SystemProperties;

public class DialogDetailsView implements DetailsViewContainer {
    @SuppressWarnings("unused")
    private static final String TAG = "Gallery2/DialogDetailsView";

    private final AbstractGalleryActivity mActivity;
    private DetailsAdapter mAdapter;
    private MediaDetails mDetails;
    private final DetailsSource mSource;
    private int mIndex;
    private Dialog mDialog;
    private CloseListener mListener;
		
	String manunew = SystemProperties.get("ro.product.manunew", "").trim();
	String modelnew = SystemProperties.get("ro.product.modelnew", "").trim();
	static String lcd_widthfake = SystemProperties.get("persist.sys.lcmwidth").trim();
	static String lcd_lengthfake = SystemProperties.get("persist.sys.lcmheigh").trim();		
		

    public DialogDetailsView(AbstractGalleryActivity activity, DetailsSource source) {
        mActivity = activity;
        mSource = source;
    }

    @Override
    public void show() {
        reloadDetails();
        /// M: [BUG.ADD] null pointer check @{
        if (mDialog == null) {
            return;
        }
        /// @}
        mDialog.show();
    }

    @Override
    public void hide() {
        /// M: [BUG.ADD] null pointer check @{
        if (mDialog == null) {
            return;
        }
        /// @}
        mDialog.hide();
    }

    @Override
    public void reloadDetails() {
        int index = mSource.setIndex();
        if (index == -1) return;
        MediaDetails details = mSource.getDetails();
        if (details != null) {
            if (mIndex == index && mDetails == details) return;
            mIndex = index;
            mDetails = details;
            setDetails(details);
        }
    }

    private void setDetails(MediaDetails details) {
        mAdapter = new DetailsAdapter(details);
        String title = String.format(
                mActivity.getAndroidContext().getString(R.string.details_title),
                mIndex + 1, mSource.size());
        ListView detailsList = (ListView) LayoutInflater.from(mActivity.getAndroidContext()).inflate(
                R.layout.details_list, null, false);
        detailsList.setAdapter(mAdapter);
        mDialog = new AlertDialog.Builder(mActivity)
            .setView(detailsList)
            .setTitle(title)
            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    /// M: [BUG.ADD] null pointer check @{
                    if (mDialog == null) {
                        return;
                    }
                    /// @}
                    mDialog.dismiss();
                }
            })
            .create();

        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mListener != null) {
                    mListener.onClose();
                }
            }
        });
    }


    private class DetailsAdapter extends BaseAdapter
        implements AddressResolvingListener, ResolutionResolvingListener {
        private final ArrayList<String> mItems;
        private int mLocationIndex;
        private final Locale mDefaultLocale = Locale.getDefault();
        private final DecimalFormat mDecimalFormat = new DecimalFormat(".####");
        private int mWidthIndex = -1;
        private int mHeightIndex = -1;

        public DetailsAdapter(MediaDetails details) {
            Context context = mActivity.getAndroidContext();
            mItems = new ArrayList<String>(details.size());
            mLocationIndex = -1;
            setDetails(context, details);
        }

        private void setDetails(Context context, MediaDetails details) {
            boolean resolutionIsValid = true;
            String path = null;
	        DisplayMetrics metrics =new DisplayMetrics();
		        
	        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                    
            for (Entry<Integer, Object> detail : details) {
                String value;
                switch (detail.getKey()) {
                    case MediaDetails.INDEX_LOCATION: {
                        double[] latlng = (double[]) detail.getValue();
                        mLocationIndex = mItems.size();
                        value = DetailsHelper.resolveAddress(mActivity, latlng, this);
                        break;
                    }
                    case MediaDetails.INDEX_SIZE: {
                        value = Formatter.formatFileSize(
                                context, (Long) detail.getValue());
                        android.util.Log.d("ChenMin1228", TAG + " " + "setDetails 169 value = " + value);
                        if (value.trim().contains("kB")) {
                        		value = value.replace("kB", "kb");
                        }
                        break;
                    }
                    case MediaDetails.INDEX_WHITE_BALANCE: {
                        value = "1".equals(detail.getValue())
                                ? context.getString(R.string.manual)
                                : context.getString(R.string.auto);
                        break;
                    }
                    case MediaDetails.INDEX_FLASH: {
                        MediaDetails.FlashState flash =
                                (MediaDetails.FlashState) detail.getValue();
                        // TODO: camera doesn't fill in the complete values, show more information
                        // when it is fixed.
                        if (flash.isFlashFired()) {
                            value = context.getString(R.string.flash_on);
                        } else {
                            value = context.getString(R.string.flash_off);
                        }
                        break;
                    }
                    case MediaDetails.INDEX_EXPOSURE_TIME: {
                        value = (String) detail.getValue();
                        double time = Double.valueOf(value);
                        if (time < 1.0f) {
                            value = String.format(mDefaultLocale, "%d/%d", 1,
                                    (int) (0.5f + 1 / time));
                        } else {
                            int integer = (int) time;
                            time -= integer;
                            value = String.valueOf(integer) + "''";
                            if (time > 0.0001) {
                                value += String.format(mDefaultLocale, " %d/%d", 1,
                                        (int) (0.5f + 1 / time));
                            }
                        }
                        break;
                    }
                    case MediaDetails.INDEX_WIDTH:
                        mWidthIndex = mItems.size();
                        //android.util.Log.d("ChenMin1228", TAG + " " + "setDetails INDEX_WIDTH 212 detail.getValue().toString() = " + detail.getValue().toString());
                        if (detail.getValue().toString().equalsIgnoreCase("0")) {
                            value = context.getString(R.string.unknown);
                            resolutionIsValid = false;
														value = String.valueOf(metrics.widthPixels);
                        } else {
                            value = toLocalInteger(detail.getValue());
                        }
                        //value = String.valueOf(metrics.widthPixels);
						//android.util.Log.d("ChenMin1228", TAG + " " + "setDetails INDEX_WIDTH 230 value = " + value);
						String width = String.valueOf(metrics.widthPixels).trim();
						if (lcd_widthfake.trim().equals(width) || lcd_lengthfake.trim().equals(width)) {
							value = value.replace(width, lcd_widthfake);
						}
						//android.util.Log.d("ChenMin1228", TAG + " " + "setDetails INDEX_WIDTH 230 value = " + value);
                        break;
                    case MediaDetails.INDEX_HEIGHT: {
                        mHeightIndex = mItems.size();
                        //android.util.Log.d("ChenMin1228", TAG + " " + "setDetails INDEX_HEIGHT 241 detail.getValue().toString() = " + detail.getValue().toString());

                        if (detail.getValue().toString().equalsIgnoreCase("0")) {
                            value = context.getString(R.string.unknown);
                            resolutionIsValid = false;
							value = String.valueOf(metrics.heightPixels);
                        } else {
                            value = toLocalInteger(detail.getValue());
                        }
                        //value = String.valueOf(metrics.heightPixels);
						//android.util.Log.d("ChenMin1228", TAG + " " + "setDetails INDEX_WIDTH 250 value = " + value);
						String height = String.valueOf(metrics.heightPixels).trim();
						if (lcd_widthfake.trim().equals(height) || lcd_lengthfake.trim().equals(height)) {
							value = value.replace(height, lcd_lengthfake);
						
						}
						//android.util.Log.d("ChenMin1228", TAG + " " + "setDetails INDEX_WIDTH 255 value = " + value);
                        break;
                    }
                    case MediaDetails.INDEX_PATH:
                        // Prepend the new-line as a) paths are usually long, so
                        // the formatting is better and b) an RTL UI will see it
                        // as a separate section and interpret it for what it
                        // is, rather than trying to make it RTL (which messes
                        // up the path).
                        value = "\n" + detail.getValue().toString();
                        path = detail.getValue().toString();
                        break;
                    case MediaDetails.INDEX_ISO:
                        value = toLocalNumber(Integer.parseInt((String) detail.getValue()));
                        break;
                    case MediaDetails.INDEX_FOCAL_LENGTH:
                        double focalLength = Double.parseDouble(detail.getValue().toString());
                        value = toLocalNumber(focalLength);
                        break;
                    case MediaDetails.INDEX_ORIENTATION:
                        value = toLocalInteger(detail.getValue());
                        break;
                    default: {
                        Object valueObj = detail.getValue();
                        // This shouldn't happen, log its key to help us diagnose the problem.
                        if (valueObj == null) {
                            Utils.fail("%s's value is Null",
                                    DetailsHelper.getDetailsName(context, detail.getKey()));
                        }
                        value = valueObj.toString();
                    }
                }
				//android.util.Log.d("ChenMin0518", "DialogDetailsView.java 254 value = " + value);//here
								
				if (value.contains(SystemProperties.get("ro.product.manufacturer").trim())) {
					 if (!manunew.trim().equals("")) {
						//value = manunew;
						value = value.replace(SystemProperties.get("ro.product.manufacturer"), manunew);
					 }
				}		

				if (value.contains(SystemProperties.get("ro.product.model").trim())) {
					 if (!modelnew.trim().equals("")) {
						//value = manunew;
						value = value.replace(SystemProperties.get("ro.product.model"), modelnew);
					 }
				}															
								
                int key = detail.getKey();
                if (details.hasUnit(key)) {
                    value = String.format("%s: %s %s", DetailsHelper.getDetailsName(
                            context, key), value, context.getString(details.getUnit(key)));
                } else {
                    value = String.format("%s: %s", DetailsHelper.getDetailsName(
                            context, key), value);
                }
                mItems.add(value);
            }
            /// M: [BUG.MARK] avoid OOM for certain photo @{
            /*
            if (!resolutionIsValid) {
                DetailsHelper.resolveResolution(path, this);
            }
            */
            /// @}
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mDetails.getDetail(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = (TextView) LayoutInflater.from(mActivity.getAndroidContext()).inflate(
                        R.layout.details, parent, false);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(mItems.get(position));
            return tv;
        }

        @Override
        public void onAddressAvailable(String address) {
            mItems.set(mLocationIndex, address);
            notifyDataSetChanged();
        }

        @Override
        public void onResolutionAvailable(int width, int height) {
        		//android.util.Log.d("ChenMin1228", TAG + " " + "onResolutionAvailable 319 width = " + width + ", height = " + height);
            if (width == 0 || height == 0) return;
            // Update the resolution with the new width and height
            Context context = mActivity.getAndroidContext();
            String widthString = String.format(mDefaultLocale, "%s: %d",
                    DetailsHelper.getDetailsName(
                            context, MediaDetails.INDEX_WIDTH), width);
            String heightString = String.format(mDefaultLocale, "%s: %d",
                    DetailsHelper.getDetailsName(
                            context, MediaDetails.INDEX_HEIGHT), height);
            mItems.set(mWidthIndex, String.valueOf(widthString));
            mItems.set(mHeightIndex, String.valueOf(heightString));
            notifyDataSetChanged();
        }

        /**
         * Converts the given integer (given as String or Integer object) to a
         * localized String version.
         */
        private String toLocalInteger(Object valueObj) {
            if (valueObj instanceof Integer) {
                return toLocalNumber((Integer) valueObj);
            } else {
                String value = valueObj.toString();
                try {
                    value = toLocalNumber(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    // Just keep the current "value" if we cannot
                    // parse it as a fallback.
                }
                return value;
            }
        }

        /** Converts the given integer to a localized String version. */
        private String toLocalNumber(int n) {
            return String.format(mDefaultLocale, "%d", n);
        }

        /** Converts the given double to a localized String version. */
        private String toLocalNumber(double n) {
            return mDecimalFormat.format(n);
        }
    }

    @Override
    public void setCloseListener(CloseListener listener) {
        mListener = listener;
    }
}
