package com.android.settings;

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
import vendor.mediatek.hardware.nvram.V1_0.INvram;
import com.android.internal.util.HexDump;
import android.os.SystemProperties;
//import com.mediatek.changeanimation.NvRAMAgent;

import android.os.IBinder ;
import android.os.ServiceManager;
import android.content.BroadcastReceiver;  
import android.content.ComponentName;
import java.util.ArrayList;

public class updateNvRAMForSign extends BroadcastReceiver{
		public static String TAG2 = "ChenMin0707";
		public static String TAG = "updateNvRAMForSign";	
		
		private static final int AP_CFG_REEB_PRODUCT_INFO_LID = 61;
		private static final int AP_CFG_RDEB_FILE_WIFI_LID = 30;
		private static final int AP_CFG_RDEB_FILE_BT_ADDR_LID = 1;
		
		private static final int nvramLenth = 256;//
	
		///vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO
		private static final String INFO_ADDRESS_FILENAME = "/mnt/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";//		
		
   
		public void onReceive(Context context, Intent mintent) {  
			Log.d(TAG2, TAG + " " + " 31 onReceive mintent.getAction() = " + mintent.getAction());
			if ("android.intent.action.updatesignaltype".equals(mintent.getAction())) {  
				//Log.d("ChenMin1101", TAG + " " + " BootReceiver 31 onReceive mintent.getAction() = " + mintent.getAction());
				readNvData();
				//readNvData();
				updateNvDataForSign();
				try {
					Thread.sleep(500);
	      } catch (Exception e) {
						Log.d(TAG2, TAG + " " + " 53 onReceive e.getMessage() = " + e.getMessage());
	          e.printStackTrace();
	      }				
				readNvData();
				//readNvData();
				//updateNvDataForSign();				
			}
		}
	
		public void updateNvDataForSign()
    { 
      int value2 = SystemProperties.getInt("persist.sys.signaltype", 4);	
      Log.d(TAG2, TAG + " " + " 51 onReceive value2 = " + value2);
			writeNvData(122, getByteValue(value2));	
			readNvData();	
    }
	
    public byte getByteValue(int position) {
        return (byte) position;
    }

    private void writeNvData(int position, byte value) {
        try {
						android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 68");
            INvram agent = INvram.getService();
            String buff = null;
						int i = 0;
            try {
								android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 68 buff = " + buff);
                buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
								android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 69 buff = " + buff);
            } catch (Exception e) {
								android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 71 getMessage = " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Remove \0 in the end
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));

            buffArr[position] = value;
            ArrayList<Byte> dataArray = new ArrayList<Byte>(nvramLenth);

            for (i = 0; i < nvramLenth; i++) {
                dataArray.add(i, new Byte(buffArr[i]));
            }
						android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 85");
            int flag = 0;
						//mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
            try {
                flag = agent.writeFileByNamevec(INFO_ADDRESS_FILENAME, nvramLenth, dataArray);//
            } catch (Exception e) {
                e.printStackTrace();
                //mToast.setText(e.getMessage() + ":" + e.getCause());
								android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 94 getMessage = " + e.getMessage() + ":" + e.getCause());
                //mToast.show();
                return;
            }
            //mToast.setText("Update SN successfully.\r\nPlease reboot this device");
            //mToast.show();
        } catch (Exception e) {
            //mToast.setText(e.getMessage() + ":" + e.getCause());
						android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java writeNvData 102 getMessage = " + e.getMessage() + ":" + e.getCause());
            //mToast.show();
            e.printStackTrace();
        }
    }
    
		public void readNvData()
	  {
	    	byte[] buffArr = new byte[nvramLenth];
				String buff = null;
				android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java readNvData 112");
	      try {
						INvram agent = INvram.getService();
	          buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
	          //buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
					
						buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
					
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
				
				for(int i = 120; i < 140; i++){
					android.util.Log.d("ChenMin1107", "updateNvRAMForSign.java 125 readNvData byteint(buffArr[" + i + "]) = " + ((int)(buffArr[i])));
				}
		}   
}
