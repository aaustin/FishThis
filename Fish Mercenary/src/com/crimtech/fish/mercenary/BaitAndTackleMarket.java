package com.crimtech.fish.mercenary;

import java.util.ArrayList;

public class BaitAndTackleMarket {
	ArrayList<InventoryItem> marketItems;
	int index;
	
	public BaitAndTackleMarket(int level) {
		marketItems = new ArrayList<InventoryItem>();
		index = 0;
		
		marketItems.add(new InventoryItem(2, new Hook(0, 0, 0), 1, 5, "basic hook", "put some bait on this to start fishing", true));
		marketItems.add(new InventoryItem(3, new Bait(0), 1, 5, "stinky bait", "this bait barely attracts all fish", true));
		
		if (level == 0) {
			marketItems.add(new InventoryItem(1, new FishLine(0), 1, 5, "thin fishing line", "this fishing line is so thin that it might break in your hands", true));
			marketItems.add(new InventoryItem(0, new Pole(1), 1, 40, "thickened wood pole", "this wood pole looks about twice as strong as your current pole", true));
			marketItems.add(new InventoryItem(1, new FishLine(1), 1, 20, "threaded fish line", "you'll need a stronger line with a new pole to catch bigger fish", true));
		} else if (level == 1) {
			marketItems.add(new InventoryItem(1, new FishLine(1), 1, 50, "threaded fish line", "you'll need a stronger line with a new pole to catch bigger fish", true));
			marketItems.add(new InventoryItem(0, new Pole(2), 1, 400, "weak steel pole", "the steel version of the pole enhances the strength compared to the wood", true));
			marketItems.add(new InventoryItem(1, new FishLine(2), 1, 100, "thin steel fish line", "a thin steel fishing line is needed to catch bigger fish", true));
		} else if (level == 2) {
			marketItems.add(new InventoryItem(1, new FishLine(2), 1, 500, "thin steel fish line", "a thin steel fishing line is needed to catch bigger fish", true));
			marketItems.add(new InventoryItem(0, new Pole(3), 1, 3700, "thickened steel pole", "the strongest standard fishing pole available on the market", true));
			marketItems.add(new InventoryItem(1, new FishLine(3), 1, 1300, "would steel fish line", "use this line with a thick steel pole to catch huge fish", true));
		} else if (level == 3) {
			marketItems.add(new InventoryItem(1, new FishLine(3), 1, 5000, "would steel fish line", "use this line with a thick steel pole to catch huge fish", true));
			marketItems.add(new InventoryItem(0, new Pole(4), 1, 39000, "sharpened harpoon gun", "the ultimate fish catching tool", true));
			marketItems.add(new InventoryItem(1, new FishLine(4), 1, 11000, "harpoon gun chain", "to reel in fish with the gun, you will need a chain", true));
		}
				
	}

}
