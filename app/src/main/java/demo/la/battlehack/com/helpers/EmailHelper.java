package demo.la.battlehack.com.helpers;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ksutardji on 2/28/15.
 */
public class EmailHelper {

    private static final String SENDGRID_USERNAME = "ksutardji";
    private static final String SENDGRID_PASSWORD = "nostringsattached";

    public Context context;

    private SendGrid sendgrid;
    public EmailHelper() {
        sendgrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);


    }

    public void sendEmailTask(Context context) {
        this.context = context;
        EmailAsync emailAsync = new EmailAsync();
        emailAsync.execute(new Void[0]);
    }


    public void sendEmail() {
        Log.e("RANDY", "SENDING EMAIL!!");

        try {

            // Sendgrid init
            SendGrid.Email email = new SendGrid.Email();
            email.addTo("randyychan@gmail.com");
            email.addCc("ksutardji@gmail.com");
            email.setFrom("noreply@nsa.battlehack");
            email.setSubject("Summary of Your Run!");

            long curTime = System.currentTimeMillis();
            Object[] bitMapDataArray = new Object[ImageSaver.INSTANCE.getImages().size()];
            int count = 0;
            for (Bitmap bitmap : ImageSaver.INSTANCE.getImages()) {
                Log.e("RANDY", "ATTACHING EMAIL!! " + count);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                bitMapDataArray[count] = bitmapdata;
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                String filename = "image" + count + ".jpg";
                email.addAttachment(filename, bs);

                count++;
                if (count > 5)
                    break;

            }

            byte[] gifArray = generateGIF();
            ByteArrayInputStream bs = new ByteArrayInputStream(gifArray);
            String filename = "animated_run.gif";
            email.addAttachment(filename, bs);
            // send to Dropbox
            DropboxUtil.INSTANCE.getDbApi().putFile("/battlehack/" + curTime + "/" + filename,
                    bs, gifArray.length, null, true, null);

            String text = "Thanks for using NoStringsAttached!\n\n";
            text += "Here is your summary:\n" +
                    "Your running partner: " + DataStore.leader + "\n" +
                    "You ran: " + DataStore.totalTime + " seconds.\n" +
                    "You thanked " + DataStore.leader + " with $" + DataStore.totalAmount + "\n" +
                    "\n" +
                    "Brought to you by NoStringsAttached";
            email.setText(text);

            try {
                SendGrid.Response response = sendgrid.send(email);
                System.out.println(response.getMessage());
            } catch (SendGridException e) {
                System.err.println(e);
            }


            count = 0;
            for (Object bss : bitMapDataArray) {
                filename = "image" + count + ".jpg";
                byte[] bytes = (byte[]) bss;
                bs = new ByteArrayInputStream(bytes);
                DropboxUtil.INSTANCE.getDbApi().putFile("/battlehack/" + curTime + "/" + filename,
                        bs, bytes.length, null, true, null);
                count++;
                if (count > 5)
                    break;

            }

            filename = "animated_run.gif";

            DropboxUtil.INSTANCE.getDbApi().putFile("/battlehack/" + curTime + "/" + filename,
                    bs, gifArray.length, null, true, null);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public byte[] generateGIF() {
        ArrayList<Bitmap> bitmaps = ImageSaver.INSTANCE.images;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GifSequenceWriter encoder = new GifSequenceWriter();
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    private class EmailAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            sendEmail();
            return null;
        }
    }
}
