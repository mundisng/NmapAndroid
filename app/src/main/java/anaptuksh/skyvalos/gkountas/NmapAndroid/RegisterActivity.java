package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Class handling GUI and data sending to server for registration
 */
public class RegisterActivity extends AppCompatActivity {
    private static EditText username;
    private static EditText password;
    private static EditText confirm_password;
    private static Button register;
    private static TextView back_to_login;
    private static Toolbar toolbar;
    CheckConnectivity cc = new CheckConnectivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        cast();
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_enabled(false);
                if (check_credentials()) {//check spelling
                    if (cc.isOnline(getApplicationContext()) == true) { //check internet connection
                        String hash = new String(Hex.encodeHex(DigestUtils.sha256(password.getText().toString())));//send hashed password
                        new RegistrationTask().execute(username.getText().toString(), hash);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No internet access!", Toast.LENGTH_SHORT).show();
                        set_enabled(true);
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Check the spelling!", Toast.LENGTH_SHORT).show();
                    set_enabled(true);
                }
            }
        });
    }

    protected void cast(){
        username=findViewById(R.id.editText);
        password=findViewById(R.id.editText2);
        confirm_password=findViewById(R.id.editText3);
        register=findViewById(R.id.button);
        back_to_login=findViewById(R.id.textView4);
        toolbar=findViewById(R.id.reg_my_toolbar);
        setSupportActionBar(toolbar);
    }
    protected boolean check_credentials() {
        String myusername = username.getText().toString();
        String mypassword = password.getText().toString();
        String myconfirmpassword = confirm_password.getText().toString();
        boolean correct = true;
        if (myusername.trim().isEmpty()) {
            correct = false;
            username.setError("incorrect username");
        } else {
            username.setError(null);
        }
        if (mypassword.equals(myconfirmpassword)) {
            if (mypassword.trim().isEmpty()) {
                correct = false;
                password.setError("incorrect password");
            } else {
                password.setError(null);
            }
            if (myconfirmpassword.trim().isEmpty()) {
                correct = false;
                confirm_password.setError("incorrect password");
            } else {
                confirm_password.setError(null);
            }
        }
        else{
            correct = false;
            password.setError("passwords should match");
            confirm_password.setError("passwords should match");
        }
        return correct;
    }
    protected void set_enabled(boolean value){
        confirm_password.setEnabled(value);
        register.setEnabled(value);
        username.setEnabled(value);
        password.setEnabled(value);
        back_to_login.setEnabled(value);
    }

    /**
     * Handling registraton sending to server
     */
    protected class RegistrationTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog myprogr;

        @Override
        protected void onPreExecute() {
            myprogr = new ProgressDialog(RegisterActivity.this,R.style.AppTheme);
            myprogr.setIndeterminate(true);
            myprogr.setTitle("Registering..");
            myprogr.setMessage("Please wait..");
            myprogr.show();
        }

        /**
         * Sending registration data and receiving response
         * @param params Username and password
         * @return true for accepted registration, false otherwise
         */
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // 10.0.2.2 for emulator
                // 10.0.3.2 for genymotion emulator
                // ip for real device
                final String url = LoginActivity.URL+"registration";
                RestTemplate restTemplate = new RestTemplate(true);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                JSONObject jobb= new JSONObject();
                jobb.put("Username",params[0]);
                jobb.put("Password",params[1]);
                HttpEntity<String> entityCredentials=new HttpEntity<String>(jobb.toString(), httpHeaders);
                ResponseEntity<String> res = restTemplate.postForEntity(url, entityCredentials,String.class);
                if(res.getStatusCode()== HttpStatus.OK){
                    return true;
                }
                else {
                    return false;
                }
            } catch (Exception e) {
                Log.e("RegistrationTask", e.getMessage(), e);
            }
            return false;
        }

        /**
         * Show appropriate message based on result
         * @param result true or false
         */
        protected void onPostExecute(Boolean result){
            try {
                myprogr.dismiss();
                if (result) {
                    Toast.makeText(RegisterActivity.this, "Credentials were registered!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Couldnt register credentials!", Toast.LENGTH_SHORT).show();
                }
                set_enabled(true);
            }catch(Exception e){
                Log.e("RegistrationTask,post", e.getMessage(), e);
            }
        }
    }
}
