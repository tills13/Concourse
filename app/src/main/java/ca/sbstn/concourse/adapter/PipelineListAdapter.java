package ca.sbstn.concourse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import ca.sbstn.concourse.R;
import ca.sbstn.concourse.api.model.Pipeline;

public class PipelineListAdapter extends BaseAdapter {
    protected  List<Pipeline> pipelines = new ArrayList<>();
    protected Context context;
    protected LayoutInflater inflater;
    protected OnPipelinePausedListener onPipelinePausedListener;

    protected boolean showPipelineStatus = true;

    public PipelineListAdapter(Context context) {
        this(context, null);
    }

    public PipelineListAdapter(Context context, OnPipelinePausedListener onPipelinePausedListener) {
        this.context = context;
        this.onPipelinePausedListener = onPipelinePausedListener;

        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return this.pipelines.size();
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public void clearPipelines() {
        this.pipelines = new ArrayList<>();
    }

    @Override
    public Pipeline getItem(int position) {
        return this.pipelines.get(position);
    }

    public List<Pipeline> getItems() {
        return this.pipelines;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setShowPipelineStatus(boolean showPipelineStatus) {
        this.showPipelineStatus = showPipelineStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.pipeline_list_item, null);
        }

        Pipeline pipeline = this.getItem(position);

        ((TextView) convertView.findViewById(R.id.pipeline_name)).setText(pipeline.getName());
        ((TextView) convertView.findViewById(R.id.pipeline_status_text)).setText(pipeline.isPaused() ? "paused" : "");

        if (this.showPipelineStatus) {
            convertView.findViewById(R.id.status_indicator).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.pipeline_status_text).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.status_indicator).setBackgroundColor(Color.parseColor(pipeline.isPaused() ? "#FFEE58" : "#66BB6A"));
        } else {
            convertView.findViewById(R.id.status_indicator).setVisibility(View.GONE);
            convertView.findViewById(R.id.pipeline_status_text).setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.context.getDrawable(pipeline.isPaused() ? R.drawable.ic_play_arrow : R.drawable.ic_pause);
            ((ImageView) convertView.findViewById(R.id.pipeline_status)).setImageDrawable(drawable);
        }

        return convertView;
    }

    public interface OnPipelinePausedListener {
        void onPipelinePaused(Pipeline pipeline);
    }
}
