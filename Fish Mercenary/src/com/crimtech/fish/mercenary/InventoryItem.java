package com.crimtech.fish.mercenary;

public class InventoryItem {
	String label;
	String description;
	
	int count;
	int cost;
	boolean disabled;
	
	// 0 pole and reel
	// 1 line
	// 2 lure
	// 3 bait
	
	int type;
	
	Object link;
	
	public InventoryItem(int type, Object link, int count, int cost, String title, String desc, boolean disabled) {
		this.type = type;
		this.link = link;
		
		this.cost = cost;
		
		this.count = count;
		this.disabled = disabled;
		
		label = title;
		description = desc;
	}
}
