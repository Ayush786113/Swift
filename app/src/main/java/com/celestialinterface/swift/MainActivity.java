package com.celestialinterface.swift;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.celestialinterface.swift.databinding.ActivityMainBinding;

import java.util.Random;
import java.util.concurrent.Executor;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
     int[] images;
    int i;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Background()).start();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.Constraintbackground.setOnLongClickListener(this);
        images = new int[]{R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4, R.drawable.background5, R.drawable.background6, R.drawable.background7, R.drawable.background8, R.drawable.background9, R.drawable.background10, R.drawable.background11, R.drawable.background12};
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            authentication();
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            Toast.makeText(MainActivity.this, "No Biometric Sensor", Toast.LENGTH_LONG).show();
        } else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            Toast.makeText(MainActivity.this, "No Fingerprint Registered", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        authentication();
        new Thread(new Background()).start();
    }

    void authentication()
    {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                Intent intent = new Intent(MainActivity.this, AppsDrawer.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, "bottom-to-up");
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(MainActivity.this, "Authetication Failed", Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo biometricPrompt1 = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("User Authentication")
                .setDescription("Authenticate Your FingerPrint To Access The Application Drawer")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(biometricPrompt1);
    }

    @Override
    public boolean onLongClick(View view) {
        startActivity(new Intent(MainActivity.this, AppsDrawer.class));
        CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");
        return true;
    }

    class Background implements Runnable {
        RelativeLayout constraintLayout = findViewById(R.id.Constraintbackground);
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                            try {
                                constraintLayout.setBackgroundResource(images[new Random().nextInt(11)]);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                }
            });
                }
            }
    @Override
    public void onBackPressed() {
        authentication();
    }
}
