package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Locale;

import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.fragment.CIActivityFragment;
import io.realm.Realm;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
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

        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
            .add(R.id.fragment_container, CIActivityFragment.newInstance(ciName))
            .commit();
    }

    public ConcourseAPIService getApi() {
        return this.api;
    }
}
