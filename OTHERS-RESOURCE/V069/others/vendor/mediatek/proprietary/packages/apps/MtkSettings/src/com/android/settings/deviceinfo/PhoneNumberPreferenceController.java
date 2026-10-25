/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.content.Context;
/// M: Add for updating phone number.
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.core.AbstractPreferenceController;
/// M: Add for updating phone number. @{
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
/// @}

import java.util.ArrayList;
import java.util.List;

/// M: Revise for updating phone number.
public class PhoneNumberPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, LifecycleObserver, OnCreate, OnDestroy {

    private static final String TAG = "PhoneNumberPreferenceController";

    private final static String KEY_PHONE_NUMBER = "phone_number";

    private final TelephonyManager mTelephonyManager;
    private final SubscriptionManager mSubscriptionManager;
    private final List<Preference> mPreferenceList = new ArrayList<>();

    /// M: Revise for updating phone number. @{
    public PhoneNumberPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = (SubscriptionManager) context.getSystemService(
                Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        // Add this controller into lifecycle.
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }
    /// @}

    @Override
    public String getPreferenceKey() {
        return KEY_PHONE_NUMBER;
    }

    @Override
    public boolean isAvailable() {
       // return mTelephonyManager.isVoiceCapable();
	   return false;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final Preference preference = screen.findPreference(getPreferenceKey());
        mPreferenceList.add(preference);

        final int phonePreferenceOrder = preference.getOrder();
        // Add additional preferences for each sim in the device
        for (int simSlotNumber = 1; simSlotNumber < mTelephonyManager.getPhoneCount();
                simSlotNumber++) {
            final Preference multiSimPreference = createNewPreference(screen.getContext());
            multiSimPreference.setOrder(phonePreferenceOrder + simSlotNumber);
            multiSimPreference.setKey(KEY_PHONE_NUMBER + simSlotNumber);
            screen.addPreference(multiSimPreference);
            mPreferenceList.add(multiSimPreference);
        }
    }

    @Override
    public void updateState(Preference preference) {
        for (int simSlotNumber = 0; simSlotNumber < mPreferenceList.size(); simSlotNumber++) {
            final Preference simStatusPreference = mPreferenceList.get(simSlotNumber);
            //simStatusPreference.setTitle(getPreferenceTitle(simSlotNumber));//wdl changed
            simStatusPreference.setSummary(getPhoneNumber(simSlotNumber));
        }
    }

    private CharSequence getPhoneNumber(int simSlot) {
       // final SubscriptionInfo subscriptionInfo = getSubscriptionInfo(simSlot);
       // if (subscriptionInfo == null) {
        //    return mContext.getString(R.string.device_info_default);
      //  }

      //  String number = mStatusExt.updatePhoneNumber(
      //          getFormattedPhoneNumber(subscriptionInfo).toString(),
        //        subscriptionInfo.getSimSlotIndex(), mContext);
      //  return number;
	  return null;
    }

    private CharSequence getPreferenceTitle(int simSlot) {
        //return mTelephonyManager.getPhoneCount() > 1 ? mContext.getString(
         //       R.string.status_number_sim_slot, simSlot + 1) : mContext.getString(
         //       R.string.status_number);
		return null;//wdl changed
    }

    @VisibleForTesting
    SubscriptionInfo getSubscriptionInfo(int simSlot) {
        final List<SubscriptionInfo> subscriptionInfoList =
                mSubscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null) {
            for (SubscriptionInfo info : subscriptionInfoList) {
                if (info.getSimSlotIndex() == simSlot) {
                    return info;
                }
            }
        }
        return null;
    }

    @VisibleForTesting
    CharSequence getFormattedPhoneNumber(SubscriptionInfo subscriptionInfo) {
        final String phoneNumber = DeviceInfoUtils.getFormattedPhoneNumber(mContext,
                subscriptionInfo);
        return TextUtils.isEmpty(phoneNumber) ? mContext.getString(R.string.device_info_default)
                : BidiFormatter.getInstance().unicodeWrap(phoneNumber, TextDirectionHeuristics.LTR);
    }

    @VisibleForTesting
    Preference createNewPreference(Context context) {
        return new Preference(context);
    }

    /// M: Register listener for updating phone number. @{
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener
            = new SubscriptionManager.OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            Log.d(TAG, "onSubscriptionsChanged");
            updateState(null);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Register listener for updating phone number.
        mSubscriptionManager.addOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener);
    }

    @Override
    public void onDestroy() {
        // Unregister listener.
        mSubscriptionManager.removeOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener);
    }
    /// @}
}
