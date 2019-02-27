package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Populates list of 3rd tab with job results
 */
public class ResultInfoAdapter extends ArrayAdapter<SAResult> {
    private List<SAResult> objects;
    private Context context;
    private int layoutResourceId;
    public ResultInfoAdapter(Context context, int layoutResourceId, List<SAResult> objects) {
        super(context, layoutResourceId, objects);
        this.objects=objects;
        this.layoutResourceId=layoutResourceId;
        this.context=context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row=convertView;
        ResultInfoHolder holder=null;
        if(row==null)
        {
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            row=inflater.inflate(layoutResourceId, parent, false);
            holder = new ResultInfoHolder();
            holder.hashresults=(TextView)row.findViewById(R.id.hashresults);
            holder.hashresultsdata=(TextView)row.findViewById(R.id.hashresultsdata);
            holder.idresults=(TextView)row.findViewById(R.id.idresults);
            holder.idresultsdata=(TextView)row.findViewById(R.id.idresultsdata);
            holder.timeresults=(TextView)row.findViewById(R.id.timeresults);
            holder.timeresultsdata=(TextView)row.findViewById(R.id.timeresultsdata);
            holder.hostnameresults=(TextView)row.findViewById(R.id.hostnameresults);
            holder.hostnameresultsdata=(TextView)row.findViewById(R.id.hostnameresultsdata);
            holder.tasksresults=(TextView)row.findViewById(R.id.tasksresults);
            holder.tasksresultsdata=(TextView)row.findViewById(R.id.tasksresultsdata);
            holder.resultsresults=(TextView)row.findViewById(R.id.resultsresults);
            holder.resultsresultsdata=(TextView)row.findViewById(R.id.resultsresultsdata);
            holder.periodicresults=(TextView)row.findViewById(R.id.periodicresults);
            holder.periodicresultsdata=(TextView)row.findViewById(R.id.periodicresultsdata);
            row.setTag(holder);
        }
        else
        {
            holder=(ResultInfoHolder)row.getTag();
        }
        SAResult sa=objects.get(position);
        holder.hashresults.setSelected(true);
        holder.hashresultsdata.setText(sa.gethash());
        holder.hashresultsdata.setSelected(true);
        holder.idresults.setSelected(true);
        holder.idresultsdata.setText(sa.getid());
        holder.idresultsdata.setSelected(true);
        holder.timeresults.setSelected(true);
        holder.timeresultsdata.setText(sa.gettime());
        holder.timeresultsdata.setSelected(true);
        holder.hostnameresults.setSelected(true);
        holder.hostnameresultsdata.setText(sa.gethostname());
        holder.hostnameresultsdata.setSelected(true);
        holder.tasksresults.setSelected(true);
        holder.tasksresultsdata.setText(sa.gettasks());
        holder.tasksresultsdata.setSelected(true);
        holder.resultsresults.setSelected(true);
        holder.resultsresultsdata.setText(sa.getresults());
        holder.resultsresultsdata.setSelected(true);
        holder.periodicresults.setSelected(true);
        holder.periodicresultsdata.setText(sa.getperiodic());
        holder.periodicresultsdata.setSelected(true);
        return row;
    }
    static class ResultInfoHolder
    {
        TextView hashresults;
        TextView hashresultsdata;
        TextView idresults;
        TextView idresultsdata;
        TextView timeresults;
        TextView timeresultsdata;
        TextView hostnameresults;
        TextView hostnameresultsdata;
        TextView tasksresults;
        TextView tasksresultsdata;
        TextView resultsresults;
        TextView resultsresultsdata;
        TextView periodicresults;
        TextView periodicresultsdata;
    }
}
