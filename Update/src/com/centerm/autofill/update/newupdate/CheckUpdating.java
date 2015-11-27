package com.centerm.autofill.update.newupdate;

import android.os.Handler;
import android.widget.TextView;

class CheckUpdating implements Runnable{
	private Handler handler;
	private int time = 1;
	private final static int delayTime = 1000;
	private TextView textView;
	private String strCheckUpdating;
	public CheckUpdating(TextView textView,String strCheckUpdating)
	{
		this.textView = textView;
		this.strCheckUpdating = strCheckUpdating;
		time =0;
		handler = new Handler();
	}
	public void run() {
		handler.postDelayed(this, delayTime);
		
		String tex = ".";
		switch (time%4) {
			case 0:
				tex=strCheckUpdating;
				break;
			case 1:
				tex=strCheckUpdating+".";
				break;
			case 2:
				tex=strCheckUpdating+"..";
				break;
			case 3:
				tex=strCheckUpdating+"...";
				break;
	
		}
		time++;
		textView.setText(tex);
	}
	
	public void startRun()
	{
		handler.postDelayed(this, delayTime);
	}
	
	public void stopRun()
	{
		handler.removeCallbacks(this);
	}
}
