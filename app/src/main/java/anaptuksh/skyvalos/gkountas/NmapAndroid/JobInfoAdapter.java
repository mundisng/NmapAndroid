package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Class to handle GUI options stop periodic and resend a previous used job from load list on 2nd tab
 */
public class JobInfoAdapter extends ArrayAdapter<jobb> {
    private List<jobb> objects;
    private Context context;
    private EditText hash_text;
    private int layoutResourceId;
    CheckConnectivity cc = new CheckConnectivity();
    public JobInfoAdapter(Context context, int layoutResourceId, List<jobb> objects,EditText hash_text) {
        super(context, layoutResourceId, objects);
        this.objects=objects;
        this.layoutResourceId=layoutResourceId;
        this.context=context;
        this.hash_text=hash_text;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row=convertView;
        JobInfoHolder holder=null;
        if(row==null)
        {
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            row=inflater.inflate(layoutResourceId, parent, false);
            holder = new JobInfoHolder();
            holder.stopperiodic=(Button)row.findViewById(R.id.StopPeriodic);
            holder.send=(Button)row.findViewById(R.id.button8);
            holder.hashjob=(TextView)row.findViewById(R.id.hashjob);
            holder.hashjobdata=(TextView)row.findViewById(R.id.hashjobdata);
            holder.idjob=(TextView)row.findViewById(R.id.idjob);
            holder.idjobdata=(TextView)row.findViewById(R.id.idjobdata);
            holder.job=(TextView)row.findViewById(R.id.job);
            holder.jobdata=(TextView)row.findViewById(R.id.jobdata);
            row.setTag(holder);
        }
        else
        {
            holder=(JobInfoHolder)row.getTag();
        }
        jobb job=objects.get(position);
        holder.hashjob.setSelected(true);
        holder.hashjobdata.setText(job.gethash());
        holder.hashjobdata.setSelected(true);
        holder.idjob.setSelected(true);
        holder.idjobdata.setText(job.getid());
        holder.idjobdata.setSelected(true);
        holder.job.setSelected(true);
        holder.jobdata.setText(job.getjob());
        holder.jobdata.setSelected(true);
        int firstcomma=job.getjob().indexOf(",");
        String periodic=job.getjob().substring(firstcomma+1).trim();
        if(periodic.startsWith("f")==true){
            holder.stopperiodic.setVisibility(row.GONE);
        }
        else{
            holder.stopperiodic.setVisibility(row.VISIBLE);
        }
        holder.stopperiodic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cc.isOnline(getContext()) == true) {
                        HttpSendJobs task = new HttpSendJobs(context);
                        task.execute(objects.get(position).gethash(), objects.get(position).getid() + ",Stop,true,periodic");
                    } else {

                        Toast.makeText(getContext(), "No internet access!Adding to DB.", Toast.LENGTH_SHORT).show();
                        AddJobsToDB task = new AddJobsToDB(context);
                        task.execute(objects.get(position).gethash(), objects.get(position).getid() + ",Stop,true,periodic");
                    }
                }catch(Exception e){
                    Log.e("stopperiodic", e.getMessage(), e);
                }
            }
        });
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String hash_textstring = hash_text.getText().toString();
                    if (hash_textstring.isEmpty() || hash_textstring == "+" || hash_textstring == "-") {
                        hash_text.setError("invalid input");
                    } else {
                        if (cc.isOnline(getContext()) == true) {
                            HttpSendJobs task = new HttpSendJobs(context);
                            task.execute(hash_textstring, objects.get(position).getjob());
                        } else {
                            Toast.makeText(getContext(), "No internet access!Adding to DB.", Toast.LENGTH_SHORT).show();
                            AddJobsToDB task = new AddJobsToDB(context);
                            task.execute(hash_textstring, objects.get(position).getjob());
                        }
                    }
                }catch(Exception e){
                    Log.e("send,listview", e.getMessage(), e);
                }
            }
        });
        return row;
    }
    static class JobInfoHolder
    {
        Button stopperiodic;
        Button send;
        TextView hashjob;
        TextView hashjobdata;
        TextView idjob;
        TextView idjobdata;
        TextView job;
        TextView jobdata;
    }
}
