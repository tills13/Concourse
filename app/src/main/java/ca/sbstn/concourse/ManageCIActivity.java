package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.fragment.CIListFragment;
import ca.sbstn.concourse.fragment.CreateOrEditCIFragment;

public class ManageCIActivity extends AppCompatActivity implements CIListFragment.OnCISelectedListener, CreateOrEditCIFragment.OnCreateOrSaveCIListener {
    protected FragmentManager fm;

    protected FloatingActionButton fab;
    protected ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_layout);

        this.fm = this.getSupportFragmentManager();

        this.fab = (FloatingActionButton) this.findViewById(R.id.fab);

        this.setSupportActionBar((Toolbar) this.findViewById(R.id.toolbar));
        this.actionBar = this.getSupportActionBar();

        if (this.actionBar != null) {
            this.actionBar.setTitle("Concourse");
        }

        FragmentTransaction transaction = this.fm.beginTransaction();
        transaction.add(R.id.fragment_container, CIListFragment.newInstance()).commit();

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = ManageCIActivity.this.fm.beginTransaction();

                transaction.replace(R.id.fragment_container, CreateOrEditCIFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.fm.beginTransaction().replace(
            R.id.fragment_container,
            CIListFragment.newInstance()
        ).commit();
    }

    @Override
    public boolean onCILongPressed(Concourse ci) {
        this.fm.beginTransaction()
            .replace(R.id.fragment_container, CreateOrEditCIFragment.newInstance(ci.getName()))
            .addToBackStack(null)
            .commit();

        return true;
    }

    @Override
    public void onCISelected(Concourse ci) {

    }

    @Override
    public void onCreateOrSaveCI(Concourse ci) {
        this.fm.popBackStack();
    }
}
