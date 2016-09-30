package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

import ca.sbstn.concourse.CIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.adapter.PipelineListAdapter;
import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class CIActivityFragment extends Fragment {
    public static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected Realm realm;
    protected Concourse ci;
    protected CIActivity context;

    protected View layout;
    protected ListView pipelineListView;

    public CIActivityFragment() {}

    public static CIActivityFragment newInstance(String ciName) {
        CIActivityFragment fragment = new CIActivityFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_CI_NAME, ciName);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (CIActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.fragment_ci, container, false);

        this.pipelineListView = (ListView) this.layout.findViewById(R.id.pipelines);
        this.pipelineListView.setAdapter(new PipelineListAdapter(this.getContext()));
        //this.pipelineListView.setEmptyView(this.layout.findViewById(R.id.ci_list_empty_notice));

        this.pipelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pipeline pipeline = ((PipelineListAdapter) adapterView.getAdapter()).getItem(i);
                String name = pipeline.getName();

                context.getApi().getPipelineJobs(name).enqueue(new Callback<List<Job>>() {
                    @Override
                    public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                        for (Job j : response.body()) {
                            Log.d("NAME", j.getFinishedBuild().getName());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Job>> call, Throwable t) {

                    }
                });
            }
        });

        this.context.getApi().getPipelines().enqueue(new Callback<List<Pipeline>>() {
            @Override
            public void onResponse(Call<List<Pipeline>> call, Response<List<Pipeline>> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ((PipelineListAdapter) pipelineListView.getAdapter()).setPipelines(response.body());
                    ((PipelineListAdapter) pipelineListView.getAdapter()).notifyDataSetChanged();

                    CIActivityFragment.this.layout.findViewById(R.id.loading_ci).setVisibility(View.GONE);

                    for (Pipeline pipeline : ((PipelineListAdapter) pipelineListView.getAdapter()).getItems()) {
                        Call<List<Job>> mCall = pipeline.getTeam() == null ?
                            context.getApi().getPipelineJobs(pipeline.getName()) :
                            context.getApi().getTeamPipelineJobs(pipeline.getTeam(), pipeline.getName());

                        mCall.enqueue(new Callback<List<Job>>() {
                            @Override
                            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                                Log.d("asdasdasdasd", call.request().url().toString());
                                Job lastBuild = null;

                                List<Job> jobs = response.body();

                                if (jobs != null) {
                                    for (Job job : response.body()) {
                                        Log.d("asdasdasdasdasd", job.getName());
                                        if (job.getFinishedBuild() != null) {
                                            Log.d("asdasdasdasdasd", job.getFinishedBuild().getEndTime() + "");
                                        } else {
                                            Log.d("asdasdasdasdasd", "no last build");
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Job>> call, Throwable t) {
                                Log.d("ASDASDASDAsd", t.getMessage());
                            }
                        });
                    }
                } else {
                    Snackbar.make(CIActivityFragment.this.layout, String.format(
                        Locale.getDefault(),
                        "Something went wrong (code: %d)",
                        response.code()
                    ), Snackbar.LENGTH_INDEFINITE).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pipeline>> call, Throwable t) {
                Log.d("asdasdasd", "ERROR");
            }
        });

        return this.layout;
    }
}
