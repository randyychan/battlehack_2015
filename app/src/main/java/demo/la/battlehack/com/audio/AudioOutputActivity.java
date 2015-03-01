package demo.la.battlehack.com.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

import demo.la.battlehack.com.randyopencv.R;

/**
 * Created by ksutardji on 2/28/15.
 */
public class AudioOutputActivity extends ActionBarActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final int MIDDLE = 0;
    public static final int[] freq = {100, 200, 300, 400, 500};
    public static final int[] range = {};
    MediaPlayer mediaPlayer;
    Context context;
    Button button;
    boolean playing = false,
            prepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.media_activity);
        context = getApplicationContext();


        // inits the MediaPlayer. prepare() is already called, so do not call prepare() again
//        mediaPlayer = MediaPlayer.create(context, R.raw.mono_audio);
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

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playing) {

                    long time = 100;
                    while(true) {
                        try {
                            mediaPlayer.start();
                            playing = true;
                            Thread.sleep(100);
                            mediaPlayer.pause();
                            playing = false;
                            Thread.sleep(time);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                      mediaPlayer.pause();
//                    mediaPlayer.stop();
                    playing = false;
//                    prepared = false;
//                    mediaPlayer.prepareAsync();
                }
            }
        });
    }

    public void beep(float num) {

        long pauseFreq;
        try {

            mediaPlayer.start();
            playing = true;

            // get left or right vibration signal
            int leftOrRight = leftOrRight(num);
            // set pause frequency
            pauseFreq = freq(num);

            // set left or right volume
            setVolume(leftOrRight);
            // pause frequency
            Thread.sleep(100);
            mediaPlayer.pause();
            playing = false;
            Thread.sleep(pauseFreq);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setVolume(int leftOrRight) {
        if(leftOrRight == -1) {
            mediaPlayer.setVolume(1, 0);
        }
        else if(leftOrRight == 1) {
            mediaPlayer.setVolume(0, 1);
        }
        else {
            mediaPlayer.setVolume(1, 1);
        }
    }

    public int leftOrRight(float num) {
        if(num < 0) return LEFT;
        else if(num > 0) return RIGHT;
        return MIDDLE;
    }

    public long freq(float num) {
        double freq = 100/(num-0.2);
        return (long)freq;
    }

    /**
     * MediaPlayer onPrepared callback function
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(context, "Media Player prepared!", Toast.LENGTH_LONG).show();
        prepared = true;
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

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
