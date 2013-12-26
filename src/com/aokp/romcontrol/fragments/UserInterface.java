
package com.aokp.romcontrol.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import java.util.List;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class UserInterface extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

	public static final String TAG = "UserInterface";

    private static ContentResolver mContentResolver;

	private static final String PREF_STATUSBAR_BRIGHTNESS = "statusbar_enable_brightness_slider";

	CheckBoxPreference mStatusbarSliderPreference;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_ui);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_ui);

        PreferenceScreen prefs = getPreferenceScreen();
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver cr = mContext.getContentResolver();
        mContentResolver = getContentResolver();


    	mStatusbarSliderPreference = (CheckBoxPreference) findPreference(PREF_STATUSBAR_BRIGHTNESS);
    	mStatusbarSliderPreference.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
            Settings.System.STATUSBAR_ENABLE_BRIGHTNESS_SLIDER, true));

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {

        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
    	if (preference == mStatusbarSliderPreference) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_ENABLE_BRIGHTNESS_SLIDER,
                    isCheckBoxPrefernceChecked(preference));
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);

    }
}