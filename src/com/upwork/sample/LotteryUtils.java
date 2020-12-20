package com.upwork.sample;

public class LotteryUtils {
	
	final static int MIN_VALUE = 1;
	final static int MAX_VALUE = 50;
	
	public static boolean inRange(int x, int min, int max) {
	    return ((x-min)*(x-max) <= 0);
	}

}
