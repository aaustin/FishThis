package com.crimtech.fish.mercenary;

public class LevelManager {
	int targetFish;
	int numConcurrentFish;
	double percentTargetFish;
	
	int currentMinFishIndex;
	int currentMaxFishIndex;;
	
	int currentLevel;
		
	public LevelManager(int level) {
		currentLevel = level;
		
		set_level_data(level);
	}

	// select from the pre defined indices
	private void set_level_data(int level) {
		switch (level) {
			case 0:				
				targetFish = 0;
				numConcurrentFish = 7;
				percentTargetFish = 0.2;
				
				currentMinFishIndex = 0;
				currentMaxFishIndex = 3;
				break;
			case 1:			
				break;
			case 2:			
				break;
			case 3:		
				break;
			case 4:				
				break;
			default:
				break;
		}
	}
}
