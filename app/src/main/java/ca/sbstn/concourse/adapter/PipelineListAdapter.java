package ca.sbstn.concourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import ca.sbstn.concourse.R;
import ca.sbstn.concourse.api.model.Pipeline;

/**
 * Created by tills13 on 2016-09-14.
 */
public class PipelineListAdapter extends BaseAdapter {
    protected  List<Pipeline> pipelines = new ArrayList<>();
    protected Context context;
    protected LayoutInflater inflater;

    public PipelineListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return this.pipelines.size();
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.pipeline_list_item, null);
        }

        Pipeline pipeline = this.getItem(position);

        ((TextView) convertView.findViewById(R.id.pipeline_name)).setText(pipeline.getName());
        ((TextView) convertView.findViewById(R.id.pipeline_status_text)).setText(pipeline.isPaused() ? "paused" : "");

        //((ImageView) convertView.findViewById(R.id.pipeline_status)).setImageDrawable(this.context.getDrawable(R.drawable.));

        return convertView;
    }
}
