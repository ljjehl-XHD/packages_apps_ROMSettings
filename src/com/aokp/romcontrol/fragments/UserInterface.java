
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
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import static android.provider.Settings.System.SCREEN_OFF_ANIMATION;

import java.util.List;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class UserInterface extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

	public static final String TAG = "UserInterface";

    private static ContentResolver mContentResolver;

	private static final String PREF_STATUSBAR_BRIGHTNESS = "statusbar_enable_brightness_slider";
    private static final String KEY_SCREEN_OFF_ANIMATION = "screen_off_animation";
    private static final String KEY_LOW_BATTERY_WARNING_POLICY = "pref_low_battery_warning_policy";

	CheckBoxPreference mStatusbarSliderPreference;
    ListPreference mScreenOffAnimationPreference;
    ListPreference mLowBatteryWarning;

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
        ContentResolver resolver = getActivity().getContentResolver();

    	mStatusbarSliderPreference = (CheckBoxPreference) findPreference(PREF_STATUSBAR_BRIGHTNESS);
    	mStatusbarSliderPreference.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
            Settings.System.STATUSBAR_ENABLE_BRIGHTNESS_SLIDER, true));

        mScreenOffAnimationPreference = (ListPreference) findPreference(KEY_SCREEN_OFF_ANIMATION);
        final int currentAnimation = Settings.System.getInt(resolver, SCREEN_OFF_ANIMATION,
                1 /* CRT-off */);
        mScreenOffAnimationPreference.setValue(String.valueOf(currentAnimation));
        mScreenOffAnimationPreference.setOnPreferenceChangeListener(this);
        updateScreenOffAnimationPreferenceDescription(currentAnimation);

        mLowBatteryWarning = (ListPreference) findPreference(KEY_LOW_BATTERY_WARNING_POLICY);
        int lowBatteryWarning = Settings.System.getInt(getActivity().getContentResolver(),
                                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, 0);
        mLowBatteryWarning.setValue(String.valueOf(lowBatteryWarning));
        mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntry());
        mLowBatteryWarning.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mScreenOffAnimationPreference) {
            int value = Integer.parseInt((String) objValue);
            try {
                Settings.System.putInt(getContentResolver(), SCREEN_OFF_ANIMATION, value);
                updateScreenOffAnimationPreferenceDescription(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist screen-off animation setting", e);
            }
            return true;
        } else if (preference == mLowBatteryWarning) {
            int lowBatteryWarning = Integer.valueOf((String) objValue);
            int index = mLowBatteryWarning.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY,
                    lowBatteryWarning);
            mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntries()[index]);
            return true;
        }

        return false;
    }

    private void updateScreenOffAnimationPreferenceDescription(int currentAnim) {
        ListPreference preference = mScreenOffAnimationPreference;
        String summary;
        if (currentAnim < 0) {
            // Unsupported value
            summary = "";
        } else {
            final CharSequence[] entries = preference.getEntries();
            final CharSequence[] values = preference.getEntryValues();
            if (entries == null || entries.length == 0) {
                summary = "";
            } else {
                summary = entries[currentAnim].toString();
            }
        }
        preference.setSummary(summary);
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