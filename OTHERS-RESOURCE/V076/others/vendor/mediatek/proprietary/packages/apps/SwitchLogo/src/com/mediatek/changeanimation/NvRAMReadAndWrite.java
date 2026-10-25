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
//for Test chenmin add
public class NvRAMReadAndWrite extends Service{   
		private static final int AP_CFG_REEB_PRODUCT_INFO_LID = 36;
		private static final int AP_CFG_RDEB_FILE_WIFI_LID = 30;
		private static final int AP_CFG_RDEB_FILE_BT_ADDR_LID = 1;
		
		private static final int startAdd = 120;
		private static final int endAdd = 136;		
		
    @Override
    public IBinder onBind(Intent intent) {  
        return null;
    }

		@Override
    public void onStart(Intent intent, int startId) 
    {
			android.util.Log.d("ChenMin0721", "NvRAMReadAndWrite.java onStart 42 ===================================================================");
			Log.v("lile","onStart");
	
			//updateNvData();
			readNvData(); 
			stopSelf();
		}
	
		public void readNvData()
    {
    	byte[] buffArr = new byte[256];
    	IBinder binder = null;
    	NvRAMAgent agent = null;
      try {
          binder = ServiceManager.getService("NvRAMAgent");
          agent = NvRAMAgent.Stub.asInterface(binder);
          buffArr = agent.readFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO");
          //buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
      } catch (Exception e) {
          e.printStackTrace();
      }
      android.util.Log.d("ChenMin0721", "NvRAMReadAndWrite.java 61 first starting read.........");
      for (int t = startAdd; t <= endAdd; t++) {
      	android.util.Log.d("ChenMin0721", "NvRAMReadAndWrite.java 63 readNvData byteint(buffArr[" + t + "]) = " + byteint(buffArr[t]));
      } 
      
      android.util.Log.d("ChenMin0721", "NvRAMReadAndWrite.java 66 starting write.........");
      for (int t = startAdd; t <= endAdd; t++) {
      	writeNvData(t, getByteValue(0));	
      }
      
			try {
          buffArr = agent.readFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO");
          //buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
      } catch (Exception e) {
          e.printStackTrace();
      } 
           
      android.util.Log.d("ChenMin0721", "NvRAMReadAndWrite.java 78 again starting read.........");
      for (int t = startAdd; t <= endAdd; t++) {
      	android.util.Log.d("ChenMin0721", "NvRAMReadAndWrite.java readNvData 63 byteint(buffArr[" + t + "]) = " + byteint(buffArr[t]));
      }      
            
    }

		public int byteint(byte data)
    {
        return (int) data;
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
