package com.centerm.autofill.dev;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;


public class DevActivity {

	public static final int REQ_READIDCARD = 1;// ������֤
	public static final int REQ_READMAGCARD = 2;// ���ſ�
	public static final int REQ_SELECTREADCARD = 3;//ѡ�����ͺ󣬶���

	public final static int RES_CANCEL = -2;	//ȡ����ȡ
	public final static int RES_SKIP = -3;		//����

	//����������֤��activity
	public static void startReadIDCard( Fragment fragment, String title ) {
		startReadIDCard( fragment, title, false );
	}

	//����������֤��activity
	public static void startReadIDCard( Fragment fragment, String title, boolean canSkip ) {
		final String name = "com.centerm.autofill.dev.idcard.ReadIDCardActivity";
		final String pkg = "com.centerm.autofill.dev";
		try {
			// ����������֤��Ӧ�ó���
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

	//�������ſ���activity
	public static void startReadMagCard(Fragment fragment) {
		final String name = "com.centerm.autofill.dev.crt.ReadCrtActivity";
		final String pkg = "com.centerm.autofill.dev";
		try {
			//������Ӧ�ó���
			ComponentName appComponent = new ComponentName(pkg, name);
			Intent intent = new Intent();
			intent.setComponent(appComponent);
			intent.putExtra( "title", "��ˢ�ſ�" );
			fragment.startActivityForResult(intent, REQ_READMAGCARD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//����ѡ����ſ���activity
	public static void startSelectMagCard(Activity act) {
		final String name = "com.centerm.autofill.dev.card.CardSelectActivity";
		final String pkg = "com.centerm.autofill.dev";
		try {
			//������Ӧ�ó���
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
		case REQ_READIDCARD:// ������֤�ɹ�
			if (resultCode == 0) {// �ɹ�
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

			} else {//-1��ʧ�ܣ�ֱ�ӷ���null

			}
			break;
		default:			
			break;
		}
		return null;
	}
}
