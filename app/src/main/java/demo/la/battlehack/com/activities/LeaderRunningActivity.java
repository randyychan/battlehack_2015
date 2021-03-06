package demo.la.battlehack.com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import demo.la.battlehack.com.helpers.DataStore;
import demo.la.battlehack.com.helpers.ParseSender;
import demo.la.battlehack.com.helpers.TimerHelper;
import demo.la.battlehack.com.randyopencv.R;
import demo.la.battlehack.com.venmo.VenmoLibrary;

/**
 * Created by ksutardji on 2/28/15.
 */
public class LeaderRunningActivity extends ActionBarActivity implements TimerHelper.TimerTickListener {
    TextView durationTextView;
    Button stop;
    boolean running = false;
    TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        timerHelper = new TimerHelper(this);

        durationTextView = (TextView) findViewById(R.id.durationTextView);

        stop = (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop timer
                timerHelper.stopTimer();

                Log.e("TOTAL TIME", "TOTAL TIME: " + DataStore.totalTime);
                // send ParsePush to follower to end service
                ParsePush push = ParseSender.sendStop();
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        // generate Venmo request
                        Intent venmoIntent = VenmoLibrary.openVenmoPayment("2410", "NoStringsAttached",
                                DataStore.recipientVenmo,
                                String.valueOf(calculateAmount()),
                                "You ran for " + DataStore.totalTime + " seconds. I hope you enjoyed your run!",
                                "charge");
                        startActivityForResult(venmoIntent, 1111);
                        finish();
                    }
                });
            }
        });

        timerHelper.startTimer();
    }

    private double calculateAmount() {
        return DataStore.totalTime * 10;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void tick() {
        Log.e("Leader", "TICK");

        DataStore.totalTime++;
        LeaderRunningActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                durationTextView.setText(DataStore.totalTime + " secs");
            }
        });

    }
}
