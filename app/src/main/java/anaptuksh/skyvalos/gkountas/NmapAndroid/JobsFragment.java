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
import java.util.Arrays;
import java.util.List;

/**
 * Implements GUI options send and load from 2nd tab.
 */
public class JobsFragment extends Fragment {
    private static View myview;
    private static Button load;
    private static ListView mylistview;
    private static Button send;
    private static EditText hash_text;
    private static EditText multiline_text;
    private JobInfoAdapter myadapter;
    CheckConnectivity cc = new CheckConnectivity();
    public JobsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview=inflater.inflate(R.layout.fragment_jobs, container, false);
        hash_text=(EditText)myview.findViewById(R.id.editText6);
        multiline_text=(EditText)myview.findViewById(R.id.editText7);
        load=(Button)myview.findViewById(R.id.button6);
        send=(Button)myview.findViewById(R.id.button5);
        mylistview=(ListView)myview.findViewById(R.id.listView4);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load.setEnabled(false);
                if (cc.isOnline(getContext()) == true) {
                    new JobInfoTask().execute();
                }
                else {
                    Toast.makeText(getContext(), "No internet access!", Toast.LENGTH_SHORT).show();
                    load.setEnabled(true);
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.setEnabled(false);
                hash_text.setEnabled(false);
                multiline_text.setEnabled(false);
                String multiline_textstring = multiline_text.getText().toString();
                String hash_textstring = hash_text.getText().toString();
                if (hash_textstring.isEmpty() || hash_textstring == "+" || hash_textstring == "-") {
                    hash_text.setError("invalid input");
                } else if (multiline_textstring.isEmpty()) {
                    multiline_text.setError("no jobs");
                } else {
                    if (cc.isOnline(getContext()) == true) {   //Check if we have internet
                        System.out.println("We have internet!");
                        new HttpSendJobs(getContext()).execute(hash_textstring, multiline_textstring);
                    } else {
                        Toast.makeText(getContext(), "No internet access!Adding to DB.", Toast.LENGTH_SHORT).show();
                        new AddJobsToDB(getContext()).execute(hash_textstring, multiline_textstring);
                    }
                }
                send.setEnabled(true);
                hash_text.setEnabled(true);
                multiline_text.setEnabled(true);
            }
        });
        return myview;
    }


    protected class JobInfoTask extends AsyncTask<Void, Void, List<jobb>> {
        private ProgressDialog myprogr;

        @Override
        protected void onPreExecute() {
            myprogr = new ProgressDialog(getActivity(),R.style.AppTheme);
            myprogr.setIndeterminate(true);
            myprogr.setTitle("Getting jobs..");
            myprogr.setMessage("Please wait..");
            myprogr.show();
        }

        /**
         * Sends request to receive jobs
         * @return Results from server in jarray. In case of no results, empty array returns.
         */
        @Override
        public List<jobb> doInBackground(Void... params) {
            try {
                String url = LoginActivity.URL+"getJobs";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<List<jobb>> res = restTemplate.exchange(url, HttpMethod.GET,null,new ParameterizedTypeReference<List<jobb>>(){});
                if (res.getStatusCode()== HttpStatus.OK) {
                        return res.getBody();
                }
            }catch (Exception e){
                Log.e("JobInfoTask", e.getMessage(), e);
            }

            return null;
        }

        /**
         * Show correct message for every kind of result
         * @param result_list jarray with jobs from server
         */
        protected void onPostExecute(List<jobb> result_list){
            try {
                myprogr.dismiss();
                if (result_list == null) {
                    Toast.makeText(getContext(), "Couldn't connect to server!", Toast.LENGTH_SHORT).show();
                } else if (result_list.isEmpty()) {
                    Toast.makeText(getContext(), "0 jobs were found", Toast.LENGTH_SHORT).show();
                } else {
                    myadapter = new JobInfoAdapter(getActivity(), R.layout.list_jobinfo, result_list, hash_text);
                    mylistview.setAdapter(myadapter);
                }
                load.setEnabled(true);
            }catch(Exception e){
                Log.e("JobInfoTask,post", e.getMessage(), e);
            }
        }
    }
}
