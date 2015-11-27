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
	
	//排序后的标题
	final static String[] appPackageSorted = {
		"com.centerm.autofill.app.nmgnx.currentopenaccount",//"活期存款开户"
		"com.centerm.autofill.app.nmgnx.fixedperiodopenaccount",//"定期存款开户"
		"com.centerm.autofill.app.nmgnx.icaccountapply",// "借记卡结算帐户申请"
		"com.centerm.autofill.app.nmgnx.withdraw",// "储蓄取款"
		"com.centerm.autofill.app.nmgnx.deposit", //储蓄存款
		"com.centerm.autofill.app.nmgnx.withdrawmethod",//支取方式变更
		"com.centerm.autofill.app.nmgnx.transfer",//存款转帐
		"com.centerm.autofill.app.nmgnx.persional.crossbanktransfer",//个人跨地转帐
		"com.centerm.autofill.app.nmgnx.tongcun", //通存业务
		"com.centerm.autofill.app.nmgnx.tongdui", //通兑业务
		"com.centerm.autofill.app.nmgnx.persionreportloss", //个人挂失
		"com.centerm.autofill.app.nmgnx.currentcloseaccount",//活期存款销户
		"com.centerm.autofill.app.nmgnx.fixedperiodcloseaccount",//定期存款销户
		"com.centerm.autofill.app.nmgnx.personal.personalebank",//网上银行
		"com.centerm.autofill.app.nmgnx.personal.persionalnote",//手机银行
		"com.centerm.autofill.app.nmgnx.msgserviceapply", //个人短信
		"com.centerm.autofill.app.nmgnx.closeaccount",//存款销户		
		"com.centerm.autofill.app.nmgnx.crossbanktransaction",//通存通兑
		"com.centerm.autofill.app.nmgnx.publics.openaccount",//企业开户
		"com.centerm.autofill.app.nmgnx.corpdeposit", //现金进帐单
		"com.centerm.autofill.app.nmgnx.publics.payinslip",//企业进帐单
		"com.centerm.autofill.app.nmgnx.crossbanktransfer",//企业跨地转帐
		"com.centerm.autofill.app.nmgnx.enterprisemsgservice",//企业短信
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
		grid.setNumColumns( 5 );//设置成五列
		
		enableTimer();//开启倒计时定时器
	}
	
	//子类可以实现进行界面排序
	protected void sort( List<ResolveInfo> list ){
		Collections.sort( list, appComparator );		
	}
	
	//实现按照数组指定顺序排名
	private static class AppComparator implements Comparator<ResolveInfo>{

		@Override
		public int compare(ResolveInfo app1, ResolveInfo app2) {
			
			int n1 = appPackageSorted.length;	//记录应用1的排序号
			int n2 = appPackageSorted.length;	//记录应用2的排序号
			String pkg1 = app1.activityInfo.packageName;//应用1的包名
			String pkg2 = app2.activityInfo.packageName;//应用2的包名
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
