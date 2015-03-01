package demo.la.battlehack.com.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by ksutardji on 2/28/15.
 */
public class AudioOutput implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    enum Channel {
        LEFT, RIGHT, MIDDLE;
    }

    private static final int constantPause = 100;
    MediaPlayer mediaPlayer;
    Context context;
    Button button;
    boolean isPlaying = false,
            prepared = false;
    public float num = 0;

    public AudioOutput() {

    }

    public void beep() {

        long pauseFreq;
        while(true) {
            try {
                // get left or right vibration signal
                Channel leftOrRight = leftOrRight(getNum());

                if(leftOrRight == Channel.MIDDLE) {
                    // no need to beep
                    continue;
                }

                // set left or right volume
                setVolume(leftOrRight);

                // set pause frequency
                pauseFreq = freq(getNum());

                // start beep
                mediaPlayer.start();
                setIsPlaying(true);

                // pause frequency
                Thread.sleep(constantPause);

                mediaPlayer.pause();
                // pause based on frequency
                Thread.sleep(pauseFreq);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    public Channel leftOrRight(float num) {
        if(num <= -0.2) return Channel.LEFT;
        else if(num >= 0.2) return Channel.RIGHT;
        return Channel.MIDDLE;
    }

    public long freq(float num) {
        // just get frequency since left or right has been determined
        num = Math.abs(num);
        // 1 is max
        if(num > 1) num = 1;
        return (long)(100/(num-0.2));
    }

    public void setNum(float num) {
        this.num = num;
    }

    public float getNum() {
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
        mediaPlayer.release();
        mediaPlayer = null;

    }
}
