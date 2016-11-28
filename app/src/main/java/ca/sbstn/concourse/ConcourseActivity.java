package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Locale;

import ca.sbstn.concourse.adapter.PipelineListAdapter;
import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Build;
import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import ca.sbstn.concourse.fragment.BuildDetailsFragment;
import ca.sbstn.concourse.fragment.PipelineListFragment;
import ca.sbstn.concourse.fragment.JobDetailsFragment;
import ca.sbstn.concourse.fragment.PipelineJobsFragment;

import io.realm.Realm;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConcourseActivity extends AppCompatActivity implements
    PipelineListFragment.OnPipelineSelectedListener,
    PipelineJobsFragment.OnJobSelectedListener,
    JobDetailsFragment.OnBuildSelectedListener {
    public static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected ActionBar actionBar;
    protected Realm realm;

    protected Concourse ci;

    protected ConcourseAPIService api;
    protected FragmentManager fm;

    protected DrawerLayout drawerContainer;
    protected LinearLayout drawerLayout;
    protected ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ci);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerContainer = (DrawerLayout) findViewById(R.id.drawer_container);
        drawerLayout = (LinearLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onPipelineSelected((Pipeline) adapterView.getAdapter().getItem(i), false);
                drawerContainer.closeDrawer(drawerLayout, true);
            }
        });

        PipelineListAdapter adapter = new PipelineListAdapter(this);
        adapter.setShowPipelineStatus(false);
        drawerList.setAdapter(adapter);

        String ciName = this.getIntent().getStringExtra(ARG_CI_NAME);

        this.actionBar = this.getSupportActionBar();

        if (this.actionBar != null) {
            this.actionBar.setTitle(ciName);
            this.actionBar.setDisplayHomeAsUpEnabled(true);
            this.actionBar.setHomeButtonEnabled(true);
        }

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerContainer, toolbar, R.string.app_name, R.string.app_name) {
            String previousTitle;
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                actionBar.setTitle(previousTitle == null ? ci.getName() : previousTitle);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                previousTitle = actionBar.getTitle() == null ? "" : actionBar.getTitle().toString();
                actionBar.setTitle("Pipelines");
            }
        };

        drawerContainer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        this.realm = Realm.getDefaultInstance();
        this.ci = this.realm.where(ca.sbstn.concourse.api.model.Concourse.class).equalTo("name", ciName).findFirst();

        HttpUrl url = HttpUrl.parse(ci.getHost())
            .newBuilder()
            .addPathSegments("api/v1/")
            .build();

        Retrofit.Builder rBuilder = new Retrofit.Builder();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();

                if (false) {
                    builder.addHeader("Authorization", String.format(Locale.getDefault(), "%s %s", "Bearer", "token"));
                }

                return chain.proceed(builder.build());
            }
        });

        if (ci.requiresProxy()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,  new InetSocketAddress(ci.getProxyHost(), ci.getProxyPort()));
            clientBuilder.proxy(proxy);
        }

        rBuilder.client(clientBuilder.build());

        Retrofit retrofit = rBuilder.baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        this.api = retrofit.create(ConcourseAPIService.class);

        this.fm = this.getSupportFragmentManager();
        this.fm.beginTransaction()
            .add(R.id.fragment_container, PipelineListFragment.newInstance(ciName))
            .commit();

        this.populateSidebarPipelines();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((PipelineListAdapter) this.drawerList.getAdapter()).clearPipelines();
        ((PipelineListAdapter) this.drawerList.getAdapter()).notifyDataSetChanged();

        this.populateSidebarPipelines();
    }

    public void populateSidebarPipelines() {
        this.api.getPipelines().enqueue(new Callback<List<Pipeline>>() {
            @Override
            public void onResponse(Call<List<Pipeline>> call, retrofit2.Response<List<Pipeline>> response) {
                ((PipelineListAdapter) drawerList.getAdapter()).setPipelines(response.body());
                ((PipelineListAdapter) drawerList.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Pipeline>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBuildSelected(Build build) {
        this.actionBar.setTitle(String.format(Locale.getDefault(), "Build #%d", build.getId()));
        this.actionBar.setSubtitle(build.getPipelineName());

        this.fm.beginTransaction()
            .replace(R.id.fragment_container, BuildDetailsFragment.newInstance(build))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onJobSelected(Pipeline pipeline, Job job) {
        this.actionBar.setTitle(pipeline.getName());
        this.actionBar.setSubtitle(job.getName());

        this.fm.beginTransaction()
            .replace(R.id.fragment_container, JobDetailsFragment.newInstance(pipeline, job))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onPipelineSelected(Pipeline pipeline) {
        this.onPipelineSelected(pipeline, true);
    }

    public void onPipelineSelected(Pipeline pipeline, boolean addToBackStack) {
        this.actionBar.setTitle(pipeline.getName());
        this.actionBar.setSubtitle(null);

        FragmentTransaction ft = this.fm.beginTransaction()
            .replace(R.id.fragment_container, PipelineJobsFragment.newInstance(pipeline));

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.commit();
    }

    public ConcourseAPIService getApi() {
        return this.api;
    }
}
