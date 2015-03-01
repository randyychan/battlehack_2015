package demo.la.battlehack.com.helpers;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
            email.setFrom("noreply@nsa.battlehack");
            email.setSubject("Summary of Your Run!");


            int count = 0;
            for (Bitmap bitmap : ImageSaver.INSTANCE.getImages()) {
                Log.e("RANDY", "ATTACHING EMAIL!! " + count);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                String filename = "image" + count + ".jpg";
                email.addAttachment(filename, bs);
                DropboxUtil.INSTANCE.getDbApi().putFile("/battlehack/" + filename,
                        bs, bitmapdata.length, null, true, null);
                count++;
                if (count > 5)
                    break;
            }

            String text = "Thanks for using NoStringsAttached!\n\n";
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

    private class EmailAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            sendEmail();
            return null;
        }
    }
}
