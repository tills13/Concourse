package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.sbstn.concourse.ManageCIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.adapter.CIListAdapter;
import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class CIListFragment extends Fragment {
    protected ListView ciList;
    protected TextView listEmptyNotice;
    protected OnCISelectedListener onCISelectedListener;

    public CIListFragment() {}

    public static CIListFragment newInstance() {
        return new CIListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.onCISelectedListener = (ManageCIActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();

        Realm.init(this.getContext());
        Realm realm = Realm.getDefaultInstance();
        List<Concourse> ciServers = realm.where(Concourse.class).findAll();

        this.listEmptyNotice.setVisibility(ciServers.size() == 0 ? View.VISIBLE : View.GONE);

        ((CIListAdapter) this.ciList.getAdapter()).setServers(ciServers);
        ((CIListAdapter) this.ciList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ci_list_fragment, container, false);

        this.ciList = (ListView) view.findViewById(R.id.ci_list);
        this.listEmptyNotice = (TextView) view.findViewById(R.id.ci_list_empty_notice);

        CIListAdapter adapter = new CIListAdapter(this.getContext());
        this.ciList.setAdapter(adapter);

        this.ciList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Concourse ci = ((CIListAdapter) adapterView.getAdapter()).getItem(i);

                if (CIListFragment.this.onCISelectedListener != null) {
                    CIListFragment.this.onCISelectedListener.onCISelected(ci);
                }
            }
        });

        this.ciList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Concourse ci = ((CIListAdapter) adapterView.getAdapter()).getItem(i);

                if (CIListFragment.this.onCISelectedListener == null) {
                    return false;
                }

                return CIListFragment.this.onCISelectedListener.onCILongPressed(ci);
            }
        });

        return view;
    }

    public interface OnCISelectedListener {
        void onCISelected(Concourse ci);
        boolean onCILongPressed(Concourse ci);
    }
}
