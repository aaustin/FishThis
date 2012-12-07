package com.crimtech.fish.mercenary;

public class Pole {
	double maxUserForce;
	double currentUserForce;
	
	int poleindex;
	
	public Pole(int poleindex) {
		this.poleindex = poleindex;
		currentUserForce = 0;
		select_pole_type(poleindex);
	}
	

	// select from the pre defined indices
	private void select_pole_type(int poleindex) {
		switch (poleindex) {
			case 0:
				maxUserForce = 50;
				break;
			case 1:				
				maxUserForce = 100;
				break;
			case 2:				
				maxUserForce = 150;
				break;
			case 3:				
				maxUserForce = 200;
				break;
			case 4:
				maxUserForce = 400;
			default:
				break;
		}
	}

}
