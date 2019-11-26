package ir.payebash;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

import ir.payebash.Classes.DataBaseHelper;
import ir.payebash.DI.AppModule;
import ir.payebash.DI.DaggerMainComponent;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.DI.MainComponent;
import ir.payebash.DI.NetModule;

public class Application extends android.app.Application {


    public static SQLiteDatabase database;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static int myAds = 42907631;
    public static Resources resources;
    public static Activity activity;
    private static MainComponent component;

    public static Application get(AppCompatActivity activity) {
        return (Application) activity.getApplication();
    }

    public static MainComponent getComponent() {
        return component;
    }

   /* @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        MainComponent appComponent = DaggerMainComponent.builder().application(this).build();
        appComponent.inject(this);

        return appComponent;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        resources = getResources();

        component = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .imageLoaderMoudle(new ImageLoaderMoudle(this))
                .netModule(new NetModule())
                .build();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        try {
            new DataBaseHelper(getApplicationContext()) {
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    /*String DB_PATH = Environment.getDataDirectory() + "/data/" + getBaseContext().getPackageName() + "/databases/Paye.db";
                    String myPath = DB_PATH;
                    SQLiteDatabase.deleteDatabase(new File(myPath));
                    getBaseContext().deleteDatabase(Environment.getDataDirectory() + "/data/" + getBaseContext().getPackageName() + "/databases/Paye.db");*/
                }

                @Override
                public void onCreate(SQLiteDatabase db) {
                }
            };

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        String path = Environment.getDataDirectory() + "/data/" + getPackageName() + "/databases/Paye.db";
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

    }
}
