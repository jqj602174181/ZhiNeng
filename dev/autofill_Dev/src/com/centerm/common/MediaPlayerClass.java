package com.centerm.common;

import java.io.IOException;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

public final class MediaPlayerClass
{
	public static MediaPlayer create(Context context, Uri uri)
	{
		try
		{
			MediaPlayer mp = new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mp.setDataSource(context, uri);
			mp.prepare();
			return mp;
		} 
		catch (IOException ex)
		{
			ex.printStackTrace();
		} 
		catch (IllegalArgumentException ex)
		{
			ex.printStackTrace();
		} 
		catch (SecurityException ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}

	public static MediaPlayer create(Context context, int resid)
	{
		try
		{
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
			if (afd == null)
			{
				return null;
			}
			MediaPlayer mp = new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			mp.prepare();
			return mp;
		} 
		catch (IOException ex)
		{
			ex.printStackTrace();
		} 
		catch (IllegalArgumentException ex)
		{
			ex.printStackTrace();
		} 
		catch (SecurityException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	// 释放多媒体资源
	public static void releaseMediaPlayer(MediaPlayer mp)
	{
		if ( mp != null)
		{
			mp.release();
			mp = null;
		}
	}
}
