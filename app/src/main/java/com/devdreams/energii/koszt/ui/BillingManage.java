package com.devdreams.energii.koszt.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.devdreams.energii.koszt.MainActivity;
import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.rooms.RoomListFragment;

import java.util.ArrayList;
import java.util.List;

public class BillingManage {
    public static BillingClient billingClient;
    Context context;

    BillingClientStateListener billingClientStateListener;
    final PurchasesUpdatedListener purchaseUpdateListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
               Toast.makeText(context, context.getResources().getString(R.string.billing_canceled), Toast.LENGTH_SHORT).show();
            }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
               Toast.makeText(context, context.getResources().getString(R.string.billing_item_already_owned), Toast.LENGTH_SHORT).show();
            }else{
                // Handle any other error codes.
                Toast.makeText(context,billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    public BillingManage(Context context) {
        this.context = context;
    }

    public void initializeClient(){
        billingClient = BillingClient.newBuilder(this.context)
                .setListener(this.purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

        billingClientStateListener = new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR){
                Toast.makeText(context,billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                billingClient.startConnection(this);

            }
        };
        billingClient.startConnection(billingClientStateListener);
    }

    void handlePurchase(final Purchase purchase) {
        Toast.makeText(context, context.getResources().getString(R.string.billing_verification_of_transaction), Toast.LENGTH_SHORT).show();
        this.checkPurchase(purchase.getPurchaseToken(),true);
    }

    public void checkPurchase(final String token, final Boolean turnNotify) {
        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(token)
                        .build();

        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if(turnNotify){
                        Toast.makeText(context, context.getResources().getString(R.string.billing_transaction_ok), Toast.LENGTH_SHORT).show();
                        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(context);
                        sqlLiteDBHelper.addTokenToDB(token);
                    }
                    disableAds();

                } else {
                    if(turnNotify) {
                        Toast.makeText(context, billingResult.getDebugMessage() + "error acknowledgePurchaseResponseListener ", Toast.LENGTH_SHORT).show();
                    }
                    enableAds();
                }
            }

        };
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
    }

    void disableAds() {
        Toast.makeText(context, "disableAds", Toast.LENGTH_SHORT).show();
        ActionMenuItemView menuItem = MainActivity.view.findViewById(R.id.disable_ads);
        if(menuItem != null){
            menuItem.setVisibility(View.INVISIBLE);
        }
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(this.context);
        sqlLiteDBHelper.setEnableAds(false);

    }
    void enableAds(){
        Toast.makeText(context, "enableAds", Toast.LENGTH_SHORT).show();
        ActionMenuItemView menuItem = MainActivity.view.findViewById(R.id.disable_ads);
        if(menuItem != null){
            menuItem.setVisibility(View.VISIBLE);
        }
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(this.context);
        sqlLiteDBHelper.setEnableAds(true);
        // TODO dodac do bazy sql

    }

    public void startPurchase(final Activity activity) {
        List<String> skuList = new ArrayList<>();
        skuList.add("ads.test");
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

                        billingClient.launchBillingFlow(activity, billingFlowParams);
                    }
                });
    }
}
