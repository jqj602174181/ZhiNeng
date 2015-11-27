package com.centerm.autofill.update.newupdate;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.centerm.autofill.update.R;
import com.centerm.autofill.update.newupdate.UpdateOperatorBase.CloseTime;
import com.centerm.autofill.update.newupdate.UpdateOperatorBase.UpdateFtp;
import com.centerm.autofill.update.updateapp.AppUpdate;
import com.centerm.autofill.update.updateapp.SocketCorrespondence;

public class UpdateAppOperator extends UpdateOperatorBase{


	private AppUpdate appUpdate;
	private String versionUpdate;
	private String updateOver;
	private boolean isOver = false;
	public UpdateAppOperator(View mainView,NewUpdateActivity activity,String serial,String ip,int port,Handler handler) {
		// TODO Auto-generated constructor stub
		super(activity, mainView, ip, handler);
		this.mainView	   = mainView;
		this.activity	   = activity;
		versionUpdate	   = activity.getString(R.string.appUpdate);
		updateOver		   = activity.getString(R.string.updateOver);
		appUpdate		   = new AppUpdate(activity, serial, ip, port, handler);
		initView();
	}
	
	protected void initView()
	{
		romName			 = activity.getString(R.string.appModel);
		tvCurrentVersion = (TextView)mainView.findViewById(R.id.LABEL_UpdateApp);
		tvUPdateVersion  = (TextView)mainView.findViewById(R.id.LABEL_UpdateAppResult);
		imgUpdateWait	 = mainView.findViewById(R.id.IMG_AppRoate);
		
		super.initView();
		
	}
	
	public boolean startCheckUpdate(String version){
		checkUpdating.startRun();
		Thread thread = new Thread(appUpdate);
		thread.start();
		return true;
	}
	
	public void startFtpUpdate()
	{
		if(!isCanUpdate){
			updateSuccessOver();
			return;
		}

		btnUpdate.setVisibility(View.VISIBLE);
		StringBuilder builder = new StringBuilder();
		builder.append(updating);
		builder.append(space);
		builder.append(romName);
		btnUpdate.setText(builder.toString());
	
		setRoateAnim();
		appUpdate.startAppUpdate();
	}
	
	
	protected void setUpdateState(String jsonStr)
	{
		
		tvUPdateVersion.setText(versionUpdate);
	}
	private void setUpdateOver(String text,int textColor)
	{
		
		btnUpdate.setVisibility(View.VISIBLE);
		btnUpdate.setEnabled(true);
		btnUpdate.setOnClickListener(this);
		if(closeTime==null){
			closeTime = new CloseTime(text, textColor);
		}
		closeTime.startRun();
	}
	protected void  updateSuccessOver()
	{
		if(isOver)return ;
		isOver = true;
		setUpdateOver(updateOver, normalColor);
	//	closeTime = new CloseTime(updateOver, normalColor);
	}
	protected void updateFailOver()
	{
		if(isOver)return ;
		isOver = true;
		super.updateFailOver();
		setUpdateOver(romName+space+updateError,errorColor);
	}
}
