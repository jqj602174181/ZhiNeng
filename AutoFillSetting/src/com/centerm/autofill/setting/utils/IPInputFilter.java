package com.centerm.autofill.setting.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class IPInputFilter implements InputFilter {
	@Override
    public CharSequence filter(CharSequence source, int start,
            int end, Spanned dest, int dstart, int dend) {
        if( end > start ) {//输入新字符
        	//得到结果字符串
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart) +
            		source.subSequence(start, end) + destTxt.substring(dend);//结果字符串            
           
            //判断是否xxx.xxx.xxx.xxx格式
            if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                    "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
                return "";
            } else {//判断每个数字不超示255
                String[] splits = resultingTxt.split("\\.");
                for (int i=0; i < splits.length; i++) {
                    if (Integer.valueOf(splits[i]) > 255) {
                        return "";
                    }
                }
            }
        }
    return null;
	}

}
