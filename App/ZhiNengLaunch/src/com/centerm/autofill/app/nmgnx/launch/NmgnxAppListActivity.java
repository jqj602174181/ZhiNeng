package com.centerm.autofill.app.nmgnx.launch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.centerm.autofill.app.nmgnx.launch.R;
import com.centerm.autofill.launch.AppListActivity;
//com.example.appnmgnxlaunch.NmgnxAppListActivity
public class NmgnxAppListActivity  extends AppListActivity{
	
	//�����ı���
	final static String[] appPackageSorted = {
		"com.centerm.autofill.app.nmgnx.currentopenaccount",//"���ڴ���"
		"com.centerm.autofill.app.nmgnx.fixedperiodopenaccount",//"���ڴ���"
		"com.centerm.autofill.app.nmgnx.icaccountapply",// "��ǿ������ʻ�����"
		"com.centerm.autofill.app.nmgnx.withdraw",// "����ȡ��"
		"com.centerm.autofill.app.nmgnx.deposit", //������
		"com.centerm.autofill.app.nmgnx.withdrawmethod",//֧ȡ��ʽ���
		"com.centerm.autofill.app.nmgnx.transfer",//���ת��
		"com.centerm.autofill.app.nmgnx.persional.crossbanktransfer",//���˿��ת��
		"com.centerm.autofill.app.nmgnx.tongcun", //ͨ��ҵ��
		"com.centerm.autofill.app.nmgnx.tongdui", //ͨ��ҵ��
		"com.centerm.autofill.app.nmgnx.persionreportloss", //���˹�ʧ
		"com.centerm.autofill.app.nmgnx.currentcloseaccount",//���ڴ������
		"com.centerm.autofill.app.nmgnx.fixedperiodcloseaccount",//���ڴ������
		"com.centerm.autofill.app.nmgnx.personal.personalebank",//��������
		"com.centerm.autofill.app.nmgnx.personal.persionalnote",//�ֻ�����
		"com.centerm.autofill.app.nmgnx.msgserviceapply", //���˶���
		"com.centerm.autofill.app.nmgnx.closeaccount",//�������		
		"com.centerm.autofill.app.nmgnx.crossbanktransaction",//ͨ��ͨ��
		"com.centerm.autofill.app.nmgnx.publics.openaccount",//��ҵ����
		"com.centerm.autofill.app.nmgnx.corpdeposit", //�ֽ���ʵ�
		"com.centerm.autofill.app.nmgnx.publics.payinslip",//��ҵ���ʵ�
		"com.centerm.autofill.app.nmgnx.crossbanktransfer",//��ҵ���ת��
		"com.centerm.autofill.app.nmgnx.enterprisemsgservice",//��ҵ����
	};
	
	private static AppComparator appComparator = new AppComparator();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView titleImageView 	= (ImageView)findViewById(R.id.IMG_LOGO);
		RelativeLayout headLayout 	=	(RelativeLayout)findViewById(R.id.BLOCK_HEADER);
		GridView grid 				= (GridView)findViewById( R.id.GRID_APPS );
//		titleImageView.setImageResource(R.drawable.title_logo);
		headLayout.setBackgroundResource(R.drawable.banner);
		grid.setBackgroundResource(R.drawable.background);
		grid.setNumColumns( 5 );//���ó�����
		
		enableTimer();//��������ʱ��ʱ��
	}
	
	//�������ʵ�ֽ��н�������
	protected void sort( List<ResolveInfo> list ){
		Collections.sort( list, appComparator );		
	}
	
	//ʵ�ְ�������ָ��˳������
	private static class AppComparator implements Comparator<ResolveInfo>{

		@Override
		public int compare(ResolveInfo app1, ResolveInfo app2) {
			
			int n1 = appPackageSorted.length;	//��¼Ӧ��1�������
			int n2 = appPackageSorted.length;	//��¼Ӧ��2�������
			String pkg1 = app1.activityInfo.packageName;//Ӧ��1�İ���
			String pkg2 = app2.activityInfo.packageName;//Ӧ��2�İ���
			for( int i = 0; i < appPackageSorted.length; i++ ){
				String pkg = appPackageSorted[i];
				if( pkg.equals( pkg1 ) )
					n1 = i;
				if( pkg.equals( pkg2 ))
					n2 = i;
			}
			
			return n1 - n2;
		}
		
	}
}
