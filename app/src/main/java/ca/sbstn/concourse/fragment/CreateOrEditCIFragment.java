package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Locale;

import ca.sbstn.concourse.ManageCIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;

public class CreateOrEditCIFragment extends Fragment {
    public static final String TAG = "CreateOrEditCIFragment";
    protected static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected String ciName; // name of ci server we are editing

    protected Realm realm;

    protected View layout;
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
        this.layout = inflater.inflate(R.layout.create_or_edit_ci, container, false);

        this.ciNameEditText = (EditText) this.layout.findViewById(R.id.ci_name);
        this.ciHostEditText = (EditText) this.layout.findViewById(R.id.ci_host);
        this.ciProxyHostEditText = (EditText) this.layout.findViewById(R.id.ci_proxy_host);
        this.ciProxyPortEditText = (EditText) this.layout.findViewById(R.id.ci_proxy_port);

        if (this.ciName != null) {
            Concourse ci = this.realm.where(Concourse.class).equalTo("name", this.ciName).findFirst();

            if (ci != null) {
                this.ciNameEditText.setText(ci.getName());
                this.ciHostEditText.setText(ci.getHost());
                this.ciProxyHostEditText.setText(ci.getProxyHost());
                this.ciProxyPortEditText.setText(String.format(Locale.getDefault(), "%d", ci.getProxyPort()));

                ActionBar actionBar = ((AppCompatActivity) this.getActivity()).getSupportActionBar();

                if (actionBar != null) {
                    actionBar.setTitle(String.format("Editing %s", ci.getName()));
                }
            } else {
                this.ciName = null; // why
            }
        }

        this.setHasOptionsMenu(true);

        return this.layout;
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

        Concourse ci = (this.ciName != null && !this.ciName.equals("")) ?
            realm.where(Concourse.class).equalTo("name", this.ciName).findFirst() : // fetch realm object
            realm.createObject(Concourse.class, primaryKey); // create a tracked realm object

        if (this.ciName != null && !this.ciName.equals(primaryKey)) {
            ci.setName(primaryKey);
        }

        ci.setHost(this.ciHostEditText.getText().toString());
        ci.setProxyHost(this.ciProxyHostEditText.getText().toString());

        String portString = this.ciProxyPortEditText.getText().toString();
        if (portString.length() != 0) {
            try {
                int port = Integer.parseInt(portString);
                ci.setProxyPort(port < 0 ? -1 : port);
            } catch (NumberFormatException e) {
                Snackbar.make(this.layout, "Invalid proxy port #", Snackbar.LENGTH_LONG).show();
            }
        }

        realm.commitTransaction();

        return ci;
    }

    public interface OnCreateOrSaveCIListener {
        void onCreateOrSaveCI(Concourse ci);
    }
}
