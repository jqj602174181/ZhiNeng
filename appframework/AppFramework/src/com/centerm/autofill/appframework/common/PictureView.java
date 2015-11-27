/**
 *
 */
package com.centerm.autofill.appframework.common;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.listener.PreviewListener;

public class PictureView extends View {
	// 初始的图片资源
	private Bitmap bitmap;
	private Bitmap btnbitmap;//按钮视图
	private PointF firstPoint = new PointF();
	// Matrix 实例
	private Matrix matrix = new Matrix();
	private Matrix matrix1 = new Matrix();
	private int width, height;
	private Point mScreenPoint = new Point();
	// 缩放比例
	private float scale = 1.0f;
	private Paint paint;//文字提示画笔
	private Paint paintCircle;//提示圆圈背景画笔

	private final int TYPE_NONE = -1;// 初始状态
	private final int TYPE_TRANSLATE = 0;// 平移
	private final int TYPE_SCALE = 2;// 缩放
	private int mCurrentType = TYPE_NONE;// 当前操作

	private PointF prePoint = new PointF();
	private PointF prePoint1 = new PointF();
	private PointF prePoint2 = new PointF();
	DialogFragment mDlg = null;

	public Bitmap readBitMap(Context context, int resId) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public PictureView(Context context, int imgId, DialogFragment dialog) {
		super(context);
		
		//定义绘制画笔
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);  
	    paint.setStrokeWidth(3);  
	    paint.setTextSize(30);  
	    paint.setColor(Color.WHITE);
	    //使用阴影防止背景正好是白色时看不到文字
	    paint.setShadowLayer((float)16.0, (float)0.0, (float)0.0, Color.BLACK );
	    
	    //画圆圈
	    paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	    paintCircle.setColor( Color.BLACK );
	    paint.setStrokeWidth(1); 

		// 获得位图
		// bitmap = BitmapFactory.decodeResource(getResources(),imgId);
		// bitmap = ((BitmapDrawable) context.getResources().getDrawable(imgId))
		// .getBitmap();
	    btnbitmap = readBitMap( context, R.drawable.next_btn_circle );

		bitmap = readBitMap(context, imgId);
		// 获得位图宽
		width = bitmap.getWidth();
		// 获得位图高
		height = bitmap.getHeight();
		// 使当前视图获得焦点
		this.setFocusable(true);

		// 获取屏幕宽度和高度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		wm.getDefaultDisplay().getSize(mScreenPoint);

		float fTmp = Math.max((float) width  / (float)mScreenPoint.x,
				(float) height / (float)mScreenPoint.y);//不用采用整除方式
		if (fTmp > 1) {
			scale = 1 / fTmp;
			matrix1.postScale(scale, scale);
		}

		matrix1.postTranslate((mScreenPoint.x - width * scale) / 2, (mScreenPoint.y - height * scale) / 2);
		matrix.set(matrix1);

		mDlg = dialog;
		postInvalidate();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.drawBitmap(bitmap, matrix, null);
		canvas.restore();				
		
		//先画一个圆圈背景，再画圆圈图片，再画含有阴影效果的文字，防止背景正好是白色时看不到文字
        canvas.drawCircle(1234, 70, 36, paintCircle );
	    canvas.drawBitmap(btnbitmap, 1200, 36, null );	    
	    canvas.drawText("点此继续", 1064, 80, paint );		
	}

	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		// 图片4个顶点的坐标
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * width + f[1] * 0 + f[2];
		float y2 = f[3] * width + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * height + f[2];
		float y3 = f[3] * 0 + f[4] * height + f[5];
		float x4 = f[0] * width + f[1] * height + f[2];
		float y4 = f[3] * width + f[4] * height + f[5];
		// 图片现宽度
		double mywidth = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
				* (y1 - y2));
		// 缩放比率判断
		if (mywidth < mScreenPoint.x / 3 || mywidth > mScreenPoint.x * 3) {
			return true;
		}

		// 出界判断
		if ((x1 < mScreenPoint.x / 3 && x2 < mScreenPoint.x / 3
				&& x3 < mScreenPoint.x / 3 && x4 < mScreenPoint.x / 3)
				|| (x1 > mScreenPoint.x * 2 / 3 && x2 > mScreenPoint.x * 2 / 3
						&& x3 > mScreenPoint.x * 2 / 3 && x4 > mScreenPoint.x * 2 / 3)
				|| (y1 < mScreenPoint.y / 3 && y2 < mScreenPoint.y / 3
						&& y3 < mScreenPoint.y / 3 && y4 < mScreenPoint.y / 3)
				|| (y1 > mScreenPoint.y * 2 / 3 && y2 > mScreenPoint.y * 2 / 3
						&& y3 > mScreenPoint.y * 2 / 3 && y4 > mScreenPoint.y * 2 / 3)) {
			return true;
		}
		return false;
	}

	// 触碰两点间距离
	private float spacing(PointF pointStart, PointF pointEnd) {
		float x = pointStart.x - pointEnd.x;
		float y = pointStart.y - pointEnd.y;

		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			firstPoint.set(event.getX(), event.getY());
			prePoint.set(event.getX(), event.getY());
			mCurrentType = TYPE_TRANSLATE;// 平移
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			prePoint1.set(event.getX(0), event.getY(0));
			prePoint2.set(event.getX(1), event.getY(1));
			mCurrentType = TYPE_SCALE;// 缩放
			break;

		case MotionEvent.ACTION_MOVE:
			if (mCurrentType == TYPE_TRANSLATE) {// 平移
				matrix1.postTranslate(event.getX() - prePoint.x, event.getY()
						- prePoint.y);

				prePoint.set(event.getX(), event.getY());
				if (!matrixCheck()) {
					matrix.set(matrix1);
					postInvalidate();
				} else {
					matrix1.set(matrix);
				}

			} else if (mCurrentType == TYPE_SCALE) {// 缩放
				float preSpacing = spacing(prePoint1, prePoint2);
				prePoint1.set(event.getX(0), event.getY(0));
				prePoint2.set(event.getX(1), event.getY(1));

				float currentSpacing = spacing(prePoint1, prePoint2);
				// float temp = scale * currentSpacing / preSpacing;
				// if (temp < 1)// 最小不能小于原图大小
				// {
				// break;
				// }
				scale *= currentSpacing / preSpacing;
				matrix1.postScale(currentSpacing / preSpacing, currentSpacing
						/ preSpacing, (prePoint1.x + prePoint2.x) / 2,
						(prePoint1.y + prePoint2.y) / 2);

				matrix.set(matrix1);
				postInvalidate();
			}
			break;

		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			mCurrentType = TYPE_NONE;
			// 判断是否为用户点击
			PointF endPoint = new PointF(event.getX(), event.getY());
			if (spacing(firstPoint, endPoint) < 5) {
				if (mDlg != null) {
					// 回收机制
					if (bitmap.isRecycled()) {
						bitmap.recycle();
					}

					((PreviewListener) (mDlg.getActivity()))
							.onPreviewComplete();
					mDlg.dismiss();
				}
			}
			break;
		}

		return true;
	}
}
