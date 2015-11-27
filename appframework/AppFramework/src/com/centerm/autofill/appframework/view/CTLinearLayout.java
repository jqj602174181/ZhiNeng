package com.centerm.autofill.appframework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
// com.centerm.autofill.appframework.view.CTLinearLayout
public class CTLinearLayout extends LinearLayout {

	private int width = 0;
	private int height = 0;

	private RectF drawRect;
	private Paint drawPaint;
	
	private int padding = 0;
	private float density;
	private boolean isFirst = true;
	public CTLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();

	}

	
	public CTLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init()
	{
		drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		drawPaint.setColor(0xff999999);
		drawPaint.setStrokeWidth(1);
		drawPaint.setStyle(Paint.Style.STROKE);
		density = getContext().getResources().getDisplayMetrics().density;	
		padding = (int)(20*density+0.5);

	
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawRoundRect(drawRect, padding, padding, drawPaint);
		
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if(isFirst){
			width = getWidth();
			height = getHeight();
			drawRect = new RectF(padding, padding, width-padding, height-padding);
			isFirst = false;
		}
	
	}
	
	

}
