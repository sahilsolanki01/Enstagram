/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.solanki.enstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        //password : KFuzpL3hgA3j
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("91d480c4f59aae7e71b89129609876c025a64338")
                .clientKey("6771510b30208e8ecf900167cf4118e676031cca")
                .server("http://3.15.217.151:80/parse/")
                .build()
        );


        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
