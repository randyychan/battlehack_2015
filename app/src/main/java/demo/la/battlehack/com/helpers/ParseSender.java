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
        String recipient = "";

        Log.e("PUSH Start", "Creating New Push");
        ParsePush push = new ParsePush();
        // Find devices associated with the user
        ParseQuery deviceQuery = ParseInstallation.getQuery();
        deviceQuery.whereEqualTo("username", recipient);
        push.setQuery(deviceQuery);


        try {
            // set up data
            JSONObject data = new JSONObject();
            data.put(Constants.SENDER, DataStore.sender);
            push.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return push;
    }
}
