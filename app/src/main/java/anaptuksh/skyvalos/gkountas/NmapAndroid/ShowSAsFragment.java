package anaptuksh.skyvalos.gkountas.NmapAndroid;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * GUI for first tab (SA).
 */
public class ShowSAsFragment extends Fragment {
    private static View myview;
    private static Button mybutton;
    private static ListView mylistview;
    CheckConnectivity cc = new CheckConnectivity();
    public ShowSAsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_showsas, container, false);
        mybutton = myview.findViewById(R.id.button4);
        mylistview =  myview.findViewById(R.id.listView3);
        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mybutton.setEnabled(false);
                if (cc.isOnline(getContext()) == true) {
                    new SAInfoTask(getActivity(),myview).execute();
                }
                else {
                    Toast.makeText(getContext(), "No internet access!", Toast.LENGTH_SHORT).show();
                    mybutton.setEnabled(true);
                }
            }
        });
        return myview;
    }
}
