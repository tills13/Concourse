package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.sbstn.concourse.CIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class CIActivityFragment extends Fragment {
    public static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected Realm realm;
    protected Concourse ci;
    protected CIActivity context;

    public CIActivityFragment() {}

    public static CIActivityFragment newInstance(String ciName) {
        CIActivityFragment fragment = new CIActivityFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_CI_NAME, ciName);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (CIActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ci, container, false);
    }
}
