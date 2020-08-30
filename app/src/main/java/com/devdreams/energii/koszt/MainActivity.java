package com.devdreams.energii.koszt;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.devdreams.energii.koszt.ui.settings.SettingActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
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
    BillingClientStateListener billingClientStateListener;
    Purchase purchaseTest;


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
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                    Toast.makeText(MainActivity.this,billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    if(purchases != null){
                        for (Purchase purchase : purchases) {
                            Toast.makeText(MainActivity.this,purchase.getPurchaseState(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
            billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

            billingClientStateListener = new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
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
            };

        billingClient.startConnection(billingClientStateListener);
      //  checkPurchase(); todo przechowywać tokem w bazie, sprawdzać czy zapłata jest aktualna

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_rooms, R.id.nav_fragment_sun_energy_calculator_layout, R.id.nav_about_us)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void checkPurchase() {
        System.out.println(this.purchaseTest);
        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(this.purchaseTest.getPurchaseToken())
                        .build();

        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(MainActivity.this, "consumeResponseListener OK ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, billingResult.getDebugMessage() + " consumeResponseListener", Toast.LENGTH_SHORT).show();
                }
            }

        };
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
    }

    void handlePurchase(final Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        Toast.makeText(MainActivity.this, "listener", Toast.LENGTH_SHORT).show();
        this.purchaseTest = purchase;
        this.checkPurchase();
    }

    void disableAds() {
        //TODO po sprawdzeniu zapłaty zgodnie z tokenem, wykonać odpowiednie czynnosci
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
          //  Intent intent = new Intent(this, SettingActivity.class);
          //  startActivity(intent);
          //  Animatoo.animateSlideLeft(MainActivity.this);
            checkPurchase();
            return true;
        }else if(item.getItemId() == R.id.disable_ads){
            if(billingClient.isReady()){
                startPurchase();
            }else{
                billingClient.startConnection(this.billingClientStateListener);
                Toast.makeText(MainActivity.this,"Brak połaczenia z internetem lub inny bła",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void startPurchase() {

        List<String> skuList = new ArrayList<>();
        skuList.add("xdd.test.tenn");
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetailsList.get(0))
                                .build();
                        billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
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