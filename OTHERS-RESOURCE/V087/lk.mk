LOCAL_DIR := $(GET_LOCAL_DIR)
TARGET := k37mv1_bsp_512
MODULES += app/mt_boot \
           dev/lcm
MTK_EMMC_SUPPORT = yes
DEFINES += MTK_NEW_COMBO_EMMC_SUPPORT
MTK_KERNEL_POWER_OFF_CHARGING = yes
MTK_PUMP_EXPRESS_SUPPORT := no
MTK_LCM_PHYSICAL_ROTATION = 270
CUSTOM_LK_LCM="ek79007_wsvga_q8_7c jd9165_wsvga_hk_7c ft3528_wxga_incell_boe_7c jd9165_wsvga_boe_7c_a2 jd9165ba_wsvga_boe_zg_7c yc54477_wsvga_boe_7c jd9165ba_wsvga_boe_pc_7c yc54477_wsvga_hk_jc_7c jd9165ba_wsvga_boe_rs_7c jd9165ba_wsvga_boe_csj_n11_7c jd9165ba_wsvga_boe_bcd_n11_7c jd9165ba_wsvga_hkc_7c jd9165ba_wsvga_hkc_htgd_7c jd9165ba_wsvga_boe_b5_rs_7c jd9165ba_wsvga_hkc_htgd_cy_7c ey3866_wsvga_boe_7c gh7002_wsvga_ghw_n11_7c"
#hx8392a_dsi_cmd = yes
MTK_SECURITY_SW_SUPPORT = yes
MTK_VERIFIED_BOOT_SUPPORT = yes
MTK_SEC_FASTBOOT_UNLOCK_SUPPORT = yes
DEBUG := 0
BOOT_LOGO := wsvganl
#DEFINES += WITH_DEBUG_DCC=1
DEFINES += WITH_DEBUG_UART=1
#DEFINES += WITH_DEBUG_FBCON=1
#DEFINES += MACH_FPGA=y
#DEFINES += SB_LK_BRINGUP=y
DEFINES += MTK_GPT_SCHEME_SUPPORT
#DEFINES += MACH_FPGA_NO_DISPLAY=y
MTK_GOOGLE_TRUSTY_SUPPORT = no
MTK_DM_VERITY_OFF = no
MTK_DYNAMIC_CCB_BUFFER_GEAR_ID =
MTK_USERIMAGES_USE_F2FS = yes
MTK_MRDUMP_ENABLE := no
#prize-switch logo and animation-lanwm-20170907-begin
PRIZE_SWITCH_BOOTANIMA_SUPPORT = yes
#prize-switch logo and animation-lanwm-20170907-end
