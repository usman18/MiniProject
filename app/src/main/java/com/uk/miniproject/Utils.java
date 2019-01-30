package com.uk.miniproject;

import android.content.SharedPreferences;
import android.content.Context;


/**
 * Created by usman on 24-01-2019.
 */

public class Utils {

	
	public static boolean setRole(final Context context, final int role) {
		
		if (role != Constants.ADMIN && role != Constants.STUDENT)
			return false;       //edition unsuccessful
		
		SharedPreferences preferences = context.getSharedPreferences(Constants.ROLE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("role",role);
		editor.apply();
		
		return true;
		
	}
	
	
	public static int getRole(Context context) {
		
		SharedPreferences preferences = context.getSharedPreferences(Constants.ROLE, Context.MODE_PRIVATE);
		return preferences.getInt("role",-1);
	}
	

}
