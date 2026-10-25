#ifndef __GSL_TS_DRIVER_H__
#define __GSL_TS_DRIVER_H__
/*********************************/
//#define TPD_HAVE_BUTTON		//virtual key
#define GSL_ALG_ID			//id suanfa
//#define GSL_DEBUG			//debug
#define TPD_PROC_DEBUG		//adb debug
//#define GSL_TIMER			//dingshiqi
//#define TPD_PROXIMITY			//proximity
//#define GSL_GESTURE
#define GSL9XX_VDDIO_1800		0
#define TPD_DEBUG_TIME	0x20130424
struct gsl_touch_info
{
	int x[10];
	int y[10];
	int id[10];
	int finger_num;
};

struct gsl_ts_data {
	struct i2c_client *client;
	struct workqueue_struct *wq;
	struct work_struct work;
	unsigned int irq;
	//struct early_suspend pm;
};

/*button*/
#ifdef TPD_HAVE_BUTTON
#define TPD_KEY_COUNT           3
#define TPD_KEYS                {KEY_MENU,KEY_HOMEPAGE,KEY_BACK}
#define TPD_KEYS_DIM            {{80,1030,20,20},{400,2000,120,80},{460,1030,20,20}}
//#define TPD_KEYS_DIM          {{80,1030,20,20},{200,3000,20,20},{460,1030,20,20}}
//#define TPD_KEYS_DIM          {{80,2000,120,80},{330,2000,120,80},{400,2000,120,80}}
#endif

#ifdef GSL_ALG_ID
extern unsigned int gsl_mask_tiaoping(void);
extern unsigned int gsl_version_id(void);
extern void gsl_alg_id_main(struct gsl_touch_info *cinfo);
extern void gsl_DataInit(int *ret);
#endif

/* Fixme mem Alig */
struct fw_data
{
    u32 offset : 8;
    u32 : 0;
    u32 val;
};

#ifdef GSL_DRV_WIRE_IDT_TP
#include "gsl_idt_tp.h"
#endif

#include "gsl1680/zlq5_wsvga_1680.h"

static unsigned char gsl_cfg_index = 0;

struct fw_config_type
{
	const struct fw_data *fw;
	unsigned int fw_size;
	unsigned int *data_id;
	unsigned int data_size;
};
static struct fw_config_type gsl_cfg_table[9] = {
/*0*/{GSLX680_FW,(sizeof(GSLX680_FW)/sizeof(struct fw_data)),
	gsl_config_data_id,(sizeof(gsl_config_data_id)/4)},
};

#endif
