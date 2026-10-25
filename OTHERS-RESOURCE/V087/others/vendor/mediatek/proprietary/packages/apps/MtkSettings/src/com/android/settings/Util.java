package com.android.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.os.SystemProperties;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import android.content.Context;
import android.util.Log;
import android.os.storage.StorageManager;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import android.os.StatFs;
import java.util.Locale;
import java.util.HashMap;//add wdl
import java.util.Map;//add wdl

public class Util {
	static final String TAG = "Util";
	public static String readFileContent(String fileName) {
	    File file = new File(fileName);
	    
	    if (!file.exists()) {

	    	return "0";	    	
	    }
	    
	    BufferedReader reader = null;
	    StringBuffer sbf = new StringBuffer();
	    try {
	        reader = new BufferedReader(new FileReader(file));
	        String tempStr;
	        while ((tempStr = reader.readLine()) != null) {
	            sbf.append(tempStr);
	        }
	        reader.close();
	        return sbf.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	    }
	    return sbf.toString().trim();
	}
	
	/*public static long getRomfs() {
		String romfs = readFileContent("/system/bin/kernel_rominfo");
				
		String romfsinfo = SystemProperties.get("ro.product.showromsize", "0").trim();
		
		if (!romfsinfo.equals("0")) {	 		
			romfs = romfsinfo;
		}		
	    return  Long.parseLong(romfs);
	}*/
	
	public static long getRomfs() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		//Log.d("ChenMin0720", "getTotRomSize 4865 totalBlocks * blockSize     = " + (totalBlocks * blockSize) + ", blockSize = " + blockSize + ", totalBlocks = " + totalBlocks);
				
		long totle = totalBlocks * blockSize;
		
		long totleGB = totle/1024/1024/1024;
		//Log.d("ChenMin0720", "getTotRomSize 4870 totleGB  = " + totleGB);
		
		int showromsize = SystemProperties.getInt("persist.sys.showromsize", 0);
		
		if (showromsize != 0) {
			//Log.d("ChenMin0717", "getRomfs 83 return showromsize  = " + showromsize);
			return (long)showromsize;
		}
		
		if (totleGB < 4) {
			totleGB = 4;
		} else if (totleGB > 4 && totleGB < 8) {
			totleGB = 8;
		} else {
			totleGB = ((totleGB/16) + 1) * 16;
		}
		//Log.d("ChenMin0421", "getTotRomSize 48654985 totleGB  = " + totleGB);
		//Log.d("ChenMin0717", "getRomfs 83 return showromsize  = " + totleGB);
		return totleGB;//4/8/16/32/64/128/256
	} 		
	
	public static long getRomfsdiv() {
		String romfsdiv = readFileContent("/system/bin/kernel_rominfo");
		
		if (romfsdiv.equals("32")) {			
			romfsdiv = "10";
		} else if (romfsdiv.equals("64")) {
			romfsdiv = "18";
		} else if (romfsdiv.equals("128")) {
			romfsdiv = "32";
		} else if (romfsdiv.equals("256")) {
			romfsdiv = "64";
		} else if (romfsdiv.equals("512")) {
			romfsdiv = "128";
		}
		
		String romfsdivinfo = SystemProperties.get("persist.sys.romfsdiv", "0").trim();
		
		if (!romfsdivinfo.equals("0")) {			
			romfsdiv = romfsdivinfo;
		}
		return 1;	
	    //return Long.parseLong(romfsdiv);
	}
	
	public static float getAddramsetdiff(String actual, String diff) {
		float total = Float.valueOf(actual);
		
		float diffsize = 0f;
		
		int multiple = ((int)total/2 + 1);
		//Log.d("ChenMin0720",  " " + "getAddramsetdiff multiple = " + multiple);
		
		if (multiple <= 1) {			
			diffsize = 0.1f;
		} else if (multiple >= 2 && multiple <= 4){
			diffsize = 0.15f * multiple;
		} else {
			diffsize = 0.20f * multiple;			
		}
		
		if (!diff.equals("0")) {			
			diffsize = Float.parseFloat(diff);
		}	
		//Log.d("ChenMin0720",  " " + "getAddramsetdiff diffsize = " + diffsize);
	    return  diffsize;
	}

	/****
	String used = Formatter.formatFileSize(context, com.android.settings.Util.getUseStorageSize(context));
	**/	
	public static long getUseStorageSize(Context mContext) {
		StorageManagerVolumeProvider mStorageManagerVolumeProvider;
		StorageManager storageManager = mContext.getSystemService(StorageManager.class);
    	mStorageManagerVolumeProvider = new StorageManagerVolumeProvider(storageManager);
			
		final PrivateStorageInfo info = PrivateStorageInfo.getPrivateStorageInfo(mStorageManagerVolumeProvider);
		
    	long tempsize = 0;
    	long tempsize2 = 0;		
		long usedBytes = 0;
				
		int showromsize = SystemProperties.getInt("persist.sys.showromsize", 0);
		//Log.d("ChenMin0526", " " + "StorageVolumePreference 161 showromsize = " + showromsize);
		if (showromsize != 0) {
	    	tempsize = info.totalBytes/2;
			if (showromsize == 512 || showromsize == 640 || showromsize == 1024 || showromsize == 2048) {
				tempsize = showromsize * 1000 * 1000 * 1000l;//512000000000l;
				//Log.d("ChenMin0526", " " + "StorageVolumePreference 166 showromsize = " + showromsize + ", tempsize = " + tempsize);
				
				if (showromsize == 2048) {
					tempsize = showromsize * 1024 * 1024 * 1024l;
					android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", showromsize = " + showromsize + ", tempsize = " + tempsize);
					tempsize = 2199023255552l;
				}
			}
			//Log.d("ChenMin0526", " " + "StorageVolumePreference 168 showromsize = " + showromsize);
	    	
			//android.util.Log.e("ChenMin0716", "" + getLineNumber() + ", showromsize = " + showromsize + ", tempsize = " + tempsize + ", info.freeBytes = " + info.freeBytes);
			if (showromsize == 2048) {
				tempsize2 = info.freeBytes;
			} else {
				tempsize2 = info.freeBytes*93/100;			
			}					
			usedBytes = tempsize - tempsize2;
			//Log.d("ChenMin0526", " " + "StorageVolumePreference 171 ================================ tempsize = " + tempsize + ", tempsize2 = " + tempsize2 + ", usedBytes = " + usedBytes);				
		} else {
		    //tempsize = info.totalBytes;
		    //tempsize2 = info.freeBytes;		
			usedBytes = info.totalBytes - info.freeBytes;	
			//Log.d("ChenMin0526", " " + "StorageVolumePreference 175 showromsize = " + showromsize);		
		}										
		//Log.d("ChenMin0526", " " + "StorageVolumePreference 177 info.totalBytes = " + info.totalBytes);
		//Log.d("ChenMin0526", " " + "getUseStorageSize 178 info.totalBytes = " + info.totalBytes + ", info.freeBytes = " + info.freeBytes + ", tempsize " + tempsize + ", tempsize2 = " + tempsize2 + ", usedBytes = " + usedBytes);	
		//Log.d("ChenMin0526", " " + "StorageVolumePreference 176 usedBytes = " + usedBytes);
		
	  return usedBytes;
	}
	
	public static long getAvailMemoryInfo(Context context) {
       ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
  
       MemoryInfo outInfo = new MemoryInfo();
       am.getMemoryInfo(outInfo);
	   
       //Log.d("ChenMin0720", " " + "getAvailMemoryInfo 136 outInfo.availMem = " + outInfo.availMem);
	   
       return outInfo.availMem;
    }
	
	public static String getTotalRam(){//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + " GB";
    }
//add wdl
	private static final Map<Character, Character> arabicToWesternMap = new HashMap<>();
	static {
        // 阿拉伯数字字符到西方数字字符的映射
        arabicToWesternMap.put('٠', '0');
        arabicToWesternMap.put('١', '1');
        arabicToWesternMap.put('٢', '2');
        arabicToWesternMap.put('٣', '3');
        arabicToWesternMap.put('٤', '4');
        arabicToWesternMap.put('٥', '5');
        arabicToWesternMap.put('٦', '6');
        arabicToWesternMap.put('٧', '7');
        arabicToWesternMap.put('٨', '8');
        arabicToWesternMap.put('٩', '9');
    }
	public static String convertArabicToWestern(String arabicString) {
        StringBuilder westernString = new StringBuilder();
        for (char c : arabicString.toCharArray()) {
            westernString.append(arabicToWesternMap.getOrDefault(c, c));
        }
        return westernString.toString();
    }
    
    public static float parseFloatSafely(String totalString) {
        String westernString = convertArabicToWestern(totalString);
        try {
            return Float.parseFloat(westernString);
        } catch (NumberFormatException e) {
            // 处理解析失败的情况
            //System.err.println("Failed to parse the string: " + totalString);
            return Float.NaN; // 或者其他适当的默认值
        }
		
    }
//add wdl		
/*	public static String getTotalRamNUM(){//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "";
    }*/		
	
	public static String getUseRamForStr(Context mContext){//GB
       // String totalString = getTotalRamNUM();//getTotalRam();//Formatter.formatShortFileSize(context, (long) totalRam);
	   String totalString = getTotalRam();//Formatter.formatShortFileSize(context, (long) totalRam);
				//not adapter Arabic
				//String freeString = String.valueOf(com.android.settings.Util.getAvailMemoryInfo(mContext)/1024/1024/1024f);//
        //String freeString = String.valueOf(com.android.settings.Util.getAvailMemoryInfo(mContext));// + " GB"; ///1024/1024/1024f getUseRamFormatStr(context, (long) com.android.settings.Util.getAvailMemoryInfo(mContext));
						
				//freeString = getNumDecimals(freeString);
String freeString = String.valueOf(com.android.settings.Util.getAvailMemoryInfo(mContext)/1024/1024/1024f);
						
		freeString = getNumDecimals(freeString);
		freeString = freeString.replace(",", ".");
				//Log.d("ChenMin0720", "getUseRamForStr 239 freeString = " + freeString + ", totalString = " + totalString);
		
				//Float usedRam = (Float.parseFloat(getNumForString(totalString)) - Float.parseFloat(getNumForString(freeString)));//not adapter Arabic
				//Float usedRam = (Float.parseFloat(getNumForString(totalString)) * 1024 * 1024 * 1024- Float.parseFloat(getNumForString(freeString)));//
				Float usedRam = (parseFloatSafely(getNumForString(totalString)) - parseFloatSafely(getNumForString(freeString)));//add wdl
				//Log.d("ChenMin0720", "getUseRamForStr 243 totalString = " + totalString + ", usedRam = " + usedRam);
							
				String usedResult = getNumDecimals(String.valueOf(usedRam));// + " GB";
				//Log.d("ChenMin0720", "getUseRamForStr 246 totalString = " + totalString + ", usedResult = " + usedResult + ", usedRam = " + usedRam);
				
        return usedResult;
    }
	
	public static String getNumDecimals(String str) {
			java.text.DecimalFormat df = new java.text.DecimalFormat("#.#");  
			double d = Double.parseDouble(str);  
			//System.out.println(df.format(d));
     return df.format(d);
	}
	
	public static String getNumDecimalsTwo(String str) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");  
		double d = Double.parseDouble(str);  
        return df.format(d);
	}	
	
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
    }
	
    static public long getTotRomSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
				//Log.d("ChenMin0720", TAG + " " + "getTotRomSize 212 totalBlocks * blockSize     = " + (totalBlocks * blockSize) + ", blockSize = " + blockSize + ", totalBlocks = " + totalBlocks);
        return totalBlocks * blockSize;
    }


    static public long getAvaRomSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        
        long availableBlocks = stat.getAvailableBlocks();
				//Log.d("ChenMin0720", TAG + " " + "getAvaRomSize 223 availableBlocks * blockSize = " + (availableBlocks * blockSize) + ", blockSize = " + blockSize + ", availableBlocks = " + availableBlocks);
        return availableBlocks * blockSize;
    }
	
	public static String getFreeRomForStr(long romSize){
        String romSizeStr = String.valueOf(romSize/1024/1024/1024) + " GB";
        return romSizeStr;
    }	
	
	public static long getshowromsize() {				
		int showromsize = SystemProperties.getInt("persist.sys.showromsize", 0);		
	  	return showromsize;
	}
	
	public static String getsizeForBitToGB(double romSize){
        String romSizeStr = getNumDecimalsTwo(String.valueOf((double)(romSize/1024/1024/1024))) + " GB";
        return romSizeStr;
    }
	
    /**
     * android.util.Log.e("ChenMin0716", "" + getLineNumber());
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
	
	public static String getDisplayLanguage() {		
		Locale currentLocale = Locale.getDefault();		
		String language = currentLocale.getLanguage();
		String country = currentLocale.getCountry();
		
        return language + "_" + country;
	}				
}
