package com.swift;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.swift.databinding.ActivityMainBinding;

import java.util.concurrent.Executor;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);
        permission();
        changeWallpaper();
        binding.background.setOnLongClickListener(this);
        biometric();
    }
    void permission()
    {
        int storage_granted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(storage_granted != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
    void biometric()
    {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            authentication();
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            alert("fingerprint sensor not found, long press home screen to access application drawer");
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            alert("fingerprint not registered, long press home screen to access application drawer");
        }
        else
            alert("Something isn't right. Contact the Developer.");
    }
    void alert(String message)
    {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
    void changeWallpaper(){
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
            binding.wallpaper.setBackground(wallpaperManager.getDrawable());
        }
        catch(Exception e){
            alert("Storage permission not granted yet. It is required for changing background");
        }
    }
    void changeIntent(){
        Intent intent = new Intent(MainActivity.this, AppsDrawer.class);
        startActivity(intent);
    }
    void authentication()
    {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricCallbacks = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                CustomIntent.customType(MainActivity.this, "bottom-to-up");
                changeIntent();
            }
            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(MainActivity.this, "Authetication Failed", Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo biometricPrompt = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("User Authentication")
                .setDescription("Authenticate Your FingerPrint To Access The Application Drawer")
                .setNegativeButtonText("Cancel")
                .build();
        biometricCallbacks.authenticate(biometricPrompt);
    }

    @Override
    public boolean onLongClick(View view) {
        changeIntent();
        CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");
        return true;
    }
    @Override
    public void onBackPressed() {
        biometric();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        permission();
        biometric();
    }
    @Override
    protected void onResume() {
        super.onResume();
        biometric();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1)
        {
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                Toast.makeText(MainActivity.this, "Storage permission not granted. Background will be unusual.", Toast.LENGTH_SHORT).show();
            }
            else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Storage permission granted. Background will be updated.", Toast.LENGTH_SHORT).show();
                changeWallpaper();
            }
        }
    }
}
