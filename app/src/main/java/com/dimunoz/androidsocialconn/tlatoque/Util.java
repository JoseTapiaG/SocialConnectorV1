package com.dimunoz.androidsocialconn.tlatoque;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 25/11/12
 * Time: 09:11 PM
 * Edited by Diego Munoz
 * Date: 15/09/15
 */
public class Util {

	private static boolean[] carouselPaused = new boolean[]{false, false, false, false, false};
    public static final int PAUSED_ZOOM = 3;
	public static final int PAUSED_LOADING = 4;

	public static List<String> permissions = new ArrayList<>();

	public static void setPaused(int flag, boolean paused) {
		carouselPaused[flag] = paused;
	}

	public static boolean isPaused() {
		for (boolean paused : carouselPaused) {
			if (paused) {
				return true;
			}
		}
		return false;
	}
}
