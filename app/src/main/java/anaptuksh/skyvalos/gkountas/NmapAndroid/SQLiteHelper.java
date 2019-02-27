package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Handling all sqlite database operations
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteHelper mInstance = null;
    public static final String DB_NAME=LoginActivity.DBNAME;
    private static final String DATABASE_CREATE="CREATE DATABASE sendjobs;";
    private static final String TABLE_CREATE="CREATE TABLE IF NOT EXISTS jobs (Hash int(15),Job varchar(255));";


    public static SQLiteHelper getInstance(Context context){ //singleton
        if (mInstance == null) {
            mInstance = new SQLiteHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public SQLiteHelper(Context context){

        super(context,DB_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase mydb){ //Create table
        mydb.execSQL(TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase mydb,int oldv,int newv){ //Upgrade database
        mydb.execSQL("DROP TABLE IF EXISTS jobs");
        onCreate(mydb);
    }
    public void insertData(int hash,String job){ //Insert data to database
        try {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Hash", hash);
            values.put("Job", job);
            mydb.insert("jobs", null, values);
            mydb.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public JSONArray readData(){ //Read all database data
        JSONArray ja = new JSONArray();
        String selectQuery = "SELECT  * FROM jobs;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery(selectQuery, null);
        try {
            if (cr.moveToFirst()) {
                do {
                    JSONObject jobb = new JSONObject();
                    jobb.put("hash", cr.getString(0));
                    jobb.put("job", cr.getString(1));
                    ja.put(jobb);
                    System.out.println(ja.toString());
                } while (cr.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        cr.close();
        db.close();
        return ja;
    }
    public void DeleteData(){ //Delete all database data
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("jobs", null, null);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
