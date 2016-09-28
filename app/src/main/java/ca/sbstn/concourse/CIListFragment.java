package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import ca.sbstn.concourse.adapter.CIListAdapter;
import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class CIListFragment extends Fragment {
    protected ListView ciList;

    public CIListFragment() {}

    public static CIListFragment newInstance() {
        CIListFragment fragment = new CIListFragment();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        Realm.init(this.getContext());
        Realm realm = Realm.getDefaultInstance();
        List<Concourse> ciServers = realm.where(Concourse.class).findAll();

        ((CIListAdapter) this.ciList.getAdapter()).setServers(ciServers);
        ((CIListAdapter) this.ciList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ci_list_fragment, container, false);

        Log.d("adasd", "hjere2");

        this.ciList = (ListView) view.findViewById(R.id.ci_list);

        CIListAdapter adapter = new CIListAdapter(this.getContext());
        this.ciList.setAdapter(adapter);

        return view;
    }
}
