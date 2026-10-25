package com.shcy.select;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.internal.util.HexDump;
import java.util.ArrayList;
import com.mediatek.changeanimation.R;
import android.util.Log;
import android.util.TypedValue;
import com.mediatek.changeanimation.NvRAM;
import android.os.IBinder ;
import android.os.ServiceManager;
import com.mediatek.changeanimation.NvRAMAgent;
import vendor.mediatek.hardware.nvram.V1_0.INvram;//
import android.content.ComponentName;
import android.os.UserHandle;

public class SelectActivity extends Activity {
    private View mAnimContain;
    private FlowRadioGroup mAnimGroup;
    private View mBackCamContain;
    private FlowRadioGroup mBackCamGroup;
    private View mBatteryContain;
    private FlowRadioGroup mBatteryGroup;
    private BluetoothAdapter mBluetoothAdapter;
    private View mCPUContain;
    private FlowRadioGroup mCPUGroup;
    private View mCoreContain;
    private FlowRadioGroup mCoreGroup;
    private View mDeviceNameContain;
    private FlowRadioGroup mDeviceNameGroup;
    private EditText mEditName;
    private View mEditNameContain;
    private Button mFinishButton;
    private View mFrontCamContain;
    private FlowRadioGroup mFrontCamGoup;
    private View mImeiContain;
    private FlowRadioGroup mImeiGroup;
    private View mInchesContain;
    private FlowRadioGroup mInchesGroup;
    private ProgressDialog mProgressDialog;
    private View mRamContain;
    private FlowRadioGroup mRamGroup;
    private View mRomContain;
    private FlowRadioGroup mRomGroup;
    private View mScreenContain;
    private FlowRadioGroup mScreenGroup;
    private View mSignalContain;
    private FlowRadioGroup mSignalGroup;
    private View mVersionContain;
    private FlowRadioGroup mVersionGroup;
    private WifiManager mWifiManager;
    private WifiConfiguration mWifiConfig = null;
    private Handler mHandler = new Handler();
		
	private static final String fileName = "/mnt/vendor/nvdata/APCFG/APRDEB/WIFI";//WIFI PRODUCT_INFO
		
	private static final String INFO_ADDRESS_FILENAME = "/mnt/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";//
	private static final int nvramLenth = 256;//
	private static final int AP_CFG_REEB_PRODUCT_INFO_LID = 61;		
		
	private static String TAG = "ChenMin0612";	
		
	private Toast mToast;
		
    Runnable mCheckRunnable = new Runnable() { 
        @Override // java.lang.Runnable
        public void run() {
            if (SelectActivity.this.mProgressDialog != null) {
                SelectActivity.this.mProgressDialog.hide();
                SelectActivity.this.mProgressDialog.dismiss();
            }
            SelectActivity.this.rebootNow();
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_xml);
		android.util.Log.d("ChenMin0612", "NvRAM.java readNvData 92 ========================================");
        this.mFinishButton = (Button) findViewById(R.id.reboot);
        this.mAnimGroup = (FlowRadioGroup) findViewById(R.id.select_anim_group);
        this.mRomGroup = (FlowRadioGroup) findViewById(R.id.select_rom_group);
        this.mRamGroup = (FlowRadioGroup) findViewById(R.id.select_ram_group);
        this.mSignalGroup = (FlowRadioGroup) findViewById(R.id.select_signal_group);
        this.mBackCamGroup = (FlowRadioGroup) findViewById(R.id.select_back_cam_group);
        this.mFrontCamGoup = (FlowRadioGroup) findViewById(R.id.select_front_cam_group);
        this.mDeviceNameGroup = (FlowRadioGroup) findViewById(R.id.select_device_name_group);
        this.mVersionGroup = (FlowRadioGroup) findViewById(R.id.select_version_group);
        this.mCoreGroup = (FlowRadioGroup) findViewById(R.id.select_core_group);
        this.mCPUGroup = (FlowRadioGroup) findViewById(R.id.select_cpu_group);
        this.mBatteryGroup = (FlowRadioGroup) findViewById(R.id.select_battery_group);
        this.mInchesGroup = (FlowRadioGroup) findViewById(R.id.select_inches_group);
        this.mScreenGroup = (FlowRadioGroup) findViewById(R.id.select_screen_group);
        this.mImeiGroup = (FlowRadioGroup) findViewById(R.id.select_imei_group);
        this.mAnimContain = findViewById(R.id.select_anim_contain);
        this.mRomContain = findViewById(R.id.select_rom_contain);
        this.mRamContain = findViewById(R.id.select_ram_contain);
        this.mSignalContain = findViewById(R.id.select_signal_contain);
        this.mBackCamContain = findViewById(R.id.select_back_cam_contain);
        this.mFrontCamContain = findViewById(R.id.select_front_cam_contain);
        this.mDeviceNameContain = findViewById(R.id.select_device_name_contain);
        this.mVersionContain = findViewById(R.id.select_version_contain);
        this.mCoreContain = findViewById(R.id.select_core_contain);
        this.mCPUContain = findViewById(R.id.select_cpu_contain);
        this.mBatteryContain = findViewById(R.id.select_battery_contain);
        this.mInchesContain = findViewById(R.id.select_inches_contain);
        this.mScreenContain = findViewById(R.id.select_screen_contain);
        this.mImeiContain = findViewById(R.id.select_imei_contain);
        this.mEditNameContain = findViewById(R.id.edit_device_name_contain);
        this.mEditName = (EditText) findViewById(R.id.shcy_model_name_edit);
        updateSupportUI(getIntent().getStringExtra("shcy_support_select_ui"));
        this.mFinishButton.setOnClickListener(new View.OnClickListener() { // 
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                String mName = SelectActivity.this.mEditName.getText().toString();
                if (mName != null && (!"".equals(mName))) {
                    if (mName.length() > 15) {
                        Toast.makeText(SelectActivity.this, (int) R.string.devicce_name_too_long, 0).show();
                    } else {
                        SystemProperties.set("persist.sys.cyname", mName);
                        //SelectActivity.this.writeNameNvData(129, mName);
                        SelectActivity.this.updateWifiConfigName(mName);
                        SelectActivity.this.updateBTName(mName);
                    }
                }
                SelectActivity.this.rebootDelay();
            }
        });
				/*writeNvData(120, SelectActivity.this.getByteValue(0));
				writeNvData(121, SelectActivity.this.getByteValue(0));
				writeNvData(127, SelectActivity.this.getByteValue(0));
				writeNvData(131, SelectActivity.this.getByteValue(0));*/		
				readNvData();
    }

    private void updateSupportUI(String supportui) {
        if (supportui == null || !(!"".equals(supportui))) {
            initAnimGroup();
            initRomGroup();
            initRamGroup();
            initSignalGroup();
            initBackCamGroup();
            initFrontCamGroup();
            initDeviceName();
            initVersionGroup();
            initCoreGroup();
            initCpuGroup();
            initImeiGroup();
						initBatteryGroup();
						initInchesGroup();
						initScreenGroup();
            initEditNameGroup();
            return;
        }
        if (supportui.contains("anim")) {
            initAnimGroup();
        } else {
            this.mAnimContain.setVisibility(View.GONE);
        }
        if (supportui.contains("rom")) {
            initRomGroup();
        } else {
            this.mRomContain.setVisibility(View.GONE);
        }
        if (supportui.contains("ram")) {
            initRamGroup();
        } else {
            this.mRamContain.setVisibility(View.GONE);
        }
        if (supportui.contains("signal")) {
            initSignalGroup();
        } else {
            this.mSignalContain.setVisibility(View.GONE);
        }
        if (supportui.contains("backcam")) {
            initBackCamGroup();
        } else {
            this.mBackCamContain.setVisibility(View.GONE);
        }
        if (supportui.contains("frontcam")) {
            initFrontCamGroup();
        } else {
            this.mFrontCamContain.setVisibility(View.GONE);
        }
        if (supportui.contains("devicename")) {
            initDeviceName();
            initEditNameGroup();
        } else {
            this.mDeviceNameContain.setVisibility(View.GONE);
            this.mEditNameContain.setVisibility(View.GONE);
        }
        if (supportui.contains("version")) {
            initVersionGroup();
        } else {
            this.mVersionContain.setVisibility(View.GONE);
        }
        if (supportui.contains("core")) {
            initCoreGroup();
        } else {
            this.mCoreContain.setVisibility(View.GONE);
        }
        if (supportui.contains("cpu")) {
            initCpuGroup();
        } else {
            this.mCPUContain.setVisibility(View.GONE);
        }
        if (supportui.contains("battery")) {
            initBatteryGroup();
        } else {
            this.mBatteryContain.setVisibility(View.GONE);
        }
        if (supportui.contains("inches")) {
            initInchesGroup();
        } else {
            this.mInchesContain.setVisibility(View.GONE);
        }
        if (supportui.contains("screen")) {
            initScreenGroup();
        } else {
            this.mScreenContain.setVisibility(View.GONE);
        }
        if (supportui.contains("imei")) {
            initImeiGroup();
        } else {
            this.mImeiContain.setVisibility(View.GONE);
        }
    }

    
    public void rebootNow() {
        Intent i = new Intent("android.intent.action.REBOOT");
        i.putExtra("nowait", 1);
        i.putExtra("interval", 1);
        i.putExtra("window", 0);
        sendBroadcast(i);
    }

    
    public void rebootDelay() {
        this.mProgressDialog = getProgressDialog();
        this.mProgressDialog.show();
        this.mHandler.postDelayed(this.mCheckRunnable, 15000);
    }

    private void initAnimGroup() {
        String animSelect = SystemProperties.get("persist.sys.changeain");
        int selectValue = 0;
        if (animSelect != null && (!animSelect.equals(""))) {
            selectValue = Integer.valueOf(animSelect).intValue();
        }
        this.mAnimContain.setVisibility(View.GONE);//GONE VISIBLE
        String[] mAnimNames = getResources().getStringArray(R.array.shcy_select_anim_entries);
		
        if (mAnimNames.length > 1) {
            for (int i = 0; i < mAnimNames.length; i++) {				
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);				
                tempButton.setText(mAnimNames[i]);				
                tempButton.setTag(Integer.valueOf(i));
                this.mAnimGroup.addView(tempButton);
				//Log.d("chenq","use tempButton " + mAnimNames[i]);
                if (selectValue == i) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mAnimGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
								//android.util.Log.d("ChenMin0721", "SelectActivity.java initAnimGroup 268 checkedId = " + checkedId); 
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
										android.util.Log.d("ChenMin0721", "SelectActivity.java initAnimGroup 272 value = " + value);
                    //SelectActivity.this.writeNvData(0, SelectActivity.this.getByteValue(value));
                    int value2 = value;// + 1;
                    SystemProperties.set("persist.sys.changeain", value2 + "");
                    SelectActivity.this.writeNvData(131, SelectActivity.this.getByteValue(value2));	
										
										/*for (int t = 120; t <= 140; t++) {
											writeNvData(t, SelectActivity.this.getByteValue(0));
										}*/																							
                }
            }
        });
    }

    private void initRomGroup() {
        String romSelect = SystemProperties.get("persist.sys.rom");
        int selectValue = 0;
        if (romSelect != null && (!romSelect.equals(""))) {
            selectValue = Integer.valueOf(romSelect).intValue();
        }
				android.util.Log.d("ChenMin0721", "SelectActivity.java mRomGroup 310 selectValue = " + selectValue);
        this.mRomContain.setVisibility(View.VISIBLE);
        String[] mRomNames = getResources().getStringArray(R.array.shcy_select_rom_entries);
        String[] mRomValues = getResources().getStringArray(R.array.shcy_select_rom_entrvalues);
        if (mRomNames.length > 1) {
            for (int i = 0; i < mRomNames.length; i++) {
                int value = Integer.valueOf(mRomValues[i]).intValue();
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mRomNames[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mRomGroup.addView(tempButton);
				//Log.d("chenq","use tempButton mRomNames" + mRomNames[i]);
								android.util.Log.d("ChenMin0721", "SelectActivity.java mRomGroup 310 value = " + value);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        } else {
            this.mRomContain.setVisibility(View.GONE);
        }
        this.mRomGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
                    SystemProperties.set("persist.sys.rom", value2 + "");
					android.util.Log.d("ChenMin0721", "SelectActivity.java mRomGroup 316 value = " + value2);
					if (value2 == 65) {											
						SystemProperties.set("persist.sys.showromsize", 128 + "");
					} else if (value2 == 66) {											
						SystemProperties.set("persist.sys.showromsize", 256 + "");
					} else if (value2 == 67) {											
						SystemProperties.set("persist.sys.showromsize", 512 + "");
					} else if (value2 == 68) {											
						SystemProperties.set("persist.sys.showromsize", 640 + "");
					} else if (value2 == 69) {											
						SystemProperties.set("persist.sys.showromsize", 1024 + "");
					} else if (value2 == 70) {											
						SystemProperties.set("persist.sys.showromsize", 2048 + "");
					}	else {
						SystemProperties.set("persist.sys.showromsize", value2 + "");
					}							
                    SelectActivity.this.writeNvData(120, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initRamGroup() {
        String ramSelect = SystemProperties.get("persist.sys.ram");
        int selectValue = 0;
        if (ramSelect != null && (!ramSelect.equals(""))) {
            selectValue = Integer.valueOf(ramSelect).intValue();
        }
		android.util.Log.d("ChenMin0721", "SelectActivity.java initRamGroup 366 selectValue = " + selectValue);
        this.mRamContain.setVisibility(View.VISIBLE);
        String[] mRamNames = getResources().getStringArray(R.array.shcy_select_ram_entries);
        String[] mRamValues = getResources().getStringArray(R.array.shcy_select_ram_entrvalues);
        if (mRamNames.length > 1) {
            for (int i = 0; i < mRamNames.length; i++) {
                int value = Integer.valueOf(mRamValues[i]).intValue();
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mRamNames[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mRamGroup.addView(tempButton);
				android.util.Log.d("ChenMin0721", "SelectActivity.java initRamGroup 378 value = " + value);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        } else {
            this.mRamContain.setVisibility(View.GONE);
        }
        this.mRamGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
                    SystemProperties.set("persist.sys.ram", value2 + "");
					//android.util.Log.d("ChenMin0721", "SelectActivity.java mRamGroup 356 value2 = " + value2);
                    SelectActivity.this.writeNvData(121, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initSignalGroup() {
        String signalSelect = SystemProperties.get("persist.sys.signaltype");
        int selectValue = 0;
        if (signalSelect != null && (!signalSelect.equals(""))) {
            selectValue = Integer.valueOf(signalSelect).intValue();
        }
        this.mSignalContain.setVisibility(View.GONE);//View.VISIBLE GONE
        String[] mSignalNames = getResources().getStringArray(R.array.shcy_select_single_entries);
        String[] mSignalValues = getResources().getStringArray(R.array.shcy_select_single_entrvalues);
        if (mSignalNames.length > 1) {
            for (int i = 0; i < mSignalNames.length; i++) {
                int value = Integer.valueOf(mSignalValues[i]).intValue();
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mSignalNames[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mSignalGroup.addView(tempButton);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        } else {
            this.mSignalContain.setVisibility(View.GONE);
        }
        this.mSignalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
                    SystemProperties.set("persist.sys.signaltype", value2 + "");
					//android.util.Log.d("ChenMin0721", "SelectActivity.java initSignalGroup 434 value2 = " + value2);
                    //SelectActivity.this.sendBroadcast(new Intent("com.android.action.UPDATE_SIGNAL_STATE"));
                    SelectActivity.this.writeNvData(122, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initFrontCamGroup() {
        String camSelect = SystemProperties.get("persist.sys.camfront2");
        int selectValue = 0;
        if (camSelect != null && (!camSelect.equals(""))) {
            selectValue = Integer.valueOf(camSelect).intValue();
        }
        this.mFrontCamContain.setVisibility(View.GONE);//View.VISIBLE GONE
        String[] mCamNames = getResources().getStringArray(R.array.shcy_select_cam_entries);
        String[] mCamValues = getResources().getStringArray(R.array.shcy_select_cam_entrvalues);
        if (mCamNames.length > 1) {
            for (int i = 0; i < mCamNames.length; i++) {
                int value = Integer.valueOf(mCamValues[i]).intValue();
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mCamNames[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mFrontCamGoup.addView(tempButton);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        } else {
            this.mFrontCamContain.setVisibility(View.GONE);
        }
        this.mFrontCamGoup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
										//android.util.Log.d("ChenMin0721", "SelectActivity.java initFrontCamGroup 438 value2 = " + value2);
                    SystemProperties.set("persist.sys.camfront2", value2 + "");
					if(value2==2){
						SystemProperties.set("persist.sys.picsizefront", "1600x1200,1920x1152,1920x1080");
					}else if (value2==3){
						SystemProperties.set("persist.sys.picsizefront", "2048x1536,2240x1344,2304x1296");
					}else if (value2==5){
						SystemProperties.set("persist.sys.picsizefront", "2560x1920,2880x1728,2880x1616");
					}else if (value2==8){
						SystemProperties.set("persist.sys.picsizefront", "3264x2448,3600x2160,3840x2160");
					}else if (value2==13){
						SystemProperties.set("persist.sys.picsizefront", "4096x3072,4608x2592");
					}else if (value2==15){
						SystemProperties.set("persist.sys.picsizefront", "5120x2880,4480x3360");
					}else if (value2==16){
						SystemProperties.set("persist.sys.picsizefront", "4608x3456");
					}else if (value2==20){
						SystemProperties.set("persist.sys.picsizefront", "5120x3840");
					}else if (value2==30){
						SystemProperties.set("persist.sys.picsizefront", "6400x4800");
					}
					android.util.Log.d("ChenMin0612", "SelectActivity.java" + " " + "initFrontCamGroup 495");
					Intent intent = new Intent("android.intent.action.updatecamer");   
    				intent.setComponent(new ComponentName("com.mediatek.camera", "com.mediatek.camera.upDateCamePicSizeReceiver"));		
					intent.putExtra("cameraId", "1"); 
        			SelectActivity.this.sendBroadcast(intent);							
					SelectActivity.this.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
																			
                    SelectActivity.this.writeNvData(123, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initBackCamGroup() {
        String camSelect = SystemProperties.get("persist.sys.camback2");
        int selectValue = 0;
        if (camSelect != null && (!camSelect.equals(""))) {
            selectValue = Integer.valueOf(camSelect).intValue();
        }
        this.mBackCamContain.setVisibility(View.GONE);//View.VISIBLE GONE
        String[] mCamNames = getResources().getStringArray(R.array.shcy_select_cam_entries);
        String[] mCamValues = getResources().getStringArray(R.array.shcy_select_cam_entrvalues);
        if (mCamNames.length > 1) {
            for (int i = 0; i < mCamNames.length; i++) {
                int value = Integer.valueOf(mCamValues[i]).intValue();
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mCamNames[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mBackCamGroup.addView(tempButton);
								android.util.Log.d("ChenMin0721", "SelectActivity.java initBackCamGroup 516 value = " + value);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        } else {
            this.mBackCamContain.setVisibility(View.GONE);
        }
        this.mBackCamGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
					android.util.Log.d("ChenMin0721", "SelectActivity.java initBackCamGroup 534 value2 = " + value2);
                    SystemProperties.set("persist.sys.camback2", value2 + "");
					if(value2==2){
						SystemProperties.set("persist.sys.picsizeback", "1600x1200,1920x1152,1920x1080");
					}else if (value2==3){
						SystemProperties.set("persist.sys.picsizeback", "2048x1536,2240x1344,2304x1296");
					}else if (value2==5){
						SystemProperties.set("persist.sys.picsizeback", "2560x1920,2880x1728,2880x1616");
					}else if (value2==8){
						SystemProperties.set("persist.sys.picsizeback", "3264x2448,3600x2160,3840x2160");
					}else if (value2==13){
						SystemProperties.set("persist.sys.picsizeback", "4096x3072,4608x2592");
					}else if (value2==15){
						SystemProperties.set("persist.sys.picsizeback", "5120x2880,4480x3360");
					}else if (value2==16){
						SystemProperties.set("persist.sys.picsizeback", "4608x3456");
					}else if (value2==20){
						SystemProperties.set("persist.sys.picsizeback", "5120x3840");
					}else if (value2==30){
						SystemProperties.set("persist.sys.picsizeback", "6400x4800");
					}
					android.util.Log.d("ChenMin0612", "SelectActivity.java" + " " + "initBackCamGroup 560");
					Intent intent = new Intent("android.intent.action.updatecamer");   
    				intent.setComponent(new ComponentName("com.mediatek.camera", "com.mediatek.camera.upDateCamePicSizeReceiver"));		
					intent.putExtra("cameraId", "0"); 
        			SelectActivity.this.sendBroadcast(intent);
					SelectActivity.this.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
																				
                    SelectActivity.this.writeNvData(124, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initDeviceName() {
        String deviceNameSelect = SystemProperties.get("persist.sys.cyname");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        this.mWifiConfig = this.mWifiManager.getWifiApConfiguration();
        this.mDeviceNameContain.setVisibility(View.GONE);//View.VISIBLE View.GONE
        String[] mDeviceNames = getResources().getStringArray(R.array.shcy_select_devicename_entries);
        if (mDeviceNames.length > 1) {
            for (int i = 0; i < mDeviceNames.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mDeviceNames[i]);
                tempButton.setTag(mDeviceNames[i]);
                this.mDeviceNameGroup.addView(tempButton);
                if (mDeviceNames[i].equals(deviceNameSelect)) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mDeviceNameGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    String value = String.valueOf(radioButton.getTag());
                    SystemProperties.set("persist.sys.cyname", value);
                    if (SelectActivity.this.mEditName != null) {
                        SelectActivity.this.mEditName.setText(value);
                    }
                }
            }
        });
    }

    private void initVersionGroup() {
        String version = SystemProperties.get("persist.sys.cyversion");
        this.mVersionContain.setVisibility(View.GONE);//VISIBLE GONE
        int selectValue = 0;
        if (version != null && (!version.equals(""))) {
            selectValue = Integer.valueOf(version).intValue();
        }
				//android.util.Log.d("ChenMin0721", "SelectActivity.java initVersionGroup 559 version = " + version);
				//ro.build.version.release
				String realversion = SystemProperties.get("ro.build.version.release");
				//android.util.Log.d("ChenMin0721", "SelectActivity.java initVersionGroup 562 realversion = " + realversion);
				
        String[] mVersions = getResources().getStringArray(R.array.shcy_select_version_entries);
        String[] mVersionValues = getResources().getStringArray(R.array.shcy_select_version_entrvalues);
        if (mVersions.length > 1) {
            for (int i = 0; i < mVersions.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                int value = Integer.valueOf(mVersionValues[i]).intValue();
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mVersions[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mVersionGroup.addView(tempButton);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mVersionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
					SystemProperties.set("persist.sys.cyversion", value2 + "");
					//android.util.Log.d("ChenMin0721", "SelectActivity.java initVersionGroup 582 value2 = " + value2);
					SystemProperties.set("persist.sys.newversion", value2 + "");
					/*if (value2 == 7) {
						SystemProperties.set("persist.sys.newversion", 7.1 + "");
					} else if (value2 == 8) {
						SystemProperties.set("persist.sys.newversion", 8.1 + "");
					} else if (value2 == 9) {
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
					}  else {
						SystemProperties.set("persist.sys.newversion", value2 + "");										
					}*/
                    if (value2 == 9) {
						SystemProperties.set("persist.sys.newversion", 9.0 + "");
					}
                    SelectActivity.this.writeNvData(125, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initCoreGroup() {
        String core = SystemProperties.get("persist.sys.cycore");
        this.mCoreContain.setVisibility(View.GONE);//View.VISIBLE GONE
        int selectValue = 0;
        if (core != null && (!core.equals(""))) {
            selectValue = Integer.valueOf(core).intValue();
        }
        String[] mCores = getResources().getStringArray(R.array.shcy_select_core_entries);
        String[] mCoreValues = getResources().getStringArray(R.array.shcy_select_core_entrvalues);
        if (mCores.length > 1) {
            for (int i = 0; i < mCores.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                int value = Integer.valueOf(mCoreValues[i]).intValue();
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mCores[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mCoreGroup.addView(tempButton);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mCoreGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
										//android.util.Log.d("ChenMin0721", "SelectActivity.java initCoreGroup 582 value2 = " + value2);
                    SystemProperties.set("persist.sys.cycore", value2 + "");
										
										SystemProperties.set("persist.sys.cpuinfo", value2 + "");
                    SelectActivity.this.writeNvData(126, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initCpuGroup() {
        String cpu = SystemProperties.get("persist.sys.cycpu");
        this.mCPUContain.setVisibility(View.GONE);//View.VISIBLE GONE
        int selectValue = 0;
        if (cpu != null && (!cpu.equals(""))) {
            selectValue = Integer.valueOf(cpu).intValue();
        }
        String[] mCPUs = getResources().getStringArray(R.array.shcy_select_cpu_entries);
        String[] mCPUValues = getResources().getStringArray(R.array.shcy_select_cpu_entrvalues);
        if (mCPUs.length > 1) {
            for (int i = 0; i < mCPUs.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                int value = Integer.valueOf(mCPUValues[i]).intValue();
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mCPUs[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mCPUGroup.addView(tempButton);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mCPUGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
										//android.util.Log.d("ChenMin0721", "SelectActivity.java initCpuGroup 658 value2 = " + value2);
                    SystemProperties.set("persist.sys.cycpu", value2 + "");
										
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
										
                    SelectActivity.this.writeNvData(127, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initBatteryGroup() {
        String battery = SystemProperties.get("persist.sys.cybattery");
        this.mBatteryContain.setVisibility(View.GONE);//View.VISIBLE GONE
        int selectValue = 0;
        if (battery != null && (!battery.equals(""))) {
            selectValue = Integer.valueOf(battery).intValue();
        }
        String[] mBatterys = getResources().getStringArray(R.array.shcy_select_battery_entries);
        String[] mBatteryValues = getResources().getStringArray(R.array.shcy_select_battery_entrvalues);
				android.util.Log.d("ChenMin1118", "SelectActivity.java initBatteryGroup 782 selectValue = " + selectValue);
        if (mBatterys.length > 1) {
            for (int i = 0; i < mBatterys.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                int value = Integer.valueOf(mBatteryValues[i]).intValue();
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mBatterys[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mBatteryGroup.addView(tempButton);
								android.util.Log.d("ChenMin1118", "SelectActivity.java initBatteryGroup 791 value = " + value);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mBatteryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
										//android.util.Log.d("ChenMin0721", "SelectActivity.java initBatteryGroup 695 value2 = " + (Integer.valueOf(String.valueOf(radioButton.getTag())).intValue() + ""));
                    SystemProperties.set("persist.sys.cybattery", Integer.valueOf(String.valueOf(radioButton.getTag())).intValue() + "");
										
										SystemProperties.set("persist.sys.battery", Integer.valueOf(String.valueOf(radioButton.getTag())).intValue() * 1000 + "");
										
                    int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
										//SystemProperties.set("persist.sys.cybattery", radioButton.getText().toString());
										SelectActivity.this.writeNvData(128, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initInchesGroup() {
        String inches = SystemProperties.get("persist.sys.inches");
        this.mInchesContain.setVisibility(View.GONE);//View.VISIBLE GONE
        int selectValue = 0;
        if (inches != null && (!inches.equals(""))) {
            selectValue = Integer.valueOf(inches).intValue();
        }
        String[] mInches = getResources().getStringArray(R.array.shcy_select_inches_entries);
        String[] mInchesValues = getResources().getStringArray(R.array.shcy_select_inches_entrvalues);
        if (mInches.length > 1) {
            for (int i = 0; i < mInches.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                int value = Integer.valueOf(mInchesValues[i]).intValue();
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mInches[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mInchesGroup.addView(tempButton);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mInchesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
					int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
                    SystemProperties.set("persist.sys.inches", Integer.valueOf(String.valueOf(radioButton.getTag())).intValue() + "");
					SelectActivity.this.writeNvData(129, SelectActivity.this.getByteValue(value2));
                }
            }
        });
    }

    private void initScreenGroup() {
        String screen = SystemProperties.get("persist.sys.screen");
        this.mScreenContain.setVisibility(View.GONE);//View.VISIBLE GONE
        int selectValue = 0;
        if (screen != null && (!screen.equals(""))) {
            selectValue = Integer.valueOf(screen).intValue();
        }
		//
		android.util.Log.d("ChenMin1118", "SelectActivity.java initScreenGroup 862 selectValue = " + selectValue);
        String[] mScreen = getResources().getStringArray(R.array.shcy_select_screen_entries);
        String[] mScreenValues = getResources().getStringArray(R.array.shcy_select_screen_entrvalues);
        if (mScreen.length > 1) {
            for (int i = 0; i < mScreen.length; i++) {
                RadioButton tempButton = new RadioButton(this);
                int value = Integer.valueOf(mScreenValues[i]).intValue();
                tempButton.setPadding(2, 0, 0, 0);
                tempButton.setText(mScreen[i]);
                tempButton.setTag(Integer.valueOf(value));
                this.mScreenGroup.addView(tempButton);
								android.util.Log.d("ChenMin1118", "SelectActivity.java initScreenGroup 873 value = " + value);
                if (selectValue == value) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        }
        this.mScreenGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) SelectActivity.this.findViewById(checkedId);
                if (radioButton != null) {
										int value2 = Integer.valueOf(String.valueOf(radioButton.getTag())).intValue();
                    SystemProperties.set("persist.sys.screen", Integer.valueOf(String.valueOf(radioButton.getTag())).intValue() + "");
										SelectActivity.this.writeNvData(130, SelectActivity.this.getByteValue(value2));
										String temp = "";
										if (value2 == 1) {
											temp = "800*1280";
										} else if (value2 == 2) {
											temp = "1920*1280";
										} else if (value2 == 3) {
											temp = "1960*1080";
										} else if (value2 == 4) {
											temp = "2560*1600";
										} else if (value2 == 5) {
											temp = "1920*1200";
										} else if (value2 == 6) {
											temp = "3440*1040";
										} else if (value2 == 7) {
											temp = "2048*1536";
										}	else if (value2 == 2) {
											temp = "800*1280";
										}				
										
										String tempArr[] = temp.split("\\*");																		
										SystemProperties.set("persist.sys.lcmwidth", tempArr[0]);
										SystemProperties.set("persist.sys.lcmheigh", tempArr[1]);				
                }
            }
        });
    }

    private void initImeiGroup() {
        this.mImeiContain.setVisibility(View.GONE);
    }

    private void initEditNameGroup() {
        String name = SystemProperties.get("persist.sys.cyname");
        this.mEditNameContain.setVisibility(View.GONE);
        this.mEditName.setText(name);
    }

    
    public void updateWifiConfigName(String name) {
        if (this.mWifiConfig != null) {
            this.mWifiConfig.SSID = name;
            this.mWifiManager.setWifiApConfiguration(this.mWifiConfig);
        }
    }

   
    public void updateBTName(final String name) {
        if (this.mBluetoothAdapter == null) {
            return;
        }
        if (!this.mBluetoothAdapter.isEnabled()) {
            this.mBluetoothAdapter.enable();
            this.mHandler.postDelayed(new Runnable() { 
                @Override // java.lang.Runnable
                public void run() {
                    SelectActivity.this.mBluetoothAdapter.setName(name);
                    SelectActivity.this.mBluetoothAdapter.disable();
                }
            }, 10000);
            return;
        }
        this.mBluetoothAdapter.setName(name);
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait));
        return progressDialog;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.mProgressDialog != null) {
            this.mProgressDialog.hide();
            this.mProgressDialog.dismiss();
        }
    }

    
    public byte getByteValue(int position) {
        return (byte) position;
    }

		static public INvram getINvAgent() {
        INvram agent = null;
        try {
				     android.util.Log.d("ChenMin0612", "SelectActivity.java getINvAgent 956");
             agent = INvram.getService();
             if (agent == null) {
                Log.d(TAG, "agent111 is null");
                return null;
             }
        } catch (Exception e) {
            Log.e(TAG, "e=" + e);
            return null;
        }

        return agent;
    }
		
		static public String readSNForID() {
        INvram agent = getINvAgent();
				android.util.Log.d("ChenMin0612", "SelectActivity.java readSNForID 971 =======================");
        if (agent == null) {
            return null;
        }
        try {
            String buff = agent.mnqReadFileByLid(AP_CFG_REEB_PRODUCT_INFO_LID, nvramLenth);//readFileBylid

						android.util.Log.d("ChenMin0612", "SelectActivity.java readSNForID 978 buff = " + buff + ", buff.substring(8) = " + buff.substring(8));
            if (buff != null) {
                return buff.substring(8);
            }
        } catch(Exception e) {
        		Log.d(TAG, "logstart:\n" + e.getMessage());
						android.util.Log.d("ChenMin0612", "SelectActivity.java readSNForID 984 Exception = " + e.getMessage());
        }
        return null;
    }		
		
		static public String readSNForFileName() {
        INvram agent = getINvAgent();

        if (agent == null) {
            return null;
        }
				
        try {
            String buff = agent.readFileByName(INFO_ADDRESS_FILENAME, 256);//readFileBylid												
						android.util.Log.d("ChenMin0612", "SelectActivity.java readSNForFileName 998 buff = " + buff + ", buff.substring(8) = " + buff.substring(8));
            if (buff != null) {
                return buff.substring(8);
            }
        } catch(Exception e) {
        		Log.d(TAG, "logstart:\n" + e.getMessage());
						android.util.Log.d("ChenMin0612", "SelectActivity.java readSNForFileName 1004 Exception = " + e.getMessage());
        }
        return null;
    }		

    private void writeNvData(int position, byte value) {
        try {
            INvram agent = INvram.getService();
            String buff = null;
						int i = 0;
            try {
                buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
								//Thread.sleep(150);
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvData 1045 buff = " + buff);
            } catch (Exception e) {
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvData 1047 getMessage = " + e.getMessage());
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
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvData 1062");
            int flag = 0;
						mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
            try {
                flag = agent.writeFileByNamevec(INFO_ADDRESS_FILENAME, nvramLenth, dataArray);//
								//Thread.sleep(150);
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvData 1068");
            } catch (Exception e) {
                e.printStackTrace();
                mToast.setText(e.getMessage() + ":" + e.getCause());
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvData 1072 getMessage = " + e.getMessage() + ":" + e.getCause());
                mToast.show();
                return;
            }
            //mToast.setText("Update SN successfully.\r\nPlease reboot this device");
            //mToast.show();
        } catch (Exception e) {
            mToast.setText(e.getMessage() + ":" + e.getCause());
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvData 1080 getMessage = " + e.getMessage() + ":" + e.getCause());
            mToast.show();
            e.printStackTrace();
        }
    }

    /*private void writeNvDataForTwo(int position, byte value) {
        try {
            int i = 0;
            INvram agent = INvram.getService();
            String buff = null;
            try {
                //buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
								buff = agent.mnqReadFileByLid(AP_CFG_REEB_PRODUCT_INFO_LID, nvramLenth);
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1061 buff = " + buff);
            } catch (Exception e) {
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1063 getMessage = " + e.getMessage());
                e.printStackTrace();
                return;
            }
            // Remove \0 in the end
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1069");

            buffArr[position] = value;
						
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1073 buffArr[position] = " + buffArr[position]);
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1074");
            ArrayList<Byte> dataArray = new ArrayList<Byte>(nvramLenth);

            for (i = 0; i < nvramLenth; i++) {
                dataArray.add(i, new Byte(buffArr[i]));
            }
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1180");
            int flag = 0;
            try {
                //flag = agent.writeFileByNamevec(INFO_ADDRESS_FILENAME, nvramLenth, dataArray);//
								flag = agent.mnqWriteFileByLidvec(AP_CFG_REEB_PRODUCT_INFO_LID, nvramLenth, dataArray);//
            } catch (Exception e) {
                e.printStackTrace();
                mToast.setText(e.getMessage() + ":" + e.getCause());
								android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1088 getMessage = " + e.getMessage() + ":" + e.getCause());
                mToast.show();
                return;
            }
            mToast.setText("Update SN successfully.\r\nPlease reboot this device");
            mToast.show();
        } catch (Exception e) {
            mToast.setText(e.getMessage() + ":" + e.getCause());
						android.util.Log.d("ChenMin0612", "SelectActivity.java writeNvDataForTwo 1096 getMessage = " + e.getMessage() + ":" + e.getCause());
            mToast.show();
            e.printStackTrace();
        }
    }
		
    private void readNvData(int position) {
        try {
            int i = 0;
            INvram agent = INvram.getService();
						android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1106");
            String buff = null;
            try {
                buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
								android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1110 buff = " + buff);
            } catch (Exception e) {
								android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1112 getMessage = " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Remove \0 in the end
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
						android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData ======================== 1119 buffArr[position] = " + buffArr[position]);

        } catch (Exception e) {
            mToast.setText(e.getMessage() + ":" + e.getCause());
						android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1123 getMessage = " + e.getMessage() + ":" + e.getCause());
            mToast.show();
            e.printStackTrace();
        }
    }	*/	

		public void readNvData()
	    {
	    	byte[] buffArr = new byte[nvramLenth];
			String buff = null;
			android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1163");
			
			try {
				INvram agent = INvram.getService();
				//Thread.sleep(200);
				android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1166");
			  	buff = agent.readFileByName(INFO_ADDRESS_FILENAME, nvramLenth);//
				//Thread.sleep(500);
				android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1168");
			  	//buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1)); 
					
				buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
					
			} catch (Exception e) {
				android.util.Log.d("ChenMin0612", "SelectActivity.java readNvData 1174 getMessage = " + e.getMessage() + ":" + e.getCause());
			  e.printStackTrace();
			}
				
			for(int i = 120; i < 140; i++){
				android.util.Log.d("ChenMin0612", "SelectActivity.java 1158 readNvData byteint(buffArr[" + i + "]) = " + ((int)(buffArr[i])));
			}
		}
	
    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        finish();
    }
	
    /**
     * android.util.Log.e("ChenMin0612", "" + getLineNumber());
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
