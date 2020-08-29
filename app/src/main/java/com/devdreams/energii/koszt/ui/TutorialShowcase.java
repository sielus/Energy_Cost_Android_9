package com.devdreams.energii.koszt.ui;

import android.app.Activity;
import android.view.View;
import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.rooms.RoomListFragment;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class TutorialShowcase {
    Activity activity;

    public TutorialShowcase(Activity activity) {
        this.activity = activity;
    }

    public void tutorial(final View target, String tutMessageTitle, String tutMessageDesc, Integer color) {
            TapTargetView.showFor(this.activity,                 // `this` is an Activity
                    TapTarget.forView(target, tutMessageTitle, tutMessageDesc)
                            // All options below are optional
                            .outerCircleColor(color)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.60f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(25)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.white)  // Specify the color of the description text
                            .textColor(R.color.white)            // Specify a color for both the title and description text
                            .dimColor(R.color.AppBackGroudColor)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(false)                   // Whether to tint the target view's color
                            .transparentTarget(false)       // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            target.performClick();
                        }
                    });
            };

    public void tutorialWithNoListener(final View target, final String tutMessageTitle, final String tutMessageDesc) {
        ;TapTargetView.showFor(this.activity,                 // `this` is an Activity
                TapTarget.forView(target, tutMessageTitle, tutMessageDesc)
                        // All options below are optional
                        .outerCircleColor(R.color.tutorialWithNoListener)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.60f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .dimColor(R.color.AppBackGroudColor)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)                   // Whether to tint the target view's color
                        .transparentTarget(false)       // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        tutorial(target, activity.getResources().getString(R.string.tutorial_new_device_title), activity.getResources().getString(R.string.tutorial_new_device_desc), R.color.tutorialWithNoListenerSecond);

                    }
                });
    }

    public void tutorialBright(View view, ShapeType rectangle, String textMessage, String tutID) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        sqlLiteDBHelper.checkFirstRunApp();
        RoomListFragment roomListFragment = new RoomListFragment();

        if (roomListFragment.checkFirstRun(view,sqlLiteDBHelper)) {
            new MaterialIntroView.Builder(this.activity)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.MINIMUM)
                    .setDelayMillis(0)
                    .enableFadeAnimation(true)
                    .performClick(true)
                    .setInfoText(textMessage)
                    .setShape(rectangle)
                    .setTarget(view)
                    .setUsageId(tutID) //THIS SHOULD BE UNIQUE ID
                    .show();
        }
    }

    public void tutorialSettings(View view, ShapeType rectangle, String textMessage, String tutID) {
            new MaterialIntroView.Builder(this.activity)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.MINIMUM)
                    .setDelayMillis(0)
                    .enableFadeAnimation(true)
                    .performClick(true)
                    .setInfoText(textMessage)
                    .setShape(rectangle)
                    .setTarget(view)
                    .setUsageId(tutID) //THIS SHOULD BE UNIQUE ID
                    .show();
        }
    }



