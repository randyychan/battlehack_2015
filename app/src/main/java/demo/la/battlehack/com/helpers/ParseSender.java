package demo.la.battlehack.com.helpers;

import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ksutardji on 2/28/15.
 */
public class ParseSender {

    public static ParsePush sendStart() {

        Log.e("PUSH Start", "Creating New Push");
        ParsePush push = new ParsePush();
        // specify who to send push to
        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", DataStore.recipient);

        // Find devices associated with the user
        ParseQuery deviceQuery = ParseInstallation.getQuery();
        deviceQuery.whereEqualTo("username", DataStore.recipient);
        push.setQuery(deviceQuery);


        try {
            // set up data
            JSONObject data = new JSONObject();
            data.put(Constants.SENDER, DataStore.sender);
            data.put(Constants.ACTION, Constants.ACTION_START);
            push.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return push;
    }

    public static ParsePush sendStop() {
        String recipient = "";

        Log.e("PUSH Stop", "Creating New Push");
        ParsePush push = new ParsePush();
        // specify who to send push to
        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", DataStore.recipient);
        // Find devices associated with the user
        ParseQuery deviceQuery = ParseInstallation.getQuery();
        deviceQuery.whereEqualTo("GCMSenderId", recipient);
        push.setQuery(deviceQuery);


        try {
            // set up data
            JSONObject data = new JSONObject();
            data.put(Constants.SENDER, DataStore.sender);
            data.put(Constants.ACTION, Constants.ACTION_STOP);
            data.put(Constants.TOTAL_TIME, DataStore.totalTime);
            push.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return push;
    }
}
