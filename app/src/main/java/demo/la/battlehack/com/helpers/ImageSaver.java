package demo.la.battlehack.com.helpers;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

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

        if (images.size() != 0) {
            return;
        }
        Log.e("RANDY", "RUNNING STILL");


        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("RANDY", "RUNNING RUNNABLE");
                Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bmp);
                images.add(bmp);
                sendEmail();
            }
        });
    }

    public void sendEmail() {
        Log.e("RANDY", "SENDING EMAIL!!");

        String SENDGRID_USERNAME = "ksutardji";
        String SENDGRID_PASSWORD = "nostringsattached";
        SendGrid sendgrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);
        Bitmap bitmap = images.get(0);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        try {
            SendGrid.Email email = new SendGrid.Email();
            email.addTo("randyychan@gmail.com");
            email.setFrom("noreply@nsa.battlehack");
            email.setSubject("Summary of Your Run!");
            email.addAttachment("image.jpg", bs);
            String text = "Thanks for using NoStringsAttached! Here is your summary!";
            text += "Here is your summary:\n" +
                    "Your running partner: " + DataStore.leader + "\n" +
                    "You ran: " + DataStore.totalTime + " seconds.\n" +
                    // TODO: add pictures or gifs
                    "\n" +
                    "Brought to you by NoStringsAttached";
            email.setText(text);

            try {
                SendGrid.Response response = sendgrid.send(email);
                System.out.println(response.getMessage());
            } catch (SendGridException e) {
                System.err.println(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Bitmap> getImages() {
        return images;
    }
}
