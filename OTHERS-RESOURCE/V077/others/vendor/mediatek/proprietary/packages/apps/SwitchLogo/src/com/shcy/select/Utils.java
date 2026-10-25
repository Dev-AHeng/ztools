package com.shcy.select;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import java.io.FileFilter;
import java.io.File; 
import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;

public class Utils {
	  public final String UNIT_B = "B";
	  public final String UNIT_KB = "KB";
	  public final String UNIT_MB = "MB";
	  public final String UNIT_GB = "GB";
	  public final String UNIT_TB = "TB";
	  private final int UNIT_INTERVAL = 1024;
	  private final double ROUNDING_OFF = 0.005;
	  private final int DECIMAL_NUMBER = 100;
	  public static String TAG = "Utils";
	  
    public static String getNumCores()
    {
        class CpuFilter implements FileFilter
        {
            @Override
            public boolean accept(File pathname)
            {
                if (Pattern.matches("cpu[0-9]", pathname.getName()))
                {
                    return true;
                }
                return false;
            }
        }

        try
        {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return String.valueOf(files.length);
        } catch (Exception e)
        {
            // Default to return 1 core
            return "1";
        }
    } 
    
    private static String getCpuName(){
      String cpuName = "";
      FileReader fr;

	  	try {
			fr = new FileReader("/proc/cpuinfo");
		      BufferedReader br = new BufferedReader(fr);
		      String text;
						
			  text = br.readLine();
	
		      while(text != null) {    	
		    	if (text.startsWith("Hardware")) {
		    		cpuName = text.split(":")[1].trim();
		    		break;
		    	}
		        text = br.readLine();
		      }
	  	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
      return cpuName;
    }    	  
}
