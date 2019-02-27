package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

/**
 * Populates list of 1st tab with SA info
 */
public class SAInfoAdapter extends ArrayAdapter<SAInfo> {
    private List<SAInfo> objects;
    private Context context;
    private int layoutResourceId;
    CheckConnectivity cc = new CheckConnectivity();
    public SAInfoAdapter(Context context, int layoutResourceId, List<SAInfo> objects) {
        super(context, layoutResourceId, objects);
        this.objects=objects;
        this.layoutResourceId=layoutResourceId;
        this.context=context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row=convertView;
        SAInfoHolder holder=null;
        if(row==null)
        {
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            row=inflater.inflate(layoutResourceId, parent, false);
            holder = new SAInfoHolder();
            holder.stopsa=(Button)row.findViewById(R.id.StopSA);
            holder.hash=(TextView)row.findViewById(R.id.hash);
            holder.hashdata=(TextView)row.findViewById(R.id.hashdata);
            holder.device=(TextView)row.findViewById(R.id.device);
            holder.devicedata=(TextView)row.findViewById(R.id.devicedata);
            holder.ip=(TextView)row.findViewById(R.id.ip);
            holder.ipdata=(TextView)row.findViewById(R.id.ipdata);
            holder.mac=(TextView)row.findViewById(R.id.mac);
            holder.macdata=(TextView)row.findViewById(R.id.macdata);
            holder.os=(TextView)row.findViewById(R.id.os);
            holder.osdata=(TextView)row.findViewById(R.id.osdata);
            holder.nmap=(TextView)row.findViewById(R.id.nmap);
            holder.nmapdata=(TextView)row.findViewById(R.id.nmapdata);
            holder.active=(TextView)row.findViewById(R.id.active);
            holder.activedata=(TextView)row.findViewById(R.id.activedata);
            row.setTag(holder);
        }
        else
        {
            holder=(SAInfoHolder)row.getTag();
        }
        SAInfo sa=objects.get(position);
        holder.hash.setSelected(true);
        holder.hashdata.setText(sa.getHash());
        holder.hashdata.setSelected(true);
        holder.device.setSelected(true);
        holder.devicedata.setText(sa.getDevice());
        holder.devicedata.setSelected(true);
        holder.ip.setSelected(true);
        holder.ipdata.setText(sa.getIP());
        holder.ipdata.setSelected(true);
        holder.mac.setSelected(true);
        holder.macdata.setText(sa.getMAC());
        holder.macdata.setSelected(true);
        holder.os.setSelected(true);
        holder.osdata.setText(sa.getOS());
        holder.osdata.setSelected(true);
        holder.nmap.setSelected(true);
        holder.nmapdata.setText(sa.getNmap());
        holder.nmapdata.setSelected(true);
        holder.active.setSelected(true);
        holder.activedata.setText(sa.getActive());
        holder.activedata.setSelected(true);
        if(sa.getActive().equals("true")){
            holder.stopsa.setEnabled(true);
        }
        else{
            holder.stopsa.setEnabled(false);
        }
        holder.stopsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cc.isOnline(getContext()) == true) {
                        HttpSendJobs task = new HttpSendJobs(context);
                        task.execute(objects.get(position).getHash(), "-1, exit(0),true,-1");
                    } else {
                        Toast.makeText(context, "No internet access!Adding to DB", Toast.LENGTH_SHORT).show();
                        AddJobsToDB task = new AddJobsToDB(context);
                        task.execute(objects.get(position).getHash(), "-1, exit(0),true,-1");
                    }
                }catch(Exception e){
                    Log.e("stopsa", e.getMessage(), e);
                }
            }
        });
        return row;
    }
    static class SAInfoHolder
    {
        Button stopsa;
        TextView hash;
        TextView hashdata;
        TextView device;
        TextView devicedata;
        TextView ip;
        TextView ipdata;
        TextView mac;
        TextView macdata;
        TextView os;
        TextView osdata;
        TextView nmap;
        TextView nmapdata;
        TextView active;
        TextView activedata;
    }
}
