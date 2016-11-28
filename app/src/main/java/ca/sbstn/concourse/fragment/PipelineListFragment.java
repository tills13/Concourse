package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

import ca.sbstn.concourse.ConcourseActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.adapter.PipelineListAdapter;
import ca.sbstn.concourse.api.model.Concourse;
import ca.sbstn.concourse.api.model.Pipeline;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class PipelineListFragment extends Fragment {
    public static final String ARG_CI_NAME = "ARG_CI_NAME";

    protected Realm realm;
    protected Concourse ci;
    protected ConcourseActivity context;

    protected View layout;
    protected SwipeRefreshLayout refreshLayout;
    protected ListView pipelineListView;
    protected OnPipelineSelectedListener onPipelineSelectedListener;

    public PipelineListFragment() {}

    public static PipelineListFragment newInstance(String ciName) {
        PipelineListFragment fragment = new PipelineListFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_CI_NAME, ciName);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (ConcourseActivity) context;
        this.onPipelineSelectedListener = (ConcourseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.pipeline_list_fragment, container, false);

        this.refreshLayout = (SwipeRefreshLayout) this.layout.findViewById(R.id.refresh_container);
        this.pipelineListView = (ListView) this.layout.findViewById(R.id.pipelines);
        this.pipelineListView.setAdapter(new PipelineListAdapter(this.getContext()));

        this.pipelineListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                PipelineListFragment.this.loadPipelines();
            }
        });

        this.pipelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pipeline pipeline = ((PipelineListAdapter) adapterView.getAdapter()).getItem(i);
                PipelineListFragment.this.onPipelineSelectedListener.onPipelineSelected(pipeline);
            }
        });

        this.loadPipelines();

        return this.layout;
    }

    public void loadPipelines() {
        this.context.getApi().getPipelines().enqueue(new Callback<List<Pipeline>>() {
            @Override
            public void onResponse(Call<List<Pipeline>> call, Response<List<Pipeline>> response) {
                layout.findViewById(R.id.loading_ci).setVisibility(View.GONE);
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ((PipelineListAdapter) pipelineListView.getAdapter()).setPipelines(response.body());
                    ((PipelineListAdapter) pipelineListView.getAdapter()).notifyDataSetChanged();
                } else {
                    Snackbar.make(layout, String.format(
                        Locale.getDefault(),
                        "Something went wrong (code: %d)",
                        response.code()
                    ), Snackbar.LENGTH_INDEFINITE).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pipeline>> call, Throwable t) {
                layout.findViewById(R.id.loading_ci).setVisibility(View.GONE);
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (layout != null) {
                    Snackbar.make(layout, String.format(
                        Locale.getDefault(),
                        "%s",
                        t.getMessage()
                    ), Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        });
    }

    public interface OnPipelineSelectedListener {
        void onPipelineSelected(Pipeline pipeline);
    }
}
