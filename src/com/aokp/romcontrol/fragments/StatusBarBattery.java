package com.aokp.romcontrol.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.AOKPPreferenceFragment;


public class StatusBarBattery extends AOKPPreferenceFragment {

    public StatusBarBattery() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.xml.prefs_statusbar_battery, container, false);

        return v;
    }
}