package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Sending any kind of job (stop sa, stop periodic, new job) to server
 */
public class HttpSendJobs extends AsyncTask<String, Void,Integer> {
    private ProgressDialog myprogr;
    private Context context;

    /**
     * Constructor
     * @param context
     */
    public HttpSendJobs(Context context) {
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        myprogr = new ProgressDialog(context,R.style.AppTheme);
        myprogr.setIndeterminate(true);
        myprogr.setTitle("Sending data..");
        myprogr.setMessage("Please wait..");
        myprogr.show();
    }

    /**
     * Receives one or multiple jobs and sends them using spring to server
     * @param params Hash and job
     * @return 0 for correct server response,-1 for any other server response, 2 for no connection with server
     */
    @Override
    public Integer doInBackground(String...params){
        String url = LoginActivity.URL+"sendjobs";
        ResponseEntity<List<joberrors>> res;
        try {
            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
                protected boolean hasError(HttpStatus statusCode) {
                    return false;    //handler to not cause exceptions on 4xx http status code
                }});
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            JSONArray ja = new JSONArray();
            String[] line_array = params[1].split("\n");
            for (String line : line_array) {
                JSONObject jobb = new JSONObject();
                jobb.put("hash", params[0]);
                jobb.put("job", line);
                ja.put(jobb);
            }
            HttpEntity<String> entityCredentials=new HttpEntity<String>(ja.toString(), httpHeaders);
            res = restTemplate.exchange(url, HttpMethod.POST, entityCredentials, new ParameterizedTypeReference<List<joberrors>>() {});
            if(res.getStatusCode()== HttpStatus.OK){
                return 0;
            }
            else {
                return -1;
            }
        }catch (Exception e){
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Displaying appropriate message for server response
     * @param what response from server, 0=ok,-1=error,2=no response
     */
    protected void onPostExecute(Integer what){
        try {
            myprogr.dismiss();
            if (what == -1) {
                Toast.makeText(context, "Internal server error on at least 1 job sent", Toast.LENGTH_SHORT).show();
            }
            if (what == 2) {
                Toast.makeText(context, "Couldn't contact server!Data wasn't sent!", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.e("HttpSendJobs,post", e.getMessage(), e);
        }
    }
}
