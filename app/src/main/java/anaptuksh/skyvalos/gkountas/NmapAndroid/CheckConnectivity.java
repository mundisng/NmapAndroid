package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Checks internet conectivity
 */
public class CheckConnectivity extends Activity {
    /**
     * Checks if internet is online
     * @param context
     * @return true if online, else false
     */
    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
