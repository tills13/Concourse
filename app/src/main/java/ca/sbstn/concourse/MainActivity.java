package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.List;

import ca.sbstn.concourse.adapter.PipelineListAdapter;
import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Pipeline;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView list = (ListView) this.findViewById(R.id.pipelines);
        list.setAdapter(new PipelineListAdapter(this));


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://concourse.rdbrck.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ConcourseAPIService concourse = retrofit.create(ConcourseAPIService.class);

        Call<List<Pipeline>> call = concourse.getPipelines ();

        call.enqueue((Call<List<Pipeline>> mCall, Response<List<Pipeline>> response) -> {

        });

        call.enqueue(new Callback<List<Pipeline>>() {
            @Override
            public void onResponse(Call<List<Pipeline>> call, Response<List<Pipeline>> response) {
                List<Pipeline> pipelines = response.body();

                ((PipelineListAdapter) list.getAdapter()).setPipelines(pipelines);
                ((PipelineListAdapter) list.getAdapter()).notifyDataSetChanged();
                //list.invalidate();
            }

            @Override
            public void onFailure(Call<List<Pipeline>> call, Throwable t) {

            }
        });

    }
}
