package demo.la.battlehack.com.helpers;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rchan on 3/1/15.
 */
public enum ImageSaver {
    INSTANCE;

    ArrayList<Bitmap> images = new ArrayList<>();
    static HandlerThread thread;
    static Handler handler;

    static {
        Log.e("RANDY", "STATIC INIT");
        thread = new HandlerThread("ImageSaver");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public void addImage(final Mat mat) {
        Log.e("RANDY", "ADDING IMAGE");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Imgproc.pyrDown(mat, mat);
                Imgproc.pyrDown(mat, mat);
                Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bmp);
                images.add(bmp);
            }
        });
    }


    public List<Bitmap> getImages() {
        return images;
    }
}
