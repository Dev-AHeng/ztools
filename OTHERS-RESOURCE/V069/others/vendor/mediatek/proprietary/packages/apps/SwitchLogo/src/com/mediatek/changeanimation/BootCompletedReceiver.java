package com.mediatek.changeanimation;

import android.net.wifi.ScanResult;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.net.UnknownHostException;
import android.os.SystemProperties;
import android.os.IBinder ;
import android.os.ServiceManager;
import java.util.Random;
import java.util.StringTokenizer;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import android.content.ComponentName;
import android.content.Intent;
import android.os.SystemProperties; 
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
not use, please see vendor\mediatek\proprietary\packages\apps\MtkSettings\src\com\android\settings\BootReceiver.java

**/
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("ChenMin1116","BootCompletedReceiver.java onReceive 52 =======================================================");
				SharedPreferences sp = context.getSharedPreferences("changeanimationfirstStart", context.MODE_PRIVATE);
       	Editor et = sp.edit();
        String str=sp.getString("cdsflag", "noting");

				/***
				not use, plase see vendor\mediatek\proprietary\packages\apps\MtkSettings\src\com\android\settings\NvRAM.java

				***/				
        /*if (!str.equals("true")) {            
	         Log.d("lile","rand_addr");
					 Intent intent1 = new Intent(context,NvRAM.class);
					 intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				   //if(SystemProperties.get("ro.product.changeanimation").equals("yes")){
				 	 //Log.d("ChenMin0721","onReceive 62 =======================================================");
			     context.startService(intent1);
					 //Log.d("ChenMin0721","onReceive 64 =======================================================");
	         et.putString("cdsflag", "true");
	         et.commit(); 
        }
				
				
			if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
				Log.d("ChenMin1116","BootCompletedReceiver.java onReceive 69 =======================================================");
				SystemProperties.getInt("persist.sys.signaltype", 4);
				int value2 = SystemProperties.getInt("persist.sys.signaltype", 4);
				
				int singMode = 9;
				
				if (value2 == 3) {
					singMode = 0;
				} else if (value2 == 4) {
					singMode = 9;
				} else if (value2 == 5) {
					singMode = 9;
				}
				
        android.provider.Settings.Global.putInt(context.getContentResolver(),
        android.provider.Settings.Global.PREFERRED_NETWORK_MODE,
        singMode);				
			}*/				
				
				
    }
}
