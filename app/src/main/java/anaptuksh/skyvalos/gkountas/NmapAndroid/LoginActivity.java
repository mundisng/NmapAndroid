package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Start screen containing Username and Password field to log in.
 */
public class LoginActivity extends AppCompatActivity {
    private static EditText username;
    private static EditText password;
    private static Button login;
    private static Button register;
    private static Toolbar toolbar;
    public static Object usingdb = new Object();
    // 10.0.2.2 for emulator
    // 10.0.3.2 for genymotion emulator
    // ip for real device
    public static String URL="http://192.168.0.13:8080/anap2/android/",DBNAME="sendjobs.db";
    CheckConnectivity cc=new CheckConnectivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings=getSharedPreferences("myshared",0);
        if(settings.getBoolean("logged",false)){
            //already logged in
            Intent i = new Intent(LoginActivity.this, GuiActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_login);
        cast();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_enabled(false);
                if (check_credentials()) { //Check spelling
                    if (cc.isOnline(getApplicationContext())==true) { //Check internet connection
                        String hash = new String(Hex.encodeHex(DigestUtils.sha256(password.getText().toString()))); //hashed password
                        new AuthenticationTask().execute(username.getText().toString(), hash);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No internet access!", Toast.LENGTH_SHORT).show();
                        set_enabled(true);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Check the spelling!", Toast.LENGTH_SHORT).show();
                    set_enabled(true);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() { //Register button
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * On back button, empty database and exit application.
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit? Stored jobs will be deleted!")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SQLiteHelper db=SQLiteHelper.getInstance(LoginActivity.this);
                        db.DeleteData();
                        LoginActivity.super.onBackPressed();
                    }
                }).create().show();
    }
    protected void set_enabled(boolean value){
        login.setEnabled(value);
        register.setEnabled(value);
        username.setEnabled(value);
        password.setEnabled(value);
    }
    protected void cast(){
        username=findViewById(R.id.editText4);
        password=findViewById(R.id.editText5);
        login=findViewById(R.id.log_login_button);
        register=findViewById(R.id.log_register_button);
        toolbar=findViewById(R.id.log_my_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(R.string.title_activity_login);
    }
    protected boolean check_credentials(){
        String myusername=username.getText().toString();
        String mypassword=password.getText().toString();
        boolean correct=true;
        if(myusername.trim().isEmpty()){
            correct=false;
            username.setError("incorrect username");
        }
        else{
            username.setError(null);
        }
        if(mypassword.trim().isEmpty()){
            correct=false;
            password.setError("incorrect password");
        }
        else{
            password.setError(null);
        }
        return correct;
    }

    /**
     * Class handling logging in to server
     */
    protected class AuthenticationTask extends AsyncTask<String, Void, Integer> {
        private ProgressDialog myprogr;

        @Override
        protected void onPreExecute() {
            myprogr = new ProgressDialog(LoginActivity.this,R.style.AppTheme);
            myprogr.setIndeterminate(true);
            myprogr.setTitle("Authenticating..");
            myprogr.setMessage("Please wait..");
            myprogr.show();
        }

        /**
         * Sending username/pass to server
         * @param params Username and password
         * @return 0 for success,-1 for wrong credentials, 1 for denied server access,2 for every other case
         */
        @Override
        protected Integer doInBackground(String... params) {

            try {
                String macadd=null,ipaddress=null,temp=null,osversion=null;
                String AndroidName = Build.MANUFACTURER +" " +Build.MODEL;
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface
                            .getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf
                                .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {

                                ipaddress = inetAddress.getHostAddress();
                                byte[] mac=intf.getHardwareAddress();
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < mac.length; i++) {   //Get MAC
                                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                                }
                                macadd=sb.toString();
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.e("IP Address", ex.toString());
                }
                osversion=android.os.Build.VERSION.RELEASE;
                final String url = LoginActivity.URL+"identification";
                RestTemplate restTemplate = new RestTemplate(true);
                restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
                    protected boolean hasError(HttpStatus statusCode) {
                        return false;
                    }});
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                JSONObject jobb= new JSONObject();
                jobb.put("Username",params[0]);
                jobb.put("Password",params[1]);
                jobb.put("Name",AndroidName);
                jobb.put("MAC", macadd);
                jobb.put("IP",ipaddress);
                jobb.put("OS",osversion);
                HttpEntity<String> entityCredentials=new HttpEntity<String>(jobb.toString(), httpHeaders);
                ResponseEntity<String> res = restTemplate.postForEntity(url, entityCredentials,String.class);
                if(res.getStatusCode()==HttpStatus.OK){
                    return 0;
                }
                else  if(res.getStatusCode()==HttpStatus.UNAUTHORIZED) {
                    return -1;
                }
                else if(res.getStatusCode()==HttpStatus.FORBIDDEN){
                    return 1;
                }
            } catch (Exception e) {
                Log.e("AuthenticationTask", e.getMessage(), e);
            }
                return 2;
        }

        /**
         * Show appropriate message based on return from doinbackground
         * @param result from doinbackground
         */
        protected void onPostExecute(Integer result){
            try {
                myprogr.dismiss();
                if (result == 0) {
                    Toast.makeText(LoginActivity.this, "Username and Password is correct!", Toast.LENGTH_SHORT).show();
                    SharedPreferences settings = getSharedPreferences("myshared", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("logged", true);
                    editor.commit();
                    Intent i = new Intent(LoginActivity.this, GuiActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else if (result == -1) {
                    Toast.makeText(LoginActivity.this, "Wrong credentials given!", Toast.LENGTH_SHORT).show();
                } else if (result == 1) {
                    Toast.makeText(LoginActivity.this, "Access denied!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Server error or couldn't connect to server!", Toast.LENGTH_SHORT).show();
                }
                set_enabled(true);
            }catch(Exception e){
                Log.e("AuthenticationTask,post", e.getMessage(), e);
            }
        }
    }
}
