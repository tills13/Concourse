package ca.sbstn.concourse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sbstn.concourse.R;
import ca.sbstn.concourse.api.model.Build;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;

public class JobsListAdapter extends BaseAdapter {
    protected  List<Job> jobs = new ArrayList<>();
    protected Context context;
    protected LayoutInflater inflater;

    public JobsListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return this.jobs.size();
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public Job getItem(int position) {
        return this.jobs.get(position);
    }

    public List<Job> getItems() {
        return this.jobs;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.pipeline_list_item, null);
        }

        Job job = this.getItem(position);

        ((TextView) convertView.findViewById(R.id.pipeline_name)).setText(job.getName());
        //((TextView) convertView.findViewById(R.id.pipeline_status_text)).setText(job.isPaused() ? "paused" : "");

        Build lastBuild = job.getFinishedBuild();


        int color = lastBuild == null ? android.R.color.white : lastBuild.getStatusColor();
        convertView.findViewById(R.id.status_indicator).setBackgroundColor(color);

        //((ImageView) convertView.findViewById(R.id.pipeline_status)).setImageDrawable(this.context.getDrawable(R.drawable.));

        return convertView;
    }
}
