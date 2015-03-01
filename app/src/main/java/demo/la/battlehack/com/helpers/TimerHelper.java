package demo.la.battlehack.com.helpers;

/**
 * Created by ksutardji on 2/28/15.
 */

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
public class TimerHelper {
    Timer timer;
    TimerTickListener listener;
    boolean isTimerRunning = false;

    public TimerHelper(TimerTickListener listener) {
        this.listener = listener;
    }

    public void startTimer() {
        scheduleTimer();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = null;
        isTimerRunning = false;
    }

    private void scheduleTimer() {
        timer = new Timer();
        if (!isTimerRunning) {
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (listener != null) {
                        listener.tick();
                    }
                }
            }, 0, 1000);
            isTimerRunning = true;
            Log.e("RANDY", "SCHEDULING TIMER");
        }
    }

    public void pauseTimer() {
        isTimerRunning = false;
        timer.cancel();
        timer.purge();
    }

    public interface TimerTickListener {
        public void tick();
    }
}

