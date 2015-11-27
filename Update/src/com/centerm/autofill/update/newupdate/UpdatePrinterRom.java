package com.centerm.autofill.update.newupdate;

import java.io.File;

import com.centerm.autofill.update.R;
import com.centerm.util.printer.PrinterService;

import android.content.Context;
import android.os.Handler;
import android.os.RecoverySystem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdatePrinterRom  extends UpdateOperatorBase{

	public UpdatePrinterRom(NewUpdateActivity activity, View mainView,
			String ip, Handler handler) {
		super(activity, mainView, ip, handler);
		// TODO Auto-generated constructor stub
	}
	protected void initView()
	{
		romName 		 = activity.getString(R.string.printerRom);
		tvCurrentVersion = (TextView)mainView.findViewById(R.id.LABEL_UpdatePrinter);
		tvUPdateVersion  = (TextView)mainView.findViewById(R.id.LABEL_UpdatePrinterResult);
		imgUpdateWait 	 = mainView.findViewById(R.id.IMG_PrinterRoate);
	//	Animation anim 	 = AnimationUtils.loadAnimation(activity, R.anim.roate); 
	//	imgUpdateWait.setAnimation(anim);
		super.initView();
	}
	
	/**
	 * 开始升级
	 * @return
	 */
	public boolean doUpdate(Context context,String localfile){
		
		PrinterService pt = PrinterService.getService(); // 获取打印机对象
		try {
			boolean is = pt.update(localfile);
			return is;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}	
}
