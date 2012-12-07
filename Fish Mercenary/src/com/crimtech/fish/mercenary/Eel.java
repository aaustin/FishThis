package com.crimtech.fish.mercenary;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public class Eel {
	private static final int COAST_TIME = 3000;
	private static double WATER_X_ACCEL = -0.00001;
	private static double WATER_X_VEL = 0.04;
	
	double posx;
	int posy;
	int maxx;
	
	double velx;
	
	int width;
	int height;
	
	ArrayList<Drawable> leftGfx;
	ArrayList<Drawable> rightGfx;
	int aniIndex;
	int frameDelta = 200;
	
	int index;
	// -1 out of action
	//0 swimming left
	// 1  right
	// 2 paused
	
	int counter;
	boolean coasting;
	
	public Eel() {
		
	}
	
	public void init(double posx, int maxx, int posy, int height, int index) {
		this.posx = posx;
		this.posy = posy;
		this.maxx = maxx;
		
		this.height = height;
		width = (int)(2.81*height);
		
		this.index = index;
		
		aniIndex = 0;
		counter = 0;
		velx = WATER_X_VEL;
		coasting = false;
	}
	
	public void increment_time(int deltaT) {
		counter = counter + deltaT;
		if (!coasting) {	
			
			if (counter > frameDelta) {
				counter = 0;
				
				aniIndex = aniIndex+1;
				
				if (aniIndex == leftGfx.size())  {
					coasting = true;
					aniIndex = 0;
				}				
			}
		} else {			
			velx = velx + WATER_X_ACCEL*deltaT;
			
			if (counter > COAST_TIME) {
				velx = WATER_X_VEL;
				counter = 0;
				coasting = false;
			}
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
	// generates a random integer between a min and max
	private int random(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
}
