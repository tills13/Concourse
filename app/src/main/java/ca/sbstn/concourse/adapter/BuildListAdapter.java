package ca.sbstn.concourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sbstn.concourse.R;
import ca.sbstn.concourse.api.model.Build;

public class BuildListAdapter extends BaseAdapter {
    private  List<Build> builds = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    private boolean showBuildStatus = true;

    public BuildListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return this.builds.size();
    }

    public void addBuild(Build build) { this.builds.add(0, build); }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    @Override
    public Build getItem(int position) {
        return this.builds.get(position);
    }

    public List<Build> getItems() {
        return this.builds;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setShowBuildStatus(boolean showBuildStatus) {
        this.showBuildStatus = showBuildStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.build_list_item, null);
        }

        Build build = this.getItem(position);

        ((TextView) convertView.findViewById(R.id.build_id_and_name)).setText(build.getName());
        ((TextView) convertView.findViewById(R.id.build_status_text)).setText(build.getStatus());

        if (this.showBuildStatus) {
            convertView.findViewById(R.id.status_indicator).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.status_indicator).setBackgroundColor(build.getStatusColor());
        } else {
            convertView.findViewById(R.id.status_indicator).setVisibility(View.GONE);
        }

        return convertView;
    }
}
