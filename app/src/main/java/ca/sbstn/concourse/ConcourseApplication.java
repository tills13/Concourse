package ca.sbstn.concourse;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ConcourseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfig);
    }
}
