package com.example.testforanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements Callback {

	private final static String TAG = "tag";

	private MyThread thread;
	private SurfaceHolder holder;
	
	private float touchY;
	
	private float moveY = 0;
	
	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		holder = getHolder();
		holder.addCallback(this);
		thread = new MyThread(holder);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceCreated");
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceChanged");

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(200, 1280);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceDestroyed");
		thread.run = false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			moveY = event.getY() - touchY;
			touchY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			moveY = 0;
		default:
			break;
		}
		
		return true;
	}

	class MyThread extends Thread {
		
		public static final int WAVE_WIDTH = 150;  
		public static final int WAVE_HEIGHT = 30;  
		public static final int WAVE_X = -2*WAVE_WIDTH;  
		public static final int WAVE_Y1 = 200;  
		public static final int WAVE_Y2 = 600;  
		
		public static final int SCREEN_WIDTH = 720/3;  
		public static final int SCREEN_HEIGHT = 1280;  
		
		public static final int WAVE_SIZE = (SCREEN_WIDTH / WAVE_WIDTH) + 3;  
        
		private SurfaceHolder holder;
		private Canvas canvas;
		private Paint paint;
		private Path path;
		private Point[] points = new Point[WAVE_SIZE + 1];  
		private Point[] controls = new Point[WAVE_SIZE];  
		private int transfer1 = 0;
		private int transfer2 = 0;
		private int transferIncrease1 = 4;
		private int transferIncrease2 = 6;
		private int colors[] = {Color.WHITE, Color.CYAN, Color.BLUE, Color.WHITE, Color.GRAY};
		
		private float upValue = 1000;
		private float downValue = 1000;
		private int y2 = 0;
		
		public boolean run = true;
		
		public MyThread(SurfaceHolder holder) {
			this.holder = holder;
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setStyle(Style.FILL);
            paint.setStrokeWidth(1);
            paint.setTextSize(50);
            paint.setTextAlign(Align.CENTER);
            path = new Path();
            
            for (int i = 0; i < WAVE_SIZE; i++) {
            	points[i] = new Point(WAVE_X + i * WAVE_WIDTH, WAVE_Y1);
            	if (i % 2 == 0) {
	            	controls[i] = new Point(WAVE_X + i * WAVE_WIDTH + WAVE_WIDTH / 2, 
	            			WAVE_Y1 + WAVE_HEIGHT);
            	} else {
            		controls[i] = new Point(WAVE_X + i * WAVE_WIDTH + WAVE_WIDTH / 2,  
            				WAVE_Y1 - WAVE_HEIGHT);
            	}
            }
            points[WAVE_SIZE] = new Point(WAVE_X + WAVE_SIZE * WAVE_WIDTH, WAVE_Y1);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (run) {
				try {
					getValue();
					synchronized (holder) {
						transfer1 += transferIncrease1;
						transfer2 += transferIncrease2;
						transfer1 %= (WAVE_WIDTH * 2);
						transfer2 %= (WAVE_WIDTH * 2);
						
						canvas = holder.lockCanvas();
						
						paint.setColor(colors[0]);
						canvas.drawRect(0, 0, SCREEN_WIDTH, WAVE_Y1, paint);
						
						paint.setAlpha(255 * y2);
						paint.setColor(colors[1]);
						canvas.drawRect(WAVE_X, WAVE_Y1, SCREEN_WIDTH, SCREEN_HEIGHT, paint);
						
						paint.setColor(colors[2]);
						canvas.drawRect(WAVE_X, WAVE_Y2 + y2, SCREEN_WIDTH, SCREEN_HEIGHT, paint);
						
//						canvas.save();
//						canvas.save();
//						canvas.translate(transfer1, 0);
						
			            // draw the quad line 
						for (int i = 0; i < WAVE_SIZE; i++) {
							if (i % 2 == 0) {
								paint.setColor(colors[0]);
							} else {
								paint.setColor(colors[1]);
							}
				            path.reset();  
				            path.moveTo(points[i].x + transfer1, points[i].y);  
				            path.quadTo(controls[i].x + transfer1, controls[i].y, 
				            		points[i+1].x + transfer1, points[i+1].y);  
				            canvas.drawPath(path, paint);
						}
						
//						canvas.restore();
//						canvas.translate(transfer2, 0);
						
						// draw the quad line 
						for (int i = 0; i < WAVE_SIZE; i++) {
							if (i % 2 == 0) {
								paint.setColor(colors[1]);
							} else {
								paint.setColor(colors[2]);
							}
							path.reset();  
							path.moveTo(points[i].x + transfer2, points[i].y + y2 + WAVE_Y2 - WAVE_Y1);  
							path.quadTo(controls[i].x + transfer2, controls[i].y + y2 + WAVE_Y2 - WAVE_Y1,
									points[i+1].x + transfer2, points[i+1].y + y2 + WAVE_Y2 - WAVE_Y1);  
							canvas.drawPath(path, paint);
						}
			            
//						canvas.restore();
						
						// draw values
						paint.setColor(colors[3]);
						canvas.drawText(String.valueOf(upValue), SCREEN_WIDTH / 2,
								WAVE_Y1 + (WAVE_Y2 + y2 - WAVE_Y1) / 2, paint);
						
						paint.setColor(colors[4]);
						canvas.drawText(String.valueOf(downValue), SCREEN_WIDTH / 2,
								WAVE_Y2 + y2 - WAVE_Y1 + (SCREEN_HEIGHT + WAVE_Y1 - WAVE_Y2 - y2) / 2, paint);
						
//						paint.setColor(colors[0]);
//						canvas.drawRect(SCREEN_WIDTH, 0, SCREEN_WIDTH * 3, SCREEN_HEIGHT, paint);
						
						Thread.sleep(33);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
			super.run();
		}

		private void getValue() {
			if (moveY != 0) {
				if (WAVE_Y2 + y2 + moveY - 2 * WAVE_HEIGHT <= WAVE_Y1) {
					moveY = 0;
				} else if (WAVE_Y2 + y2 + moveY + 2 * WAVE_HEIGHT >= SCREEN_HEIGHT) {
					moveY = 0;
				} else {
					upValue += moveY;
					downValue -= moveY;
					y2 += moveY;
				}
			}
		}
	}
}
