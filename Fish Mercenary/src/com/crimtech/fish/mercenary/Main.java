package com.crimtech.fish.mercenary;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity {

	GameView gameView;
	TextView txtStatus;
	TextView txtNotice;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        gameView = (GameView) findViewById(R.id.gameview);
        txtStatus = (TextView) findViewById(R.id.txtStatus);        
        txtNotice = (TextView) findViewById(R.id.txtNotice);
        gameView.set_interface(txtStatus, txtNotice);
        txtStatus.setText("Horay! click anywhere to start");
        
        if (savedInstanceState != null) {        
            // we are being restored: resume a previous game
        	gameView.restore_state(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
    }

    // Fills the menu with a bunch of options to control game flow
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        
        menu.add(0, 1, 0, "Stop");
        menu.add(0, 2, 0, "Pause");
        menu.add(0, 3, 0, "Resume");
        menu.add(0, 4, 0, "Exit");
        
        return true;
    }
    
    // responds to option selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0: // start
                gameView.start_threads();
                return true;
            case 1: // stop
            	gameView.stop_threads();                
                return true;
            case 2:
            	gameView.pause_threads();
                return true;
            case 3:
                gameView.resume_threads();
                return true;
            case 4:
                finish();
                return true;
        }

        return false;
    }
    
	@Override
    public void onBackPressed() {
		gameView.handle_back_press();
    }
    
    
    @Override
    protected void onResume() {
        super.onPause();
        gameView.resume_threads();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause_threads();
    }
 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        gameView.save_state(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
    
}
