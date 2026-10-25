#include <linux/syscalls.h>
#include <linux/export.h>
#include <linux/fs.h>
#include <linux/file.h>
#include <linux/mount.h>
#include <linux/namei.h>
#include <linux/statfs.h>
#include <linux/security.h>
#include <linux/uaccess.h>
#include "internal.h"

#define PRIZE_SWITCH_BOOTANIMA_SUPPORT

static int flags_by_mnt(int mnt_flags)
{
	int flags = 0;

	if (mnt_flags & MNT_READONLY)
		flags |= ST_RDONLY;
	if (mnt_flags & MNT_NOSUID)
		flags |= ST_NOSUID;
	if (mnt_flags & MNT_NODEV)
		flags |= ST_NODEV;
	if (mnt_flags & MNT_NOEXEC)
		flags |= ST_NOEXEC;
	if (mnt_flags & MNT_NOATIME)
		flags |= ST_NOATIME;
	if (mnt_flags & MNT_NODIRATIME)
		flags |= ST_NODIRATIME;
	if (mnt_flags & MNT_RELATIME)
		flags |= ST_RELATIME;
	return flags;
}

static int flags_by_sb(int s_flags)
{
	int flags = 0;
	if (s_flags & MS_SYNCHRONOUS)
		flags |= ST_SYNCHRONOUS;
	if (s_flags & MS_MANDLOCK)
		flags |= ST_MANDLOCK;
	return flags;
}

static int calculate_f_flags(struct vfsmount *mnt)
{
	return ST_VALID | flags_by_mnt(mnt->mnt_flags) |
		flags_by_sb(mnt->mnt_sb->s_flags);
}

static int statfs_by_dentry(struct dentry *dentry, struct kstatfs *buf)
{
	int retval;

	if (!dentry->d_sb->s_op->statfs)
		return -ENOSYS;

	memset(buf, 0, sizeof(*buf));
	retval = security_sb_statfs(dentry);
	if (retval)
		return retval;
	retval = dentry->d_sb->s_op->statfs(dentry, buf);
	if (retval == 0 && buf->f_frsize == 0)
		buf->f_frsize = buf->f_bsize;
	return retval;
}

int vfs_statfs(struct path *path, struct kstatfs *buf)
{
	int error;

	error = statfs_by_dentry(path->dentry, buf);
	if (!error)
		buf->f_flags = calculate_f_flags(path->mnt);
	return error;
}
EXPORT_SYMBOL(vfs_statfs);

#ifdef PRIZE_SWITCH_BOOTANIMA_SUPPORT
#define COMMAND_LINE_SIZE      4096
static char aee_cmdline[COMMAND_LINE_SIZE];
static int rom_index = 0;
static const char *mrdump_get_cmdForRom(void)
{
	struct file *fd;
	char *p;
	int rom_indexTwo = -1;
	mm_segment_t fs;
	loff_t pos = 0;

	if (aee_cmdline[0] != 0) {
		//printk("ChenMin1026 file: %s, %s_chenmin(%d)\n", __FILE__, __func__, __LINE__);
		return aee_cmdline;
	}
	fs = get_fs();
	set_fs(KERNEL_DS);
	//printk("ChenMin1026 file: %s, %s_chenmin(%d)\n", __FILE__, __func__, __LINE__);
	fd = filp_open("/proc/cmdline", O_RDONLY, 0);
	//printk("ChenMin1026 file: %s, %s_chenmin(%d)\n", __FILE__, __func__, __LINE__);
	if (IS_ERR(fd)) {
		pr_info("ChenMin1105 fail kedump: statfs.c Unable to open /proc/cmdline (%ld)",
			PTR_ERR(fd));
		set_fs(fs);
		return aee_cmdline;
	}
	vfs_read(fd, (void *)aee_cmdline, COMMAND_LINE_SIZE, &pos);
	
	p = strstr(aee_cmdline, "rom.index=");
	if( p != NULL){
		//property_set(BOOT_ANIMATION_NAME, "1");
		p += strlen("rom.index="); 
		rom_index=(int)(*(p))-48;
		p++;
		rom_indexTwo = (int)(*(p));

		if (rom_indexTwo >= 49 && rom_indexTwo <= 57) {
			rom_index = rom_index * 10 + rom_indexTwo - 48;
		}
		
		if (rom_index == 7 && rom_indexTwo == 48) {
			rom_index = 70;
		}			

		//printk("ChenMin1026 file: %s, %s_chenmin(%d) rom_index=%d, rom_indexTwo =%d\n", __FILE__, __func__, __LINE__, rom_index, rom_indexTwo);
	} else {
		//printk("ChenMin1026 file: %s, %s_chenmin(%d) rom_index=%d\n", __FILE__, __func__, __LINE__, rom_index);
	}	
	
	filp_close(fd, NULL);
	fd = NULL;
	set_fs(fs);
	//printk("ChenMin1105 file: %s, %s_chenmin(%d), aee_cmdline = %s\n", __FILE__, __func__, __LINE__, aee_cmdline);
	return aee_cmdline;
}
#endif

int user_statfs(const char __user *pathname, struct kstatfs *st)
{
	struct path path;
	int error;
	
	/*--prize modify for ram and rom   by peisaisai 20181031 begin--*/
	int rom;
	long romsize;
	long romsized;
	int mlp;
	int result;
	char tmp[128] = {'\0'};
	//int i = 32;
 
	int romSizeFile=0;
#ifdef PRIZE_SWITCH_BOOTANIMA_SUPPORT	
	int romSizeId=0;	
#endif  
    struct file *filp;
    struct inode *inode;
    mm_segment_t fs;
    off_t fsize;
    char *bufReader;
    unsigned long magic;
	
/*--prize modify for ram and rom   by peisaisai 20181031 end--*/
	unsigned int lookup_flags = LOOKUP_FOLLOW|LOOKUP_AUTOMOUNT;
retry:
	error = user_path_at(AT_FDCWD, pathname, lookup_flags, &path);
	if (!error) {
		error = vfs_statfs(&path, st);
		path_put(&path);
		if (retry_estale(error, lookup_flags)) {
			lookup_flags |= LOOKUP_REVAL;
			goto retry;
		}
	}
        /*--prize modify for ram and rom    by peisaisai 20181031 begin--*/


    //printk("<1>start....\n");
    //filp = filp_open("/system/bin/kernel_rominfo", O_RDONLY, 0);
#ifdef PRIZE_SWITCH_BOOTANIMA_SUPPORT	
	mrdump_get_cmdForRom();
#endif	
	filp = filp_open("/system/bin/ROM", O_RDONLY, 0);
    //printk("ChenMin1026 file: %s, %s_chenmin(%d)\n", __FILE__, __func__, __LINE__);
    if(IS_ERR(filp))
    {
    	//printk("<1>error file not exsit....\n");
    }
    else
    {
		inode=filp->f_dentry->d_inode;
		magic=inode->i_sb->s_magic;
		fsize=inode->i_size;
		//printk("ChenMin0706 127 fsize = %ld\n",fsize);
		bufReader=(char *) kmalloc(fsize+1,GFP_ATOMIC);
		fs=get_fs();
		set_fs(KERNEL_DS);
		filp->f_op->read(filp,bufReader,fsize,&(filp->f_pos));
		set_fs(fs);
		//printk("ChenMin0706 132 bufReader = %s\n",bufReader);
		filp_close(filp,NULL);
	   // memoryExpand = bufReader[18]-'0';
		romSizeFile = simple_strtol(bufReader,NULL,10);
		romSizeFile = romSizeFile;

#ifdef PRIZE_SWITCH_BOOTANIMA_SUPPORT		
		romSizeId = rom_index;
		//romSizeFile = romSizeFile;
		//printk("\n");
		//printk("ChenMin1026 137 romSizeFile = %d\n", romSizeFile);
		//printk("\n");
		//printk("ChenMin0519 file: %s, %s_chenmin(%d), romSizeId = %d \n", __FILE__, __func__, __LINE__, romSizeId);
		if(romSizeId == 0){
			romSizeFile = 128;//persist.sys.showromsize
		} else if (romSizeId == 16){
			romSizeFile = 16;
		} else if(romSizeId == 32){
			romSizeFile = 32;
		} else if(romSizeId == 64){
			romSizeFile = 64;
		} else if(romSizeId == 65){
			romSizeFile = 128;
		} else if(romSizeId == 66){
			romSizeFile = 256;
		} else if(romSizeId == 67){
			romSizeFile = 512;
		} else if(romSizeId == 68){
			romSizeFile = 640;
		} else if(romSizeId == 69){
			romSizeFile = 1024;
		} else if(romSizeId == 70){
			romSizeFile = 2048;
		}			
#endif		
		//printk("ChenMin0519 file: %s, %s_chenmin(%d), romSizeFile = %d \n", __FILE__, __func__, __LINE__, romSizeFile);
		//printk("\n");
		//printk("ChenMin0706 137 romSizeFile = %d\n", romSizeFile);
		//printk("\n");
		if(romSizeFile <= 0){
			romSizeFile = 0;
		}
		
		/*if (fsize == 3) {//128 256 512			
			if (romSizeFile >= 1280 && romSizeFile < 2560) {
				romSizeFile = 128;
			} else if (romSizeFile >= 2560 && romSizeFile < 5120){
				romSizeFile = 256;
			} else if (romSizeFile >= 5120 && romSizeFile < 5130){
				romSizeFile = 512;
			} 
		}

		if (fsize == 2) {//32 64			
			if (romSizeFile >= 320 && romSizeFile < 640) {
				romSizeFile = 32;
			} else if (romSizeFile >= 640 && romSizeFile < 650){
				romSizeFile = 64;
			} 
		}*/	
		//printk("ChenMin0706 file: %s, %s_chenmin(%d), romSizeFile = %d \n", __FILE__, __func__, __LINE__, romSizeFile);
#ifdef PRIZE_SWITCH_BOOTANIMA_SUPPORT
#else		
		if (fsize == 3 && romSizeFile > 1000) {//128 256 512			
			romSizeFile = (romSizeFile/10);
		}

		if (fsize == 2 && romSizeFile > 100) {//32 64			
			romSizeFile = (romSizeFile/10);
		}
#endif			
		//printk("ChenMin0519 file: %s, %s_chenmin(%d), romSizeFile = %d \n", __FILE__, __func__, __LINE__, romSizeFile);
  	}	
		
  if (copy_from_user(tmp, (void __user *)pathname, sizeof(tmp))) {
   //printk(" %s  %d  copy_from_user fail\n",__func__, sizeof(tmp));
  }
  //else
  //{
   //printk(" %s  %d  copy_from_user successfully\n",__func__, sizeof(tmp));
   //for(i = 0; i < 128; i ++)
   //{
  
   // printk("%c",tmp[i]);
   //}  
  // printk("\n");
  //}

  //printk("peisaisai fd_get_rom() pathname=%s   tmp:%s\n","111111111111111111", tmp);
#if 0
  memcpy(tmp, pathname, 128);
  for(i = 0; i < 128; i ++)
  {
   if (i/16)
    printk("\n");
   printk(" %d:%c",i, tmp[i]);
  }
#endif
  //printk("ChenMin0706 170 peisaisai fd_get_rom() pathname=%s\n",tmp);
  /*printk("peisaisai fd_get_rom() pathname=%d\n",strcmp("/data/media", tmp));
  printk("peisaisai fd_get_rom() pathname=%d\n",strcmp("/data", tmp));
  printk("peisaisai fd_get_rom() pathname=%d\n",strcmp("/storage/emulated/0", tmp));
  printk("peisaisai fd_get_rom() pathname=%d\n",strcmp("/storage/emulated/0/", tmp));*/
  //printk("peisaisai fd_get_rom() *pathname=%s\n",*pathname);
  if (((strcmp("/data/media", tmp) == 0) || (strcmp("/data", tmp) == 0) || (strcmp("/storage/emulated/0", tmp) == 0)  
    || (strcmp("/storage/emulated/0/", tmp) == 0))&& st != 0) {
		/*if(isRomReadFlag){
			rom = romDefault;
		}else{
			rom = fd_get_rom();
			romDefault = rom;
		}*/
		
		if (romSizeFile != 0) {
			rom = romSizeFile;
		
			//rom = 32;
			
			//printk("peisaisai fd_get_rom() romDefault=%d\n",romDefault);
			////printk("ChenMin0706 190 =============================ChenMin0799===========================peisaisai fd_get_rom() rom=%d\n",rom);
			
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), rom = %d \n", __FILE__, __func__, __LINE__, rom);
					
			romsize = st->f_blocks;
					
			//printk("peisaisai fd_get_rom() 111 romsize=%ld\n",romsize);
			romsized = st->f_blocks;
			//printk("peisaisai fd_get_rom() 111 romsized=%ld\n",romsized);
			
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), rom = %d, romsize = %ld,  romsized = %ld \n", __FILE__, __func__, __LINE__, rom, romsize, romsized);
			
			if(rom*1024*256 > romsize){
			   // printk("peisaisai fd_get_rom() modify romsize romsized\n");
			   //printk("ChenMin0706 file: %s, %s_chenmin(%d), rom*1024*256 > romsize\n", __FILE__, __func__, __LINE__);
			   romsize = rom*1024*256;
			   romsized = rom*1024*256;
			}
					
			//printk("peisaisai fd_get_rom() 222 romsize=%ld\n",romsize);
			//printk("peisaisai fd_get_rom() 222 romsized=%ld\n",romsized);
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), rom = %d, romsize = %ld,  romsized = %ld \n", __FILE__, __func__, __LINE__, rom, romsize, romsized);
			
			mlp = do_div(romsize, st->f_blocks);
			//printk("peisaisai fd_get_rom() 222 mlp=%d\n",mlp);
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), mlp = %d \n", __FILE__, __func__, __LINE__, mlp);
			result = romsize;
			
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), st->f_bavail = %lld \n", __FILE__, __func__, __LINE__, st->f_bavail);
			
			if(st->f_bavail <= (1000* 1024 / 4)){//about 1G
				//printk("ChenMin0706 file: %s, %s_chenmin(%d), st->f_bavail <= (1000* 1024 / 4)\n", __FILE__, __func__, __LINE__);
			} else {
				//printk("ChenMin0706 file: %s, %s_chenmin(%d), !(st->f_bavail <= (1000* 1024 / 4))\n", __FILE__, __func__, __LINE__);
				st->f_bavail = (romsized - (st->f_blocks - st->f_bavail)) - 2 * 1024 * 1024 /2;
			}
			
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), st->f_bfree = %lld \n", __FILE__, __func__, __LINE__, st->f_bfree);
			
			if(st->f_bfree <= (1000* 1024 / 4)){
				//printk("ChenMin0706 file: %s, %s_chenmin(%d), st->f_bfree <= (1000* 1024 / 4)\n", __FILE__, __func__, __LINE__);
			} else {
				//printk("ChenMin0706 file: %s, %s_chenmin(%d), !(st->f_bfree <= (1000* 1024 / 4))\n", __FILE__, __func__, __LINE__);
				st->f_bfree = (romsized - (st->f_blocks - st->f_bfree)) - 2 * 1024 * 1024 / 2;
			}
					
			st->f_blocks = romsized;
			//printk("ChenMin0706 file: %s, %s_chenmin(%d), st->f_blocks = %lld, st->f_bfree = %lld,  st->f_bavail = %lld \n", __FILE__, __func__, __LINE__, st->f_blocks, st->f_bfree, st->f_bavail);
		}
	}
    /*--prize modify for ram and rom   by peisaisai 20181031 end--*/
	return error;
}

int fd_statfs(int fd, struct kstatfs *st)
{
	struct fd f = fdget_raw(fd);
	int error = -EBADF;
	if (f.file) {
		error = vfs_statfs(&f.file->f_path, st);
		fdput(f);
	}
	return error;
}

static int do_statfs_native(struct kstatfs *st, struct statfs __user *p)
{
	struct statfs buf;

	if (sizeof(buf) == sizeof(*st))
		memcpy(&buf, st, sizeof(*st));
	else {
		if (sizeof buf.f_blocks == 4) {
			if ((st->f_blocks | st->f_bfree | st->f_bavail |
			     st->f_bsize | st->f_frsize) &
			    0xffffffff00000000ULL)
				return -EOVERFLOW;
			/*
			 * f_files and f_ffree may be -1; it's okay to stuff
			 * that into 32 bits
			 */
			if (st->f_files != -1 &&
			    (st->f_files & 0xffffffff00000000ULL))
				return -EOVERFLOW;
			if (st->f_ffree != -1 &&
			    (st->f_ffree & 0xffffffff00000000ULL))
				return -EOVERFLOW;
		}

		buf.f_type = st->f_type;
		buf.f_bsize = st->f_bsize;
		buf.f_blocks = st->f_blocks;
		buf.f_bfree = st->f_bfree;
		buf.f_bavail = st->f_bavail;
		buf.f_files = st->f_files;
		buf.f_ffree = st->f_ffree;
		buf.f_fsid = st->f_fsid;
		buf.f_namelen = st->f_namelen;
		buf.f_frsize = st->f_frsize;
		buf.f_flags = st->f_flags;
		memset(buf.f_spare, 0, sizeof(buf.f_spare));
	}
	if (copy_to_user(p, &buf, sizeof(buf)))
		return -EFAULT;
	return 0;
}

static int do_statfs64(struct kstatfs *st, struct statfs64 __user *p)
{
	struct statfs64 buf;
	if (sizeof(buf) == sizeof(*st))
		memcpy(&buf, st, sizeof(*st));
	else {
		buf.f_type = st->f_type;
		buf.f_bsize = st->f_bsize;
		buf.f_blocks = st->f_blocks;
		buf.f_bfree = st->f_bfree;
		buf.f_bavail = st->f_bavail;
		buf.f_files = st->f_files;
		buf.f_ffree = st->f_ffree;
		buf.f_fsid = st->f_fsid;
		buf.f_namelen = st->f_namelen;
		buf.f_frsize = st->f_frsize;
		buf.f_flags = st->f_flags;
		memset(buf.f_spare, 0, sizeof(buf.f_spare));
	}
	if (copy_to_user(p, &buf, sizeof(buf)))
		return -EFAULT;
	return 0;
}

SYSCALL_DEFINE2(statfs, const char __user *, pathname, struct statfs __user *, buf)
{
	struct kstatfs st;
	int error = user_statfs(pathname, &st);
	if (!error)
		error = do_statfs_native(&st, buf);
	return error;
}

SYSCALL_DEFINE3(statfs64, const char __user *, pathname, size_t, sz, struct statfs64 __user *, buf)
{
	struct kstatfs st;
	int error;
	if (sz != sizeof(*buf))
		return -EINVAL;
	error = user_statfs(pathname, &st);
	if (!error)
		error = do_statfs64(&st, buf);
	return error;
}

SYSCALL_DEFINE2(fstatfs, unsigned int, fd, struct statfs __user *, buf)
{
	struct kstatfs st;
	int error = fd_statfs(fd, &st);
	if (!error)
		error = do_statfs_native(&st, buf);
	return error;
}

SYSCALL_DEFINE3(fstatfs64, unsigned int, fd, size_t, sz, struct statfs64 __user *, buf)
{
	struct kstatfs st;
	int error;

	if (sz != sizeof(*buf))
		return -EINVAL;

	error = fd_statfs(fd, &st);
	if (!error)
		error = do_statfs64(&st, buf);
	return error;
}

int vfs_ustat(dev_t dev, struct kstatfs *sbuf)
{
	struct super_block *s = user_get_super(dev);
	int err;
	if (!s)
		return -EINVAL;

	err = statfs_by_dentry(s->s_root, sbuf);
	drop_super(s);
	return err;
}

SYSCALL_DEFINE2(ustat, unsigned, dev, struct ustat __user *, ubuf)
{
	struct ustat tmp;
	struct kstatfs sbuf;
	int err = vfs_ustat(new_decode_dev(dev), &sbuf);
	if (err)
		return err;

	memset(&tmp,0,sizeof(struct ustat));
	tmp.f_tfree = sbuf.f_bfree;
	tmp.f_tinode = sbuf.f_ffree;

	return copy_to_user(ubuf, &tmp, sizeof(struct ustat)) ? -EFAULT : 0;
}
