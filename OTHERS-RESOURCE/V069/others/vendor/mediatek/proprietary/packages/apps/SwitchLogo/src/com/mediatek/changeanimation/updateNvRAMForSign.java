package com.mediatek.changeanimation;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.util.Log;
import java.util.Random;
import java.util.StringTokenizer;
import java.text.NumberFormat;
import java.text.DecimalFormat;
//import vendor.mediatek.hardware.nvram.V1_0.INvram;
import com.android.internal.util.HexDump;
import android.os.SystemProperties;
import com.mediatek.changeanimation.NvRAMAgent;

import android.os.IBinder ;
import android.os.ServiceManager;
import com.mediatek.changeanimation.NvRAMAgent;
import android.content.BroadcastReceiver;  

public class updateNvRAMForSign extends BroadcastReceiver{
		public static String TAG2 = "ChenMin0721";
		public static String TAG = "updateNvRAMForSign";	
   
		public void onReceive(Context context, Intent mintent) {  
				Log.d(TAG2, TAG + " " + " 31 onReceive mintent.getAction() = " + mintent.getAction());
				if ("android.intent.action.updatesignaltype".equals(mintent.getAction())) {  
				//Log.d("ChenMin1101", TAG + " " + " BootReceiver 31 onReceive mintent.getAction() = " + mintent.getAction());
				updateNvDataForSign();
			}
		}
	
		public void updateNvDataForSign()
    {
    	byte[] buffArr = new byte[256];
      try {
          IBinder binder = ServiceManager.getService("NvRAMAgent");
          NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
          buffArr = agent.readFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO");
          //buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
      } catch (Exception e) {
          e.printStackTrace();
      }
      
      int value2 = SystemProperties.getInt("persist.sys.signaltype", 4);	
      Log.d(TAG2, TAG + " " + " 51 onReceive value2 = " + value2);
			writeNvData(122, getByteValue(value2));		
    }
	
    public byte getByteValue(int position) {
        return (byte) position;
    }

    public void writeNvData(int position, byte c) {
        try {
            IBinder binder = ServiceManager.getService("NvRAMAgent");
            NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
            byte[] buff = agent.readFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO");
            //byte[] buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1));
            if (position < buff.length) {
                buff[position] = c;
            }
            //byte[] dataArray = new byte(buff.length);
           // for (int i = 0; i < buff.length; i++) {
           //     dataArray.add(i, new Byte(buff[i]));
           /// }
            agent.writeFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO", buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	

}
