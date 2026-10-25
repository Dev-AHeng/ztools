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
import java.util.ArrayList;
import android.widget.Toast;
import android.provider.Settings;

//PRIZE_SWITCH_BOOTANIMA_SUPPORT
public class NvRAM extends Service{
	
   
	//private static final int AP_CFG_REEB_PRODUCT_INFO_LID = 36;
	private static final int AP_CFG_RDEB_FILE_WIFI_LID = 30;
	private static final int AP_CFG_RDEB_FILE_BT_ADDR_LID = 1;
	
	private static final String fileName = "/mnt/vendor/nvdata/APCFG/APRDEB/WIFI";
	private static final String INFO_ADDRESS_FILENAME = "/mnt/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";//
	private static final int nvramLenth = 256;//
	private static final int AP_CFG_REEB_PRODUCT_INFO_LID = 61;		
	
	private Toast mToast;
	
  @Override
  public IBinder onBind(Intent intent) {  
      return null;
  }

	@Override
    public void onStart(Intent intent, int startId) 
    {
		SystemProperties.set("persist.sys.test777", "777");
		android.util.Log.d("ChenMin0721", "MtkSettings NvRAM.java onStart 49 ===================================================================");
		Log.v("lile","onStart");

			//updateAddr(readAddr(1),1);
			//updateAddr(readAddr(2),2);
			/*if(readData()==0){
				writeData(1);
			}else{
				writeData(0);
				readData();
				}
			*/
		 	updateNvData();
		 readNvData();//for N Version
		 stopSelf();
	}

	public void readNvData()
    {
    	byte[] buffArr = new byte[nvramLenth];
			String buff = null;
      try {
					INvram agent = INvram.getService();
          buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
          //buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
					
					buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
					
      } catch (Exception e) {
          e.printStackTrace();
      }
				
			for(int i = 120; i <140; i++){
				android.util.Log.d("ChenMin0721", "MtkSettings NvRAM.java 84 readNvData byteint(buffArr[" + i + "]) = " + byteint(buffArr[i]));
			}
	}
	
	public void updateNvData()
    {
    	byte[] buffArr = new byte[nvramLenth];
			String buff = null;
      try {
					INvram agent = INvram.getService();
          buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
          //buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
					
					buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
					
      } catch (Exception e) {
          e.printStackTrace();
      }


			//SystemProperties.set("persist.sys.test333", byteint(buffArr[120]) + "");
			//SystemProperties.set("persist.sys.test444", byteint(buffArr[121]) + "");
			if(byteint(buffArr[120])!=0){//
				SystemProperties.set("persist.sys.rom", byteint(buffArr[120]) + "");
				int value2 = byteint(buffArr[120]);		
				if (value2 == 16) {
					SystemProperties.set("persist.sys.showromsize", 16 + "");
				} else if (value2 == 32) {
					SystemProperties.set("persist.sys.showromsize", 32 + "");
				}  else if (value2 == 64) {
					SystemProperties.set("persist.sys.showromsize", 64 + "");
				} else if (value2 == 65) {
					SystemProperties.set("persist.sys.showromsize", 128 + "");
				} else if (value2 == 66) {
					SystemProperties.set("persist.sys.showromsize", 256 + "");
				} else if (value2 == 67) {
					SystemProperties.set("persist.sys.showromsize", 512 + "");
				}	else if (value2 == 68) {
					SystemProperties.set("persist.sys.showromsize", 640 + "");
				} else if (value2 == 69) {
					SystemProperties.set("persist.sys.showromsize", 1024 + "");
				} else if (value2 == 70) {
					SystemProperties.set("persist.sys.showromsize", 2048 + "");
				}				
			} else if (byteint(buffArr[120])==0) {
				int value2 = SystemProperties.getInt("persist.sys.showromsize", 0);		
				if (value2 == 16) {
					SystemProperties.set("persist.sys.rom", 16 + "");
					writeNvData(120, getByteValue(32));
				} else if (value2 == 32) {
					SystemProperties.set("persist.sys.rom", 32 + "");
					writeNvData(120, getByteValue(32));
				} else if (value2 == 64) {
					SystemProperties.set("persist.sys.rom", 64 + "");
					writeNvData(120, getByteValue(64));
				} else if (value2 == 128) {
					SystemProperties.set("persist.sys.rom", 65 + "");
					writeNvData(120, getByteValue(65));
				} else if (value2 == 256) {
					SystemProperties.set("persist.sys.rom", 66 + "");
					writeNvData(120, getByteValue(66));
				} else if (value2 == 512) {
					SystemProperties.set("persist.sys.rom", 67 + "");
					writeNvData(120, getByteValue(67));
				}	else if (value2 == 640) {
					SystemProperties.set("persist.sys.rom", 68 + "");
					writeNvData(120, getByteValue(68));
				} else if (value2 == 1024) {
					SystemProperties.set("persist.sys.rom", 69 + "");
					writeNvData(120, getByteValue(69));
				} else if (value2 == 2048) {
					SystemProperties.set("persist.sys.rom", 70 + "");
					writeNvData(120, getByteValue(69));
				}	
				//		
			}
		
			if(byteint(buffArr[121])!=0){//
				SystemProperties.set("persist.sys.ram", byteint(buffArr[121]) + "");
				int value2 = byteint(buffArr[121]);		

				if (value2 == 1) {
					SystemProperties.set("persist.sys.showramsize", 1536 + "");	
				} else if (value2 == 2) {
					SystemProperties.set("persist.sys.showramsize", 2048 + "");
				} else if (value2 == 3) {
					SystemProperties.set("persist.sys.showramsize", 3072 + "");	
				}	else if (value2 == 4) {
					SystemProperties.set("persist.sys.showramsize", 4096 + "");	
				} else if (value2 == 6) {
					SystemProperties.set("persist.sys.showramsize", 6144 + "");	
				} else if (value2 == 8) {
					SystemProperties.set("persist.sys.showramsize", 8192 + "");	
				}	else if (value2 == 10) {
					SystemProperties.set("persist.sys.showramsize", 10240 + "");
				} else if (value2 == 12) {
					SystemProperties.set("persist.sys.showramsize", 12288 + "");
				} else if (value2 == 16) {
					SystemProperties.set("persist.sys.showramsize", 16384 + "");
				} else if (value2 == 18) {
					SystemProperties.set("persist.sys.showramsize", 18432 + "");
				} else if (value2 == 20) {
					SystemProperties.set("persist.sys.showramsize", 20480 + "");
				} else if (value2 == 22) {
					SystemProperties.set("persist.sys.showramsize", 22528 + "");
				} else if (value2 == 24) {
					SystemProperties.set("persist.sys.showramsize", 24576 + "");
				}				
			} else if (byteint(buffArr[121])==0) {
				int value2 = SystemProperties.getInt("persist.sys.showramsize", 0);			

				if (value2 == 1536) {
					SystemProperties.set("persist.sys.ram", 1 + "");
					writeNvData(121, getByteValue(1));	
				} else if (value2 == 2048) {
					SystemProperties.set("persist.sys.ram", 2 + "");
					writeNvData(121, getByteValue(2));	
				} else if (value2 == 3072) {
					SystemProperties.set("persist.sys.ram", 3 + "");
					writeNvData(121, getByteValue(3));	
				}	else if (value2 == 4096) {
					SystemProperties.set("persist.sys.ram", 4 + "");
					writeNvData(121, getByteValue(4));	
				} else if (value2 == 6144) {
					SystemProperties.set("persist.sys.ram", 6 + "");
					writeNvData(121, getByteValue(6));	
				} else if (value2 == 8192) {
					SystemProperties.set("persist.sys.ram", 8 + "");
					writeNvData(121, getByteValue(8));	
				}	else if (value2 == 10240) {
					SystemProperties.set("persist.sys.ram", 10 + "");
					writeNvData(121, getByteValue(10));	
				} else if (value2 == 12288) {
					SystemProperties.set("persist.sys.ram", 12 + "");
					writeNvData(121, getByteValue(12));	
				} else if (value2 == 16384) {
					SystemProperties.set("persist.sys.ram", 16 + "");
					writeNvData(121, getByteValue(16));	
				} else if (value2 == 18432) {
					SystemProperties.set("persist.sys.ram", 18 + "");
					writeNvData(121, getByteValue(18));	
				} else if (value2 == 20480) {
					SystemProperties.set("persist.sys.ram", 20 + "");
					writeNvData(121, getByteValue(20));	
				} else if (value2 == 22528) {
					SystemProperties.set("persist.sys.ram", 22 + "");
					writeNvData(121, getByteValue(22));	
				} else if (value2 == 24576) {
					SystemProperties.set("persist.sys.ram", 24 + "");
					writeNvData(121, getByteValue(24));	
				}						
				//writeNvData(121, getByteValue(value2));		24576
			}
		
			if(byteint(buffArr[122]) != 0){
				SystemProperties.set("persist.sys.signaltype", byteint(buffArr[122]) + "");
				int value2 = byteint(buffArr[122]);
				
				int singMode = 9;
				
				if (value2 == 3) {//3g
					singMode = 0;//1 0
				} else if (value2 == 4) {//4g
					singMode = 0;
				} else if (value2 == 5) {//5g
					singMode = 9;
				}
				android.util.Log.d("ChenMin1107", "MtkSettings NvRAM.java updateNvData 183 singMode = " + singMode);
				String modesubid = Settings.Global.getString(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE);
				
				SystemProperties.set("persist.sys.ttttt", modesubid);
				
				String modesubid1 = Settings.Global.getString(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE + 1);
				String modesubid2 = Settings.Global.getString(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE + 2);
				String modesubid3 = Settings.Global.getString(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE + 3);
				String modesubid4 = Settings.Global.getString(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE + 4);
				String modesubid5 = Settings.Global.getString(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE + 5);
				
				String netmode1s = SystemProperties.get("persist.sys.netmode1s", "");//persist.sys.netmode1s
				android.util.Log.d("ChenMin1107", "MtkSettings NvRAM.java updateNvData 195 netmode1s = " + netmode1s);
				Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE, singMode + "," + singMode);
				
				android.util.Log.d("ChenMin0721", "MtkSettings NvRAM.java updateNvData 190 modesubid = " + modesubid+ ", modesubid1= " + modesubid1 + ", modesubid2 = " + modesubid2 + ", modesubid3 = " + modesubid3 + ", modesubid4 = " + modesubid4 + ", modesubid5 = " + modesubid);
				
				if (!netmode1s.trim().equals("")) {
						String modeArr[] = netmode1s.split(",");
						String mNetworkType = "0";
						
						if (value2 == 3) {
							mNetworkType = modeArr[1];//modeArr[2]
						} else if (value2 == 4) {
							mNetworkType = modeArr[1];
						} else if (value2 == 5) {
							mNetworkType = modeArr[0];
						}						
						android.util.Log.d("ChenMin0721", "MtkSettings NvRAM.java updateNvData 211 mNetworkType = " + mNetworkType);
						
            if (modesubid1 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 1, mNetworkType);
						}
						
            if (modesubid2 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 2, mNetworkType);
						}
						
            if (modesubid3 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 3, mNetworkType);
						}
						
            if (modesubid4 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 4, mNetworkType);
						}
						
            if (modesubid5 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 5, mNetworkType);
						}																								
				} else {
						android.util.Log.d("ChenMin0721", "MtkSettings NvRAM.java updateNvData 233 singMode = " + singMode);
            if (modesubid1 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 1, singMode + "");
						}
						
            if (modesubid2 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 2, singMode + "");
						}
						
            if (modesubid3 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 3, singMode + "");
						}
						
            if (modesubid4 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 4, singMode + "");
						}
						
            if (modesubid5 != null) {
							Settings.Global.putString(this.getContentResolver(),  Settings.Global.PREFERRED_NETWORK_MODE + 5, singMode + "");
						}									
				
				}
				
        //android.provider.Settings.Global.putInt(this.getContentResolver(), android.provider.Settings.Global.PREFERRED_NETWORK_MODE, singMode);			
			} else {
				String value2 = SystemProperties.get("persist.sys.signaltype", "4");
				android.util.Log.d("ChenMin1107", "MtkSettings NvRAM.java updateNvData 259 value2 = " + value2);
				writeNvData(122, getByteValue(Integer.parseInt(value2)));	
			}
		
			/*if(byteint(buffArr[123])!=0){
					SystemProperties.set("persist.sys.camfront2", byteint(buffArr[123]) + "");
					int value2 = byteint(buffArr[123]);

					if (value2 == 2) {
						SystemProperties.set("persist.sys.picsizefront", "1600x1200,1920x1152,1920x1080");
					} else if (value2 == 3) {
						SystemProperties.set("persist.sys.picsizefront", "2048x1536,2240x1344,2304x1296");
					}  else if (value2 == 5) {
						SystemProperties.set("persist.sys.picsizefront", "2560x1920,2880x1728,2880x1616");
					} else if (value2 == 8) {
						SystemProperties.set("persist.sys.picsizefront", "3264x2448,3600x2160,3840x2160");
					} else if (value2 == 13) {
						SystemProperties.set("persist.sys.picsizefront", "4096x3072,4608x2592");
					} else if (value2 == 15) {
						SystemProperties.set("persist.sys.picsizefront", "5120x2880,4480x3360");
					} else if (value2 == 16) {
						SystemProperties.set("persist.sys.picsizefront", "4608x3456");
					} else if (value2 == 20) {
						SystemProperties.set("persist.sys.picsizefront", "5120x3840");
					} else if (value2 == 30) {
						SystemProperties.set("persist.sys.picsizefront", "6400x4800");
					} else {
	        	//SystemProperties.set("persist.sys.picsizefront", "1600x1200,1920x1152,1920x1080");										
					}
			} else if (byteint(buffArr[123])==0) {
					String value2 = SystemProperties.get("persist.sys.picsizefront", "").trim();	

					if (value2.equals("1600x1200,1920x1152,1920x1080")) {
							SystemProperties.set("persist.sys.camfront2", 2 + "");
					} else if (value2.equals("2048x1536,2240x1344,2304x1296")) {
							SystemProperties.set("persist.sys.camfront2", 3 + "");
					} else if (value2.equals("2560x1920,2880x1728,2880x1616")) {
							SystemProperties.set("persist.sys.camfront2", 5 + "");
					} else if (value2.equals("3264x2448,3600x2160,3840x2160")) {
							SystemProperties.set("persist.sys.camfront2", 8 + "");
					} else if (value2.equals("4096x3072,4608x2592")) {
							SystemProperties.set("persist.sys.camfront2", 13 + "");
					} else if (value2.equals("5120x2880,4480x3360")) {
							SystemProperties.set("persist.sys.camfront2", 15 + "");
					} else if (value2.equals("4608x3456")) {
							SystemProperties.set("persist.sys.camfront2", 16 + "");
					} else if (value2.equals("5120x3840")) {
							SystemProperties.set("persist.sys.camfront2", 20 + "");
					} else if (value2.equals("6400x4800")) {
							SystemProperties.set("persist.sys.camfront2", 30 + "");
					} else  {
							SystemProperties.set("persist.sys.camfront2", 2 + "");
					}
			}
		
			if(byteint(buffArr[124])!=0){
					SystemProperties.set("persist.sys.camback2", byteint(buffArr[124]) + "");
					int value2 = byteint(buffArr[124]);
					android.util.Log.d("ChenMin0721", "NvRAM.java 246 value2 = " + value2);
					if (value2 == 2) {
						SystemProperties.set("persist.sys.picsizeback", "1600x1200,1920x1152,1920x1080");
					} else if (value2 == 3) {
						SystemProperties.set("persist.sys.picsizeback", "2048x1536,2240x1344,2304x1296");
					}  else if (value2 == 5) {
						SystemProperties.set("persist.sys.picsizeback", "2560x1920,2880x1728,2880x1616");
					} else if (value2 == 8) {
						SystemProperties.set("persist.sys.picsizeback", "3264x2448,3600x2160,3840x2160");
					} else if (value2 == 13) {
						SystemProperties.set("persist.sys.picsizeback", "4096x3072,4608x2592");
					} else if (value2 == 15) {
						SystemProperties.set("persist.sys.picsizeback", "5120x2880,4480x3360");
					} else if (value2 == 16) {
						SystemProperties.set("persist.sys.picsizeback", "4608x3456");
					} else if (value2 == 20) {
						SystemProperties.set("persist.sys.picsizeback", "5120x3840");
					} else if (value2 == 30) {
						SystemProperties.set("persist.sys.picsizeback", "6400x4800");
					} else {
	        	//SystemProperties.set("persist.sys.picsizefront", "1600x1200,1920x1152,1920x1080");										
					}
			} else if (byteint(buffArr[124])==0) {
					String value2 = SystemProperties.get("persist.sys.picsizeback", "").trim();	
					android.util.Log.d("ChenMin0721", "NvRAM.java 270 value2 = " + value2);
					if (value2.equals("1600x1200,1920x1152,1920x1080")) {
							SystemProperties.set("persist.sys.camback2", 2 + "");
					} else if (value2.equals("2048x1536,2240x1344,2304x1296")) {
							SystemProperties.set("persist.sys.camback2", 3 + "");
					} else if (value2.equals("2560x1920,2880x1728,2880x1616")) {
							SystemProperties.set("persist.sys.camback2", 5 + "");
					} else if (value2.equals("3264x2448,3600x2160,3840x2160")) {
							SystemProperties.set("persist.sys.camback2", 8 + "");
					} else if (value2.equals("4096x3072,4608x2592")) {
							SystemProperties.set("persist.sys.camback2", 13 + "");
					} else if (value2.equals("5120x2880,4480x3360")) {
							SystemProperties.set("persist.sys.camback2", 15 + "");
					} else if (value2.equals("4608x3456")) {
							SystemProperties.set("persist.sys.camback2", 16 + "");
					} else if (value2.equals("5120x3840")) {
							SystemProperties.set("persist.sys.camback2", 20 + "");
					} else if (value2.equals("6400x4800")) {
							SystemProperties.set("persist.sys.camback2", 30 + "");
					} else  {
							SystemProperties.set("persist.sys.camback2", 2 + "");
					}
			}*/
		
			/*if(byteint(buffArr[125])!=0){//
				SystemProperties.set("persist.sys.cyversion", byteint(buffArr[125]) + "");
				int value2 = byteint(buffArr[125]);
				SystemProperties.set("persist.sys.newversion", value2 + "");
				if (value2 == 7) {
					SystemProperties.set("persist.sys.newversion", 7.1 + "");
				} else if (value2 == 8) {
					SystemProperties.set("persist.sys.newversion", 8.1 + "");
				}  else if (value2 == 9) {
					SystemProperties.set("persist.sys.newversion", 9.0 + "");
				} else if (value2 == 10) {
					SystemProperties.set("persist.sys.newversion", 10.0 + "");
				} else if (value2 == 11) {
					SystemProperties.set("persist.sys.newversion", 11.0 + "");
				} else if (value2 == 12) {
					SystemProperties.set("persist.sys.newversion", 12.0 + "");
				} else if (value2 == 13) {
					SystemProperties.set("persist.sys.newversion", 13.0 + "");
				} else if (value2 == 14) {
					SystemProperties.set("persist.sys.newversion", 14.0 + "");
				} else if (value2 == 15) {
					SystemProperties.set("persist.sys.newversion", 15.0 + "");
				} else if (value2 == 16) {
					SystemProperties.set("persist.sys.newversion", 16.0 + "");
				} else {
        			SystemProperties.set("persist.sys.newversion", value2 + "");										
				}
				 if (value2 == 9) {
					SystemProperties.set("persist.sys.newversion", 9.0 + "");
				}
			} else {
				String value2 = SystemProperties.get("persist.sys.newversion", "7");	
				SystemProperties.set("persist.sys.cyversion", value2 + "");				
				if (value2.equals("7.1")) {
					SystemProperties.set("persist.sys.cyversion", 7 + "");
					writeNvData(125, getByteValue(7));
				} else if (value2.equals("8.1")) {
					SystemProperties.set("persist.sys.cyversion", 8 + "");
					writeNvData(125, getByteValue(8));
				}  else if (value2.equals("9.0")) {
					SystemProperties.set("persist.sys.cyversion", 9 + "");
					writeNvData(125, getByteValue(9));
				} else if (value2.equals("10.0")) {
					SystemProperties.set("persist.sys.cyversion", 10 + "");
					writeNvData(125, getByteValue(10));
				} else if (value2.equals("11.0")) {
					SystemProperties.set("persist.sys.cyversion", 11 + "");
					writeNvData(125, getByteValue(11));
				} else if (value2.equals("12.0")) {
					SystemProperties.set("persist.sys.cyversion", 12 + "");
					writeNvData(125, getByteValue(12));
				} else if (value2.equals("13.0")) {
					SystemProperties.set("persist.sys.cyversion", 13 + "");
					writeNvData(125, getByteValue(13));
				} else if (value2.equals("14.0")) {
					SystemProperties.set("persist.sys.cyversion", 14 + "");
					writeNvData(125, getByteValue(14));
				} else if (value2.equals("15.0")) {
					SystemProperties.set("persist.sys.cyversion", 15 + "");
					writeNvData(125, getByteValue(15));
				} else if (value2.equals("16.0")) {
					SystemProperties.set("persist.sys.cyversion", 16 + "");
					writeNvData(125, getByteValue(16));
				} else {
        			SystemProperties.set("persist.sys.cyversion", 7 + "");	
					writeNvData(125, getByteValue(7));									
				}								
                if (value2.equals("9.0")) {
					SystemProperties.set("persist.sys.cyversion", 9.0 + "");
					writeNvData(125, getByteValue(9));
				}
			}
		
			if(byteint(buffArr[126])!=0){
				SystemProperties.set("persist.sys.cycore", byteint(buffArr[126]) + "");
				SystemProperties.set("persist.sys.cpuinfo", byteint(buffArr[126]) + "");
			} else {
				String cpuinfo = SystemProperties.get("persist.sys.cpuinfo", "4");	
				SystemProperties.set("persist.sys.cycore", cpuinfo);
			}
		
			if(byteint(buffArr[127])!=0){//
				int value2 = byteint(buffArr[127]);
				SystemProperties.set("persist.sys.cycpu", byteint(buffArr[127]) + "");
				
				if (value2 == 1) {
					SystemProperties.set("persist.sys.cpukernel", "MT6582");
				} else if (value2 == 2) {
					SystemProperties.set("persist.sys.cpukernel", "MT6595");
				} else if (value2 == 3) {
					SystemProperties.set("persist.sys.cpukernel", "MT6735");
				} else if (value2 == 4) {
					SystemProperties.set("persist.sys.cpukernel", "MT6753");
				} else if (value2 == 5) {
					SystemProperties.set("persist.sys.cpukernel", "MT6762");
				} else if (value2 == 6) {
					SystemProperties.set("persist.sys.cpukernel", "MT6797");
				} else if (value2 == 7) {
					SystemProperties.set("persist.sys.cpukernel", "MT6763");
				} else if (value2 == 8) {
					SystemProperties.set("persist.sys.cpukernel", "MT6580");
				} else if (value2 == 9) {
					SystemProperties.set("persist.sys.cpukernel", "MT6755");
				} else if (value2 == 10) {
					SystemProperties.set("persist.sys.cpukernel", "MT6795");
				} else if (value2 == 11) {
					SystemProperties.set("persist.sys.cpukernel", "MT6750");
				} else if (value2 == 12) {
					SystemProperties.set("persist.sys.cpukernel", "MT6752");
				} else if (value2 == 13) {
					SystemProperties.set("persist.sys.cpukernel", "Snapdragon 860");
				} else if (value2 == 14) {
					SystemProperties.set("persist.sys.cpukernel", "Snapdragon 870");
				} else if (value2 == 15) {
					SystemProperties.set("persist.sys.cpukernel", "Snapdragon 888");
				}  else {
        	SystemProperties.set("persist.sys.cpukernel", "");										
				}
			} else {
				String value2 = SystemProperties.get("persist.sys.cpukernel", "MT6735");	
				
				if (value2.equals("MT6582")) {
					SystemProperties.set("persist.sys.cycpu", 1 + "");
					writeNvData(127, getByteValue(1));
				} else if (value2.equals("MT6595")) {
					SystemProperties.set("persist.sys.cycpu", 2 + "");
					writeNvData(127, getByteValue(2));
				} else if (value2.equals("MT6735")) {
					SystemProperties.set("persist.sys.cycpu", 3 + "");
					writeNvData(127, getByteValue(3));
				} else if (value2.equals("MT6753")) {
					SystemProperties.set("persist.sys.cycpu", 4 + "");
					writeNvData(127, getByteValue(4));
				} else if (value2.equals("MT6762")) {
					SystemProperties.set("persist.sys.cycpu", 5 + "");
					writeNvData(127, getByteValue(5));
				} else if (value2.equals("MT6797")) {
					SystemProperties.set("persist.sys.cycpu", 6 + "");
					writeNvData(127, getByteValue(6));
				} else if (value2.equals("MT6763")) {
					SystemProperties.set("persist.sys.cycpu", 7 + "");
					writeNvData(127, getByteValue(7));
				} else if (value2.equals("MT6580")) {
					SystemProperties.set("persist.sys.cycpu", 8 + "");
					writeNvData(127, getByteValue(3));
				} else if (value2.equals("MT6755")) {
					SystemProperties.set("persist.sys.cycpu", 9 + "");
					writeNvData(127, getByteValue(4));
				} else if (value2.equals("MT6795")) {
					SystemProperties.set("persist.sys.cycpu", 10 + "");
					writeNvData(127, getByteValue(5));
				} else if (value2.equals("MT6750")) {
					SystemProperties.set("persist.sys.cycpu", 11 + "");
					writeNvData(127, getByteValue(6));
				} else if (value2.equals("MT6752")) {
					SystemProperties.set("persist.sys.cycpu", 12 + "");
					writeNvData(127, getByteValue(7));
				} else if (value2.equals("Snapdragon 860")) {
					SystemProperties.set("persist.sys.cycpu", 13 + "");
					writeNvData(127, getByteValue(5));
				} else if (value2.equals("Snapdragon 870")) {
					SystemProperties.set("persist.sys.cycpu", 14 + "");
					writeNvData(127, getByteValue(6));
				} else if (value2.equals("Snapdragon 888")) {
					SystemProperties.set("persist.sys.cycpu", 15 + "");
					writeNvData(127, getByteValue(7));
				}  else {
        	SystemProperties.set("persist.sys.cycpu", 1 + "");	
					writeNvData(127, getByteValue(1));									
				}
								
				//SystemProperties.set("persist.sys.cycpu", "3");
			}*/
		
			if(byteint(buffArr[128])!=0){//
				SystemProperties.set("persist.sys.cybattery", byteint(buffArr[128]) + "");
				
				SystemProperties.set("persist.sys.battery", byteint(buffArr[128]) * 1000 + "");
			} else {
				//String battery = SystemProperties.get("persist.sys.battery", "5000");	
				int cybattery = SystemProperties.getInt("persist.sys.battery", 5000);
				SystemProperties.set("persist.sys.cybattery", cybattery/1000 + "");
			}
		
			if(byteint(buffArr[129])!=0){
				SystemProperties.set("persist.sys.inches", byteint(buffArr[129]) + "");
				int temp = byteint(buffArr[129]);
				if (temp == 10) {
						SystemProperties.set("persist.sys.inchestemp", "10.1");
				} else if (temp == 11) {
						SystemProperties.set("persist.sys.inchestemp", "11.6");
				} else if (temp == 12) {
						SystemProperties.set("persist.sys.inchestemp", "12.0");
				} else if (temp == 15) {
						SystemProperties.set("persist.sys.inchestemp", "15.0");
				}
			} else {
				String value2 = SystemProperties.get("persist.sys.inches", "10");
				if (value2.equals("10")) {
					SystemProperties.set("persist.sys.inchestemp", "10.1");
				} else if (value2.equals("11")) {
					SystemProperties.set("persist.sys.inchestemp", "11.6");
				} else if (value2.equals("12")) {
					SystemProperties.set("persist.sys.inchestemp", "12.0");
				} else if (value2.equals("15")) {
					SystemProperties.set("persist.sys.inchestemp", "15.0");
				}
			}
		
			if(byteint(buffArr[130])!=0){
				SystemProperties.set("persist.sys.screen", byteint(buffArr[130]) + "");
				
				int value2 = byteint(buffArr[130]);
				//SystemProperties.set("persist.sys.cycpu", byteint(buffArr[127]) + "");
				
				if (value2 == 1) {
					SystemProperties.set("persist.sys.lcmwidth", "800");
					SystemProperties.set("persist.sys.lcmheigh", "1280");
				} else if (value2 == 2) {
					SystemProperties.set("persist.sys.lcmwidth", "1920");
					SystemProperties.set("persist.sys.lcmheigh", "1280");
				} else if (value2 == 3) {
					SystemProperties.set("persist.sys.lcmwidth", "1960");
					SystemProperties.set("persist.sys.lcmheigh", "1080");
				} else if (value2 == 4) {
					SystemProperties.set("persist.sys.lcmwidth", "2560");
					SystemProperties.set("persist.sys.lcmheigh", "1600");
				} else if (value2 == 5) {
					SystemProperties.set("persist.sys.lcmwidth", "1920");
					SystemProperties.set("persist.sys.lcmheigh", "1200");
				} else if (value2 == 6) {
					SystemProperties.set("persist.sys.lcmwidth", "3440");
					SystemProperties.set("persist.sys.lcmheigh", "1040");
				} else if (value2 == 7) {
					SystemProperties.set("persist.sys.lcmwidth", "2048");
					SystemProperties.set("persist.sys.lcmheigh", "1536");
				} 
								
			} else {
					String lcd_widthfake = SystemProperties.get("persist.sys.lcmwidth").trim();
	 				String lcd_lengthfake = SystemProperties.get("persist.sys.lcmheigh").trim();
					
					String screen = lcd_widthfake + "*" + lcd_lengthfake;
					
					if (screen.equals("800*1280")) {
						SystemProperties.set("persist.sys.screen", 1 + "");
					} else if (screen.equals("1920*1280")) {
						SystemProperties.set("persist.sys.screen", 2 + "");
					} else if (screen.equals("1960*1080")) {
						SystemProperties.set("persist.sys.screen", 3 + "");
					} else if (screen.equals("2560*1600")) {
						SystemProperties.set("persist.sys.screen", 4 + "");
					} else if (screen.equals("1920*1200")) {
						SystemProperties.set("persist.sys.screen", 5 + "");
					}else if (screen.equals("3440*1040")) {
						SystemProperties.set("persist.sys.screen", 6 + "");
					} else if (screen.equals("2048*1536")) {
						SystemProperties.set("persist.sys.screen", 7 + "");
					}
			}
    }

		public int byteint(byte data)
    {
        return (int) data;
    }
		
		public int byte2int(byte[] data)
    {
        int target = (data[0] & 0xff) | ((data[1] << 8) & 0xff00) | ((data[2] << 24) >>> 8) | (data[3] << 24);
        return target;
    }

		public byte[] getBytes(int n)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
    
	
    public byte getByteValue(int position) {
        return (byte) position;
    }

    private void writeNvData(int position, byte value) {
        try {
            INvram agent = INvram.getService();
            String buff = null;
						int i = 0;
            try {
                buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
								android.util.Log.d("ChenMin1107", "MtkSettings NvRAM.java writeNvData 1015 buff = " + buff);
								Thread.sleep(150);
            } catch (Exception e) {
								android.util.Log.d("ChenMin1107", "NvRAM.java writeNvData 1017 getMessage = " + e.getMessage());
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
						android.util.Log.d("ChenMin1107", "MtkSettings NvRAM.java writeNvData 1031");
            int flag = 0;
						mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
            try {
                flag = agent.writeFileByNamevec(INFO_ADDRESS_FILENAME, nvramLenth, dataArray);//
								Thread.sleep(150);
            } catch (Exception e) {
                e.printStackTrace();
                mToast.setText(e.getMessage() + ":" + e.getCause());
								android.util.Log.d("ChenMin1107", "MtkSettings NvRAM.java writeNvData 1039 getMessage = " + e.getMessage() + ":" + e.getCause());
                mToast.show();
                return;
            }
            //mToast.setText("Update SN successfully.\r\nPlease reboot this device");
            //mToast.show();
        } catch (Exception e) {
            //mToast.setText(e.getMessage() + ":" + e.getCause());
						android.util.Log.d("ChenMin1107", "MtkSettings SelectActivity.java writeNvData 1047 getMessage = " + e.getMessage() + ":" + e.getCause());
            mToast.show();
            e.printStackTrace();
        }
    }


}
