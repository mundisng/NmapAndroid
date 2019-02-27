package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Adds all jobs {[hash],[job]} given to database
 */
public class AddJobsToDB extends AsyncTask<String, Void, String> {
    private Context mContext;
    public AddJobsToDB (Context context){
        mContext = context;
    }

    @Override
    public String doInBackground(String...params){
        SQLiteHelper db=SQLiteHelper.getInstance(mContext);
        String[] line_array=params[1].split("\n");  //Split results
        synchronized(LoginActivity.usingdb) {
            for (String line : line_array) {
                db.insertData(Integer.parseInt(params[0]), line); //Insert in database
            }
        }
        return null;
    }

}
