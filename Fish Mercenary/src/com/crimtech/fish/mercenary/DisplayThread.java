package com.crimtech.fish.mercenary;

import java.util.ArrayList;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class DisplayThread extends Thread {

    public static final int STATE_START = -1;
    public static final int STATE_PLAY = 0;
    public static final int STATE_INVENTORY = 10;
    public static final int STATE_STORE = 20;
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_RUNNING = 3;    
    
    private static final int CAST_FLASH_TIME = 3000;

    private Handler txtStatusHandler;
    private Handler txtNoticeHandler;
    private SurfaceHolder surfHolder;
    private Context ctx;
    
    private PhysicsThread pthread;
    
    private int gameState;
    private boolean dthreadRun = false;

    private int canvasHeight = 1;
    private int canvasWidth = 1;
    
    private long now;
    private long prevTime;
    
    public static final double WATER_PERCENTAGE = 0.80;
    public static final double NON_FISHER_PERCENTAGE = 0.80;
    
    private static final double CLOUD1_START = 0.6;
    private static final double CLOUD2_START = 0.35;
    private static final double CLOUD3_START = 0.15;
    private static final double ROCK1_START = 0.65;
    
    private static final double BAD_TENSION = 0.6;
    
    private int fisherStart;
    private int airHeight;
    private int fisherWidth;
    
    private int fishTargetStartx;
    private int fishTargetStarty;
    private int coinStartx;
    private int coinStarty;
    private int menuItemSize;
    private int boxStarty;
    private int storeStarty;
    
    
    private Drawable fisherNormImage;
    private Drawable fisherPullImage;
    private Drawable boxImage;
    private Drawable storeImage;
    private ArrayList<Drawable> fishLeftGfx;
    private ArrayList<Drawable> fishRightGfx;
    private ArrayList<Drawable> eelLeftGfx;
    private ArrayList<Drawable> eelRightGfx;
          
    private Drawable sunImg;
    private Drawable cloud1Img;
    private Drawable cloud2Img;
    private Drawable cloud3Img;
    private Drawable sandImg;
    private Drawable rock1Img;
    private Drawable rock2Img;
    
    private Paint tempAirPaint;
    private Paint tempWaterPaint;
    private Paint tempMenuPaint;
    private TextPaint tempGoldPaint;
    private Paint tempPausePaint;
    private Paint tempTargetPaint;
    private Paint tempLurePaint;
    private Paint tempLinePaint;
    private Paint tempFishPaint;
    private Paint tempFishHeadPaint;
    private Paint tempTensionOutlinePaint;
    private Paint tempTensionGoodPaint;
    private Paint tempTensionBadPaint;
    private TextPaint tempTensionGoodTextPaint;
    private TextPaint tempTensionBadTextPaint;
    private Paint tempWhitePaint;
    private TextPaint tempWhiteTextPaint;
    
	private RectF tempRect;
	
	StaticLayout txtLayout;
  
	private int flashTimer;
  
    public DisplayThread(SurfaceHolder surfaceHolder, Context context, Handler txtHandler, Handler txtNotice) {

        surfHolder = surfaceHolder;
        this.txtStatusHandler = txtHandler;
        this.txtNoticeHandler = txtNotice;
        ctx = context;
               
        gameState = STATE_START;      
        
        // initialize graphics
        sunImg = context.getResources().getDrawable(R.drawable.sun);
        cloud1Img = context.getResources().getDrawable(R.drawable.cloud1);
        cloud2Img = context.getResources().getDrawable(R.drawable.cloud2);
        cloud3Img = context.getResources().getDrawable(R.drawable.cloud3);        
        sandImg = context.getResources().getDrawable(R.drawable.sand);  
        rock1Img = context.getResources().getDrawable(R.drawable.rock1);  
        rock2Img = context.getResources().getDrawable(R.drawable.rock2);
        
        fisherNormImage = context.getResources().getDrawable(R.drawable.fisher);
        fisherPullImage = context.getResources().getDrawable(R.drawable.fisherpulll);
        boxImage = context.getResources().getDrawable(R.drawable.box);
        storeImage = context.getResources().getDrawable(R.drawable.market);
        
        fishLeftGfx = new ArrayList<Drawable>();
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.green1left));        
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.pink1left));        
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.blue2left));
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.orange2left));
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.pink2left));
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.purple2left));
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.blue1left));
        fishLeftGfx.add(context.getResources().getDrawable(R.drawable.yellow1left));

        fishRightGfx = new ArrayList<Drawable>();
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.green1right));        
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.pink1right));
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.blue2right));
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.orange2right));
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.pink2right));
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.purple2right));
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.blue1right));
        fishRightGfx.add(context.getResources().getDrawable(R.drawable.yellow1right));
        
        eelLeftGfx = new ArrayList<Drawable>();
        eelLeftGfx.add(context.getResources().getDrawable(R.drawable.green4firstleft));
        eelLeftGfx.add(context.getResources().getDrawable(R.drawable.green4secondleft));
        eelLeftGfx.add(context.getResources().getDrawable(R.drawable.green4thirdleft));
        
        eelRightGfx = new ArrayList<Drawable>();
        eelRightGfx.add(context.getResources().getDrawable(R.drawable.green4firstright));
        eelRightGfx.add(context.getResources().getDrawable(R.drawable.green4secondright));
        eelRightGfx.add(context.getResources().getDrawable(R.drawable.green4thirdright));
        
        
             
        // Initialize paints for objects
        tempAirPaint = new Paint();
        tempAirPaint.setAntiAlias(true);     	
        tempAirPaint.setARGB(255, 227, 237, 245);
        
        tempWaterPaint = new Paint();
        tempWaterPaint.setAntiAlias(true);     	
        tempWaterPaint.setARGB(255, 111, 147, 191);
        
        tempTargetPaint = new Paint();
        tempTargetPaint.setAntiAlias(true);     	
        tempTargetPaint.setARGB(255, 242, 86, 86);
        
        tempLurePaint = new Paint();
        tempLurePaint.setAntiAlias(true);     	
        tempLurePaint.setARGB(255, 0, 0, 0);
        
        tempMenuPaint = new Paint();
        tempMenuPaint.setAntiAlias(true);     	
        tempMenuPaint.setARGB(185, 40, 40, 40);
        
        tempGoldPaint = new TextPaint();
        tempGoldPaint.setAntiAlias(true);     	
        tempGoldPaint.setARGB(255, 236, 204, 29);
        
        tempWhitePaint = new Paint();
        tempWhitePaint.setAntiAlias(true);     	
        tempWhitePaint.setARGB(255, 255, 255, 255);
        
        tempWhiteTextPaint = new TextPaint();
        tempWhiteTextPaint.setAntiAlias(true);
        tempWhiteTextPaint.setARGB(255, 255, 255, 255);       
        
        tempPausePaint = new Paint();
        tempPausePaint.setAntiAlias(true);     	
        tempPausePaint.setARGB(125, 40, 40, 40);
        
        tempLinePaint = new Paint();
        tempLinePaint.setAntiAlias(true);     	
        tempLinePaint.setARGB(125, 120, 120, 120);
        
        tempFishPaint = new Paint();
        tempFishPaint.setAntiAlias(true);     	
        tempFishPaint.setARGB(255, 229, 168, 242);
        
        tempFishHeadPaint = new Paint();
        tempFishHeadPaint.setAntiAlias(true);     	
        tempFishHeadPaint.setARGB(255, 0, 0, 0);
        
        tempTensionOutlinePaint = new Paint();
        tempTensionOutlinePaint.setAntiAlias(true);     	
        tempTensionOutlinePaint.setARGB(255, 0, 0, 0);
        
        tempTensionGoodPaint = new Paint();
        tempTensionGoodPaint.setAntiAlias(true);     	
        tempTensionGoodPaint.setARGB(255, 242, 86, 86);
        
        tempTensionBadPaint = new Paint();
        tempTensionBadPaint.setAntiAlias(true);     	
        tempTensionBadPaint.setARGB(255, 242, 86, 86);
        
        tempTensionGoodTextPaint = new TextPaint();
        tempTensionGoodTextPaint.setAntiAlias(true);     	
        tempTensionGoodTextPaint.setARGB(255, 242, 86, 86);
        
        tempTensionBadTextPaint = new TextPaint();
        tempTensionBadTextPaint.setAntiAlias(true);     	
        tempTensionBadTextPaint.setARGB(255, 242, 86, 86);
     	
     	tempRect = new RectF(0, 0, 0, 0);

    }

    
    
    // generic draw function that calls other draws
    private void doDraw(Canvas canvas) {
        if (gameState == STATE_RUNNING) {
            drawRunning(canvas);
        } else if (gameState == STATE_PAUSE) {
        	drawPause(canvas);
        } else if (gameState == STATE_INVENTORY) {
        	drawInventory(canvas);
        } else if (gameState == STATE_STORE) {
        	drawStore(canvas);
        } else if (gameState == STATE_START) {
            drawReady(canvas);
        } else if (gameState == STATE_PLAY ) {        	
            drawPlay(canvas);
        } else if (gameState == STATE_LOSE) {
        	drawLose(canvas);
        }
    }

    // draws the sky (clouds and sun)
    private void sun_clouds(Canvas canvas) {
    	sunImg.setBounds(0, 0, airHeight, (int)(0.9*airHeight));
    	sunImg.draw(canvas);
    	
    	cloud1Img.setBounds((int)(canvasWidth*CLOUD1_START), 1*airHeight/8, (int)(canvasWidth*CLOUD1_START + 4*airHeight/5), 5*airHeight/8);
    	cloud1Img.draw(canvas);
    	
       	cloud2Img.setBounds((int)(canvasWidth*CLOUD2_START), 0, (int)(canvasWidth*CLOUD2_START + airHeight), airHeight/2);
    	cloud2Img.draw(canvas);
    	
       	cloud3Img.setBounds((int)(canvasWidth*CLOUD3_START), 1*airHeight/6, (int)(canvasWidth*CLOUD3_START+ 4*airHeight/3), 2*airHeight/3);
    	cloud3Img.draw(canvas);
    	
    }
    
    // draws the background
    private void background(Canvas canvas) {
    	// running all the canvas draw bitmaps for the live play
    	tempRect.set(0, 0, canvasWidth, airHeight);
        canvas.drawRect(tempRect, tempAirPaint);
        
        tempRect.set(0, airHeight, canvasWidth, canvasHeight);
        canvas.drawRect(tempRect, tempWaterPaint);        
        
        for(int i = 0; i < canvasWidth + airHeight/2; i = i + airHeight/2) {
        	sandImg.setBounds(i, canvasHeight-airHeight/2, i+airHeight/2, canvasHeight);
            sandImg.draw(canvas);
        }       
        
    	rock2Img.setBounds(0, canvasHeight-7*airHeight/8, 3*airHeight/2, canvasHeight-airHeight/12);
    	rock2Img.draw(canvas);
        
    	rock1Img.setBounds((int)(ROCK1_START*canvasWidth), canvasHeight-7*airHeight/8, (int)(ROCK1_START*canvasWidth+5*airHeight/4), canvasHeight-airHeight/8);
    	rock1Img.draw(canvas);
      
    }
    
    // draws the fish
    private void fish(Canvas canvas) {
    	// FISH DRAWING
        for (int i = 0; i < pthread.currentFish.size(); i++) {

    		Fish curr = pthread.currentFish.get(i);
    		
        	if (curr.state == 0 || curr.state == 2 || curr.state == 5) {
            	Drawable draw = fishLeftGfx.get(curr.index);
            	draw.setBounds((int)curr.posx, (int)curr.posy, (int)curr.posx+curr.width, (int)curr.posy+curr.height);
            	draw.draw(canvas);
        	} else {
        		Drawable draw = fishRightGfx.get(curr.index);
        		draw.setBounds((int)curr.posx-curr.width, (int)curr.posy, (int)curr.posx, (int)curr.posy+curr.height);
            	draw.draw(canvas);
        	}        	
        }
        
        for (int i = 0; i < pthread.crabs.size(); i++) {
        	Crab curr = pthread.crabs.get(i);
        	if (curr.index >= 0) {
	        	Drawable draw = curr.crabGfx.get(curr.aniIndex);
	        	draw.setBounds((int)curr.posx, (int)curr.posy, (int)curr.posx+curr.width, (int)curr.posy+curr.height);
	        	draw.draw(canvas);
        	}
        }
        
        Eel curr = pthread.eel;
        Drawable draw;
        if (curr.index == 0)
        	draw = curr.leftGfx.get(curr.aniIndex);
        else
        	draw = curr.rightGfx.get(curr.aniIndex);
    	draw.setBounds((int)curr.posx, (int)curr.posy, (int)curr.posx+curr.width, (int)curr.posy+curr.height);
    	draw.draw(canvas);
    }
    
    private void hook_and_line(Canvas canvas) {
    	// HOOK AND LINE DRAWING
        synchronized (pthread) {
	        if (pthread.hook.visible) {
	        	tempRect.set((int)pthread.hook.xpos - pthread.hook.width/2, 
	        			(int)pthread.hook.ypos - pthread.hook.height/2, 
	        			(int)(pthread.hook.xpos + pthread.hook.width/2),
	        			(int)(pthread.hook.ypos + pthread.hook.height/2));
	            canvas.drawRect(tempRect, tempLurePaint);
	        }
	        
	        if (pthread.hook.state > 0) {
	        	for(int i = 1; i < pthread.line.line.size(); i++) {
	        		canvas.drawLine((int)pthread.line.line.get(i-1).posx, 
	        				(int)pthread.line.line.get(i-1).posy, 
	        				(int)pthread.line.line.get(i).posx, 
	        				(int)pthread.line.line.get(i).posy, 
		    				tempLinePaint);
	        	}
	        }	  
        }
    }
    
    private void fisher_and_tension_bar(Canvas canvas) {
    	Drawable fishGuy = fisherNormImage;

    	
    	// LINE TENSION BAR        
        if (pthread.hook.state == 3) { // show tension bar
        	
        	
        	double tensratio = pthread.line.tension/pthread.line.maxtension;
        	
        	canvas.save();
            if (tensratio > BAD_TENSION) {
            	tempTensionBadTextPaint.setTextSize(30);
            	txtLayout = new StaticLayout("Fishing Line Tension", tempTensionBadTextPaint, canvasWidth-fisherWidth*3, Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        	} else {
        		tempTensionGoodTextPaint.setTextSize(30);
            	txtLayout = new StaticLayout("Fishing Line Tension", tempTensionGoodTextPaint, canvasWidth-fisherWidth*3, Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        	}        		            
            canvas.translate(3*fisherWidth/2, 1*airHeight/3);
            txtLayout.draw(canvas);
            canvas.restore();
        	
        	canvas.drawLine(3*fisherWidth/2, 
    				2*airHeight/3, 
    				canvasWidth-3*fisherWidth/2, 
    				2*airHeight/3, 
    				tempTensionOutlinePaint);
        	canvas.drawLine(3*fisherWidth/2, 
    				2*airHeight/3, 
    				3*fisherWidth/2, 
    				2*airHeight/3+25, 
    				tempTensionOutlinePaint);
        	canvas.drawLine(3*fisherWidth/2, 
    				2*airHeight/3+25, 
    				canvasWidth-3*fisherWidth/2, 
    				2*airHeight/3+25, 
    				tempTensionOutlinePaint);
        	canvas.drawLine(canvasWidth-3*fisherWidth/2, 
    				2*airHeight/3, 
    				canvasWidth-3*fisherWidth/2, 
    				2*airHeight/3+25, 
    				tempTensionOutlinePaint);
        	

        	double width = canvasWidth - 3*fisherWidth - 2;
        	tempRect.set(3*fisherWidth/2+1, 
    				2*airHeight/3+1, 
    				3*fisherWidth/2 + (int)(tensratio*width),
        			2*airHeight/3+24);
        	
        	if (tensratio > BAD_TENSION) {
        		canvas.drawRect(tempRect, tempTensionBadPaint);
        		fishGuy = fisherPullImage;
        	} else
        		canvas.drawRect(tempRect, tempTensionGoodPaint);
        }
        
        if (pthread.startReel)
        	fishGuy = fisherPullImage;
        
        fishGuy.setBounds(fisherStart, 0, (int)(fisherStart + 1.25*airHeight), airHeight);
        fishGuy.draw(canvas);

    }
    
    private void cast_target(Canvas canvas) {
    	// CAST TARGET DRAWING
        if (pthread.castTarget.visible) {
        	flashTimer = flashTimer + (int)(now-prevTime);          	
        	if (flashTimer > CAST_FLASH_TIME*2)
        		flashTimer = 0;
        
        	tempRect.set(0,
        			airHeight-pthread.castTarget.height/2, 
        			fisherStart, 
        			airHeight + pthread.castTarget.height/2);
        	tempTargetPaint.setAlpha((int)Math.abs(200 * (double)(flashTimer-CAST_FLASH_TIME)/CAST_FLASH_TIME));
            canvas.drawRect(tempRect, tempTargetPaint);
        }
    }
    
    private void menu(Canvas canvas) {
        
        // BASE ICONS FOR MENU, ETC
        
        tempRect.set(fisherStart, airHeight, canvasWidth, canvasHeight);
        canvas.drawRect(tempRect, tempMenuPaint);     
          	
        
        canvas.save();
        tempGoldPaint.setTextSize(40);
        txtLayout = new StaticLayout("$" + pthread.game.goldCoins, tempGoldPaint, menuItemSize, Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        canvas.translate(fishTargetStartx, coinStarty);
        txtLayout.draw(canvas);
        canvas.restore();        
        
        boxImage.setBounds(fishTargetStartx, boxStarty, fishTargetStartx + menuItemSize, boxStarty + menuItemSize);
        boxImage.draw(canvas);
        
        storeImage.setBounds(fishTargetStartx, storeStarty, fishTargetStartx + menuItemSize, storeStarty + 7*menuItemSize/8);
        storeImage.draw(canvas);
    }
    
    // draws the game as it progresses
    private void drawRunning(Canvas canvas) { 
    	now = System.currentTimeMillis();   	
    	
        background(canvas);

        sun_clouds(canvas);
        
        menu(canvas);
        
        cast_target(canvas);
        
        fish(canvas);
        
        hook_and_line(canvas);
        
        fisher_and_tension_bar(canvas);
        
    	prevTime = now;
    }
    
    // draws the inventory 
    private void drawInventory(Canvas canvas) {

        background(canvas);
        sun_clouds(canvas);
        menu(canvas);
        
        // FISH DRAWING
        fish(canvas);
        
        // draw the inventory
        
    	tempRect.set(fisherWidth/2, airHeight/2, canvasWidth-fisherWidth/2, canvasHeight-airHeight/2);
        canvas.drawRect(tempRect, tempMenuPaint);
        
        tempRect.set(3*fisherWidth/4, 3*airHeight/4, canvasWidth/2 + 3*fisherWidth/4, airHeight+airHeight/2);
        canvas.drawRect(tempRect, tempTensionOutlinePaint);
        
        canvas.save();        
        tempWhiteTextPaint.setTextSize(50);        
        txtLayout = new StaticLayout(
        		pthread.game.inventory.get(pthread.game.invIndex).label, 
        		tempWhiteTextPaint, 
        		canvasWidth/2, 
        		Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        canvas.translate(3*fisherWidth/4, 7*airHeight/8);
        txtLayout.draw(canvas);
        canvas.restore();   
        
        
        tempRect.set(canvasWidth/2 + fisherWidth, 3*airHeight/4, canvasWidth - 3*fisherWidth/4, airHeight+airHeight/2);
        canvas.drawRect(tempRect, tempTensionOutlinePaint);
        
        canvas.save();
        tempWhiteTextPaint.setTextSize(50);
        txtLayout = new StaticLayout(
        		String.valueOf(pthread.game.inventory.get(pthread.game.invIndex).count), 
        		tempWhiteTextPaint, 
        		canvasWidth/2 - 7*fisherWidth/4, 
        		Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        canvas.translate(canvasWidth/2 + fisherWidth,  7*airHeight/8);
        txtLayout.draw(canvas);
        canvas.restore();    
        
        tempRect.set(3*fisherWidth/4, 2*airHeight, canvasWidth/2, canvasHeight-airHeight);
        canvas.drawRect(tempRect, tempTensionOutlinePaint);
        
        canvas.save();
        tempWhiteTextPaint.setTextSize(30);
        txtLayout = new StaticLayout(
        		pthread.game.inventory.get(pthread.game.invIndex).description, 
        		tempWhiteTextPaint, 
        		canvasWidth/2-fisherWidth, 
        		Layout.Alignment.ALIGN_NORMAL, 1.3f, 0, false);
        canvas.translate(7*fisherWidth/8, 2*airHeight + airHeight/3);
        txtLayout.draw(canvas);
        canvas.restore();        
        
        
    }
    
    // draws the store
    private void drawStore(Canvas canvas) {

        background(canvas);
        
        menu(canvas);
        
        // FISH DRAWING
        fish(canvas);
        
        // draw the inventory
        
    	tempRect.set(fisherWidth/2, airHeight/2, canvasWidth-fisherWidth/2, canvasHeight-airHeight/2);
        canvas.drawRect(tempRect, tempMenuPaint);
        
        tempRect.set(3*fisherWidth/4, 3*airHeight/4, canvasWidth/2 + 3*fisherWidth/4, airHeight+airHeight/2);
        canvas.drawRect(tempRect, tempTensionOutlinePaint);
        
        canvas.save();        
        tempWhiteTextPaint.setTextSize(50);        
        txtLayout = new StaticLayout(
        		pthread.game.market.marketItems.get(pthread.game.market.index).label, 
        		tempWhiteTextPaint, 
        		canvasWidth/2, 
        		Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        canvas.translate(3*fisherWidth/4, 7*airHeight/8);
        txtLayout.draw(canvas);
        canvas.restore();   
        
        
        tempRect.set(canvasWidth/2 + fisherWidth, 3*airHeight/4, canvasWidth - 3*fisherWidth/4, airHeight+airHeight/2);
        canvas.drawRect(tempRect, tempTensionOutlinePaint);
        
        canvas.save();
        tempGoldPaint.setTextSize(50);
        txtLayout = new StaticLayout(
        		String.valueOf(pthread.game.market.marketItems.get(pthread.game.market.index).cost), 
        		tempGoldPaint, 
        		canvasWidth/2 - 7*fisherWidth/4, 
        		Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        canvas.translate(canvasWidth/2 + fisherWidth,  7*airHeight/8);
        txtLayout.draw(canvas);
        canvas.restore();    
        
        tempRect.set(3*fisherWidth/4, 2*airHeight, canvasWidth/2, canvasHeight-airHeight);
        canvas.drawRect(tempRect, tempTensionOutlinePaint);
        
        canvas.save();
        tempWhiteTextPaint.setTextSize(30);
        txtLayout = new StaticLayout(
        		pthread.game.market.marketItems.get(pthread.game.market.index).description, 
        		tempWhiteTextPaint, 
        		canvasWidth/2-fisherWidth, 
        		Layout.Alignment.ALIGN_NORMAL, 1.3f, 0, false);
        canvas.translate(7*fisherWidth/8, 2*airHeight + airHeight/3);
        txtLayout.draw(canvas);
        canvas.restore(); 
        
        canvas.save();
        tempWhiteTextPaint.setTextSize(50);
        txtLayout = new StaticLayout(
        		"Purchase", 
        		tempWhiteTextPaint, 
        		canvasWidth/2-fisherWidth, 
        		Layout.Alignment.ALIGN_NORMAL, 1.3f, 0, false);
        canvas.translate(25*fisherWidth/8, 2*airHeight + 2*airHeight/3);
        txtLayout.draw(canvas);
        canvas.restore(); 
            	
    }    
    
    // draws the game as it progresses
    private void drawPause(Canvas canvas) { 
        // running all the canvas draw bitmaps for the live play
        //canvas.drawBitmap(mShipFlying[mShipIndex], mJetBoyX, mJetBoyY, null);       
    	tempRect.set(0, 0, canvasWidth, airHeight);
        canvas.drawRect(tempRect, tempAirPaint);
        
        tempRect.set(0, airHeight, canvasWidth, canvasHeight);
        canvas.drawRect(tempRect, tempWaterPaint);
        
        for (int i = 0; i < pthread.currentFish.size(); i++) {
        	Fish curr = pthread.currentFish.get(i);
        	tempRect.set((int)curr.posx,
        			(int)curr.posy, 
        			(int)(curr.posx + curr.width),
        			(int)(curr.posy + curr.height));
        	canvas.drawRect(tempRect, tempFishPaint);
        }
    	
    	 tempRect.set(0, airHeight, canvasWidth, canvasHeight);
         canvas.drawRect(tempRect, tempPausePaint);
    }
    
    private void drawReady(Canvas canvas) {
    	// just draw the initial screen
        //canvas.drawBitmap(mTitleBG, 0, 0, null);
    }

    private void drawPlay(Canvas canvas) {
    	// just draw the instructions screen
       // canvas.drawBitmap(mTitleBG2, 0, 0, null);
    }
    
    private void drawLose(Canvas canvas) {
    	// just draw the lose screen
       // canvas.drawBitmap(mTitleBG2, 0, 0, null);
    }
    
    
    
    // the looping run
    public void run() {
        while (dthreadRun) {
        	
            try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	Canvas c = null;
            
            try {
                c = surfHolder.lockCanvas(null);
                synchronized (surfHolder) {
                	doDraw(c);
                }
            } finally {                
                if (c != null) {
                    surfHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
    
    // sets the state of the thread.. false kills it
    public void setRunning(boolean b) {
        dthreadRun = b;        
        if (b == true) {
        	pthread = new PhysicsThread(ctx);
        	pthread.setRunning(true);
        	pthread.start();        	
        }
        	
        
        if (b == false) {
        	// clean up resources
        	
            // kill the physics thread as well
            boolean retry = true;
            pthread.setRunning(false);
            while (retry) {
                try {
                    pthread.join();                   
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // gets the game state    
    public int getGameState() {
        synchronized (surfHolder) {
            return gameState;
        }
    }
    
    // sets the state of the game
    public void setGameState(int mode) {
        synchronized (surfHolder) {
            setGameState(mode, null);
        }
    }
    
    // sets the state of the game
    public void setGameState(int state, CharSequence message) {

        synchronized (surfHolder) {

            // change state if needed
            if (gameState != state) {
            	gameState = state;
            }

            if (gameState == STATE_PLAY) {
                //Resources res = mContext.getResources();
                //mBackgroundImageFar = BitmapFactory.decodeResource(res, R.drawable.background_a);

                // don't forget to resize the background image
                //mBackgroundImageFar = Bitmap.createScaledBitmap(mBackgroundImageFar, mCanvasWidth * 2, mCanvasHeight, true);

              
            } else if (gameState == STATE_RUNNING) {
            	prevTime = System.currentTimeMillis();
            	flashTimer = 0;
                // When we enter the running state we should clear any old
                // events in the queue
               // mEventQueue.clear();

            }

        }
    }
    
    // when dimensions change
    public void setSurfaceSize(int width, int height) {
        synchronized (surfHolder) {
            canvasWidth = width;
            canvasHeight = height;
            
            airHeight = (int)((1-WATER_PERCENTAGE)*height);
            fisherStart = (int)(NON_FISHER_PERCENTAGE*width);
            fisherWidth = width - fisherStart;
            
            fishTargetStartx = fisherStart + 1*fisherWidth/6;
            menuItemSize = 2*fisherWidth/3;
            coinStarty = airHeight + airHeight/6;
            boxStarty = coinStarty + menuItemSize/4;
            storeStarty = boxStarty + menuItemSize;
            		
            Log.i(getClass().getSimpleName(), "display width: " + String.valueOf(width) + ", display height: " + String.valueOf(height));
            pthread.initialize(width, height, airHeight, fisherStart);
            // Resize all images
            //mBackgroundImageFar = Bitmap.createScaledBitmap(mBackgroundImageFar, width * 2, height, true);          
        }
    }
    
    // will pause the game if it is running
    public void pause_playing() {
        synchronized (surfHolder) {
            if (gameState == STATE_RUNNING) {
                setGameState(STATE_PAUSE);     
                
                pthread.pause_calcs();
            }            
            // pause counters
        }
    }
    
    // will pause the game if it is running
    public void resume_playing() {
        synchronized (surfHolder) {
            if (gameState == STATE_PAUSE) {
                setGameState(STATE_RUNNING);          
                pthread.resume_calcs();
            }
            
            // pause counters
        }
    }    
   
    
    // send message to text view
    private void update_textview(Handler hand, String text, boolean visible) {
    	Message msg = hand.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        if (visible)
        	b.putInt("viz", TextView.VISIBLE);
        else
        	b.putInt("viz", TextView.INVISIBLE);
        msg.setData(b);
        hand.sendMessage(msg);
    }
    
    public void handle_back_press() {
    	if (gameState == STATE_INVENTORY) {
    		setGameState(STATE_RUNNING);
    		pthread.resume_calcs();
        } else if (gameState == STATE_STORE) {
        	setGameState(STATE_RUNNING);
        	pthread.resume_calcs();
        }
    }
    
    // handles all touch inputs
    public void handle_touching(MotionEvent event) {
    	 if (gameState == STATE_START) {    		 
             setGameState(STATE_PLAY);             
             Log.i(getClass().getSimpleName(), "clicked play from start");
             update_textview(txtStatusHandler, "After you hook a fish, tap the fisherman to reel it in. Be careful that the tension bar doesn't go too high, or you might break your line!", true);                         
         } else if (gameState == STATE_PLAY) {
        	 setGameState(STATE_RUNNING);
        	 Log.i(getClass().getSimpleName(), "clicked run from play");
        	 update_textview(txtStatusHandler, "click anywhere on the water surface to cast", true);        
         } else if (gameState == STATE_RUNNING) {
        	 if (event.getX() > fishTargetStartx && event.getY() > boxStarty && event.getY() < (boxStarty + menuItemSize)) {
        		 update_textview(txtStatusHandler, "", false);
        		 setGameState(STATE_INVENTORY);
        		 pthread.pause_calcs();
        	 }
        	 
        	 if (event.getX() > fishTargetStartx && event.getY() > storeStarty && event.getY() < (storeStarty + menuItemSize)) {
        		 update_textview(txtStatusHandler, "", false);
        		 setGameState(STATE_STORE);
        		 pthread.pause_calcs();
        	 }
        	 
        	 if (pthread.castTarget.visible) {
        		 if (pthread.hook.state == 0 && event.getX() < fisherStart) { // do cast
        			if (!pthread.game.has_bait()) {
        				update_textview(txtStatusHandler, "you need bait to fish!\n(go buy it from the market)", true);
        				return;
        			}
        			
        			if (!pthread.game.has_hook()) {
        				update_textview(txtStatusHandler, "you need a hook to fish!\n(go buy it from the market)", true);
        				return;
        			}
        			
        			if (!pthread.game.has_line()) {
        				update_textview(txtStatusHandler, "you need a fishing line to fish!\n(go buy it from the market)", true);
        				return;
        			}
        			
        			update_textview(txtStatusHandler, "", false);
         			    		
        			pthread.castTarget.posx = (int)event.getX(0);
        			Log.i(getClass().getSimpleName(), "click on x = " + pthread.castTarget.posx);
         			pthread.castTarget.visible = false;
         			pthread.hook.visible = true;
         			//update_buttons(1);
         			pthread.hook.ypos = airHeight/3;
         			pthread.hook.xpos = canvasWidth-50;
         			pthread.hook.state = 1;         			
         			
         		} 
        		 
        	 } else if (pthread.hook.state == 2) { // reset cast
      			if (event.getX() < (canvasWidth-fisherStart) && event.getY() < airHeight) { // reset
     				pthread.reel_in();
      			} else if (event.getX() > fisherStart && event.getY() < airHeight) {
      				pthread.reel();
      			}
        	 } else if (pthread.hook.state == 3) {
        		 if (event.getX() > fisherStart && event.getY() < airHeight) { // reset
      				pthread.reel_during_bite();
       			} 
        	 } 
         } else if (gameState == STATE_INVENTORY) {
        	 if (event.getX() < fisherWidth && event.getY() > airHeight && event.getY() < (canvasHeight - airHeight)) {
        		 pthread.game.invIndex = (pthread.game.invIndex - 1)%pthread.game.inventory.size();
        		 if (pthread.game.invIndex == -1)
        			 pthread.game.invIndex = pthread.game.inventory.size()-1;
        	 } else if (event.getX() > fisherStart && event.getY() > airHeight && event.getY() < (canvasHeight - airHeight)) {
        		 pthread.game.invIndex = (pthread.game.invIndex + 1)%pthread.game.inventory.size();
        	 } else if (event.getX() < canvasWidth/2 && event.getY() < airHeight) {
        		 setGameState(STATE_RUNNING);
        		 pthread.resume_calcs();
        	 }
         } else if (gameState == STATE_STORE) {
        	 if (event.getX() < fisherWidth && event.getY() > airHeight && event.getY() < (canvasHeight - airHeight)) {
        		 pthread.game.market.index = (pthread.game.market.index - 1)%pthread.game.market.marketItems.size();
        		 if (pthread.game.market.index == -1)
        			 pthread.game.market.index = pthread.game.market.marketItems.size()-1;
        	 } else if (event.getX() > fisherStart && event.getY() > airHeight && event.getY() < (canvasHeight - airHeight)) {
        		 pthread.game.market.index = (pthread.game.market.index + 1)%pthread.game.market.marketItems.size();
        	 } else if (event.getX() < canvasWidth/2 && event.getY() < airHeight) {
        		 setGameState(STATE_RUNNING);
        		 pthread.resume_calcs();
        	 } else if (event.getX() < (25*fisherWidth/8+canvasWidth/2-fisherWidth) && event.getX() > 25*fisherWidth/8 && 
        			 event.getY() > (2*airHeight + 2*airHeight/3) && event.getY() < (3*airHeight)) {
        		 pthread.purchase_item();
        	 }
         } else if (gameState == STATE_PAUSE ) {      
        	 Log.i(getClass().getSimpleName(), "clicked run from pause");
        	 resume_playing();        	 
        	 Log.i(getClass().getSimpleName(), "returned from resume playing");
        	 update_textview(txtStatusHandler, "", false);   
         } else if (gameState == STATE_LOSE) {
         	
         }
    }

}
