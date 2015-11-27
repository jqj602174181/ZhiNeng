package com.centerm.autofill.update.newupdate;

import java.io.File;

import com.centerm.autofill.update.R;
import com.centerm.util.financial.FinancialUpdate;
import com.centerm.util.printer.PrinterService;

import android.content.Context;
import android.os.Handler;
import android.os.RecoverySystem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdateFinanceRom extends UpdateOperatorBase{

	public UpdateFinanceRom(NewUpdateActivity activity, View mainView,
			String ip, Handler handler) {
		super(activity, mainView, ip, handler);
		// TODO Auto-generated constructor stub
	}
	protected void initView()
	{
		romName 		 = activity.getString(R.string.financeRom);
		tvCurrentVersion = (TextView)mainView.findViewById(R.id.LABEL_UpdateFinance);
		tvUPdateVersion  = (TextView)mainView.findViewById(R.id.LABEL_UpdateFinanceResult);
		imgUpdateWait	 = mainView.findViewById(R.id.IMG_FinanceRoate);
		super.initView();
	}
	
	/**
	 * 开始升级
	 * @return
	 */
	public boolean doUpdate(Context context,String localfile){
		
		
		FinancialUpdate finUpdate = FinancialUpdate.getService(); // 获取金融模块升级操作对象
		try {
			boolean is = finUpdate.update(localfile);
			return is;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}	
}
