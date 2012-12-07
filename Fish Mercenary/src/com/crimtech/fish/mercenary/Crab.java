package com.crimtech.fish.mercenary;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;


public class Crab {
	private static final int MAX_WALK_TIME = 10000;
	private static final int MIN_WALK_TIME = 5000;
	private static final int MIN_PAUSE_TIME = 2000;
	private static final int MAX_PAUSE_TIME = 5000;
	
	double posx;
	int posy;
	int maxx;
	
	int width;
	int height;
	
	ArrayList<Drawable> crabGfx;
	int aniIndex;
	int frameDelta = 100;
	int frameCounter;
	
	double velx;
	
	int index;
	// -1 out of action
	//0 swimming left
	// 1  right
	// 2 paused
	
	int walkTime;
	int pauseTime;
	int counter;
	

	
	public Crab() {
		velx = ((double)random(1,3))/100;
	}
	
	public void init(double posx, int maxx, int posy, int height, int index) {
		this.posx = posx;
		this.posy = posy;
		this.maxx = maxx;
		
		this.height = height;
		width = (int)(0.86*height);
		
		this.index = index;
		
		aniIndex = 1;
		walkTime = random(MIN_WALK_TIME, MAX_WALK_TIME);
		pauseTime = 0;
		counter = 0;
	}
	
	public void increment_time(int deltaT) {
		if (index == 2) {
			aniIndex = 1;
			counter = counter + deltaT;
			if (counter > pauseTime) {
				this.index = random(0,1);
				counter = 0;
				walkTime = random(MIN_WALK_TIME, MAX_WALK_TIME);
				frameCounter = 0;
			}
		} else if (index <= 1) {			
			frameCounter = frameCounter + deltaT;
			if (frameCounter > frameDelta) {
				aniIndex = (aniIndex+1)%crabGfx.size();
				frameCounter = 0;
			}
			
			counter = counter + deltaT;
			if (counter > walkTime) {
				this.index = 2;
				counter = 0;
				pauseTime = random(MIN_PAUSE_TIME, MAX_PAUSE_TIME);
			}
			
			if (index == 0) {
				if (posx < 0) {
					index = 1;
				}
				posx = posx - deltaT*velx;
			} else if (index == 1) {
				if (posx > maxx) {
					index = 0;
				}
				posx = posx + deltaT*velx;				
			}
		}
	}
	
	// generates a random integer between a min and max
	 private int random(int min, int max) {
		 return min + (int)(Math.random() * ((max - min) + 1));
	 }
}
