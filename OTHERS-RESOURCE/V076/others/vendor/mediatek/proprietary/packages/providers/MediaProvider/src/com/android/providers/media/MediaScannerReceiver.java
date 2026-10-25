/*
* Copyright (C) 2018 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/* //device/content/providers/media/src/com/android/providers/media/MediaScannerReceiver.java
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.android.providers.media;

import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;


import android.os.SystemProperties;
import java.io.IOException;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.content.Context;
import android.content.ComponentName;

public class MediaScannerReceiver extends BroadcastReceiver {
    private final static String TAG = "MediaScannerReceiver";
    /// M: handle scan before all storage mounted issue(may cause delete
    ///  these unmounted storage's entries in db
    /// when do prescan), wait all storage mounted only if check time out(TIMEOUT_VALUE).
    private static final int MSG_CHECK_ALL_STORAGE_MOUNTED = 10;
    private static final int CHECK_INTERVAL = 1000;
    private static final int TIMEOUT_VALUE = 5000;
    private static Handler sHandler;
    /// M: When not boot complete should not scan external storage.
    private static boolean sIsBootComplete = false;
    /// M: When device shutdown, we need do nothing(when shutdown request, sys.shutdown.requested
    /// has value, otherwise no this property).
    private static final String ACTION_SHUTDOWN_IPO = "mediatek.intent.action.ACTION_SHUTDOWN_IPO";
    static boolean sIsShutdown = !"def_value".equals(SystemProperties.get(
            "sys.shutdown.requested", "def_value"));
    private static final String DEVICE_MOUNT_BEFORE_BOOT = "dev.mount_before_boot";
	
	Context mContext = null;
	
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
		mContext = context;
        Log.v(TAG, "onReceive action = " + action);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {//
            Log.v(TAG,
            "onReceive BOOT_COMPLETED,begin to scan internal and external storage.");
            // Scan both internal and external storage
            scan(context, MediaProvider.INTERNAL_VOLUME);
			//chenmin add start
			String isneedcp = SystemProperties.get("ro.product.isneedcp","0");//persist.sys.ishadcp
			//android.util.Log.d("ChenMin1211", "MediaReceiver.java onReceive 86 isneedcp = " + isneedcp); 
			if (isneedcp.trim().equals("1")) {
				String ishadcp = SystemProperties.get("persist.sys.ishadcp", "no");//persist.sys.ishadcp
				//android.util.Log.d("ChenMin0607", "MediaReceiver.java onReceive 38 ishadcp = " + ishadcp);
				if(ishadcp.trim().equals("no")){
					//android.util.Log.d("ChenMin0607", "MediaReceiver.java onReceive 40");
					//intent.setClassName("com.android.providers.media", "com.android.providers.media.CopyService");
					//context.startService(intent);
			
					/*Intent copyService=new Intent(context, CopyService.class);

			    	context.startForegroundService(copyService);*/
										
					//android.util.Log.d("ChenMin0607", "MediaReceiver.java onReceive 51");
					Intent intentCast = new Intent("android.intent.action.filescpfinish"); 
					intentCast.setComponent(new ComponentName("com.android.settings", "com.android.settings.BootReceiver"));
					context.sendBroadcast(intentCast);
								
					Handler handler = new Handler();
					handler.postDelayed(new CopyFileshandler(), 2000);
																								
				}						
				//SystemProperties.set("persist.sys.ishadcp", "yes");//persist.sys.ishadcp
			}						
			//chenmin add end
            /// M: only do scan external until all storages have been mounted or check time out.
            scanUntilAllStorageMounted(context);
            sIsBootComplete = true;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(DEVICE_MOUNT_BEFORE_BOOT, false);
            editor.commit();
            sIsShutdown = false;
        } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {//
            scanTranslatable(context);
        } else if (action.equals(Intent.ACTION_SHUTDOWN) || action.equals(ACTION_SHUTDOWN_IPO)) {
            /// M: When device shutdown, MediaProvider need not respond any action.
            sIsShutdown = true;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(DEVICE_MOUNT_BEFORE_BOOT, true);
            editor.commit();
        } else if (action.equals("mediatek.intent.action.OVERTIME_REMOVAL")) {
            /** Added for SD hot-plug optimization.@**/
            Uri uri = MediaStore.Files.getContentUri(MediaProvider.EXTERNAL_VOLUME);
            context.getContentResolver().call(uri, MediaUtils.ACTION_REMOVE_OVERTIME, null, null);
            /**@}**/
        } else if (!sIsShutdown) {
            final Uri uri = intent.getData();
            if (uri != null && uri.getScheme().equals("file")) {
                // handle intents related to external storage
                String path = uri.getPath();
                String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
                String legacyPath = Environment.getLegacyExternalStorageDirectory().getPath();

                try {
                    path = new File(path).getCanonicalPath();
                } catch (IOException e) {
                    Log.e(TAG, "couldn't canonicalize " + path);
                    return;
                }
                if (path.startsWith(legacyPath)) {
                    path = externalStoragePath + path.substring(legacyPath.length());
                }

                Log.d(TAG, "action: " + action + " path: " + path);
                if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {//========================
                    /// M: Do not scan external storage before
                    /// internal storage was scanned. Use two variables
                    /// to make sure we scan external storage after
                    /// boot complete. When media process be kill
                    /// sIsBootComplete will be restore to default
                    ///value and need use mount service send
                    /// parameter first_boot_mounted in intent
                    ///  to check whether boot complete
                    SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(context);
                    boolean isMountedBeforeBoot = prefs.getBoolean(DEVICE_MOUNT_BEFORE_BOOT, true);
                    if (isMountedBeforeBoot && !sIsBootComplete) {
                        Log.v(TAG, "Mounted before boot completed with path: " + path);
                        return;
                    }
                    /// M: only do scan until all storage have mounted or check time out.
                    scanUntilAllStorageMounted(context);
                } else if (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE.equals(action)
                  && isInScanDirectory(context, path)) {//
                    /// M: only scan these files store in devices avail external storage
                    scanFile(context, path);
                }
            }
        }
    }

    private void scan(Context context, String volume) {
        Bundle args = new Bundle();
        args.putString("volume", volume);
        context.startService(
                new Intent(context, MediaScannerService.class).putExtras(args));
    }    

    private void scanFile(Context context, String path) {
        Bundle args = new Bundle();
        args.putString("filepath", path);
        context.startService(
                new Intent(context, MediaScannerService.class).putExtras(args));
    }    

    private void scanTranslatable(Context context) {
        final Bundle args = new Bundle();
        args.putBoolean(MediaStore.RETRANSLATE_CALL, true);
        context.startService(new Intent(context, MediaScannerService.class).putExtras(args));
    }

    /// M: handle scan before all storage mounted issue(may cause
    /// delete these unmounted storage's entries in db
    /// when do prescan), wait all storage mounted only if check time out(TIMEOUT_VALUE). {@
    /**
     * M: scan external storage until all storages have been mounted or check time out.
     *
     * @param context
     */
    private void scanUntilAllStorageMounted(Context context) {
        Handler handler = getHandler();
        handler.removeCallbacksAndMessages(null);
        Message msg = handler.obtainMessage(MSG_CHECK_ALL_STORAGE_MOUNTED, 0, 0, context);
        msg.sendToTarget();
    }

    /**
     * M: Check the given path whether in all exist directories, only yes we need do scan.
     *
     * @param context context
     * @param path path given to check
     * @return true if is in any directories, otherwise false.
     */
    private boolean isInScanDirectory(Context context, String path) {
        if (path == null) {
            Log.w(TAG, "scan path is null");
            return false;
        }
        StorageManager storageManager = (StorageManager)
         context.getSystemService(Context.STORAGE_SERVICE);
        String[] directories = storageManager.getVolumePaths();
        if (directories == null || directories.length == 0) {
            Log.w(TAG, "there are no valid directores");
            return false;
        }
        for (String directory : directories) {
            if (path.startsWith(directory)) {
                return true;
            }
        }
        Log.w(TAG, "invalid scan path " + path + ", not in any directories");
        return false;
    }

    /**
     * M: check whether all storages are mounted, if yes we can do scan external storages.
     *
     * @param context
     * @return
     */
    private boolean isAllStorageMounted(Context context) {
        StorageManager storageManager = (StorageManager)
         context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] externalVolumeList = storageManager.getVolumeList();
        for (StorageVolume storageVolume : externalVolumeList) {
            String path = storageVolume.getPath();
            String state = storageVolume.getState();
            Log.v(TAG, "isAllStorageMounted: path = " + path + ", state = " + state);
            if (Environment.MEDIA_UNMOUNTED.equals(state)
               || Environment.MEDIA_CHECKING.equals(state)) {
                return false;
            }
        }
        return true;
    }

    /// M: get main handler to do check whether all storage
    /// have been mounted every CHECK_INTERVAL time.
    private Handler getHandler() {
        if (sHandler == null) {
            sHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Context context = (Context) msg.obj;
                    int waitTime = msg.arg1;
                    Log.v(TAG,
                    "Check whether all storage mounted,have waited " + waitTime + "ms");
                    if (MSG_CHECK_ALL_STORAGE_MOUNTED == msg.what) {
                        /// When all storage mounted or check time out, begin to scan
                        if (waitTime > TIMEOUT_VALUE || isAllStorageMounted(context)) {
                            Log.v(TAG,
                            "All storages have mountedor check time out, begin to scan.");
                            scan(context, MediaProvider.EXTERNAL_VOLUME);
                            removeCallbacksAndMessages(null);
                            sHandler = null;
                        } else {
                            Log.v(TAG,
                            "Some storage has not been mounted, wait it mounted until time out.");
                            Message next = obtainMessage(msg.what, waitTime +
                            CHECK_INTERVAL, -1, msg.obj);
                            sendMessageDelayed(next, CHECK_INTERVAL);
                        }
                    }
                };
            };
        }
        return sHandler;
    }
    /// @}
	
   class CopyFileshandler implements Runnable
   {
      public void run()
      {
			CopyFiles copyFiles = new CopyFiles();								
								
			try {
				copyFiles.copyStart(mContext);
			} catch (IOException e) {
				android.util.Log.d("ChenMin0607", "MediaReceiver.java onReceive 82 e.getMessage() = " + e.getMessage());
	    	  	e.printStackTrace();						    
	    	}	
      }
   }	
}
