package com.pedroso.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CanvasGrid extends SurfaceView implements Runnable ,SurfaceHolder.Callback
{
	private Thread thread;
	private boolean runThread;
	
	private int rows;
	private int columns;
	private int cellWidth;
	private int cellHeight;
	private int marginX;
	private int marginY;
	private int backColor;
	private int[][] colors;
	
	public CanvasGrid(Context context, int cellWidth, int cellHeight){
		super(context);
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.backColor = Color.BLACK;
		
		getHolder().addCallback(this);
	}
	
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		initializeGrid();
	}
	
	private void initializeGrid()
	{
		int screenWidth = this.getWidth();
		int screenHeight = this.getHeight();
		
		int relativeCellWidth = cellWidth + 1;
		int relativeCellHeight = cellHeight + 1;
		
		rows = screenWidth / relativeCellWidth;
		columns = screenHeight / relativeCellHeight;
		
		initializegGridColorArray(Color.WHITE);
		
		marginX = screenWidth - ( relativeCellWidth * rows );
		marginY = screenHeight - (relativeCellHeight * columns);
		marginX /= 2;
		marginY /= 2;
	}

	private void initializegGridColorArray(int color)
	{
		colors  = new int[rows][columns];
		
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				colors[i][j] = color;
			}
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			return touchedSquare(event.getX(), event.getY(), Color.BLUE);
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			return touchedSquare(event.getX(), event.getY(), Color.WHITE);
		}
		
		return false;
	}

	private boolean touchedSquare(float x, float y, int color)
	{
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				int left = i*(cellWidth + 1) + marginX;
				int top = j*(cellHeight + 1) + marginY;
				int right = left + cellWidth;
				int bottom = top + cellHeight;

				if(new Rect(left, top, right, bottom).contains((int)x,(int)y)){
					colors[i][j] = color;
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void draw(Canvas canvas){
		Paint paint =  new Paint();
		
		paint.setStyle(Paint.Style.FILL);
		
		paint.setColor(backColor);
		canvas.drawPaint(paint);
		
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				float left = i*(cellWidth + 1) + marginX;
				float top = j*(cellHeight + 1) + marginY;
				float right = left + cellWidth;
				float bottom = top + cellHeight;
				
				paint.setColor(colors[i][j]);
				canvas.drawRect(left, top, right, bottom, paint);
			}
		}
	}

	public void run() {
		while (runThread) {
			Canvas canvas = null;
			try {
				canvas = getHolder().lockCanvas(null);
				synchronized (getHolder()) {
					draw(canvas);
				}
			} finally {
				if (canvas != null) {
					getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	public void start(){
        if (thread == null || !thread.isAlive()) {
        	thread = new Thread(this);
        }
        runThread = true;
        thread.start();
	}
	
	public void stop(){
        boolean retry = true;
        runThread = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
        Log.i("thread", "Thread terminated...");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		start();
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		stop();
	}
	
}
