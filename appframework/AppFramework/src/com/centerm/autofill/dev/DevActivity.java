package com.centerm.autofill.dev;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;


public class DevActivity {

	public static final int REQ_READIDCARD = 1;// 读二代证
	public static final int REQ_READMAGCARD = 2;// 读磁卡
	public static final int REQ_SELECTREADCARD = 3;//选择卡类型后，读卡

	public final static int RES_CANCEL = -2;	//取消读取
	public final static int RES_SKIP = -3;		//跳过

	//启动读二代证的activity
	public static void startReadIDCard( Fragment fragment, String title ) {
		startReadIDCard( fragment, title, false );
	}

	//启动读二代证的activity
	public static void startReadIDCard( Fragment fragment, String title, boolean canSkip ) {
		final String name = "com.centerm.autofill.dev.idcard.ReadIDCardActivity";
		final String pkg = "com.centerm.autofill.dev";
		try {
			// 启动读二代证的应用程序
			ComponentName appComponent = new ComponentName(pkg, name);
			Intent intent = new Intent();
			intent.setComponent(appComponent);
			intent.putExtra("title", title);
			intent.putExtra( "canskip", canSkip );
			fragment.startActivityForResult(intent, REQ_READIDCARD);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//启动读磁卡的activity
	public static void startReadMagCard(Fragment fragment) {
		final String name = "com.centerm.autofill.dev.crt.ReadCrtActivity";
		final String pkg = "com.centerm.autofill.dev";
		try {
			//启动的应用程序
			ComponentName appComponent = new ComponentName(pkg, name);
			Intent intent = new Intent();
			intent.setComponent(appComponent);
			intent.putExtra( "title", "请刷磁卡" );
			fragment.startActivityForResult(intent, REQ_READMAGCARD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//启动选择读磁卡的activity
	public static void startSelectMagCard(Activity act) {
		final String name = "com.centerm.autofill.dev.card.CardSelectActivity";
		final String pkg = "com.centerm.autofill.dev";
		try {
			//启动的应用程序
			ComponentName appComponent = new ComponentName(pkg, name);
			Intent intent = new Intent();
			intent.setComponent(appComponent);
			act.startActivityForResult(intent, REQ_SELECTREADCARD );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static IDCardMsg GetIdCardMsg(int requestCode, int resultCode,
			Intent data) {
		IDCardMsg idcardmsg = new IDCardMsg();
		switch (requestCode) {
		case REQ_READIDCARD:// 读二代证成功
			if (resultCode == 0) {// 成功
				idcardmsg.setName(data.getStringExtra("name"));
				idcardmsg.setPinyin( data.getStringExtra("pinyin"));
				idcardmsg.setSex(data.getStringExtra("sex"));
				idcardmsg.setNation_str(data.getStringExtra("nation_str"));
				idcardmsg.setBirth_year(data.getStringExtra("birth_year"));
				idcardmsg.setBirth_month( data.getStringExtra("birth_month"));
				idcardmsg.setBirth_day(data.getStringExtra("birth_day"));
				idcardmsg.setAddress(data.getStringExtra("address"));
				idcardmsg.setId_num(data.getStringExtra("id_num"));
				idcardmsg.setSign_office(data.getStringExtra("sign_office"));
				idcardmsg.setUseful_s_date_year(data.getStringExtra("useful_s_date_year"));
				idcardmsg.setUseful_s_date_month(data.getStringExtra("useful_s_date_month"));
				idcardmsg.setUseful_s_date_day(data.getStringExtra("useful_s_date_day"));
				idcardmsg.setUseful_e_date_year(data.getStringExtra("useful_e_date_year"));
				idcardmsg.setUseful_e_date_month(data.getStringExtra("useful_e_date_month"));
				idcardmsg.setUseful_e_date_day(data.getStringExtra("useful_e_date_day"));
				idcardmsg.setPhoto(data.getByteArrayExtra("photo"));
				
				return idcardmsg;

			} else {//-1是失败，直接返回null

			}
			break;
		default:			
			break;
		}
		return null;
	}
}
