package com.aokp.romcontrol.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.settings.BaseSetting.OnSettingChangedListener;
import com.aokp.romcontrol.settings.CheckboxSetting;
import com.aokp.romcontrol.settings.SingleChoiceSetting;


public class StatusBarBattery extends Fragment implements OnSettingChangedListener {

	CheckboxSetting mBatteryIndicator, mBatteryIndicatorPlugged;
        SingleChoiceSetting mBatteryChoice;

    public StatusBarBattery() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.xml.prefs_statusbar_battery, container, false);

    mBatteryIndicator = (CheckboxSetting) v.findViewById(R.id.battery_percentage_indicator);
    mBatteryIndicatorPlugged = (CheckboxSetting) v.findViewById(R.id.battery_percentage_indicator_plugged);
    mBatteryChoice = (SingleChoiceSetting) v.findViewById(R.id.setting_status_bar_battery_style);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBatteryIndicator.setOnSettingChangedListener(this);
    }

    @Override
    public void onSettingChanged(String table, String key, String oldValue, String value) {
        Log.v("BatterySettings", "Current setting for battery is " + mBatteryChoice.getCurrentValueIndex());
        if ("aokp".equals(table)) {
            mBatteryIndicatorPlugged.setVisibility((mBatteryIndicator.isChecked() 
            || mBatteryChoice.getCurrentValueIndex() == 6) ? View.VISIBLE : View.GONE);
        }
    }
}
