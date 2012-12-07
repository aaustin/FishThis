package com.crimtech.fish.mercenary;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class PhysicsThread extends Thread {

	 private boolean pthreadRun = false;	 
	 
	 // FISH CONTROL CONSTANTS
	 private static double FISH_CHASE_RADIUS = 0.3;
	 private static double WATER_ESCAPE_ACCEL = 0.0001;
	 private static double MAX_ESCAPE_SPEED = 0.16;
	 
	 private static int MIN_TENSION_TIME = 1000;
	 private static int MAX_TENSION_TIME = 2500;
	 private static double TENSION_DECAY_FACTOR = 0.05;
	 private static double MIN_FISH_TENSION = 0.25;
	 private static int TENSION_DECAY_TIME = 100;
	 
	 private static int WIN_THRESHOLD = 4000;
	 
	 // HOOK CONTROL CONSTANTS (all speeds in px/s)	 
	 private static double AIR_X_ACCEL = -0.0001;
	 private static double AIR_Y_ACCEL = 0.0001;
	 private static double LONG_AIR_TRAVEL_TIME = 3000;
	 private static double WATER_Y_ACCEL = 0.00002;
	 private static double WATER_TERM_Y_VEL = 0.02;
	 
	 // REEL RELATED CONSTANTS
	 private static double REEL_SHIFT_X = 0.1;
	 private static double REEL_SHIFT_Y = 0.1;
	 private static int MAX_REEL_COUNT = 100;
	 
	 // level related stuff
	 LevelManager lManager;	 
	 
	 // variables for reel related constants
	 boolean startReel;
	 private int reelCount;
	 private boolean escapeAccel;
	 
	 private boolean startTension;
	 
	 // variables for hook flight control
	 private boolean hookInit;
	 private double xvel;
	 private double yvel;
	 
	 private double xdist;
	 private double ydist;
	 
	 // fish management variables
	 ArrayList<Fish> currentFish;
	 boolean startEscape;
	 int tensionTime;
	 
	 // variables for managing collisions, displaying crap
	 private int screenWidth;
	 private int screenHeight;
	 private int airHeight;
	 private int fisherStart;
	 
	 private long prevTime;	 
	 
	 Eel eel;
	 ArrayList<Crab> crabs;
	 
	 Hook hook;
	 CastTarget castTarget;
	 FishLine line;
	 Bait bait;
	 Pole pole;
	 GameState game;
	 
	 boolean initGame = false;
	 
	 boolean displayInit = false;
	 boolean paused = false;
	 		
	 Context context;
	 
	 public PhysicsThread(Context ctx) {	      		 
		 context = ctx;
 		
	 }	 
	 // one click reel
	 public void reel() {
		 reelCount = reelCount + MAX_REEL_COUNT;
		 startReel = true;		 
	 }
	 
	 // reel during fight
	 public void reel_during_bite() {
		 pole.currentUserForce = Math.min(pole.maxUserForce, pole.currentUserForce + pole.maxUserForce*game.buttonincrement);
	 }
	 
	 // reels the hook all the way in
	 public void reel_in() {
		 reelCount = 100000;
		 startReel = true;		 
	 }
	 
	 // the looping run
	 public void run() {
		 // while running do stuff in this loop...bzzz!
	     while (pthreadRun) {
	    	 //Log.i(getClass().getSimpleName(), "physics thread run loop go");
	    	 if (displayInit && !paused) {
	    		 long now = System.currentTimeMillis();	    		 

	    		 synchronized (this) {
	    			 calculate_hook_position(now);
	    			 //calculate_line_positions(now);
	    			 calculate_fish_positions(now);
	    			 calculate_crabeel_positions(now);
	    		 }
	    		 
	    		 prevTime = now;
	    	 }
	     }
	 }	 	 
	
	 // live update of hook flight
	 private void calculate_hook_position(long now) {
		 int deltaT = (int)(now - prevTime);
		 if (hook.state == 1) {
			 if (!hookInit)
				 init_hook_flight();
			 
			 hook.xpos = hook.xpos - xvel*deltaT;
			 hook.ypos = hook.ypos + yvel*deltaT;					 
		
			 line.line.get(1).posx = hook.xpos;	
			 line.line.get(1).posy = hook.ypos;
			 			 
			 //Log.i(getClass().getSimpleName(), "hookx: " + hook.xpos + ", hooky: " + hook.ypos );
			 
			 xvel = xvel + AIR_X_ACCEL*deltaT;
			 yvel = yvel + AIR_Y_ACCEL*deltaT;
			 
			 if (xvel < 0)
				 xvel = 0;			 
			 
			 if (hook.ypos > airHeight) {
				 xvel = 0;
				 yvel = 0;				 
				 
				 line.line.add(new FishLinePoint(hook.xpos, hook.ypos));
				 startReel = false;
				 hook.state = 2;				
			 }
		 } else if (hook.state == 2) {		
			 if (startReel) {
				 calculate_reel_response();
			 } else {
				 hook.ypos = hook.ypos + yvel*deltaT;
				 
				 if ((hook.ypos + hook.height) > screenHeight)
					 hook.ypos = screenHeight - hook.height;
				 else {				 
					 if (line.line.get(1).posx > hook.xpos)
						 line.line.get(1).posx = line.line.get(1).posx - yvel*deltaT;
					 else
						 line.line.get(1).posx = hook.xpos;
				 }
				 
				 if (yvel < WATER_TERM_Y_VEL)			 
					 yvel = yvel + WATER_Y_ACCEL*deltaT;
				 
				 line.line.get(2).posx = hook.xpos;	
				 line.line.get(2).posy = hook.ypos;
			 }			 
			 
		 } else if (hook.state == 3) { // start the fight		 
			
		 } else if (hook.state == 4) {
			 if (startReel)
				 calculate_hookless_reel_response();
		 }
	 }
	 
	 // calculate hookless reel response
	 private void calculate_hookless_reel_response() {
		 boolean pullOut = false;
		 
		 line.line.get(line.line.size()-1).posx = line.line.get(line.line.size()-1).posx + REEL_SHIFT_X;
		 line.line.get(line.line.size()-1).posy = line.line.get(line.line.size()-1).posy - REEL_SHIFT_Y;
		 
		 double posx = line.line.get(line.line.size()-1).posx;
		 double posy = line.line.get(line.line.size()-1).posy;
		 
		 if (posx  > fisherStart) { 
			 hook.xpos = fisherStart;
			 pullOut = true;
		 }
		 
		 if (posy< airHeight) {
			 posy = airHeight;
			 if (pullOut)
				 reset_line();
	 	 }
		 
		 yvel = 0;
		 reelCount = reelCount - 1;
		 
		 if (reelCount == 0)
			 startReel = false;
	 }
	 
	 // response to a reel push
	 private void calculate_reel_response() {
		 boolean pullOut = false;
		 
		 if (line.line.size() > 2) {
			 line.line.get(1).posx = line.line.get(1).posx + 2*REEL_SHIFT_X;
			 
			 Log.i(getClass().getSimpleName(), "angle1: " + angle(line.line.get(0), line.line.get(1)) + ", angle2: " + angle(line.line.get(0), line.line.get(2)) );
			 
			 if (angle(line.line.get(0), line.line.get(1)) > angle(line.line.get(0), line.line.get(2))) {
				 double lineShift = ((double)(airHeight-line.line.get(0).posy)/Math.tan(angle(line.line.get(0), line.line.get(2))));
				 line.line.get(1).posx = line.line.get(0).posx 
						 	- (int)lineShift;
				 Log.i(getClass().getSimpleName(), "lineShift: " + lineShift );			 
			 }
		 }
			 
		 hook.xpos = hook.xpos + REEL_SHIFT_X;
		 hook.ypos = hook.ypos - REEL_SHIFT_Y;
		 
		 if ((hook.xpos + hook.width/2) > fisherStart) { 
			 hook.xpos = fisherStart - hook.width;
			 pullOut = true;
		 }
		 
		 if ((hook.ypos - hook.height/2)< airHeight) {
			 hook.ypos = airHeight;
			 if (pullOut)
				 reset_line();
	 	 }
		 line.line.get(line.line.size()-1).posx = hook.xpos;	
		 line.line.get(line.line.size()-1).posy = hook.ypos;
		 
		 yvel = 0;
		 reelCount = reelCount - 1;
		 
		 if (reelCount == 0)
			 startReel = false;
	 }
	 
	 // calculate the angle between two points
	 private double angle(FishLinePoint pnt1, FishLinePoint pnt2) {
		 return Math.atan((double)(pnt2.posy-pnt1.posy)/(pnt1.posx-pnt2.posx));
	 }
	 
	 // reset the line
	 private void reset_line() {
		 hook.state = 0;
		 hook.visible = false;
		 castTarget.visible = true;
		 
		 reelCount = 0;
		 startReel = false;
		 
		 hookInit = false;
	 }
	 
	 // init hook flight
	 private void init_hook_flight() {		 
		 line = new FishLine(0);
		 
		 line.line.add(new FishLinePoint(fisherStart, airHeight*0.1));		 
		 line.line.add(new FishLinePoint(hook.xpos, hook.ypos));
				 
		 xdist = hook.xpos + hook.width - castTarget.posx;
		 ydist = airHeight - hook.ypos;
		 
		 double airTravel = LONG_AIR_TRAVEL_TIME*xdist/screenWidth;		 

		 xvel = (xdist - AIR_X_ACCEL*airTravel*airTravel/2)/airTravel;
		 yvel = (ydist - AIR_Y_ACCEL*airTravel*airTravel/2)/airTravel;
		 
		 Log.i(getClass().getSimpleName(), "xvel: " + xvel + ", yvel: " + yvel);
		 Log.i(getClass().getSimpleName(), "xaccel: " + AIR_X_ACCEL + ", yaccel: " + AIR_Y_ACCEL);
		 
		 hookInit = true;
	 }
	 
	 // calculate the positions of fish	 
	 private void calculate_fish_positions(long now) {
		 int deltaT = (int)(now - prevTime);
		 
		 for(int i = 0; i < currentFish.size(); i++) {
			 Fish curr = currentFish.get(i);			 
			 
			 if (in_range_of_hook(curr)) {
				 
				 if (curr.state == 0) {  // fish first detected	
					 if (curr.posx > hook.xpos) {// no need to flip
						 curr.state = 2;		
					 } else { // either back up or flip
						 curr.state = 3;							 
					 }
					 curr.evalelapsed = 0;
				 } else if (curr.state == 1) { // fish first detected				 
					 if (curr.posx < hook.xpos) {// no need to flip
						 curr.state = 3;								 
					 } else { // either back up or flip
						 curr.state = 2;
					 }
					 curr.evalelapsed = 0;
				 } else if (curr.state == 2 || curr.state == 3) { // adjust position for bait
					 curr.evalelapsed = curr.evalelapsed + deltaT;					 
					 if (curr.evalelapsed > curr.evalperiod) { // BITE BITE BITE
						 Log.i(getClass().getSimpleName(), "BITE");
						 curr.state = 4;
						 curr.posy = hook.ypos;
						 curr.posx = hook.xpos;
						 curr.currpullforce = 0.1;
						 curr.pulltime = 0;
						 curr.tireOutTime = 0;
						 curr.wintime = 0;
						 tensionTime = random(MIN_TENSION_TIME, MAX_TENSION_TIME);
						 pole.currentUserForce = 0.1;
						 line.tension = 0;
						 hook.state = 3;
						 startEscape = false;		
						 startTension = true;
						 Log.i(getClass().getSimpleName(), "finished setting up bite");
					 } else { // chase hook
						 chase_hook(curr, deltaT);
					 }					 
				 } else if (curr.state == 4) { // bitten and pulling
					 
					 if (startTension) // pull the line out straight
						 calculate_line_pull_by_fish();
					 
					 handle_bite_mechanics(curr, deltaT);
					 
				 } 
			 } else if (curr.state == 5) { // broke free
					 if (startEscape)
						 fish_escape_response(curr, deltaT);
			 } else {
				 if (curr.state == 2) // reset if lost interest
					 curr.state = 1;
				 else if (curr.state == 3)
					 curr.state = 0;
				
				 
				 if (curr.state == 0) {
					 curr.posx = curr.posx - curr.velx*deltaT;
					 //curr.posy = curr.posy + curr.vely*deltaT;
					 	 
					 if (curr.posx < 0) { // turn around
						 curr.posx = curr.width;
						 curr.origy = pick_y_pos();
						 curr.state = 1;
					 }
				 } else if (curr.state == 1) {
					 curr.posx = curr.posx + curr.velx*deltaT;
					 //curr.posy = curr.posy + curr.vely*deltaT;
					 
					 if (curr.posx + curr.width > fisherStart) { // turn around
						 curr.posx = fisherStart - curr.width;
						 curr.origy = pick_y_pos();
						 curr.state = 0;
					 }
				 }
				 
				 if (curr.posy < curr.origy - (FISH_CHASE_RADIUS*curr.detectionrad/4))
					 curr.posy = curr.posy + curr.vely*deltaT;
				 else if (curr.posy > (curr.origy + FISH_CHASE_RADIUS*curr.detectionrad/4))
					 curr.posy = curr.posy - curr.vely*deltaT;
			 }
		 }		 
	 }
	 
	 private void calculate_line_pull_by_fish() {
		 line.line.get(1).posx = line.line.get(1).posx + REEL_SHIFT_X;		 		 

		 //Log.i(getClass().getSimpleName(), "shifting line");
		
		 if (angle(line.line.get(0), line.line.get(1)) > angle(line.line.get(0), line.line.get(2))) {
			 Log.i(getClass().getSimpleName(), "line finished shifting");
			 line.line.remove(1);
			 startTension = false;			 
		 }
	 }
	 
	 // handles the mechanics if a fish is biting
	 private void handle_bite_mechanics(Fish curr, int deltaT) {
		 boolean pullOut = false;		 
		 
		 line.tension = curr.currpullforce + pole.currentUserForce;
		 
		 pole.currentUserForce = Math.max(0, 
				 pole.currentUserForce - pole.currentUserForce * game.decayFactor * deltaT / (double)game.decayTime);		 
		 
		 double workingAngle = angle(line.line.get(0), line.line.get(line.line.size()-1));
		 
		 double ymult = Math.sin(workingAngle);
		 double xmult = Math.cos(workingAngle);

		 //Log.i(getClass().getSimpleName(), "userF: " + game.currentUserForce + ", fishF: " + curr.currpullforce);

		 
		 double vel = curr.velx;
		 
		 if (startTension) { // pulling the line tight
			 
			 curr.posy = curr.posy + 3*ymult*vel*deltaT;
			 curr.posx = curr.posx - 3*xmult*vel*deltaT;
			 
			 //Log.i(getClass().getSimpleName(), "ypos: " + curr.posy + ", xpos: " + curr.posx);
			 
			 
		 } else { // pulling against user
			 if (line.tension > line.maxtension ) {

				 Log.i(getClass().getSimpleName(), "fish broke free!!" );
				 
				 game.fish_broke_line();
				 
				 curr.state = 5;
				 		 
				 reel_in();
				 hook.state = 4;
				 startEscape = true;
				 escapeAccel = true;
				 
				 return;
			 } else if (curr.wintime > WIN_THRESHOLD) {
				 Log.i(getClass().getSimpleName(), "fish broke free!!" );		
				 
				 game.fish_stole_bait();				 
				 
				 curr.state = 5;				 		 
				 reel_in();
				 hook.state = 4;
				 startEscape = true;
				 escapeAccel = true;				 
				 return;
			 }
			 
			 double mult;
			 if (curr.currpullforce > pole.currentUserForce) {
				 mult = -1 * Math.min(1, (curr.currpullforce - pole.currentUserForce)/curr.currpullforce);
				 curr.wintime = curr.wintime + deltaT;
			 } else {
				 curr.wintime = 0;
				 mult = Math.min(1, (pole.currentUserForce - curr.currpullforce)/pole.currentUserForce);
			 }
			 
			 curr.posx = curr.posx + 4*mult*xmult*vel*deltaT;
			 curr.posy = curr.posy - 4*mult*ymult*vel*deltaT;		 			 
			 

			 curr.pulltime = curr.pulltime + deltaT;
			 curr.tireOutTime = curr.tireOutTime + deltaT;
			 if (curr.pulltime < MAX_TENSION_TIME) { // 
				 curr.currpullforce = MIN_FISH_TENSION*curr.maxpullforce 
						 + (curr.maxpullforce - MIN_FISH_TENSION*curr.maxpullforce) * ((double)curr.pulltime)/MAX_TENSION_TIME;
			 } else {
				 curr.currpullforce = Math.max(
						 MIN_FISH_TENSION*curr.maxpullforce-1, 
						 curr.currpullforce - curr.maxpullforce * TENSION_DECAY_FACTOR * deltaT / (double)TENSION_DECAY_TIME);
				 if (curr.currpullforce < MIN_FISH_TENSION*curr.maxpullforce && curr.tireOutTime < curr.maxTireOutTime) {
					 curr.pulltime = 0;
					 tensionTime = random(MIN_TENSION_TIME, MAX_TENSION_TIME);
				 }
			 }			 
		 }
		 
		 if (curr.posy > screenHeight)
			 	curr.posy = screenHeight;
		 if (curr.posy < airHeight) {
				 curr.posx = curr.posx + (airHeight - curr.posy);
				 curr.posy = airHeight;
		 }
		 if (curr.posx < 0)
			 curr.posx = 0;
		 
		 hook.xpos = curr.posx;
		 hook.ypos = curr.posy;			 
		 line.line.get(line.line.size()-1).posx = curr.posx;
		 line.line.get(line.line.size()-1).posy = curr.posy;
		 
		 if ((hook.xpos + hook.width/2) > fisherStart) { 
			 hook.xpos = fisherStart - hook.width;
			 pullOut = true;
		 }	
		 
		 if ((hook.ypos - hook.height/2)< airHeight) {
			 hook.ypos = airHeight;
			 if (pullOut) {
				 game.goldCoins = game.goldCoins + curr.value;
				 reset_line();
				 curr.posx = random(0, fisherStart); 
				 curr.posy = pick_y_pos(); 
				 curr.state = random(0,1); 
			 }
	 	 }	
		 
		 //Log.i(getClass().getSimpleName(), "ypos: " + curr.posy + ", xpos: " + curr.posx);
	 }
	 
	 // calculates the distance between the fish and the hook
	 private double fish_distance(Fish curr) {
		 return Math.sqrt(Math.pow(curr.posx - hook.xpos, 2) + Math.pow(curr.posy - hook.ypos, 2));
	 }
	 
	 // will tell if the fish is in range of the hook
	 private boolean in_range_of_hook(Fish curr) {
		 if (hook.state == 3 && curr.state < 4 || hook.state == 4 || !hook.visible)
			 return false;
		 
		 double distance = Math.sqrt(Math.pow(curr.posx - hook.xpos, 2) + Math.pow(curr.posy - hook.ypos, 2));
		 	 
		 if (distance < curr.detectionrad)
			 return true;
		 else
			 return false;
	 }
	 
	 // fish chase hook
	 private void chase_hook(Fish curr, int deltaT) {
		 if (fish_distance(curr) > FISH_CHASE_RADIUS*curr.detectionrad) {		 
			 if (curr.posy > hook.ypos)
				 curr.posy = curr.posy - 3*curr.vely*deltaT;
			 else
				 curr.posy = curr.posy + 3*curr.vely*deltaT;
			 
			 if (curr.posx > (hook.xpos + FISH_CHASE_RADIUS*curr.detectionrad/2))
				 curr.posx = curr.posx - 2*curr.velx*deltaT;
			 else if (curr.posx > hook.xpos)
				 curr.posx = curr.posx + 2*curr.velx*deltaT;
			 else if (curr.posx < (hook.xpos - FISH_CHASE_RADIUS*curr.detectionrad/2)) 
				 curr.posx = curr.posx + 2*curr.velx*deltaT;
			 else if (curr.posx < hook.xpos)
				 curr.posx = curr.posx - 2*curr.velx*deltaT;
			 
			 if (curr.state == 2) 
				 if (curr.posx > hook.xpos) // no need to flip
					 curr.state = 2;		
				 else  // either back up or flip
					 curr.state = 3;							 
			 else if (curr.state == 3)			 
				 if (curr.posx < hook.xpos)// no need to flip
					 curr.state = 3;								 
				 else // either back up or flip
					 curr.state = 2;
		 }
	 }
	 
	 // bit off the bait and escaped
	 private void fish_escape_response(Fish curr, int deltaT) {		 
		 curr.posx = curr.posx - curr.velx*deltaT;
		 hook.xpos = curr.posx;
		 
		 Log.i(getClass().getSimpleName(), "fish escape called, velx: " + curr.velx );		
		 
		 if (curr.posx < 0)
			 curr.posx = 0;
		 
		 if (escapeAccel) {	 
			 if (curr.velx > MAX_ESCAPE_SPEED) {
				 escapeAccel = false;
				 Log.i(getClass().getSimpleName(), "max escape speed reached, velx: " + curr.velx );		

			 } else
				 curr.velx = curr.velx + deltaT*WATER_ESCAPE_ACCEL;
		 } else {

			 Log.i(getClass().getSimpleName(), "lowering velocity, velx: " + curr.velx );		

			 if (curr.velx > curr.origvelx)
				 curr.velx = curr.velx - deltaT*WATER_ESCAPE_ACCEL;
			 else {

				 Log.i(getClass().getSimpleName(), "hit min velocity, velx: " + curr.velx );
				 hook.visible = false;		
				 curr.velx = curr.origvelx;
				 curr.state = 1;
				 startEscape = false;
			 }
		 }	
	 }
	  
	 // move the crabs around
	 private void calculate_crabeel_positions(long now) {
		 for(int i = 0; i < crabs.size(); i++) {
			 if (crabs.get(i).index >= 0)
				 crabs.get(i).increment_time((int)(now-prevTime));
		 }
		 if (eel != null)
			 eel.increment_time((int)(now-prevTime));
	 }
	 
	 // handles the purchase of an item for the market
	 public void purchase_item() {
		 InventoryItem targetPurchase = game.market.marketItems.get(game.market.index);
		 		 
		 if (game.goldCoins < targetPurchase.cost) {
			 // bold current gold coin count
		 } else {
			 if (targetPurchase.type <= 1) {
				 if (pole.poleindex == line.index) { // buy the first item in the level transition
					 if(targetPurchase.type == 1) {
						 if(line.index == ((FishLine)targetPurchase.link).index) {
							game.inventory.get(game.poleIndex).count += 1;
							game.goldCoins = game.goldCoins - targetPurchase.cost;
							return;
						 }
						 game.inventory.add(game.market.marketItems.get(game.market.index));
					 } else {
						 game.inventory.add(game.market.marketItems.remove(game.market.index));
					 }					 
					 game.inventory.get(game.inventory.size()-1).count = 1;
					 if (game.market.index == game.market.marketItems.size())
					 	game.market.index = 0;
					 game.goldCoins = game.goldCoins - targetPurchase.cost;
				 } else {					 
					 if(targetPurchase.type == 1) {
						 game.inventory.add(game.market.marketItems.get(game.market.index));						 
					 } else {
						 game.inventory.add(game.market.marketItems.remove(game.market.index));
					 }
					 
					 game.inventory.get(game.inventory.size()-1).count = 1;
					 
					 boolean deleteOldPole = false;
					 boolean deleteOldLine = false;
					 for(int i = 0; i < game.inventory.size(); i++) {
						 if (game.inventory.get(i).type == 0 && !deleteOldPole) {
							 game.inventory.remove(i);
							 i = i - 1;
							 deleteOldPole = true;
						 } else if (deleteOldPole) {
							 pole = (Pole)game.inventory.get(i).link;
							 game.poleIndex = i;
						 }
						 if (game.inventory.get(i).type == 1 && !deleteOldLine) {
							 game.inventory.remove(i);
							 i = i - 1;
							 deleteOldLine = true;
						 } else if (deleteOldLine) {
							 line = (FishLine)game.inventory.get(i).link;
							 game.lineIndex = i;
						 }
					 }

					 game.goldCoins = game.goldCoins - targetPurchase.cost;
					 // UPGRADE LEVEL OF FISH
				 }
			 } else {
				 if (targetPurchase.type == 2) {
					 for(int i = 0; i < game.inventory.size(); i++) {
						 if (game.inventory.get(i).type == 2) {
							 if(((Hook)game.inventory.get(i).link).index == ((Hook)targetPurchase.link).index) {
								 game.inventory.get(i).count += 1;
								 game.goldCoins = game.goldCoins - targetPurchase.cost;
								 return;
							 }
						 }
					 }
				 } else {
					 for(int i = 0; i < game.inventory.size(); i++) {
						 if (game.inventory.get(i).type == 3) {
							 if(((Bait)game.inventory.get(i).link).index == ((Bait)targetPurchase.link).index) {
								 game.inventory.get(i).count += 1;
								 game.goldCoins = game.goldCoins - targetPurchase.cost;
								 return;
							 }						 
						 }
					 }
				 }
				 game.inventory.add(game.market.marketItems.get(game.market.index));
				 game.inventory.get(game.inventory.size()-1).count = 1;				 

				 game.goldCoins = game.goldCoins - targetPurchase.cost;
			 }
		 }
	 }
	 
	 // initalize some stuff
	 private void populate_initial_positions() {
		 long now = System.currentTimeMillis();		
		 
		 pole = new Pole(0);
		 hook = new Hook(0, 0, 0);
		 castTarget = new CastTarget(0, 30);
		 line = new FishLine(0);		 
		 bait = new Bait(0);
		 
		 lManager = new LevelManager(0);
			 
		 init_crabs();
		 init_eel();
		 
		 game = new GameState();
		 game.inventory.add(new InventoryItem(0, pole, 1, -1, "weak wood pole", "this pole and reel system can barely lift the smallest of fish", true));
		 game.inventory.add(new InventoryItem(1, line, 4, -1, "thin fishing line", "this fishing line is so thin that it might break in your hands", true));
		 game.inventory.add(new InventoryItem(2, hook, 5, -1,"basic hook", "put some bait on this to start fishing", true));
		 game.inventory.add(new InventoryItem(3, bait, 5, -1, "stinky bait", "this bait barely attracts all fish", true));
		 game.poleIndex = 0;
		 game.lineIndex = 1;
		 game.hookIndex = 2;
		 game.baitIndex = 3;
		 
		 // hook needs to be initialized
		 hookInit = false;
		 
		 // initialize fish locs
		 currentFish = new ArrayList<Fish>();
		 int numTargs = (int)(lManager.numConcurrentFish*lManager.percentTargetFish);
		 for(int i = 0; i < lManager.numConcurrentFish; i++) {
			 if (i < numTargs) {
				 currentFish.add(new Fish(
						 random(0, fisherStart), 
						 pick_y_pos(), 
						 pick_y_pos(),
						 random(0,1), 
						 lManager.targetFish));
			 } else {
				 int fishInd = random(lManager.currentMinFishIndex,lManager.currentMaxFishIndex);
				 if (fishInd == lManager.targetFish)
					 fishInd = fishInd + 1;
				 currentFish.add(new Fish(
						 random(0, fisherStart), 
						 pick_y_pos(), 
						 pick_y_pos(),
						 random(0,1), 
						 fishInd));
			 }
		 }
		 
		 // reel init
		 reelCount = 0;
		 
		 initGame = true;
		 
		 prevTime = now;
	 }
	 
	 private void init_eel() {
		 eel = new Eel();
		 eel.init(random(0,fisherStart), fisherStart-(int)(2.81*50),(int)pick_y_pos(), 50, random(0,1));
		 eel.leftGfx = new ArrayList<Drawable>();
		 eel.leftGfx.add(context.getResources().getDrawable(R.drawable.green4thirdleft));
		 eel.leftGfx.add(context.getResources().getDrawable(R.drawable.green4secondleft));
		 eel.leftGfx.add(context.getResources().getDrawable(R.drawable.green4firstleft));
		 eel.leftGfx.add(context.getResources().getDrawable(R.drawable.green4thirdleft));
		 
		 eel.rightGfx = new ArrayList<Drawable>();
		 eel.rightGfx.add(context.getResources().getDrawable(R.drawable.green4thirdright));
		 eel.rightGfx.add(context.getResources().getDrawable(R.drawable.green4secondright));
		 eel.rightGfx.add(context.getResources().getDrawable(R.drawable.green4firstright));
		 eel.rightGfx.add(context.getResources().getDrawable(R.drawable.green4thirdright));
	 }
	 
	 // initialize the random crabs
	 private void init_crabs() {
		 crabs = new ArrayList<Crab>();
		 
		 crabs.add(new Crab());
		 Crab curr = crabs.get(0);		 
		 curr.crabGfx = new ArrayList<Drawable>();
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.blue3left));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.blue3center));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.blue3right));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.blue3center));
		 curr.init(random(0,fisherStart), fisherStart-40, screenHeight-(int)(0.4*airHeight), 45, random(-1,1));
		 
		 crabs.add(new Crab());
		 curr = crabs.get(1);
		 curr.crabGfx = new ArrayList<Drawable>();
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.green3left));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.green3center));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.green3right));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.green3center));
		 curr.init(random(0,fisherStart), fisherStart-30, screenHeight-(int)(0.3*airHeight), 30, random(-1,1));
		 
		 crabs.add(new Crab());
		 curr = crabs.get(2);
		 curr.crabGfx = new ArrayList<Drawable>();
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.orange3left));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.orange3center));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.orange3right));
	     curr.crabGfx.add(context.getResources().getDrawable(R.drawable.orange3center));
	     curr.init(random(0,fisherStart), fisherStart-50, screenHeight-(int)(0.47*airHeight), 50, random(-1,1));
	     
	     crabs.add(new Crab());
		 curr = crabs.get(3);
		 curr.crabGfx = new ArrayList<Drawable>();
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.yellow3left));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.yellow3center));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.yellow3right));
		 curr.crabGfx.add(context.getResources().getDrawable(R.drawable.yellow3center));
		 curr.init(random(0,fisherStart), fisherStart-35, screenHeight-(int)(0.4*airHeight), 35, random(-1,1));
	 }
	 
	 
	 // pick y position for fish
	 private double pick_y_pos() {
		 return random(airHeight+50, screenHeight-100);
	 }
	 
	 // sets the width and height of the screen for dynamic drawing
	 public void initialize(int width, int height, int airHeight, int fisherStart) {
		 screenWidth = width;
		 screenHeight = height;
		 this.airHeight = airHeight;
		 this.fisherStart = fisherStart;
		 
		 // fill the screen with the starting position of all objects
		 if (!initGame)
			 populate_initial_positions();
		 
		 displayInit = true;
	 }
	 
	 // pauses the thread to not waste resources while the user hits pause
	 public void pause_calcs() {		 
		 paused = true;
	 }	 	 
	 
	 public void resume_calcs() {
		 paused = false;
	 }
	
	 // sets the current state of the thread. Passing false will kill it
	 public void setRunning(boolean b) {
		 pthreadRun = b;        
        
		 if (b == false) {
			 // clean up resources       	
            
		 }
	 }

	 // generates a random integer between a min and max
	 private int random(int min, int max) {
		 return min + (int)(Math.random() * ((max - min) + 1));
	 }
	 
}

