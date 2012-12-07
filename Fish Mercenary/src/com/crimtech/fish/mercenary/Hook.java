package com.crimtech.fish.mercenary;

public class Hook {
	boolean visible;
	double xpos;
	double ypos;
	// 0 unlaunched
	// 1 in air
	// 2 sinking
	// 3 hooked
	// 4 broken
	int state;
	
	double weight;
	
	int index;
	
	int width;
	int height;

	public Hook(double x, double y, int hookindex) {
		state = 0;		
		
		xpos = x;
		ypos = y;
		
		visible = false;
		
		index = hookindex;
		
		select_hook_type(hookindex);
	}
	
	
	// select from the pre defined indices
	private void select_hook_type(int hookindex) {
		switch (hookindex) {
			case 0:
				width = 25;
				height = 25;
				weight = 1;				
				break;
			default:
				break;
		}
	}
}
