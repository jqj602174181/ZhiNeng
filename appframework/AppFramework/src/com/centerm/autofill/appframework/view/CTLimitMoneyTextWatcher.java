package com.centerm.autofill.appframework.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
// com.centerm.autofill.appframework.view.CTLimitMoneyTextWatcher
//限制输入的金额不能超过某个值，且小数点的位数不能超过某位
public class CTLimitMoneyTextWatcher implements TextWatcher{

	private int limitDecimalNumber = 2;//小数点的位数
	protected  double limitMoney = 999999999.99;//金额上限制
	protected String sLimitMoney = "999999999.99";
	private EditText sourceET;
	public CTLimitMoneyTextWatcher(double limitMoney,int decimalNumber,EditText sourceET)
	{
		this.limitMoney 			= limitMoney;
		sLimitMoney					= Double.toString(limitMoney);
		this.limitDecimalNumber  	= decimalNumber;
		this.sourceET 				= sourceET;
	}
	
	public CTLimitMoneyTextWatcher(int decimalNumber,EditText sourceET)
	{

		this.limitDecimalNumber  	= decimalNumber;
		this.sourceET 				= sourceET;
	}
	@Override
	public void afterTextChanged(Editable s) {
		
		String text = s.toString();
		if( text.length() == 0 )return;
		float number = Float.parseFloat(text);
		
		//超过限制金额，则回退一个
		if(number>limitMoney){
			String origintext = text.substring( 0, text.length() - 1);
			sourceET.setText(origintext);
			sourceET.setSelection(origintext.length());
			return;
		}
		
		if (text.contains(".")) {
			if (text.length() - 1 -text.indexOf(".") > limitDecimalNumber) {
				text =text.subSequence(0,text.indexOf(".") + limitDecimalNumber+1).toString();
				sourceET.setText(text);
				sourceET.setSelection(text.length());
			}
		}
		if (text.trim().substring(0).equals(".")) {
			text = "0" + text;
			sourceET.setText(text);
			sourceET.setSelection(limitDecimalNumber);
		}

		if (text.startsWith("0")
				&& text.trim().length() > 1) {
			if (!text.substring(1, 2).equals(".")) {
				sourceET.setText(text.subSequence(0, 1));
				sourceET.setSelection(1);
				return;
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

}
