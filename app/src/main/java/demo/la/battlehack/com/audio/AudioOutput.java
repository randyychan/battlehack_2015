package demo.la.battlehack.com.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import demo.la.battlehack.com.helpers.Constants;
import demo.la.battlehack.com.randyopencv.R;

/**
 * Created by ksutardji on 2/28/15.
 */
public class AudioOutput implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    enum Channel {
        LEFT, RIGHT, MIDDLE;
    }
    private boolean isTooClose = false;
    private static final int constantPause = 500;
    MediaPlayer mediaPlayer;
    Context context;
    boolean isPlaying = false,
            prepared = false;
    public double num = 0;
    private Handler handler;

    public AudioOutput(Context context) {
        HandlerThread thread = new HandlerThread("Audio Background");
        thread.start();

        handler = new Handler(thread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    beep();
                } else {
                    beepTooClose();
                }
                return false;
            }
        });

        this.context = context;

        AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.mono_audio);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBeep(int what) {
        handler.sendEmptyMessage(what);
    }

    public synchronized void beep() {
        try {

            if (isPlaying) {
                return;
            }

            long pauseFreq;
            while (true) {
                try {
                    // get left or right vibration signal
                    Channel leftOrRight = leftOrRight(getNum());

                    if (leftOrRight == Channel.MIDDLE) {
                        // no need to beep
                        if (mediaPlayer.isPlaying())
                            mediaPlayer.pause();

                        setIsPlaying(false);
                        break;
                    }

                    // set left or right volume
                    setVolume(leftOrRight);

                    // set pause frequency
                    pauseFreq = freq(getNum());

                    // start beep
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        setIsPlaying(true);
                    }

                    // pause frequency
                    Thread.sleep(constantPause);

                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        setIsPlaying(false);
                    }
                    // pause based on frequency
                    Thread.sleep(pauseFreq);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }

    public void beepTooClose() {
        try {
            while (isTooClose) {

                if (!isTooClose) {
                    // no need to beep
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.pause();

                    break;
                }

                if (mediaPlayer != null)
                    mediaPlayer.setVolume(1, 1);


                // set pause frequency

                // start beep
                if (mediaPlayer != null && !mediaPlayer.isPlaying())
                    mediaPlayer.start();
            }
        } catch (Exception e) {

        }
    }

    public void setVolume(Channel leftOrRight) {
        if(leftOrRight == Channel.LEFT) {
            mediaPlayer.setVolume(1, 0);
        }
        else if(leftOrRight == Channel.RIGHT) {
            mediaPlayer.setVolume(0, 1);
        }
        else {
            mediaPlayer.setVolume(1, 1);
        }
    }

    public Channel leftOrRight(double num) {
        if(num <= -Constants.LOWER_THRESHOLD) return Channel.LEFT;
        else if(num >= Constants.LOWER_THRESHOLD) return Channel.RIGHT;
        return Channel.MIDDLE;
    }

public long freq(double num) {
        // just get frequency since left or right has been determined
        num = Math.abs(num);
        // 1 is max
        if(num > 1) num = 1;
        return (long)(100/(num));
    }

    public void setNum(double num) {
        this.num = num;
    }

    public double getNum() {
        return this.num;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
    /**
     * MediaPlayer onPrepared callback function
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(context, "Media Player prepared!", Toast.LENGTH_LONG).show();
        prepared = true;
        mediaPlayer.start();
        mediaPlayer.pause();
    }

    /**
     * MediaPlayer onError callback function
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return true;
    }

    public void releaseMediaPlayer() {
//        mediaPlayer.release();
        mediaPlayer = null;

    }

    public void isTooClose(boolean isTooClose) {
        this.isTooClose = isTooClose;

        if (isTooClose) {
            Log.e("RANDY", "IS TOO CLOSE!!");
            startBeep(1);
        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.e("RANDY", "NOT CLOSE!!");
            mediaPlayer.pause();
        }
    }
}
