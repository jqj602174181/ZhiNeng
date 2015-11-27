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
	// ��ʼ��ͼƬ��Դ
	private Bitmap bitmap;
	private Bitmap btnbitmap;//��ť��ͼ
	private PointF firstPoint = new PointF();
	// Matrix ʵ��
	private Matrix matrix = new Matrix();
	private Matrix matrix1 = new Matrix();
	private int width, height;
	private Point mScreenPoint = new Point();
	// ���ű���
	private float scale = 1.0f;
	private Paint paint;//������ʾ����
	private Paint paintCircle;//��ʾԲȦ��������

	private final int TYPE_NONE = -1;// ��ʼ״̬
	private final int TYPE_TRANSLATE = 0;// ƽ��
	private final int TYPE_SCALE = 2;// ����
	private int mCurrentType = TYPE_NONE;// ��ǰ����

	private PointF prePoint = new PointF();
	private PointF prePoint1 = new PointF();
	private PointF prePoint2 = new PointF();
	DialogFragment mDlg = null;

	public Bitmap readBitMap(Context context, int resId) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		// ��ȡ��ԴͼƬ
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public PictureView(Context context, int imgId, DialogFragment dialog) {
		super(context);
		
		//������ƻ���
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);  
	    paint.setStrokeWidth(3);  
	    paint.setTextSize(30);  
	    paint.setColor(Color.WHITE);
	    //ʹ����Ӱ��ֹ���������ǰ�ɫʱ����������
	    paint.setShadowLayer((float)16.0, (float)0.0, (float)0.0, Color.BLACK );
	    
	    //��ԲȦ
	    paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	    paintCircle.setColor( Color.BLACK );
	    paint.setStrokeWidth(1); 

		// ���λͼ
		// bitmap = BitmapFactory.decodeResource(getResources(),imgId);
		// bitmap = ((BitmapDrawable) context.getResources().getDrawable(imgId))
		// .getBitmap();
	    btnbitmap = readBitMap( context, R.drawable.next_btn_circle );

		bitmap = readBitMap(context, imgId);
		// ���λͼ��
		width = bitmap.getWidth();
		// ���λͼ��
		height = bitmap.getHeight();
		// ʹ��ǰ��ͼ��ý���
		this.setFocusable(true);

		// ��ȡ��Ļ��Ⱥ͸߶�
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		wm.getDefaultDisplay().getSize(mScreenPoint);

		float fTmp = Math.max((float) width  / (float)mScreenPoint.x,
				(float) height / (float)mScreenPoint.y);//���ò���������ʽ
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
		
		//�Ȼ�һ��ԲȦ�������ٻ�ԲȦͼƬ���ٻ�������ӰЧ�������֣���ֹ���������ǰ�ɫʱ����������
        canvas.drawCircle(1234, 70, 36, paintCircle );
	    canvas.drawBitmap(btnbitmap, 1200, 36, null );	    
	    canvas.drawText("��˼���", 1064, 80, paint );		
	}

	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		// ͼƬ4�����������
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * width + f[1] * 0 + f[2];
		float y2 = f[3] * width + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * height + f[2];
		float y3 = f[3] * 0 + f[4] * height + f[5];
		float x4 = f[0] * width + f[1] * height + f[2];
		float y4 = f[3] * width + f[4] * height + f[5];
		// ͼƬ�ֿ��
		double mywidth = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
				* (y1 - y2));
		// ���ű����ж�
		if (mywidth < mScreenPoint.x / 3 || mywidth > mScreenPoint.x * 3) {
			return true;
		}

		// �����ж�
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

	// ������������
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
			mCurrentType = TYPE_TRANSLATE;// ƽ��
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			prePoint1.set(event.getX(0), event.getY(0));
			prePoint2.set(event.getX(1), event.getY(1));
			mCurrentType = TYPE_SCALE;// ����
			break;

		case MotionEvent.ACTION_MOVE:
			if (mCurrentType == TYPE_TRANSLATE) {// ƽ��
				matrix1.postTranslate(event.getX() - prePoint.x, event.getY()
						- prePoint.y);

				prePoint.set(event.getX(), event.getY());
				if (!matrixCheck()) {
					matrix.set(matrix1);
					postInvalidate();
				} else {
					matrix1.set(matrix);
				}

			} else if (mCurrentType == TYPE_SCALE) {// ����
				float preSpacing = spacing(prePoint1, prePoint2);
				prePoint1.set(event.getX(0), event.getY(0));
				prePoint2.set(event.getX(1), event.getY(1));

				float currentSpacing = spacing(prePoint1, prePoint2);
				// float temp = scale * currentSpacing / preSpacing;
				// if (temp < 1)// ��С����С��ԭͼ��С
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
			// �ж��Ƿ�Ϊ�û����
			PointF endPoint = new PointF(event.getX(), event.getY());
			if (spacing(firstPoint, endPoint) < 5) {
				if (mDlg != null) {
					// ���ջ���
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
