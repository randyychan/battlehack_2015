package demo.la.battlehack.com.helpers;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

/**
 * Created by ksutardji on 2/28/15.
 */
public class EmailHelper {

    private static final String SENDGRID_USERNAME = "ksutardji";
    private static final String SENDGRID_PASSWORD = "nostringsattached";

    private SendGrid sendgrid;
    public EmailHelper() {
        sendgrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);

    }

    public void sendEmail() {

        SendGrid.Email email = new SendGrid.Email();
        email.addTo("randyychan@gmail.com");
        email.setFrom("noreply@nsa.battlehack");
        email.setSubject("Summary of Your Run!");

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
        }
        catch (SendGridException e) {
            System.err.println(e);
        }
    }
}
