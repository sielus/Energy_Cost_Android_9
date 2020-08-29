package com.devdreams.energii.koszt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.devdreams.energii.koszt.ui.rooms.RoomListFragment;
import com.devdreams.energii.koszt.ui.settings.SettingActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    public View view;
    private AppBarConfiguration mAppBarConfiguration;
    public static Toolbar toolbar;
    BillingClient billingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        view = this.findViewById(android.R.id.content);

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


        final PurchasesUpdatedListener purchaseUpdateListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK  && purchases!=null ){
                    Toast.makeText(MainActivity.this,"OK",Toast.LENGTH_SHORT).show();
                }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
                    Toast.makeText(MainActivity.this,"USER_CANCELED",Toast.LENGTH_SHORT).show();
                }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
                    Toast.makeText(MainActivity.this,"ITEM_ALREADY_OWNED xx",Toast.LENGTH_SHORT).show();
                }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
                    Toast.makeText(MainActivity.this,"BILLING_UNAVAILABLE",Toast.LENGTH_SHORT).show();
                }
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(MainActivity.this,"Sukces",Toast.LENGTH_SHORT).show();

                }else {

                    Toast.makeText(MainActivity.this,"Error " + String.valueOf(billingResult.getDebugMessage()),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                billingClient.startConnection(this);
            }
        });




        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_rooms, R.id.nav_fragment_sun_energy_calculator_layout, R.id.nav_about_us)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



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
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            Animatoo.animateSlideLeft(MainActivity.this);
            return true;
        }else if(item.getItemId() == R.id.disable_ads){
           startPurchase();

        }
        return super.onOptionsItemSelected(item);
    }

    private void startPurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add("energy.cost.disable.ads");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetailsList.get(0))
                                .build();
                        billingClient.launchBillingFlow(MainActivity.this, billingFlowParams).getResponseCode();
                        }
                });
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