package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application {
	
	@Override
	public void onCreate() { 
		super.onCreate();
	    Parse.initialize(this, 
	    	"So08UwtShKeyfByco1lA7KIDC8VSFJSnw7gzF3De",
	    	"KBDsj0hMa9q5snYyzOL03Vwn3nBZdVfFTsh7nQto");
	}
}
