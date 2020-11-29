package com.devdreams.energii.koszt;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AboutUs extends Fragment {
    public View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_about_us, container, false);
        String version = null;
        try {
            PackageInfo pInfo = root.getContext().getPackageManager().getPackageInfo(root.getContext().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.about_us, null));
        requireActivity().getWindow().setStatusBarColor(requireActivity().getResources().getColor(R.color.about_us));
        TextView appVersion = root.findViewById(R.id.app_version);
        appVersion.setText(root.getContext().getResources().getString(R.string.app_name) + " " + version);
        return root;
    }
}