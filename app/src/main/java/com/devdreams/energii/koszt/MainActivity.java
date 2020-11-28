package com.devdreams.energii.koszt;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.devdreams.energii.koszt.ui.BillingManage;
import com.devdreams.energii.koszt.ui.SQLLiteDBHelper;
import com.devdreams.energii.koszt.ui.settings.SettingActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static View view;
    private AppBarConfiguration mAppBarConfiguration;
    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;
    BillingClient billingClient;
    BillingManage billingManage;
    static DrawerLayout drawer;
    BillingClientStateListener billingClientStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        view = this.findViewById(android.R.id.content);

        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(3)
                .setRemindInterval(1)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

        View headerView = navigationView.getHeaderView(0);
        final TextView studioMail = headerView.findViewById(R.id.studioMail);
        studioMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyMailtoCopyBoard(studioMail);
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        billingManage = new BillingManage(this);
        billingManage.initializeClient();
        billingClient = BillingManage.billingClient;
      //  billingClient.startConnection(billingClientStateListener);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(billingClient.isReady()) {
                    if(getUserTokenFromDB() != null){
                        billingManage.checkPurchase(getUserTokenFromDB(),false);
                    }
                }
            }
        }, 1000);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_rooms, R.id.nav_fragment_sun_energy_calculator_layout, R.id.nav_about_us)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private String getUserTokenFromDB() {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(this);
        return sqlLiteDBHelper.getTokenFromDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openSettings();
            return true;
        }else if(item.getItemId() == R.id.disable_ads){
            openStartBillingDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openStartBillingDialog() {
        if(billingClient.isReady()){
            billingManage.startPurchase(this);
        }else{
//            billingClient.startConnection(this.billingClientStateListener);
            Toast.makeText(MainActivity.this,getResources().getString(R.string.billing_no_connect),Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettings() {
          Intent intent = new Intent(this, SettingActivity.class);
          startActivity(intent);
          Animatoo.animateSlideLeft(MainActivity.this);
    }

    private void copyMailtoCopyBoard(TextView studioMail) {
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip;
        String text = studioMail.getText().toString();
        myClip = ClipData.newPlainText("text", text);
        Objects.requireNonNull(myClipboard).setPrimaryClip(myClip);
        Toast.makeText(this,getResources().getString(R.string.mail),Toast.LENGTH_SHORT).show();
    }
}