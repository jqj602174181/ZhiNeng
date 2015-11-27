package com.centerm.autofill.setting.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class IPInputFilter implements InputFilter {
	@Override
    public CharSequence filter(CharSequence source, int start,
            int end, Spanned dest, int dstart, int dend) {
        if( end > start ) {//�������ַ�
        	//�õ�����ַ���
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart) +
            		source.subSequence(start, end) + destTxt.substring(dend);//����ַ���            
           
            //�ж��Ƿ�xxx.xxx.xxx.xxx��ʽ
            if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                    "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
                return "";
            } else {//�ж�ÿ�����ֲ���ʾ255
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
