package com.centerm.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


import android.annotation.SuppressLint;

public class PinYin {
	@SuppressLint("DefaultLocale") 
	public static String getPinYin(String input) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<input.length(); i++)
		{
			sb.append(toPinYin(input.charAt(i)));
		}
		return sb.toString();
	}


    public static String toPinYin(char hanzi){
        HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
        hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[] pinyinArray=null;
        try {
            //是否在汉字范围内
            if(hanzi>=0x4e00 && hanzi<=0x9fa5){
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {

            e.printStackTrace();
        }

        //将获取到的拼音返回
        return pinyinArray[0];
    }
  }

