package anaptuksh.skyvalos.gkountas.NmapAndroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Handles tab position changes.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {
    int tab_number;
    public MyPagerAdapter(FragmentManager myfragman, int tab_number) {
        super(myfragman);
        this.tab_number=tab_number;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ShowSAsFragment showsas_frag=new ShowSAsFragment();
                return showsas_frag;
            case 1:
                JobsFragment jobs_frag=new JobsFragment();
                return jobs_frag;
            case 2:
                ResultsFragment results_frag=new ResultsFragment();
                return results_frag;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return tab_number;
    }
}
