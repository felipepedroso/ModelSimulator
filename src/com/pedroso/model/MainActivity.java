package com.pedroso.model;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		
		CanvasGrid view = new CanvasGrid(this, 10, 10);
        //setContentView(R.layout.main);
		setContentView(view);
    }
}
