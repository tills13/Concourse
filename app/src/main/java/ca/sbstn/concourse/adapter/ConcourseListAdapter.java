package ca.sbstn.concourse.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sbstn.concourse.api.model.Concourse;

/**
 * Created by tills13 on 14/09/16.
 */
public class ConcourseListAdapter extends BaseAdapter {
    List<Concourse> servers = new ArrayList<>();

    protected LayoutInflater inflater;
    protected Context context;

    public ConcourseListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.servers.size();
    }

    @Override
    public Concourse getItem(int position) {
        return this.servers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setServers(List<Concourse> servers) {
        this.servers = servers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(this.context);
        }

        ((TextView) convertView).setText(this.getItem(position).getName());
        return convertView;
    }
}
