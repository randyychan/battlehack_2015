package demo.la.battlehack.com.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import demo.la.battlehack.com.helpers.DataStore;
import demo.la.battlehack.com.helpers.ParseSender;
import demo.la.battlehack.com.helpers.TimerHelper;
import demo.la.battlehack.com.randyopencv.R;

/**
 * Created by ksutardji on 2/28/15.
 */
public class LeaderActivity extends ActionBarActivity implements TimerHelper.TimerTickListener {
    Button startStopButton;
    boolean running = false;
    TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        timerHelper = new TimerHelper(this);

        startStopButton = (Button) findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running) {
                    // send ParsePush to follower to start service
                    ParsePush push = ParseSender.sendStart();
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            // start timer
                            timerHelper.startTimer();
                            // change button text to "Stop"
                            startStopButton.setText("Stop Run");
                            //running set to true
                            setRunning(true);
                        }
                    });
                }
                else {
                    // stop timer
                    timerHelper.stopTimer();
                    Log.e("TOTAL TIME", "TOTAL TIME: " + DataStore.totalTime);
                    // send ParsePush to follower to end service
                    ParsePush push = ParseSender.sendStop();
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            DataStore.totalTime = 0;
                            // change button text to "Start"
                            startStopButton.setText("Start Run");
                            // running set to false
                            setRunning(false);
                        }
                    });
                }
            }
        });
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


    }
}
