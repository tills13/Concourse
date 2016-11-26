package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.HttpURLConnection;
import java.util.List;

import ca.sbstn.concourse.CIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.adapter.JobsListAdapter;
import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class PipelineJobsFragment extends Fragment {
    public static final String ARG_PIPELINE_NAME = "ARG_PIPELINE_NAME";

    protected Realm realm;
    protected Pipeline pipeline;
    protected CIActivity context;

    protected View layout;
    protected SwipeRefreshLayout refreshLayout;
    protected ListView jobsListView;

    protected OnJobSelectedListener onJobSelectedListener;

    public PipelineJobsFragment() {}

    public static PipelineJobsFragment newInstance(Pipeline pipeline) {
        PipelineJobsFragment fragment = new PipelineJobsFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_PIPELINE_NAME, pipeline);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (CIActivity) context;
        this.onJobSelectedListener = (OnJobSelectedListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getArguments() != null) {
            this.pipeline = (Pipeline) this.getArguments().getSerializable(ARG_PIPELINE_NAME);
        }

        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pipeline_details_view_menu, menu);

        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.context.getDrawable(this.pipeline.isPaused() ? R.drawable.ic_play_arrow : R.drawable.ic_pause);
            drawable.setTint(Color.parseColor("#ffffff"));
            menu.findItem(R.id.action_pipeline_status).setIcon(drawable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pipeline_status: {
                ConcourseAPIService api = this.context.getApi();
                Call<ResponseBody> call = this.pipeline.isPaused() ? api.unpausePipeline(this.pipeline.getName()) : api.pausePipeline(this.pipeline.getName());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                Drawable drawable = context.getDrawable(pipeline.isPaused() ? R.drawable.ic_pause : R.drawable.ic_play_arrow);
                                drawable.setTint(Color.parseColor("#ffffff"));
                                item.setIcon(drawable);
                            }
                        } else {
                            // ??
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {}
                });

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.jobs_list_fragment, container, false);

        this.refreshLayout = (SwipeRefreshLayout) this.layout.findViewById(R.id.refresh_container);
        this.jobsListView = (ListView) this.layout.findViewById(R.id.jobs);
        this.jobsListView.setAdapter(new JobsListAdapter(this.getContext()));

        this.jobsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                refreshLayout.setEnabled(firstVisibleItem == 0 && absListView.getTop() == 0);
            }
        });

        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PipelineJobsFragment.this.loadPipelineJobs();
            }
        });

        this.jobsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Job job = ((JobsListAdapter) adapterView.getAdapter()).getItem(i);
                PipelineJobsFragment.this.onJobSelectedListener.onJobSelected(PipelineJobsFragment.this.pipeline, job);
            }
        });

        this.loadPipelineJobs();

        return this.layout;
    }

    public void loadPipelineJobs() {
        ConcourseAPIService api = this.context.getApi();

        Call<List<Job>> call = this.pipeline.hasTeam() ?
            api.getTeamPipelineJobs(this.pipeline.getTeam(), this.pipeline.getName()) :
            api.getPipelineJobs(this.pipeline.getName());

        Log.d("PipelinejobsFragment", call.request().url().toString());

        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                layout.findViewById(R.id.loading_jobs).setVisibility(View.GONE);

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ((JobsListAdapter) jobsListView.getAdapter()).setJobs(response.body());
                    ((JobsListAdapter) jobsListView.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                layout.findViewById(R.id.loading_jobs).setVisibility(View.GONE);

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public interface OnJobSelectedListener {
        void onJobSelected(Pipeline pipeline, Job job);
    }
}
