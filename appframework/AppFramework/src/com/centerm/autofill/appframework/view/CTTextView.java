package com.centerm.autofill.appframework.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;
//com.centerm.autofill.appframework.view.CTTextView
//有加*的自定义的TextView
public class CTTextView extends TextView{

	public CTTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		//解析更换标题后，星号消失的问题
		SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder(text); 
      	mSpannableStringBuilder.setSpan
	        (new ForegroundColorSpan(0xffcd0000), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     
		super.setText(mSpannableStringBuilder, type);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		reSetText();
	}
	

	//首个星号标红
	public void reSetText()
	{
		String text = this.getText().toString();
		SpannableStringBuilder mSpannableStringBuilder=new SpannableStringBuilder(text); 
      	mSpannableStringBuilder.setSpan
	        (new ForegroundColorSpan(0xffcd0000), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     
	    setText(mSpannableStringBuilder);
	}
	

}
