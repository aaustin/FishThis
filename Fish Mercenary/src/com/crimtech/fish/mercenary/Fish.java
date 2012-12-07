package com.crimtech.fish.mercenary;

public class Fish {
	int index;
	double posx;
	double posy;
	double origy;
	double origvelx;
	double origvely;
	double velx;
	double vely;
	
	// 0 swimming left
	// 1 swimming right
	// 2 following bait - face left
	// 3 following bait - face right
	// 4 bitten and pulling away
	// 5 broke free
	
	int state;
	
	int width;
	int height;	
	
	double maxpullforce;	
	double currpullforce;
	int pulltime;
	int wintime;
	
	int tireOutTime;	
	int maxTireOutTime;
	
	int detectionrad;
	int evalperiod;
	int evalelapsed;
	
	int value;
	
	public Fish(double x, double y, double newy, int leftright, int fishindex) {
		state = leftright;
		posx = x;
		posy = y;
		origy = newy;
		currpullforce = 0;
		pulltime = 0;
		tireOutTime = 0;
		
		evalelapsed = 0;
		index = fishindex;
		
		select_fish_type(fishindex);
		
		if (state == 1)
			posx = posx + width;
	}
	
	
	// select from the pre defined indices
	private void select_fish_type(int fishindex) {
		switch (fishindex) {
			case 0:				
				width = 42;
				height = 30;	
				velx = 0.04;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 55;
				value = 5;
				maxTireOutTime = 12000;				
				break;
			case 1:				
				width = 56;
				height = 40;	
				velx = 0.03;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 60;
				value = 7;
				maxTireOutTime = 14000;	
				break;
			case 2:				
				width = 56;
				height = 52;	
				velx = 0.04;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;

				maxpullforce = 70;
				value = 15;
				
				maxTireOutTime = 15000;	
				break;
			case 3:				
				width = 70;
				height = 65;
				velx = 0.015;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 90;				
				value = 25;
				maxTireOutTime = 16000;	
				break;
			case 4:			
				width = 56;
				height = 40;	
				velx = 0.025;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 110;
				value = 45;
				maxTireOutTime = 13000;	
				break;
			case 5:				
				width = 56;
				height = 40;	
				velx = 0.03;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 120;
				value = 53;
				maxTireOutTime = 14000;	
				break;
			case 6:				
				width = 56;
				height = 40;	
				velx = 0.04;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;

				maxpullforce = 140;
				value = 70;
				
				maxTireOutTime = 16000;	
				break;
			case 7:				
				width = 70;
				height = 50;
				velx = 0.015;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 180;				
				value = 90;
				maxTireOutTime = 17000;	
				break;
			case 8:			
				width = 56;
				height = 40;	
				velx = 0.025;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 220;
				value = 470;
				maxTireOutTime = 14000;	
				break;
			case 9:				
				width = 56;
				height = 40;	
				velx = 0.03;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 240;
				value = 590;
				maxTireOutTime = 15000;	
				break;
			case 10:				
				width = 56;
				height = 40;	
				velx = 0.04;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;

				maxpullforce = 280;
				value = 1000;
				
				maxTireOutTime = 16000;	
				break;
			case 11:				
				width = 70;
				height = 50;
				velx = 0.015;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 360;				
				value = 1200;
				maxTireOutTime = 17000;	
				break;
			case 12:			
				width = 56;
				height = 40;	
				velx = 0.025;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 440;
				value = 4900;
				maxTireOutTime = 15000;	
				break;
			case 13:				
				width = 56;
				height = 40;	
				velx = 0.03;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 480;
				value = 6000;
				maxTireOutTime = 16000;	
				break;
			case 14:				
				width = 56;
				height = 40;	
				velx = 0.04;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;

				maxpullforce = 560;
				value = 19000;
				
				maxTireOutTime = 17000;	
				break;
			case 15:				
				width = 70;
				height = 50;
				velx = 0.015;
				vely = velx/4;
				origvelx = velx;
				origvely = vely;
				
				detectionrad = 70;
				evalperiod = 3000;
				
				maxpullforce = 720;				
				value = 24000;
				maxTireOutTime = 18000;	
				break;
			default:
				break;
		}
	}
	
}
