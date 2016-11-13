package com.android.keyboard;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application {
	
	@Override
	public void onCreate() { 
		super.onCreate();
		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);

		// Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(getString(R.string.parse_client_key))
                .server(getString(R.string.parse_server))
                .build()
        );
//		ParseUser.enableAutomaticUser();
//		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
//		ParseACL.setDefaultACL(defaultACL, true);
	}
}
