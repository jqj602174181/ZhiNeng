package com.centerm.autofill.appframework.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;


public class PlaySoundClass
{
	public final int INPUT_AUDIO = 1;
	public final int INPUTAGAIN_AUDIO = 2;
	public final int KEY_AUDIO = 10;
	private MediaPlayer startPlayer = null;
	private SoundPool sndPool = null;
	private AudioManager audioManager = null;
	private final Context mContext;
	private int sndid = -1;

	public PlaySoundClass( Context context )
	{
		mContext = context;
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	// 播放语音
	public void playAudio(String filename)
	{
		Uri uri = Uri.parse(filename);
		startPlayer = MediaPlayerClass.create(mContext, uri);
		startPlayer.start();
	}

	// 播放语音
	public void playAudio(int resid)
	{
		startPlayer = MediaPlayerClass.create(mContext, resid);
		startPlayer.start();
	}

	public void playKeyBeep()
	{
		int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		float fSoundVolume = (float) currentVol / (float) maxVol;
		
		if (sndPool != null)
		{
			sndPool.play(sndid, fSoundVolume, fSoundVolume, 1, 0, (float) 1.0);
		}
	}

	public void initKeyBeep(int resid)
	{
		sndPool = new SoundPool(10, AudioManager.STREAM_ALARM, 0);
		sndid = sndPool.load(mContext, resid, 1);
	}
	
	public void releaseKeyBeep()
	{
		if (sndPool != null)
		{
			sndPool.unload(sndid);
			sndPool.release();
			sndPool = null;
		}
	}

	// 释放多媒体资源
	public void releaseMediaPlayer()
	{
		if (startPlayer != null)
		{
			MediaPlayerClass.releaseMediaPlayer(startPlayer);
		}
		releaseKeyBeep();
	}

}
