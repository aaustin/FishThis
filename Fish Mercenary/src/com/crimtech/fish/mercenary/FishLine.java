package com.crimtech.fish.mercenary;

import java.util.ArrayList;

public class FishLine {
	double maxtension;
	double tension;
	ArrayList<FishLinePoint> line;	
	int index;
	
	public FishLine(int index) {		
		tension = 0;
		this.index = index;
		line = new ArrayList<FishLinePoint>();
		select_line_type(index);
	}
	
	// select from the pre defined indices
	private void select_line_type(int lineindex) {
		switch (lineindex) {
			case 0:
				maxtension = 100;				
				break;
			case 1:
				maxtension = 200;
				break;
			case 2:
				maxtension = 395;
				break;
			case 3:
				maxtension = 780;
				break;
			case 4:
				maxtension = 1200;
				break;
			default:
				break;
		}
	}
}
