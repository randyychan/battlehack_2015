package demo.la.battlehack.com.helpers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import demo.la.battlehack.com.activities.CameraActivity;

/**
 * Created by ksutardji on 2/28/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {
    Context context;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Log.e("ParseReceiver", intent.getExtras().toString());

        this.context = context;

        Bundle data = intent.getExtras();
        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject(data.get("com.parse.Data").toString());

            // get sender
            String sender = jsonData.getString(Constants.SENDER);
            // get action
            String action = jsonData.getString(Constants.ACTION);
            if(action.equals(Constants.ACTION_START)) {
                startFollowerService();
            }
            else if(action.equals(Constants.ACTION_STOP)) {
                if(jsonData.has(Constants.TOTAL_TIME)) {
                    DataStore.totalTime = jsonData.getInt(Constants.TOTAL_TIME);
                    stopFollowerService();
                }
            }


        } catch(JSONException e) {
            Log.e("RANDY", "Push Data FAILED");
            e.printStackTrace();
        }

        super.onPushReceive(context, intent);

    }

    private void startFollowerService() {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void stopFollowerService() {
        PackageManager pm = context.getPackageManager();

        try {
            Intent intent = pm.getLaunchIntentForPackage("com.venmo");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if(intent != null) {
                //receiver sends summary of run to him/herself
                EmailHelper emailHelper = new EmailHelper();

                emailHelper.sendEmailTask();
                context.startActivity(intent);
            }
        } catch(ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
