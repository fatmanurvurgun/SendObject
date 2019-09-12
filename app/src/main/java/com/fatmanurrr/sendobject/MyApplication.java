package com.fatmanurrr.sendobject;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Lenovo on 9.09.2019.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("denemeR.realm").deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

    }
}
