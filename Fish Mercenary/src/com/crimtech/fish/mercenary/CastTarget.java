package com.crimtech.fish.mercenary;

public class CastTarget {		
	boolean visible;

	int width;
	int height;
	
	int posx;
	
	public CastTarget (int width, int height) {		
		posx = 0;
		
		this.width = width;
		this.height = height;
		
		visible = true;
	}
}
