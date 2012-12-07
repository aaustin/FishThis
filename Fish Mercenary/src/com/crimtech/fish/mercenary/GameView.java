package com.crimtech.fish.mercenary;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {   

    private Context ctx;
    private TextView txtStatus;    
    private TextView txtNotice;

    private DisplayThread dthread;
    
    private boolean newTouchEvent;
    
    // init the view and thread
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        newTouchEvent = true;
        
        ctx  = context;
        if (isInEditMode() == false) {
	        dthread = new DisplayThread(holder, ctx, new Handler() {
	            @Override
	            public void handleMessage(Message m) {
	            	txtStatus.setVisibility(m.getData().getInt("viz"));
	            	txtStatus.setText(m.getData().getString("text"));
	            }
	        }, new Handler() {
	            @Override
	            public void handleMessage(Message m) {
	            	txtNotice.setVisibility(m.getData().getInt("viz"));
	            	txtNotice.setText(m.getData().getString("text"));
	            }
	        });
        }
        setFocusable(true); 
    }
    
    // start threads
    public void start_threads() {
    	dthread.setRunning(true);
    	dthread.start();
    }
    	
    // pause threads
    public void pause_threads() {
    	
    }
    
    // resume threads
    public void resume_threads() {
    	
    }
    
    // stop threads, end game
    public void stop_threads() {
    	boolean retry = true;
        dthread.setRunning(false);
        while (retry) {
        	try {                
                dthread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }   
    
    // save the state of the game if save state called
    public void save_state(Bundle outState) {
    	
    }
    
    // restore the state of the game
    public void restore_state(Bundle instate) {
    	
    }
    
    // the user pressed the back button
    public void handle_back_press() {
    	dthread.handle_back_press();
    }        
          
    @Override
    public boolean onTouchEvent(MotionEvent event) {		
        // tell Thread to handle event
    	if (event.getAction() == MotionEvent.ACTION_DOWN && newTouchEvent) {
    		dthread.handle_touching(event);
    		newTouchEvent = false;
    	}
    	
    	if (event.getAction() == MotionEvent.ACTION_UP)
    		newTouchEvent = true;
    	
    	return true;
    }

   
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) pause_threads();
    }

    // connect all the buttons and interface guys
    public void set_interface(TextView textView, TextView txtnotice) {
        txtStatus = textView;      
        txtNotice = txtnotice;
    }

    // change the display parameters if surface is a different size
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        dthread.setSurfaceSize(width, height);
    }

    // created the surface so safe to start display and physics threads
    public void surfaceCreated(SurfaceHolder holder) {
    	start_threads();
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        stop_threads();        
    }

	

	
}