package com.swift;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.swift.databinding.ActivityAppsDrawerBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class AppsDrawer extends AppCompatActivity {

    PackageManager packageManager;
    LinkedList<App> apps;
    GridView list;
    ArrayAdapter<App> adapter;
    ActivityAppsDrawerBinding activityAppsDrawerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAppsDrawerBinding = ActivityAppsDrawerBinding.inflate(getLayoutInflater());
        View view = activityAppsDrawerBinding.getRoot();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);
        changeWallpaper();
        packageManager = getPackageManager();
        apps = Helper.APPS;
        if(apps == null)
            loadApps();
        else
            loadAppDrawer(apps);
    }
    @Override
    public void onBackPressed() {
        home();
    }
    @Override
    protected void onResume() {
        super.onResume();
        search();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        home();
    }

    void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = new LinkedList<App>();
        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : availableActivities) {
            App app = new App();
            app.setLabel((String) ri.loadLabel(packageManager));
            app.setName(ri.activityInfo.packageName);
            app.setIcon(ri.activityInfo.loadIcon(packageManager));
            apps.add(app);
        }
        Collections.sort(apps, new Arrange());
        Helper.APPS = apps;
        loadAppDrawer(apps);
    }
    void loadAppDrawer(LinkedList<App> app) {
        list = findViewById(R.id.grid_view);
        adapter = new ArrayAdapter<App>(this, android.R.layout.simple_gallery_item, app) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null)
                {
                    convertView = getLayoutInflater().inflate(R.layout.apps_collection, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.appicon);
                imageView.setImageDrawable(app.get(position).icon);
//                TextView textView = convertView.findViewById(R.id.appname);
//                textView.setText(apps.get(position).getLabel());
                return convertView;
            }
        };
        list.setAdapter(adapter);
        new Thread(new AppLaunch(app)).start();
    }
    void changeWallpaper(){
        try{
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(AppsDrawer.this);
            activityAppsDrawerBinding.wallpaper.setBackground(wallpaperManager.getDrawable());
        }
        catch(Exception e){
            Toast.makeText(AppsDrawer.this, "Storage permission not granted yet. It is required for changing background", Toast.LENGTH_LONG).show();
        }
    }
    void search(){
        activityAppsDrawerBinding.search.requestFocus();
        activityAppsDrawerBinding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LinkedList<App> result = new LinkedList<App>();
                for(App app : apps)
                {
                    if (app.getLabel().toLowerCase().startsWith(charSequence.toString().trim()))
                        result.add(app);
                }
                if(result.size() > 0)
                    loadAppDrawer(result);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    class AppLaunch implements Runnable
    {
        LinkedList<App> appArrayList;
        AppLaunch(LinkedList<App> appArrayList)
        {
            this.appArrayList = appArrayList;
        }
        @Override
        public void run() {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = packageManager.getLaunchIntentForPackage(appArrayList.get(i).name);
                    startActivity(intent);
                    Toast.makeText(AppsDrawer.this, appArrayList.get(i).getLabel(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
    }

    void home() {
        Intent intent = new Intent(AppsDrawer.this, MainActivity.class);
        startActivity(intent);
        CustomIntent.customType(AppsDrawer.this, "up-to-bottom");
    }
    private static class Arrange implements Comparator<App>{
        @Override
        public int compare(App app1, App app2) {
            return app1.getLabel().compareTo(app2.getLabel());
            }
        }
    }