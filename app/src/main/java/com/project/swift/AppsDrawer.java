package com.project.swift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelStore;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.project.swift.databinding.ActivityAppsDrawerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import maes.tech.intentanim.CustomIntent;

public class AppsDrawer extends AppCompatActivity {

    PackageManager packageManager;
    ArrayList<AppInfo> apps;
    GridView list;
    ArrayAdapter<AppInfo> adapter;
    ActivityAppsDrawerBinding activityAppsDrawerBinding;
    int images[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_drawer);
        activityAppsDrawerBinding = ActivityAppsDrawerBinding.inflate(getLayoutInflater());
        View view = activityAppsDrawerBinding.getRoot();
        setContentView(view);
        activityAppsDrawerBinding.relativelayout.setBackgroundResource(R.drawable.background1);
        images = new int[]{R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4, R.drawable.background5, R.drawable.background6, R.drawable.background7, R.drawable.background8, R.drawable.background9, R.drawable.background10, R.drawable.background11, R.drawable.background12};
        loadApps();
        loadAppDrawer();
        new Thread(new AppLaunch()).start();
        new Thread(new Background()).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Background()).start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread(new Background()).start();
    }

    private class AppInfo {
        CharSequence label;
        CharSequence name;
        Drawable icon;
    }

    void loadApps() {
        packageManager = getPackageManager();
        apps = new ArrayList<>();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        ArrayList<ResolveInfo> availableActivities = (ArrayList<ResolveInfo>) packageManager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppInfo appInfo = new AppInfo();
            appInfo.label = ri.loadLabel(packageManager);
            appInfo.name = ri.activityInfo.packageName;
            appInfo.icon = ri.activityInfo.loadIcon(packageManager);
            apps.add(appInfo);
        }
    }

    void loadAppDrawer() {
        list = findViewById(R.id.grid_view);
        adapter = new ArrayAdapter<AppInfo>(this, android.R.layout.simple_gallery_item, apps) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null)
                {
                    convertView = getLayoutInflater().inflate(R.layout.apps_collection, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.appicon);
                imageView.setImageDrawable(apps.get(position).icon);
                TextView textView = convertView.findViewById(R.id.appname);
                textView.setText(apps.get(position).label);


                return convertView;
            }
        };
        list.setAdapter(adapter);
    }
    class AppLaunch implements Runnable
    {
        @Override
        public void run() {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = packageManager.getLaunchIntentForPackage(apps.get(i).name.toString());
                    startActivity(intent);
                }
            });

        }
    }
    void home()
    {
        Intent intent = new Intent(AppsDrawer.this, MainActivity.class);
        startActivity(intent);
        CustomIntent.customType(AppsDrawer.this, "up-to-bottom");
    }

    @Override
    public void onBackPressed() {
        home();
    }

    class Background implements Runnable {
        RelativeLayout relativeLayout = findViewById(R.id.relativelayout);
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                            try {
                                relativeLayout.setBackgroundResource(images[new Random().nextInt(11)]);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }

                });
            }
        }
    }