package com.kent.university.privelt.ui.hotels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HotelsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HotelsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hotels fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}