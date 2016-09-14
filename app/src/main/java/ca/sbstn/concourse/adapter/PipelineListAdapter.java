package ca.sbstn.concourse.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sbstn.concourse.api.model.Pipeline;

/**
 * Created by tills13 on 2016-09-14.
 */
public class PipelineListAdapter extends BaseAdapter {
    List<Pipeline> pipelines = new ArrayList<>();
    Context context;

    public PipelineListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.pipelines.size();
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    @Override
    public Object getItem(int position) {
        return this.pipelines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = new TextView(this.context);
        v.setText(((Pipeline) this.getItem(position)).name);

        return v;
    }
}
