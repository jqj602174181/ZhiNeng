package com.centerm.autofill.appframework.common;

//********************************************************/
//
//	�Զ��嶨ʱ��
//
//*******************************************************/
import android.os.Handler;

public class CTTimer {
	private int limitTime;//���ڼ�ʱ��
	private Handler timeHandler;
	private Runnable runnable;
	private boolean isStart;
	private OnTimerListener listener;
	
	private int useLimitTime;//��ʱ��
	private  int time = 1000;//ÿ�γ�ʱʱ��

	public CTTimer(OnTimerListener listener,int limitTime)
	{
		this.listener 		= listener;
		this.limitTime 		= limitTime;
		useLimitTime		= limitTime;
		timeHandler 		= new Handler();
		setTimeRunnable();
	}
	
	public CTTimer(OnTimerListener listener,int limitTime,int timeOutTime)
	{
		this.listener 		= listener;
		this.limitTime 		= limitTime;
		useLimitTime		= limitTime;
		time				= timeOutTime;
		timeHandler 		= new Handler();
		setTimeRunnable();
	}
	
	private void setTimeRunnable()
	{
		runnable = new Runnable() {


			public void run() {
				// TODO Auto-generated method stub
		
				timeHandler.postDelayed(runnable, time);
				if(isStart){//isStart
					if(limitTime>0){
						limitTime--;
						if(listener!=null)
							listener.onTime(limitTime);
					}
					
					
					if (limitTime == 0){
						if(listener!=null)
							listener.TimeOver();
						isStart = false;
						timeHandler.removeCallbacks(runnable);
					}
	
				}//isStart
				
			
				
			}
		};
	}
	
	public void setLimitTime(int limitTime)
	{
		this.limitTime = limitTime;
		useLimitTime = limitTime;
		stopTime();
	}
	public void startTime()//��ʼ
	{
		if(!isStart){
		//	limitTime = useLimitTime;
			timeHandler.postDelayed(runnable, time);
			isStart = true;
		}
	
	}
	public void stopTime()//ֹͣ
	{
		if(isStart){
			timeHandler.removeCallbacks(runnable);
			isStart = false;
		}
		
	}
	
	public interface OnTimerListener{
		public void onTime(int time);
		public void TimeOver();
	}
}
