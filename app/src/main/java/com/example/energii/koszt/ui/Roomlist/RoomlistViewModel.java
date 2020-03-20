package com.example.energii.koszt.ui.Roomlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoomlistViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RoomlistViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment test2");
    }

    public LiveData<String> getText() {
        return mText;
    }
}