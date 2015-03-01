package demo.la.battlehack.com;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import demo.la.battlehack.com.helpers.DropboxUtil;

/**
 * Created by ksutardji on 2/28/15.
 */
public class NsaApplication extends Application {

    private static final String PARSE_APP_ID = "wjD2aNqdnjhtgQBVoT6IoW5M6PoCRTjjpsfbEwUq";
    private static final String PARSE_CLIENT_KEY = "yfxVehx2HXZHzZLDwm0gDFQ9jliXoXUTV9bskes4";

    private static NsaApplication gApplication;

    public NsaApplication() { gApplication = this; }

    public static NsaApplication getInstance() {
        return gApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse setup
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

        subscribeForPush();


        // Dropbox init
        Log.e("NsaApplication Dropbox", "init Dropbox");
        DropboxUtil.INSTANCE.auth(this);

    }

    private void subscribeForPush() {
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
//                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

}
