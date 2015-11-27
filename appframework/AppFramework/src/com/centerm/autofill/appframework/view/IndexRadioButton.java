package com.centerm.autofill.appframework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class IndexRadioButton extends RadioButton{

	private int index = 0;
	public IndexRadioButton(Context context) {
		super(context);
	}
	
	public IndexRadioButton(Context context, AttributeSet attrs) {
	        super(context, attrs);

	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener, int index) {
		super.setOnCheckedChangeListener(listener);
		this.index = index;
	}

	public int indexOfGroup()
	{
		return index;
	}
}
