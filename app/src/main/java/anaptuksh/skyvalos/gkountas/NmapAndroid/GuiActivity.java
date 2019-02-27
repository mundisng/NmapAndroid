package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Main GUI, also handling resend button
 */
public class GuiActivity extends AppCompatActivity {
    private static Toolbar toolbar;
    CheckConnectivity cc=new CheckConnectivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygui);
        cast();

        TabLayout mytablayout=findViewById(R.id.mygui_tab_layout);
        mytablayout.addTab(mytablayout.newTab().setText("SA"));
        mytablayout.addTab(mytablayout.newTab().setText("Jobs"));
        mytablayout.addTab(mytablayout.newTab().setText("Results"));
        mytablayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager myviewpager=findViewById(R.id.mygui_pager);
        final MyPagerAdapter myadapter=new MyPagerAdapter(getSupportFragmentManager(), mytablayout.getTabCount());
        myviewpager.setAdapter(myadapter);
        myviewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mytablayout));
        mytablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myviewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * If back is pressed, exit and delete database;
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit? Stored jobs will be deleted..")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SQLiteHelper db=SQLiteHelper.getInstance(GuiActivity.this);
                        db.DeleteData();
                        GuiActivity.super.onBackPressed();
                    }
                }).create().show();
    }
    protected void cast(){
        toolbar=findViewById(R.id.mygui_my_toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mygui_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Logout" action
                Toast.makeText(GuiActivity.this, "Logging out!", Toast.LENGTH_SHORT).show();
                SharedPreferences settings=getSharedPreferences("myshared",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("logged", false);
                editor.commit();
                Intent i = new Intent(GuiActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;
            case R.id.action_resend:
                if (cc.isOnline(this)==true) {
                    Toast.makeText(getApplicationContext(), "Sending all jobs to server..", Toast.LENGTH_SHORT).show();
                    new SendJobsFromDB(this).execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No internet access!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Send all jobs to server that were left over in database
     */
    protected class SendJobsFromDB extends AsyncTask<String, Void, String> {
        private Context mContext;
        public SendJobsFromDB (Context context){
            mContext = context;
        }

        @Override
        public String doInBackground(String... params) {
            String url = LoginActivity.URL+"sendjobs";
            try {
                RestTemplate restTemplate = new RestTemplate(true);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                SQLiteHelper db=SQLiteHelper.getInstance(mContext);
                JSONArray ja;
                synchronized(LoginActivity.usingdb) {
                    ja = db.readData();
                    db.DeleteData();
                }
                if (ja.length()==0){
                    return null;
                }
                    HttpEntity<String> entityCredentials = new HttpEntity<String>(ja.toString(), httpHeaders);
                ResponseEntity<List<joberrors>> res = restTemplate.exchange(url, HttpMethod.POST, entityCredentials, new ParameterizedTypeReference<List<joberrors>>() {});
                if(res.getStatusCode()== HttpStatus.OK){
                    return res.getBody().get(0).getresponss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
