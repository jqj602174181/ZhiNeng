package com.centerm.autofill.setting.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

//ÎÄ¼þ²Ù×÷
public class FileUtil {

	public static boolean copyFile( String srcPath, String desPath ){
		File src = new File( srcPath );
		File dst = new File( desPath );
		try {
			InputStream in = new FileInputStream(src);
		    OutputStream out = new FileOutputStream(dst);
		    byte[] buf = new byte[4096];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		    return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
