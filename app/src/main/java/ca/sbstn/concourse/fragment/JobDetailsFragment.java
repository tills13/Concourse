package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ca.sbstn.concourse.CIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.adapter.BuildListAdapter;
import ca.sbstn.concourse.adapter.JobsListAdapter;
import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Build;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobDetailsFragment extends Fragment {
    private static final String ARG_JOB = "ARG_JOB";
    private static final String ARG_PIPELINE = "ARG_PIPELINE";
    private static final int REFRESH_RATE_SECONDS = 5;

    private Job job;
    private Pipeline pipeline;
    protected CIActivity context;
    private Handler handler;
    private Timer timer;

    private View layout;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout previousBuildContainer;
    private LinearLayout nextBuildContainer;
    private ListView buildList;
    private FloatingActionButton fab;

    private OnBuildSelectedListener onBuildSelectedListener;

    public JobDetailsFragment() {}

    public static JobDetailsFragment newInstance(Pipeline pipeline, Job job) {
        JobDetailsFragment fragment = new JobDetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_PIPELINE, pipeline);
        arguments.putSerializable(ARG_JOB, job);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.pipeline = (Pipeline) this.getArguments().getSerializable(ARG_PIPELINE);
            this.job = (Job) this.getArguments().getSerializable(ARG_JOB);
        }

        this.handler = new Handler(Looper.getMainLooper());
        this.timer = new Timer("refresh_timer");
    }

    @Override
    public void onResume() {
        super.onResume();

        this.timer = new Timer("refresh_timer");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.job_detail_fragment, container, false);

        this.refreshLayout = (SwipeRefreshLayout) this.layout.findViewById(R.id.refresh_container);
        this.nextBuildContainer = (LinearLayout) this.layout.findViewById(R.id.next_build_container);
        this.previousBuildContainer = (LinearLayout) this.layout.findViewById(R.id.previous_build_container);
        this.buildList = (ListView) this.layout.findViewById(R.id.build_list);
        this.fab = (FloatingActionButton) this.layout.findViewById(R.id.fab);

        final BuildListAdapter adapter = new BuildListAdapter(this.getContext());
        this.buildList.setAdapter(adapter);

        this.buildList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Build build = ((BuildListAdapter) adapterView.getAdapter()).getItem(i);
                onBuildSelectedListener.onBuildSelected(build);
            }
        });

        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateJob();
            }
        });

        this.buildList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                //refreshLayout.setEnabled(firstVisibleItem == 0 && absListView.getTop() == 0);
            }
        });

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getApi().createNewBuild(pipeline.getName(), job.getName()).enqueue(new Callback<Build>() {
                    @Override
                    public void onResponse(Call<Build> call, Response<Build> response) {
                        Build build = response.body();

                        setNextBuild(build);
                        adapter.addBuild(build);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Build> call, Throwable t) {

                    }
                });
            }
        });

        updateJob();

        return layout;
    }

    public void getJobBuilds() {
        this.context.getApi().getJobBuilds(this.pipeline.getName(), this.job.getName()).enqueue(new Callback<List<Build>>() {
            @Override
            public void onResponse(Call<List<Build>> call, Response<List<Build>> response) {
                BuildListAdapter adapter = (BuildListAdapter) buildList.getAdapter();
                adapter.setBuilds(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Build>> call, Throwable t) {
                Snackbar.make(layout, String.format(
                    Locale.getDefault(),
                    "%s",
                    t.getMessage()
                ), Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    public void setNextBuild(final Build build) {
        if (build != null) {
            ((TextView) this.nextBuildContainer.findViewById(R.id.next_build_id)).setText(String.format(Locale.getDefault(), "Build #%d", build.getId()));
            ((TextView) this.nextBuildContainer.findViewById(R.id.next_build_status)).setText(build.getStatus());
            ((TextView) this.nextBuildContainer.findViewById(R.id.next_build_status)).setTextColor(build.getStatusColor());

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                // otherwise screw you, get a better phone
                if (build.getStatus().equals("pending") || build.getStatus().equals("started")) {
                    ((ImageView) this.nextBuildContainer.findViewById(R.id.action_button)).setImageDrawable(context.getDrawable(R.drawable.ic_stop));
                    this.nextBuildContainer.findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.getApi().abortBuild(build.getId()).enqueue(new Callback<Build>() {
                                @Override
                                public void onResponse(Call<Build> call, Response<Build> response) {
                                    updateJob();

                                    Snackbar.make(layout, String.format(
                                        Locale.getDefault(),
                                        "Aborted build #%d",
                                        build.getId()
                                    ), Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Build> call, Throwable t) {
                                    updateJob();

                                    Snackbar.make(layout, String.format(
                                        Locale.getDefault(),
                                        "%s",
                                        t.getMessage()
                                    ), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }

            this.nextBuildContainer.setVisibility(View.VISIBLE);
        } else {
            this.nextBuildContainer.setVisibility(View.GONE);
            //((TextView) this.nextBuildContainer.findViewById(R.id.next_build_id)).setText(String.format(Locale.getDefault(), "Build #%d", build.getId()));
            //((TextView) this.nextBuildContainer.findViewById(R.id.next_build_status)).setText(build.getStatus());
        }
    }

    public void setPreviousBuild(Build build) {
        if (build != null) {
            ((TextView) this.previousBuildContainer.findViewById(R.id.previous_build_id)).setText(String.format(Locale.getDefault(), "Build #%d", build.getId()));
            ((TextView) this.previousBuildContainer.findViewById(R.id.previous_build_status)).setText(build.getStatus());
            ((TextView) this.previousBuildContainer.findViewById(R.id.previous_build_status)).setTextColor(build.getStatusColor());

            this.previousBuildContainer.setVisibility(View.VISIBLE);
        } else {
            this.previousBuildContainer.setVisibility(View.GONE);
        }
    }

    public void updateJob() {
        this.refreshLayout.setRefreshing(true);

        ConcourseAPIService api = this.context.getApi();

        Call<Job> call = this.pipeline.hasTeam() ?
            api.getTeamPipelineJob(this.pipeline.getTeam(), this.pipeline.getName(), this.job.getName()) :
            api.getJob(this.pipeline.getName(), this.job.getName());

        Log.d("JobDetailsFragment", call.request().url().toString());

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                refreshLayout.setRefreshing(false);
                job = response.body();

                if (job != null) {
                    JobDetailsFragment.this.setNextBuild(job.getNextBuild());
                    JobDetailsFragment.this.setPreviousBuild(job.getFinishedBuild());

                    ((TextView) layout.findViewById(R.id.job_name)).setText(job.getName());
                }

                getJobBuilds();

                // reset the timer
                timer.purge();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // fml
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateJob();
                            }
                        });
                    }
                }, REFRESH_RATE_SECONDS * 1000);
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (CIActivity) context;
        this.onBuildSelectedListener = (OnBuildSelectedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (this.timer != null) {
            this.timer.purge();
            this.timer.cancel();
            this.timer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.timer != null) {
            this.timer.purge();
            this.timer.cancel();
            this.timer = null;
        }
    }

    public interface OnBuildSelectedListener {
        void onBuildSelected(Build build);
    }
}
