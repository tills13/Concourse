package ca.sbstn.concourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ca.sbstn.concourse.adapter.ConcourseListAdapter;
import ca.sbstn.concourse.adapter.PipelineListAdapter;
import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.api.model.Pipeline;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    protected Realm realm;
    protected ListView cListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfig);

        this.realm = Realm.getDefaultInstance();
        this.cListView = (ListView) this.findViewById(R.id.concourse_servers);

        this.cListView.setAdapter(new ConcourseListAdapter(this));

        List<Concourse> servers = this.realm.where(Concourse.class).findAll();
        ((ConcourseListAdapter) this.cListView.getAdapter()).setServers(servers);

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://concourse.rdbrck.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ConcourseAPIService concourse = retrofit.create(ConcourseAPIService.class);

        Call<List<Pipeline>> call = concourse.getPipelines();

        call.enqueue(new Callback<List<Pipeline>>() {
            @Override
            public void onResponse(Call<List<Pipeline>> call, Response<List<Pipeline>> response) {
                List<Pipeline> pipelines = response.body();

                ((PipelineListAdapter) list.getAdapter()).setPipelines(pipelines);
                ((PipelineListAdapter) list.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Pipeline>> call, Throwable t) {

            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Concourse> servers = this.realm.where(Concourse.class).findAll();
        ((ConcourseListAdapter) this.cListView.getAdapter()).setServers(servers);
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
