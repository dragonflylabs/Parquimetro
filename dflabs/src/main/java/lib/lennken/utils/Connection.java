package lib.lennken.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by caprinet on 10/13/14.
 */
public abstract class Connection {

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        }catch (Exception e){
            ConnectivityManager CManager =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo NInfo = CManager.getActiveNetworkInfo();
            if (NInfo != null && NInfo.isConnected() && NInfo.isAvailable()) {
                return true;
            }
            return false;
        }
    }


}
