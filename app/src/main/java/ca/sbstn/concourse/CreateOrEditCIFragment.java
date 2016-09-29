package ca.sbstn.concourse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;

public class CreateOrEditCIFragment extends Fragment {
    public static final String TAG = "CreateOrEditCIFragment";
    protected static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected String ciName; // name of ci server we are editing

    protected Realm realm;

    protected EditText ciNameEditText;
    protected EditText ciHostEditText;
    protected EditText ciProxyHostEditText;
    protected EditText ciProxyPortEditText;

    private OnFragmentInteractionListener mListener;

    public CreateOrEditCIFragment() {}

    public static CreateOrEditCIFragment newInstance(String name) {
        CreateOrEditCIFragment fragment = new CreateOrEditCIFragment();

        if (name != null) {
            Bundle arguments = new Bundle();
            arguments.putString(ARG_CI_NAME, name);

            fragment.setArguments(arguments);
        }

        return fragment;
    }

    public static CreateOrEditCIFragment newInstance() {
        return CreateOrEditCIFragment.newInstance(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.realm = Realm.getDefaultInstance();

        if (this.getArguments() != null) {
             this.ciName = this.getArguments().getString(ARG_CI_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_or_edit_ci, container, false);

        this.ciNameEditText = (EditText) view.findViewById(R.id.ci_name);
        this.ciHostEditText = (EditText) view.findViewById(R.id.ci_host);
        this.ciProxyHostEditText = (EditText) view.findViewById(R.id.ci_proxy_host);
        this.ciProxyPortEditText = (EditText) view.findViewById(R.id.ci_proxy_port);

        if (this.ciName != null) {
            Concourse ci = this.realm.where(Concourse.class).equalTo("name", this.ciName).findFirst();

            if (ci != null) {
                this.ciNameEditText.setText(ci.getName());
                this.ciHostEditText.setText(ci.getHost());
                this.ciProxyHostEditText.setText(ci.getProxyHost());
                this.ciProxyPortEditText.setText(ci.getProxyPort());
            } else {
                this.ciName = null; // why
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onSave() {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        Concourse ci = (this.ciName != null && !this.ciName.equals("")) ?
            realm.where(Concourse.class).equalTo("name", this.ciName).findFirst() :
            realm.createObject(Concourse.class); // create a tracked realm object

        ci.setName(this.ciNameEditText.getText().toString());
        ci.setHost(this.ciHostEditText.getText().toString());
        ci.setProxyHost(this.ciProxyHostEditText.getText().toString());
        ci.setProxyPort(Integer.parseInt(this.ciProxyPortEditText.getText().toString()));

        realm.commitTransaction();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}
