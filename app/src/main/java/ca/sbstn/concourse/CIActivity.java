package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.net.InetSocketAddress;
import java.net.Proxy;

import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.fragment.CIActivityFragment;
import io.realm.Realm;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.name;

public class CIActivity extends AppCompatActivity {
    public static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected ActionBar actionBar;
    protected Realm realm;
    protected Concourse ci;
    protected ConcourseAPIService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ci);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String ciName = this.getIntent().getStringExtra(ARG_CI_NAME);

        this.actionBar = this.getSupportActionBar();

        if (this.actionBar != null) {
            this.actionBar.setTitle(ciName);
        }

        this.realm = Realm.getDefaultInstance();
        this.ci = this.realm.where(ca.sbstn.concourse.api.model.Concourse.class).equalTo("name", name).findFirst();

        String ciHost = ci.getHost();

        HttpUrl url = new HttpUrl.Builder()
            .host(ciHost)
            .addPathSegments("api/v1/")
            .build();

        Retrofit.Builder rBuilder = new Retrofit.Builder();

        if (ci.requiresProxy()) {
            java.net.Proxy proxy = new Proxy(Proxy.Type.HTTP,  new InetSocketAddress(ci.getProxyHost(), ci.getProxyPort()));
            OkHttpClient client = new OkHttpClient.Builder().proxy(proxy).build();

            rBuilder.client(client);
        }


        Retrofit retrofit = rBuilder.baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        this.api = retrofit.create(ConcourseAPIService.class);

        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
            .add(R.id.fragment_container, CIActivityFragment.newInstance(ciName))
            .commit();
    }

    public ConcourseAPIService getApi() {
        return this.api;
    }
}
