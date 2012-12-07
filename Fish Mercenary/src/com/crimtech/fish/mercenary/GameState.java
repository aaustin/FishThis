package com.crimtech.fish.mercenary;

import java.util.ArrayList;

public class GameState {
	double decayTime = 300;
	double decayFactor = 0.1;
	double buttonincrement;
	
	BaitAndTackleMarket market;
	
	ArrayList<InventoryItem> inventory;
	int invIndex;
	
	int poleIndex;
	int lineIndex;
	int hookIndex;
	int baitIndex;
	
	int goldCoins;
	// add items here
	
	public GameState() {
		market = new BaitAndTackleMarket(0);
	
		buttonincrement = 0.05;
		
		inventory = new ArrayList<InventoryItem>();
		invIndex = 0;
		
		goldCoins = 0;
	}
	
	public void fish_stole_bait() {		
		inventory.get(hookIndex).count = inventory.get(hookIndex).count - 1;
		inventory.get(baitIndex).count = inventory.get(baitIndex).count - 1;
		remove_if_empty();
	}
	
	public void fish_broke_line() {
		inventory.get(lineIndex).count = inventory.get(lineIndex).count - 1;
		inventory.get(hookIndex).count = inventory.get(hookIndex).count - 1;
		inventory.get(baitIndex).count = inventory.get(baitIndex).count - 1;
		remove_if_empty();
	}
	
	private void remove_if_empty() {
		for (int i = 0; i<inventory.size(); i++) {
			if ((inventory.get(i).type == 2 || inventory.get(i).type == 3) && inventory.get(i).count == 0) {
				if (inventory.get(i).type == 2)
					hookIndex = -1;
				else
					baitIndex = -1;
				inventory.remove(i);
				i = i - 1;				
			} else if (inventory.get(i).type == 1 && inventory.get(i).count == 0)
				lineIndex = -1;			
		}
	}
	
	public boolean has_pole() {
		if (poleIndex < 0) {
			for(int i = 0; i < inventory.size(); i++)
				if (inventory.get(i).type == 0) {
					poleIndex = i;
					return true;
				}
			return false;
		} else {
			if (inventory.get(poleIndex).count > 0)
				return true;
			else
				return false;
		}
	}
	
	public boolean has_line() {
		if (lineIndex < 0) {
			for(int i = 0; i < inventory.size(); i++)
				if (inventory.get(i).type == 1) {
					lineIndex = i;
					return true;
				}
			return false;
		} else {
			if (inventory.get(lineIndex).count > 0)
				return true;
			else
				return false;
		}
	}
	
	public boolean has_hook() {
		if (hookIndex < 0) {
			for(int i = 0; i < inventory.size(); i++)
				if (inventory.get(i).type == 2) {
					hookIndex = i;
					return true;
				}
			return false;
		} else {
			if (inventory.get(hookIndex).count > 0)
				return true;
			else
				return false;
		}
	}
	
	public boolean has_bait() {
		if (baitIndex < 0) {
			for(int i = 0; i < inventory.size(); i++)
				if (inventory.get(i).type == 3) {
					baitIndex = i;
					return true;
				}
			return false;
		} else {
			if (inventory.get(baitIndex).count > 0)
				return true;
			else
				return false;
		}
	}
}
