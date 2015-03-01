package demo.la.battlehack.com.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ksutardji on 2/28/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Log.e("ParseReceiver", intent.getExtras().toString());

        Bundle data = intent.getExtras();
        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject(data.get("com.parse.Data").toString());
        } catch(JSONException e) {
            Log.e("RANDY", "Push Data FAILED");
            e.printStackTrace();
        }

        super.onPushReceive(context, intent);

    }
}
