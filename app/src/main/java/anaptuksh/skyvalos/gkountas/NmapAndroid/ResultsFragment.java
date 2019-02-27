package anaptuksh.skyvalos.gkountas.NmapAndroid;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Arrays;
import java.util.List;

/**
 * Classing handling GUI and showing all jobs previously sent by server.
 */
public class ResultsFragment extends Fragment {
    private static View myview;
    private static Button show;
    private static ListView mylistview;
    private static EditText hash_text;
    private static EditText last_text;
    private ResultInfoAdapter myadapter;
    public ResultsFragment() {
        // Required empty public constructor
    }
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview=inflater.inflate(R.layout.fragment_results, container, false);
        show=(Button)myview.findViewById(R.id.button7);
        hash_text=(EditText)myview.findViewById(R.id.editText8);
        last_text=(EditText)myview.findViewById(R.id.editText9);
        mylistview=(ListView)myview.findViewById(R.id.listView5);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setEnabled(false);
                hash_text.setEnabled(false);
                last_text.setEnabled(false);
                String hash_textstring=hash_text.getText().toString();
                String last_textstring=last_text.getText().toString();
                CheckConnectivity cc = new CheckConnectivity();
                if (cc.isOnline(getContext()) == true) {
                    new ResultInfoTask().execute(hash_textstring, last_textstring);
                }
                else {
                    Toast.makeText(getContext(), "No internet access!", Toast.LENGTH_SHORT).show();
                    show.setEnabled(true);
                    hash_text.setEnabled(true);
                    last_text.setEnabled(true);

                }
            }
        });
        return myview;
    }

    /**
     * Handling receiving past jobs from server
     */
    protected class ResultInfoTask extends AsyncTask<String, Void, List<SAResult>> {
        private ProgressDialog myprogr;

        @Override
        protected void onPreExecute() {
            myprogr = new ProgressDialog(getActivity(),R.style.AppTheme);
            myprogr.setIndeterminate(true);
            myprogr.setTitle("Getting Results..");
            myprogr.setMessage("Please wait..");
            myprogr.show();
        }

        /**
         * Requesting jobs from server based on 2 parameters
         * @param params SA to specify which SA we want the jobs it has been sent to, and amountofresults
         * @return jarray with all jobs, can be empty
         */
        @Override
        public List<SAResult> doInBackground(String... params) {
            try {
                String whichSA,amountofresults;
                if(params[0].isEmpty()){
                    whichSA="all";
                }
                else{
                    whichSA=params[0];
                }
                if(params[1].isEmpty()){
                    amountofresults="all";
                }
                else{
                    amountofresults=params[1];
                }
                String url = LoginActivity.URL+"get";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("SA",whichSA).queryParam("results",amountofresults);
                System.out.println(builder.build().encode().toUriString());
                ResponseEntity<List<SAResult>> res = restTemplate.exchange(builder.build().encode().toUriString(), HttpMethod.GET,null,new ParameterizedTypeReference<List<SAResult>>(){});
                if (res.getStatusCode()== HttpStatus.OK) {
                        return res.getBody();
                }
            }catch (Exception e){
                Log.e("ResultInfoTask", e.getMessage(), e);
            }
            return null;
        }

        /**
         * Show appropriate message based on return from doinbackground
         * @param result_list Jarray with all jobs
         */
        protected void onPostExecute(List<SAResult> result_list){
            try {
                myprogr.dismiss();
                if (result_list == null) {
                    Toast.makeText(getContext(), "Couldn't connect to server!", Toast.LENGTH_SHORT).show();
                } else if (result_list.isEmpty()) {
                    Toast.makeText(getContext(), "0 results were found", Toast.LENGTH_SHORT).show();
                } else {
                    myadapter = new ResultInfoAdapter(getActivity(), R.layout.list_resultsinfo, result_list);
                    mylistview.setAdapter(myadapter);
                }
                show.setEnabled(true);
                hash_text.setEnabled(true);
                last_text.setEnabled(true);
            }catch(Exception e){
                Log.e("ResultInfoTask,post", e.getMessage(), e);
            }
        }
    }
}
