package com.pedroso.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
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
	private int backColor = Color.BLACK;;
	private int[][] colors;
	
	public CanvasGrid(Context context){
		super(context);		
		getHolder().addCallback(this);
	}
	
	public CanvasGrid(Context context, AttributeSet attrs){
		super(context,attrs);
		initCellDimensions(attrs);
		getHolder().addCallback(this);
	}
	
	public CanvasGrid(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
		initCellDimensions(attrs);
		getHolder().addCallback(this);
	}
	
	
	private void initCellDimensions(AttributeSet attrs) {
		TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.CanvasGrid);
		
		cellWidth = arr.getInt(R.styleable.CanvasGrid_cellWidth, 10);
		cellHeight = arr.getInt(R.styleable.CanvasGrid_cellHeight, 10);
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
				setColor(color, i, j);
			}
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
		float touchedX = event.getX();
		float touchedY = event.getY();
		
		int[] squareIndex = getSquareIndex(touchedX, touchedY);
		
		if(squareIndex != null){
			int eventAction = event.getAction(); 
			
			switch (eventAction) {
			case MotionEvent.ACTION_MOVE:
				
				break;
			case MotionEvent.ACTION_DOWN:
				
				break;
			case MotionEvent.ACTION_UP:
				
				break;
	

			default:
				break;
			}

		}
				
		return false;
	}

	private int[] getSquareIndex(float x, float y)
	{
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				int left = i*(cellWidth + 1) + marginX;
				int top = j*(cellHeight + 1) + marginY;
				int right = left + cellWidth;
				int bottom = top + cellHeight;

				if(new Rect(left, top, right, bottom).contains((int)x,(int)y)){
					int[] coordinates = {i, j};
					return coordinates;
				}
			}
		}
		
		return null;
	}
	
	public void setColor(int color, int x, int y){
		if(x < rows && x >= 0 && y < columns && y>= 0){
			colors[x][y] = color;
		}
	}
	
	public void draw(Canvas canvas){
		if(canvas != null){
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
