package ca.sbstn.concourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ca.sbstn.concourse.adapter.CIListAdapter;
import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    protected Realm realm;
    protected ListView cListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfig);

        this.realm = Realm.getDefaultInstance();
        this.cListView = (ListView) this.findViewById(R.id.concourse_servers);


        this.cListView.setAdapter(new CIListAdapter(this));

        List<Concourse> servers = this.realm.where(Concourse.class).findAll();
        ((CIListAdapter) this.cListView.getAdapter()).setServers(servers);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Concourse> servers = this.realm.where(Concourse.class).findAll();
        ((CIListAdapter) this.cListView.getAdapter()).setServers(servers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("New").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, EditServerActivity.class);
                startActivity(intent);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
