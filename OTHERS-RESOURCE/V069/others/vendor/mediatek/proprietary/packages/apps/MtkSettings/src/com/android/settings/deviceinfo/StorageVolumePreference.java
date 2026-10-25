/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.settings.deviceinfo;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.settings.R;
import com.android.settings.deviceinfo.StorageSettings.UnmountTask;
import com.android.settingslib.Utils;

import java.io.IOException;
import java.io.File;

/**
 * Preference line representing a single {@link VolumeInfo}, possibly including
 * quick actions like unmounting.
 */
public class StorageVolumePreference extends Preference {
    private static final String TAG = StorageVolumePreference.class.getSimpleName();

    private final StorageManager mStorageManager;
    private final VolumeInfo mVolume;

    private int mColor;
    private int mUsedPercent = -1;

	static long romfs = com.android.settings.Util.getRomfs();
    // TODO: ideally, VolumeInfo should have a total physical size.
    public StorageVolumePreference(Context context, VolumeInfo volume, int color, long totalBytes) {
        super(context);

        mStorageManager = context.getSystemService(StorageManager.class);
        mVolume = volume;
        mColor = color;

        setLayoutResource(R.layout.storage_volume);

        setKey(volume.getId());
        setTitle(mStorageManager.getBestVolumeDescription(volume));

        Drawable icon;
        if (VolumeInfo.ID_PRIVATE_INTERNAL.equals(volume.getId())) {
            icon = context.getDrawable(R.drawable.ic_storage);
        } else {
            icon = context.getDrawable(R.drawable.ic_sim_sd);
        }

        if (volume.isMountedReadable()) {
            // TODO: move statfs() to background thread
            final File path = volume.getPath();
			
			//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", path.getAbsolutePath() = " + path.getAbsolutePath());
            long freeBytes = 0;
            long usedBytes = 0;
            if (volume.getType() == VolumeInfo.TYPE_PRIVATE) {
                final StorageStatsManager stats =
                        context.getSystemService(StorageStatsManager.class);
                try {
                    /*totalBytes = stats.getTotalBytes(volume.getFsUuid());
                    freeBytes = stats.getFreeBytes(volume.getFsUuid());
					usedBytes = totalBytes - freeBytes;*/
					
		            long tempsize = totalBytes;
		            long tempsize2 = freeBytes;
					totalBytes = stats.getTotalBytes(volume.getFsUuid()) / 2;
					//double privateUsedBytes;									
					if (com.android.settings.Util.getshowromsize() == 0) {
			            //tempsize = info.totalBytes;
			            //tempsize2 = info.freeBytes;		
						//privateUsedBytes = (double)(tempsize - tempsize2);					
					} else {
						//total = 1024000000000;
						tempsize = com.android.settings.Util.getTotRomSize();	
						//tempsize2 = com.android.settings.Util.getAvaRomSize()*93/100;

						if (romfs == 512 || romfs == 640 || romfs == 1024 || romfs == 2048) {
							//tempsize2 = com.android.settings.Util.getAvaRomSize()*93/100;
							tempsize2 = com.android.settings.Util.getAvaRomSize();
							totalBytes = romfs * 1000 * 1000 * 1000l;
						} else {
							tempsize2 = com.android.settings.Util.getAvaRomSize();
						}				
					}
					freeBytes = tempsize2;
					//usedBytes = totalBytes - freeBytes;
                    usedBytes = com.android.settings.Util.getTotRomSize() - tempsize2;
					//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", tempsize = " + tempsize + ", tempsize2 = " + tempsize2);
					//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", com.android.settings.Util.getTotRomSize() = " + com.android.settings.Util.getTotRomSize());
                } catch (IOException e) {
                    Log.w(TAG, e);
                }
				//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", volume.getType() == VolumeInfo.TYPE_PRIVATE) usedBytes = " + usedBytes + ", totalBytes = " + totalBytes);
            } else {
                // StorageStatsManager can only query private volumes.
                // Default to previous storage calculation for public volumes.
                if (totalBytes <= 0) {
                    totalBytes = path.getTotalSpace();
                }
                freeBytes = path.getFreeSpace();
                usedBytes = totalBytes - freeBytes;
				//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", !volume.getType() == VolumeInfo.TYPE_PRIVATE) usedBytes = " + usedBytes + ", totalBytes = " + totalBytes);
            }

            String used = Formatter.formatFileSize(context, usedBytes);//*93/100
            String total = Formatter.formatFileSize(context, totalBytes);//
			/*if (romfs == 512 || romfs == 640 || romfs == 1024 || romfs == 2048) {
				//total = Formatter.formatFileSize(context, romfs * 1000 * 1000 * 1000l);//
				used = Formatter.formatFileSize(context, usedBytes*107/100);
				if (romfs == 2048) {
					//used = Formatter.formatFileSize(context, usedBytes*150/100);
				}
			}*/ 			
			//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", used = " + used + ", total = " + total);
			String displayLanguage = com.android.settings.Util.getDisplayLanguage();
				
			if (displayLanguage.equals("es_ES") && total.contains("TB")) {
				total = total.trim().replace(",", ".");
			}			
			if (total.trim().equals("1.02 TB")) {//
				total = "1 TB";
			}
			
			if (total.trim().equals("2.05 TB")) {
				total = "2 TB";
			}
			
            setSummary(context.getString(R.string.storage_volume_summary, used, total));
            if (totalBytes > 0) {
                mUsedPercent = (int) ((usedBytes * 100) / totalBytes);
            }
			//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", freeBytes = " + freeBytes + ", mStorageManager.getStorageLowBytes(path) = " + mStorageManager.getStorageLowBytes(path));
            if (freeBytes < mStorageManager.getStorageLowBytes(path)) {
				//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", freeBytes < mStorageManager.getStorageLowBytes(path)");
                mColor = Utils.getColorAttr(context, android.R.attr.colorError);
                icon = context.getDrawable(R.drawable.ic_warning_24dp);
            }

        } else {
            setSummary(volume.getStateDescription());
            mUsedPercent = -1;
        }

        icon.mutate();
        icon.setTint(mColor);
        setIcon(icon);

        if (volume.getType() == VolumeInfo.TYPE_PUBLIC
                && volume.isMountedReadable()) {
            setWidgetLayoutResource(R.layout.preference_storage_action);
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        final ImageView unmount = (ImageView) view.findViewById(R.id.unmount);
        if (unmount != null) {
            unmount.setImageTintList(ColorStateList.valueOf(Color.parseColor("#8a000000")));
            unmount.setOnClickListener(mUnmountListener);
        }

        final ProgressBar progress = (ProgressBar) view.findViewById(android.R.id.progress);
        if (mVolume.getType() == VolumeInfo.TYPE_PRIVATE && mUsedPercent != -1) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(mUsedPercent);
            progress.setProgressTintList(ColorStateList.valueOf(mColor));
        } else {
            progress.setVisibility(View.GONE);
        }

        super.onBindViewHolder(view);
    }

    private final View.OnClickListener mUnmountListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            new UnmountTask(getContext(), mVolume).execute();
        }
    };
	
    /**
     * //android.util.Log.e("ChenMin0716", "" + getLineNumber());
     * import java.text.SimpleDateFormat;
     * import java.util.Date;
     * **/
	public static String getLineNumber(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();       
        if (stackTrace.length < 4) {
        	return "";
        }
        String fileName = stackTrace[3].getFileName();
        String methodName = stackTrace[3].getMethodName();
        int LineNumber = stackTrace[3].getLineNumber();
        
        return "file = " + fileName + ", method = " + methodName + ", Line = " + String.valueOf(LineNumber);
    }	
}
