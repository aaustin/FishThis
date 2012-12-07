package com.crimtech.fish.mercenary;

public class Bait {
	int index;
	public Bait(int baitindex) {
		index = baitindex;
		select_bait_type(baitindex);
	}
	
	// select from the pre defined indices
	private void select_bait_type(int baitindex) {
		switch (baitindex) {
			case 0:
							
				break;
			default:
				break;
		}
	}

}
