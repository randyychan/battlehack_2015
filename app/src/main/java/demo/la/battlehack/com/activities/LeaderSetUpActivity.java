package demo.la.battlehack.com.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import demo.la.battlehack.com.randyopencv.R;

public class LeaderSetUpActivity extends ActionBarActivity {

    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_set_up);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

// Intent filters for exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
        }
        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

    }

    @Override
    protected void onResume() {
        super.onResume();
        enableNdefExchangeMode();
    }

    private void enableNdefExchangeMode() {
        NdefMessage message = null;
        message = new NdefMessage(NdefRecord.createTextRecord("UTF-8", "RANDY"));

        mNfcAdapter.enableForegroundNdefPush(LeaderSetUpActivity.this, message);

                mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                        mNdefExchangeFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            NdefMessage[] msgs = NfcUtils.getNdefMessages(intent);
//            fireFriendRequest(msgs[0]);
            Toast.makeText(this, "sent friend request via nfc!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_leader_set_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
