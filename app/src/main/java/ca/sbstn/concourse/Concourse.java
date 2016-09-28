package ca.sbstn.concourse;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by tills13 on 9/27/2016.
 */

public class Concourse extends Application {
    public Concourse() {
        super();

        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfig);
    }
}
