
package com.aokp.romcontrol.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.R.xml;

public class PowerMenu extends AOKPPreferenceFragment {

    //private static final String PREF_POWER_SAVER = "show_power_saver";
    //private static final String PREF_SCREENSHOT = "show_screenshot";
    //private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    //private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String KEY_REBOOT = "power_menu_reboot";
    private static final String KEY_SILENT = "power_menu_silent";
    private static final String IMMERSIVE_MODE = "power_menu_immersive_mode";

    //CheckBoxPreference mShowPowerSaver;
    //CheckBoxPreference mShowScreenShot;
    //CheckBoxPreference mShowTorchToggle;
    //CheckBoxPreference mShowAirplaneToggle;
    CheckBoxPreference mShowScreenShot;
    CheckBoxPreference mShowAirplaneToggle;
    CheckBoxPreference mRebootPref;
    CheckBoxPreference mSilentPref;
    CheckBoxPreference mImmersiveModePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_powermenu);
                
        mShowScreenShot = (CheckBoxPreference) findPreference(PREF_SCREENSHOT);
        mShowScreenShot.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_MENU_SCREENSHOT_ENABLED,
                1) == 1);


        mShowAirplaneToggle = (CheckBoxPreference) findPreference(PREF_AIRPLANE_TOGGLE);
        mShowAirplaneToggle.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_MENU_AIRPLANE_ENABLED,
                1) == 1);
        
        mRebootPref = (CheckBoxPreference) findPreference(KEY_REBOOT);
        mRebootPref.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_MENU_REBOOT_ENABLED,
                1) == 1);
                
        mSilentPref = (CheckBoxPreference) findPreference(KEY_SILENT);
        mSilentPref.setChecked(Settings.System.getInt(getActivity()
        		.getContentResolver(), Settings.System.POWER_MENU_SILENT_ENABLED,
        		1) == 1);

        mImmersiveModePref = (CheckBoxPreference) findPreference(IMMERSIVE_MODE);
        mImmersiveModePref.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_MENU_IMMERSIVE_MODE_ENABLED,
                0) == 1);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mShowScreenShot) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_MENU_SCREENSHOT_ENABLED,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mShowAirplaneToggle) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_MENU_AIRPLANE_ENABLED,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mRebootPref) {
        	Settings.System.putInt(getActivity().getContentResolver(),
        			Settings.System.POWER_MENU_REBOOT_ENABLED,
        			((CheckBoxPreference)preference).isChecked() ? 1 : 0);
        	return true;
        } else if (preference == mSilentPref) {
        	Settings.System.putInt(getActivity().getContentResolver(),
        			Settings.System.POWER_MENU_SILENT_ENABLED,
        			((CheckBoxPreference)preference).isChecked() ? 1 : 0);
        	return true;
        } else if (preference == mImmersiveModePref) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_MENU_IMMERSIVE_MODE_ENABLED,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}