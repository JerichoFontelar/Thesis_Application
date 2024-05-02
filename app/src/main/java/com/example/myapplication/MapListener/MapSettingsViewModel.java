package com.example.myapplication.MapListener;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class MapSettingsViewModel extends ViewModel {
    private final BehaviorSubject<Boolean> mapReloadSubject = BehaviorSubject.create();

    public Observable<Boolean> getMapReloadObservable() {
        return mapReloadSubject;
    }

    public void triggerMapReload() {
        mapReloadSubject.onNext(true);
        Log.d("SettingsFragment", "Map Reload Triggered");
    }
}
