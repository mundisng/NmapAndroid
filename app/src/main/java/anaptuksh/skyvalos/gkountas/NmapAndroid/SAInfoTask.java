package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

/**
 * Class handling requesting for all SA's.
 */
public class SAInfoTask extends AsyncTask<Void, Void, List<SAInfo>> {
    private ProgressDialog myprogr;
    private Context context;
    private View myview;
    
    public SAInfoTask(Context context,View myview) {
        this.context = context;
        this.myview=myview;
    }
    @Override
    protected void onPreExecute() {
        myprogr = new ProgressDialog(context, R.style.AppTheme);
        myprogr.setIndeterminate(true);
        myprogr.setTitle("Getting SA info..");
        myprogr.setMessage("Please wait..");
        myprogr.show();
    }

    /**
     * Sending request to receive all SA info.
     * @return jarray with all sainfo, can be empty
     */
    @Override
    protected List<SAInfo> doInBackground(Void... params) {
        try {
            String url = LoginActivity.URL+"getSAInfo";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<List<SAInfo>> res = restTemplate.exchange(url, HttpMethod.GET,null,new ParameterizedTypeReference<List<SAInfo>>(){});
            if (res.getStatusCode()== HttpStatus.OK) {
                    return res.getBody();
            }
       }catch (Exception e){
            Log.e("SAInfoTask", e.getMessage(), e);
       }
       return null;
    }

    /**
     * Showing appropriate message based on jarray returned by doinbackground
     * @param result_list jarray with results, can be empty
     */
    protected void onPostExecute(List<SAInfo> result_list){
        try {
            myprogr.dismiss();
            if (result_list == null) {
                Toast.makeText(context, "Couldn't connect to server!", Toast.LENGTH_SHORT).show();
            } else if (result_list.isEmpty()) {
                Toast.makeText(context, "No SAs were found", Toast.LENGTH_SHORT).show();
            } else {
                ListView mylistview =  myview.findViewById(R.id.listView3);
                SAInfoAdapter myadapter = new SAInfoAdapter(context, R.layout.list_sainfo, result_list);
                mylistview.setAdapter(myadapter);
            }
            Button mybutton =  myview.findViewById(R.id.button4);
            mybutton.setEnabled(true);
        }catch(Exception e){
            Log.e("SAInfoTask,post", e.getMessage(), e);
        }
    }
}
