package com.centerm.autofill.update.newupdate;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.os.RecoverySystem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.centerm.autofill.update.R;

class UpdateSystemRom extends UpdateOperatorBase{

	public UpdateSystemRom(NewUpdateActivity activity,View mainView,String ip,Handler handler)
	{
		super(activity, mainView,ip,handler);
	}
	
	protected void initView()
	{
		romName 		 = activity.getString(R.string.systemRom);
		tvCurrentVersion = (TextView)mainView.findViewById(R.id.LABEL_UpdateRom);
		tvUPdateVersion  = (TextView)mainView.findViewById(R.id.LABEL_UpdateSystemResult);
		imgUpdateWait	 = mainView.findViewById(R.id.IMG_RomRoate);
     
		super.initView();
	}
	
	@Override
	public boolean doUpdate(Context context, String localfile) {
		// TODO Auto-generated method stub
		///return doUpdateSystem(context,localfile);
		return false;
	}
	
	/**
	 * 开始升级
	 * @return
	 */
	public boolean doUpdateSystem(Context context,String localfile){
		File sysFile = new File( localfile );
		try 
		{
			RecoverySystem.verifyPackage(sysFile, null, null); //校验文件是否正确
			RecoverySystem.installPackage(context, sysFile ); //启动安装系统文件
			return true;
		}catch (java.security.SignatureException e){
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		} 
		return false;
	}	
	
}
