package demo.la.battlehack.com.helpers;

import android.content.Context;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by ksutardji on 3/1/15.
 */
public enum DropboxUtil {
    INSTANCE;

    private static final String DROPBOX_APP_KEY = "7e6llv53ut0cewx";
    private static final String DROPBOX_APP_SECRET = "v43rd7t45jmi6dq";

    private static DropboxAPI<AndroidAuthSession> mDBApi;

    static {
        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

    }

    public void auth(Context context) {
        mDBApi.getSession().startOAuth2Authentication(context);
    }

    public DropboxAPI<AndroidAuthSession> getDbApi() {
        return mDBApi;
    }
}
