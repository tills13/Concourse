package ca.sbstn.concourse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ca.sbstn.concourse.api.model.Concourse;
import io.realm.Realm;

/**
 * Created by tills13 on 14/09/16.
 */
public class EditServerActivity extends AppCompatActivity {
    protected Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_server);

        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Save").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                realm.beginTransaction();

                int id = (int) realm.where(Concourse.class).count();

                Concourse server = realm.createObject(Concourse.class);
                server.setId(id);
                server.setName(((TextView) EditServerActivity.this.findViewById(R.id.edit_server_name)).getText().toString());

                realm.commitTransaction();

                EditServerActivity.this.finish();

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
