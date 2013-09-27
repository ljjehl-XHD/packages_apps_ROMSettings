package com.aokp.romcontrol.fragments;


import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.util.Log;


import com.aokp.romcontrol.R;
import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.util.Helpers;


public class StatusBarSignal extends AOKPPreferenceFragment implements
        OnPreferenceChangeListener {
        
    private static final String KEY_MMS_BREATH = "mms_breath";
    private static final String KEY_MISSED_CALL_BREATH = "missed_call_breath";
    private static final String STATUS_BAR_AUTO_HIDE = "status_bar_auto_hide";
    private static final String STATUS_BAR_TRAFFIC = "status_bar_traffic";


    ListPreference mDbmStyletyle;
    ListPreference mWifiStyle;
    ColorPickerPreference mColorPicker;
    ColorPickerPreference mWifiColorPicker;
    CheckBoxPreference mHideSignal;
    private CheckBoxPreference mStatusBarTraffic;
    CheckBoxPreference mAltSignal;
    CheckBoxPreference mMMSBreath;
    CheckBoxPreference mMissedCallBreath;
    CheckBoxPreference mStatusBarAutoHide;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_statusbar_signal);
        
        PreferenceScreen prefSet = getPreferenceScreen();


        mDbmStyletyle = (ListPreference) findPreference("signal_style");
        mDbmStyletyle.setOnPreferenceChangeListener(this);
        mDbmStyletyle.setValue(Integer.toString(Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_SIGNAL_TEXT, 0)));


        mColorPicker = (ColorPickerPreference) findPreference("signal_color");
        mColorPicker.setOnPreferenceChangeListener(this);

        /*mWifiStyle = (ListPreference) findPreference("wifi_signal_style");
        mWifiStyle.setOnPreferenceChangeListener(this);
        mWifiStyle.setValue(Integer.toString(Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT, 0)));


        mWifiColorPicker = (ColorPickerPreference) findPreference("wifi_signal_color");
        mWifiColorPicker.setOnPreferenceChangeListener(this);*/


        mHideSignal = (CheckBoxPreference) findPreference("hide_signal");
        mHideSignal.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.STATUSBAR_HIDE_SIGNAL_BARS, false));


        /*mAltSignal = (CheckBoxPreference) findPreference("alt_signal");
        mAltSignal.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.STATUSBAR_SIGNAL_CLUSTER_ALT,false));*/
                
        mMMSBreath = (CheckBoxPreference) findPreference(KEY_MMS_BREATH);
        mMMSBreath.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.MMS_BREATH, 0) == 1);
                
        mMissedCallBreath = (CheckBoxPreference) findPreference(KEY_MISSED_CALL_BREATH);
        mMissedCallBreath.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.MISSED_CALL_BREATH, 0) == 1);
               
        mStatusBarAutoHide = (CheckBoxPreference) findPreference(STATUS_BAR_AUTO_HIDE);
        mStatusBarAutoHide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
              Settings.System.AUTO_HIDE_STATUSBAR, 0) == 1));

        mStatusBarTraffic = (CheckBoxPreference) findPreference(STATUS_BAR_TRAFFIC); 
        mStatusBarTraffic.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_TRAFFIC, 0) == 0));


    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
	boolean value;
        if (preference == mHideSignal) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.STATUSBAR_HIDE_SIGNAL_BARS, mHideSignal.isChecked());


            return true;
        /*} else if (preference == mAltSignal) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_CLUSTER_ALT,mAltSignal.isChecked());
            return true;*/
        } else if (preference == mMMSBreath) {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.MMS_BREATH, 
                    mMMSBreath.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mMissedCallBreath) {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.MISSED_CALL_BREATH, 
                    mMissedCallBreath.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mStatusBarAutoHide) {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.AUTO_HIDE_STATUSBAR,
            		mStatusBarAutoHide.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mStatusBarTraffic) {
            value = mStatusBarTraffic.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_TRAFFIC, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDbmStyletyle) {


            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_TEXT, val);
            mColorPicker.setEnabled(val == 0 ? false : true);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);


            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_TEXT_COLOR, intHex);


            return true;
        /*} else if (preference == mWifiStyle) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT, val);
            mWifiColorPicker.setEnabled(val == 0 ? false : true);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mWifiColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);


            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT_COLOR, intHex);


            return true;*/
        }
        return false;
    }
}

