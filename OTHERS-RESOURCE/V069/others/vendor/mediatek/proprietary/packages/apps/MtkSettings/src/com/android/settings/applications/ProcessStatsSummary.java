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
package com.android.settings.applications;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SummaryPreference;
import com.android.settings.Utils;
import com.android.settings.applications.ProcStatsData.MemInfo;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.SummaryLoader;
import android.os.SystemProperties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.FileReader;
import java.io.BufferedReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessStatsSummary extends ProcessStatsBase implements OnPreferenceClickListener {

    private static final String KEY_STATUS_HEADER = "status_header";

    private static final String KEY_PERFORMANCE = "performance";
    private static final String KEY_TOTAL_MEMORY = "total_memory";
    private static final String KEY_AVERAGY_USED = "average_used";
    private static final String KEY_FREE = "free";
    private static final String KEY_APP_LIST = "apps_list";
	private static final String TAG = "ProcessStatsSummary";

    private SummaryPreference mSummaryPref;

    private Preference mPerformance;
    private Preference mTotalMemory;
    private Preference mAverageUsed;
    private Preference mFree;
    private Preference mAppListPreference;

	private static String addramsetdiff = SystemProperties.get("persist.sys.addramsetdiff","0");
	

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.process_stats_summary);
        mSummaryPref = (SummaryPreference) findPreference(KEY_STATUS_HEADER);
        mPerformance = findPreference(KEY_PERFORMANCE);
        mTotalMemory = findPreference(KEY_TOTAL_MEMORY);
        mAverageUsed = findPreference(KEY_AVERAGY_USED);
        mFree = findPreference(KEY_FREE);
        mAppListPreference = findPreference(KEY_APP_LIST);
        mAppListPreference.setOnPreferenceClickListener(this);
    }
//wdl add
	 public static String getNumForString(String str) {
	        Pattern p = Pattern.compile("(\\d+\\.\\d+)");
	        Matcher m = p.matcher(str);
	        if (m.find()) {
	            str = m.group(1) == null ? "" : m.group(1);
	        } else {
	            p = Pattern.compile("(\\d+)");
	            m = p.matcher(str);
							if (m.find()) {
	                str = m.group(1) == null ? "" : m.group(1);
	            } else {
	                str = "";
	            }
	        }
	        //System.out.println(str);
	        return str;
	    }//wdl add end
	    
    @Override
    public void refreshUi() {
        Context context = getContext();

        MemInfo memInfo = mStatsManager.getMemInfo();

        double usedRam = memInfo.realUsedRam;
        double totalRam = memInfo.realTotalRam;
        double freeRam = memInfo.realFreeRam;
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 118 usedRam = " + usedRam);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 118 totalRam = " + totalRam);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 118 freeRam = " + freeRam);

		
        BytesResult usedResult = Formatter.formatBytes(context.getResources(), (long) usedRam,
                Formatter.FLAG_SHORTER);
        String totalString = com.android.settings.Util.getTotalRam();//Formatter.formatShortFileSize(context, (long) totalRam);//
        String freeString = Formatter.formatShortFileSize(context, (long) freeRam);
		//android.util.Log.d("kkh112", "freeString = " + freeString);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 109 freeString = " + freeString);
		//freeString = Formatter.formatShortFileSize(context, (long) com.android.settings.Util.getAvailMemoryInfo(getContext()));
		
		String num = com.android.settings.Util.getNumForString(freeString);
		
		String unit = freeString.replace(num, "").trim();
		
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 148 unit = " + unit + ", usedResult.units = " + usedResult.units);
		
//         freeString = com.android.settings.Util.getNumDecimals(String.valueOf(com.android.settings.Util.getAvailMemoryInfo(getContext())/1024/1024/1024f));// + " GB"; //getUseRamFormatStr(context, (long) com.android.settings.Util.getAvailMemoryInfo(mContext));
        freeString = (""+com.android.settings.Util.getAvailMemoryInfo(getContext())/1024/1024/1024f);// + " GB"; //getUseRamFormatStr(context, (long) com.android.settings.Util.getAvailMemoryInfo(mContext));		
		if(freeString.contains(",")){
			freeString = freeString.substring(0,freeString.indexOf(",")+2);//dhy
		}else 
			freeString = freeString.substring(0,freeString.indexOf(".")+2);//dhy
		String newFreeString = freeString.replace(",", ".");         //kekaihao add
		//android.util.Log.e("dhyxxx","xxxxxxx newFreeString1==" + newFreeString);//dhy
		newFreeString = newFreeString.substring(0,newFreeString.indexOf(".")+2);//dhy
		//android.util.Log.e("dhyxxx","xxxxxxx newFreeString2==" + newFreeString);//dhy
//        String newFreeString = freeString;
		//android.util.Log.d("kkh123", "freeString = " + freeString);

		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 113 freeString = " + freeString);
		
		String usedRamString = Formatter.formatShortFileSize(context, (long) usedRam);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 116 usedRamString = " + usedRamString);
		
		// android.util.Log.e("dhyxxx","xxxxxxx totalString==" + totalString);//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx newFreeString==" + newFreeString);//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx 11==" + com.android.settings.Util.getNumForString(totalString));//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx 22==" + com.android.settings.Util.getNumForString(newFreeString));//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx 111==" + com.android.settings.Util.getNumForString(totalString).length());//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx 222==" + com.android.settings.Util.getNumForString(newFreeString).length());//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx freeString==" + freeString);//dhy
		// android.util.Log.e("dhyxxx","xxxxxxx freeString2==" + com.android.settings.Util.getAvailMemoryInfo(getContext())/1024/1024/1024f);//dhy
		
		usedRam = (double)(Float.parseFloat(com.android.settings.Util.getNumForString(totalString)) - Float.parseFloat(com.android.settings.Util.getNumForString(newFreeString))) * 1024 * 1024 * 1024;
		
		usedRamString = Formatter.formatShortFileSize(context, (long) usedRam);
		//Float.parseFloat(com.android.settings.Util.getNumForString(totalString)) - Float.parseFloat(com.android.settings.Util.getNumForString(freeString))
		
        CharSequence memString;
        CharSequence[] memStatesStr = getResources().getTextArray(R.array.ram_states);
        int memState = mStatsManager.getMemState();
        if (memState >= 0 && memState < memStatesStr.length - 1) {
            memString = memStatesStr[memState];
        } else {
            memString = memStatesStr[memStatesStr.length - 1];
        }
        //mSummaryPref.setAmount(usedResult.value);
        //mSummaryPref.setUnits(usedResult.units);
		
		mSummaryPref.setAmount(com.android.settings.Util.getUseRamForStr(getContext()));		
		mSummaryPref.setUnits("GB");
		//android.util.Log.d("kkh149", "mSummaryPref.setAmount = " + com.android.settings.Util.getUseRamForStr(getContext()));
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 133 usedResult.value = " + usedResult.value + ", usedResult.units = " + usedResult.units);
        float usedRatio = (float)(usedRam / (freeRam + usedRam));
        mSummaryPref.setRatios(usedRatio, 0, 1 - usedRatio);

		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 101 totalString = " + totalString + ", freeString = " + freeString);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 138 usedRamString = " + usedRamString);

		//if (!addramsetdiff.trim().equals("0")) {
			float ramsetdiffTot;	
			float ramsetdiffFre;
			
			float ramsetdiffUse;
			//ramsetdiffTot = Float.parseFloat(com.android.settings.Util.getNumForString(totalString)) - Float.parseFloat(addramsetdiff);	
			//ramsetdiffFre = Float.parseFloat(com.android.settings.Util.getNumForString(freeString)) - Float.parseFloat(addramsetdiff);

			//ramsetdiffTot = Float.parseFloat(com.android.settings.Util.getNumForString(totalString)) - com.android.settings.Util.getAddramsetdiff(com.android.settings.Util.getNumForString(totalString), addramsetdiff);	
			//ramsetdiffFre = Float.parseFloat(com.android.settings.Util.getNumForString(freeString)) - com.android.settings.Util.getAddramsetdiff(com.android.settings.Util.getNumForString(totalString), addramsetdiff);	
			//ramsetdiffFre = ramsetdiffTot - Float.parseFloat(com.android.settings.Util.getNumForString(usedRamString));

			ramsetdiffTot = Float.parseFloat(com.android.settings.Util.getNumForString(com.android.settings.Util.getTotalRam()));	
			//ramsetdiffFre = Float.parseFloat(com.android.settings.Util.getNumForString(freeString)) - com.android.settings.Util.getAddramsetdiff(com.android.settings.Util.getNumForString(totalString), addramsetdiff);	
			/*if (usedRamString.contains("MB")) {
				ramsetdiffFre = ramsetdiffTot - Float.parseFloat(com.android.settings.Util.getNumForString(usedRamString)) / 1024;			
			} else {
				ramsetdiffFre = ramsetdiffTot - Float.parseFloat(com.android.settings.Util.getNumForString(usedRamString));
			}*/
			String newRamsetdiffUse = com.android.settings.Util.getUseRamForStr(getContext()).replace(",", ".");           //kekaihao add
            //android.util.Log.e("dhyxxx","xxxxxxx newRamsetdiffUse==" + newRamsetdiffUse);//dhy
		//	ramsetdiffUse = Float.parseFloat(com.android.settings.Util.getNumForString(newRamsetdiffUse));
		ramsetdiffUse = com.android.settings.Util.parseFloatSafely(getNumForString(newRamsetdiffUse));//wdl
			//android.util.Log.d("kkh171", "ramsetdiffTot = " + Float.parseFloat(com.android.settings.Util.getNumForString(com.android.settings.Util.getTotalRam())));
			//android.util.Log.d("kkh179", "ramsetdiffUse = " + Float.parseFloat(com.android.settings.Util.getNumForString(com.android.settings.Util.getUseRamForStr(getContext()))));
								 				
		//}		

        mPerformance.setSummary(memString);
        mTotalMemory.setSummary(totalString);//
        //mAverageUsed.setSummary(Utils.formatPercentage((long) usedRam, (long) totalRam));
		//mAverageUsed.setSummary(com.android.settings.Util.getNumDecimals(Utils.formatPercentage((long) ((ramsetdiffTot - ramsetdiffFre) * 1000), (long) (ramsetdiffTot * 1000))));
		mAverageUsed.setSummary(Utils.formatPercentage((long) ((ramsetdiffUse) * 1000), (long) (ramsetdiffTot * 1000)));
		
        //mFree.setSummary(freeString + usedResult.units);
		//android.util.Log.d("kkh189", "mAverageUsed = " + Utils.formatPercentage((long) ((ramsetdiffUse) * 1000), (long) (ramsetdiffTot * 1000)));
		mFree.setSummary(freeString + "GB");//unit

		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 160 memString = " + memString);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 161 totalString = " + totalString);
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 162 formatPercentage = " + Utils.formatPercentage((long) usedRam, (long) totalRam));
		//android.util.Log.d("ChenMin0724", TAG + " " + "refreshUi 163 freeString = " + freeString);
		
		//android.util.Log.d("ChenMin0724", TAG + " " + "\n");
		
        String durationString = getString(sDurationLabels[mDurationIndex]);
        int numApps = mStatsManager.getEntries().size();
        mAppListPreference.setSummary(getResources().getQuantityString(
                R.plurals.memory_usage_apps_summary, numApps, numApps, durationString));
    }

	
    @Override
    public int getMetricsCategory() {
        return MetricsEvent.PROCESS_STATS_SUMMARY;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_process_stats_summary;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mAppListPreference) {
            final Bundle args = new Bundle();
            args.putBoolean(ARG_TRANSFER_STATS, true);
            args.putInt(ARG_DURATION_INDEX, mDurationIndex);
            mStatsManager.xferStats();
            new SubSettingLauncher(getContext())
                    .setDestination(ProcessStatsUi.class.getName())
                    .setTitle(R.string.memory_usage_apps)
                    .setArguments(args)
                    .setSourceMetricsCategory(getMetricsCategory())
                    .launch();
            return true;
        }
        return false;
    }

    private static class SummaryProvider implements SummaryLoader.SummaryProvider {

        private final Context mContext;
        private final SummaryLoader mSummaryLoader;

        public SummaryProvider(Context context, SummaryLoader summaryLoader) {
            mContext = context;
            mSummaryLoader = summaryLoader;
        }

        @Override
        public void setListening(boolean listening) {
            if (listening) {
                ProcStatsData statsManager = new ProcStatsData(mContext, false);
                statsManager.setDuration(sDurations[0]);
                MemInfo memInfo = statsManager.getMemInfo();
                //String usedResult = Formatter.formatShortFileSize(mContext,
                //        (long) memInfo.realUsedRam);
                String usedResult = Formatter.formatShortFileSize(mContext,
                        (long) memInfo.realUsedRam);
                String totalResult = Formatter.formatShortFileSize(mContext,
                        (long) memInfo.realTotalRam);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", usedResult = " + usedResult + ", totalResult = " + totalResult);
						
		        String totalString = com.android.settings.Util.getTotalRam();//Formatter.formatShortFileSize(context, (long) totalRam);
		        String freeString = String.valueOf(com.android.settings.Util.getAvailMemoryInfo(mContext)/1024/1024/1024f);// + " GB"; //getUseRamFormatStr(context, (long) com.android.settings.Util.getAvailMemoryInfo(mContext));

                android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", freeString = " + freeString);		
				
				/*freeString = com.android.settings.Util.getNumDecimals(freeString);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", freeString = " + freeString);
                freeString = (""+com.android.settings.Util.getAvailMemoryInfo(mContext)/1024/1024/1024f).substring(0,3);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", res = " + (com.android.settings.Util.getAvailMemoryInfo(mContext)/1024/1024/1024f));
				android.util.Log.e("ChenMin0724", "" + getLineNumber() +  ", freeString = " + freeString);
				String newFreeString = freeString.replace(",", ".");             //kekaihao add
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", freeString = " + freeString);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", newFreeString = " + newFreeString);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() +  ", freeString = " + freeString);*/		
				freeString =  com.android.settings.Util.getNumDecimals(freeString);//chenmin add
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", freeString = " + freeString);
				
				Float usedRam = (Float.parseFloat(com.android.settings.Util.getNumForString(totalString)) - Float.parseFloat(com.android.settings.Util.getNumForString(freeString)));
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", usedRam = " + usedRam);			
				usedResult = com.android.settings.Util.getNumDecimals(String.valueOf(usedRam)) + " GB";				
				totalResult = com.android.settings.Util.getTotalRam();//
				
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", ========================================================== usedResult = " + usedResult + ", totalResult = " + totalResult);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", memInfo.realUsedRam = " + memInfo.realUsedRam + ", memInfo.realTotalRam = " + memInfo.realTotalRam);
				android.util.Log.e("ChenMin0724", "" + getLineNumber() + ", usedResult = " + usedResult + ", totalResult = " + totalResult);

                mSummaryLoader.setSummary(this, mContext.getString(R.string.memory_summary, usedResult, totalResult));
            }
        }
    }

    public static String getUseRamFormatStr(Context Context, long usedRam){
        return Formatter.formatShortFileSize(Context, (long)usedRam);
    }
	
    public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY
            = new SummaryLoader.SummaryProviderFactory() {
        @Override
        public SummaryLoader.SummaryProvider createSummaryProvider(Activity activity,
                                                                   SummaryLoader summaryLoader) {
            return new SummaryProvider(activity, summaryLoader);
        }
    };

    /**
     * android.util.Log.e("ChenMin0724", "" + getLineNumber());
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
