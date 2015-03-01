package demo.la.battlehack.com.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import demo.la.battlehack.com.helpers.DropboxUtil;
import demo.la.battlehack.com.randyopencv.R;

/**
 * Created by ksutardji on 2/28/15.
 */
public class RoleActivity extends ActionBarActivity {

    Button followerButton;
    Button leaderButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DropboxUtil.INSTANCE.auth(RoleActivity.this);
        context = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_role);

        followerButton = (Button) findViewById(R.id.button_follower);
        followerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FollowerSetUpActivity.class);
                startActivity(intent);
            }
        });
        leaderButton = (Button) findViewById(R.id.button_leader);
        leaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaderSetUpActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (id == R.id.action_camera) {
            startActivity(new Intent(this, CameraActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DropboxUtil.INSTANCE.getDbApi().getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                DropboxUtil.INSTANCE.finishAuth();

                String accessToken = DropboxUtil.INSTANCE.getAccessToken();
            } catch (IllegalStateException e) {
            }
        }
    }
}
