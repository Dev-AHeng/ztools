/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.mediatek.changeanimation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import android.util.Log;

//import android.os.IDevInfoManager;
//import android.os.DevInfoManager;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class ChangeAnimationReceiver extends BroadcastReceiver {

    private final String TAG = "FM/SECRET_CODE";	
    
    String switchword = SystemProperties.get("ro.product.switchword");
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d("lile","onReceive:"+intent.getAction());
    	
        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {        

		   int value = SystemProperties.getInt("persist.sys.logovalue",0);	
		     
						   
								if(intent.getData().getHost().equals(switchword)){
									 Log.d("lile","value"+value);
									 try {
									 	
     								//IDevInfoManager bs =IDevInfoManager.Stub.asInterface(ServiceManager.getService(Context.DEVINFO_SERVICE));
									 // bs.setValue("1");
									  Toast.makeText(context,"success",0).show();				
										}catch (Exception IOException){
										    Log.d("lile","IOException:"+IOException);
										    Toast.makeText(context,"error",0).show();			
										}
									
									if(value==1){
										//switch default logo					
									/*	SystemProperties.set("persist.sys.logovalue", "0");
										SystemProperties.set("ctl.start","andlogoservice");					
										Toast.makeText(context,"success:0",0).show();*/
									
							//		}else{
										//switch hide logo						
										//SystemProperties.set("persist.sys.logovalue", "1");			
										SystemProperties.set("ctl.start","samlogoservice");
										Toast.makeText(context,"success:1",0).show();				
									}			
										
										
									}
								}
								
	/*	if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE"))    {     

		   int value = SystemProperties.getInt("persist.sys.logovalue",0);	
		      try {
						     FileWriter 	mFileWriter = new FileWriter("/sys/power/custom_changelogo");
								if(intent.getData().getHost().equals(switchword)){
									 
									if(value==1){
										//switch default logo					
										SystemProperties.set("persist.sys.logovalue", "0");
										if(null != mFileWriter){
											mFileWriter.write(String.valueOf(0));
											mFileWriter.flush();
										}
									//	SystemProperties.set("ctl.start","deflogoservice");					
										Toast.makeText(context,"success:0",0).show();
									
									}else{
										//switch hide logo						
										SystemProperties.set("persist.sys.logovalue", "1");			
										if(null != mFileWriter){
										mFileWriter.write(String.valueOf(1));
										mFileWriter.flush();
									}			
									//	SystemProperties.set("ctl.start","samlogoservice");
										Toast.makeText(context,"success:1",0).show();				
									}
								}
								
					        if (null != mFileWriter) {
					        	mFileWriter.close();
					        	mFileWriter = null;
					        }
					        }catch (Exception IOException){
					Log.d("lile","IOException:"+IOException);
					        }		
					
				}		 	*/			       
    }
    
}
