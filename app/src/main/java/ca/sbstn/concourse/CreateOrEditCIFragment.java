package ca.sbstn.concourse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private OnCreateOrSaveCIListener onCreateOrSaveCIListener;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        this.onCreateOrSaveCIListener = (ManageCIActivity) context;
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
                this.ciProxyPortEditText.setText(String.format("%d", ci.getProxyPort()));

                ActionBar actionBar = ((AppCompatActivity) this.getActivity()).getSupportActionBar();

                if (actionBar != null) {
                    actionBar.setTitle(String.format("Editing %s", ci.getName()));
                }
            } else {
                this.ciName = null; // why
            }
        }

        this.setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.create_or_edit_server_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                Concourse ci;

                if ((ci = this.onSave()) != null) {
                    if (this.onCreateOrSaveCIListener != null) {
                        this.onCreateOrSaveCIListener.onCreateOrSaveCI(ci);
                    }

                    return true;
                } else {
                    return false;
                }
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public Concourse onSave() {
        Realm realm = Realm.getDefaultInstance();
        String primaryKey = this.ciNameEditText.getText().toString();

        realm.beginTransaction();

        // TODO: 9/28/2016 cover the case  where the user has changed the name of the instance

        Concourse ci = (this.ciName != null && !this.ciName.equals("")) ?
            realm.where(Concourse.class).equalTo("name", this.ciName).findFirst() :
            realm.createObject(Concourse.class, primaryKey); // create a tracked realm object

        if (this.ciName != null && !this.ciName.equals(primaryKey)) {
            ci.setName(primaryKey);
        }

        ci.setHost(this.ciHostEditText.getText().toString());
        ci.setProxyHost(this.ciProxyHostEditText.getText().toString());

        try {
            ci.setProxyPort(Integer.parseInt(this.ciProxyPortEditText.getText().toString()));
        } catch (NumberFormatException e) {}

        realm.commitTransaction();

        return ci;
    }

    public interface OnCreateOrSaveCIListener {
        void onCreateOrSaveCI(Concourse ci);
    }
}
