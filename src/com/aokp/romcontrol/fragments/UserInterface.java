package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.aokp.romcontrol.util.Helpers;
import com.aokp.romcontrol.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class UserInterface extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

	public static final String TAG = "UserInterface";

    private static ContentResolver mContentResolver;

	private static final String PREF_STATUSBAR_BRIGHTNESS = "statusbar_enable_brightness_slider";
    private static final String KEY_SCREEN_OFF_ANIMATION = "screen_off_animation";
    private static final String KEY_LOW_BATTERY_WARNING_POLICY = "pref_low_battery_warning_policy";
    private static final String PREF_CUSTOM_STATUS_BAR_COLOR = "custom_status_bar_color";
    private static final String PREF_STATUS_BAR_OPAQUE_COLOR = "status_bar_opaque_color";
    private static final String STATUS_BAR_CUSTOM_HEADER = "custom_status_bar_header";
    private static final String KEY_LISTVIEW_ANIMATION = "listview_animation";
    private static final String KEY_LISTVIEW_INTERPOLATOR = "listview_interpolator";

	CheckBoxPreference mStatusbarSliderPreference;
    ListPreference mScreenOffAnimationPreference;
    ListPreference mLowBatteryWarning;
    CheckBoxPreference mCustomBarColor;
    ColorPickerPreference mBarOpaqueColor;
    CheckBoxPreference mStatusBarCustomHeader;
    ListPreference mListViewAnimation;
    ListPreference mListViewInterpolator;

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
        ContentResolver mContentAppRes = mContext.getContentResolver();

    	mStatusbarSliderPreference = (CheckBoxPreference) findPreference(PREF_STATUSBAR_BRIGHTNESS);
    	mStatusbarSliderPreference.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
            Settings.System.STATUSBAR_ENABLE_BRIGHTNESS_SLIDER, true));

        mStatusBarCustomHeader = (CheckBoxPreference) findPreference(STATUS_BAR_CUSTOM_HEADER);
        mStatusBarCustomHeader.setChecked(Settings.System.getInt(resolver,
            Settings.System.STATUS_BAR_CUSTOM_HEADER, 0) == 1);
        mStatusBarCustomHeader.setOnPreferenceChangeListener(this);

        mScreenOffAnimationPreference = (ListPreference) findPreference(KEY_SCREEN_OFF_ANIMATION);
        final int currentAnimation = Settings.System.getInt(resolver, SCREEN_OFF_ANIMATION,
                1 /* CRT-off */);
        mScreenOffAnimationPreference.setValue(String.valueOf(currentAnimation));
        mScreenOffAnimationPreference.setOnPreferenceChangeListener(this);
        updateScreenOffAnimationPreferenceDescription(currentAnimation);

        mListViewAnimation = (ListPreference) findPreference(KEY_LISTVIEW_ANIMATION);
        int listviewanimation = Settings.System.getInt(getContentResolver(),
                Settings.System.LISTVIEW_ANIMATION, 0);
        mListViewAnimation.setValue(String.valueOf(listviewanimation));
        mListViewAnimation.setSummary(mListViewAnimation.getEntry());
        mListViewAnimation.setOnPreferenceChangeListener(this);

        mListViewInterpolator = (ListPreference) findPreference(KEY_LISTVIEW_INTERPOLATOR);
        int listviewinterpolator = Settings.System.getInt(getContentResolver(),
                Settings.System.LISTVIEW_INTERPOLATOR, 0);
        mListViewInterpolator.setValue(String.valueOf(listviewinterpolator));
        mListViewInterpolator.setSummary(mListViewInterpolator.getEntry());
        mListViewInterpolator.setOnPreferenceChangeListener(this);
        mListViewInterpolator.setEnabled(listviewanimation > 0);

        mLowBatteryWarning = (ListPreference) findPreference(KEY_LOW_BATTERY_WARNING_POLICY);
        int lowBatteryWarning = Settings.System.getInt(getActivity().getContentResolver(),
                                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, 0);
        mLowBatteryWarning.setValue(String.valueOf(lowBatteryWarning));
        mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntry());
        mLowBatteryWarning.setOnPreferenceChangeListener(this);

        int intColor;
        String hexColor;

        mCustomBarColor = (CheckBoxPreference) prefSet.findPreference(PREF_CUSTOM_STATUS_BAR_COLOR);
        mCustomBarColor.setChecked(Settings.System.getInt(mContentAppRes,
                Settings.System.CUSTOM_STATUS_BAR_COLOR, 0) == 1);

        mBarOpaqueColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_OPAQUE_COLOR);
        mBarOpaqueColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_OPAQUE_COLOR, 0xff000000);
        mBarOpaqueColor.setSummary(getResources().getString(R.string.default_string));
        mBarOpaqueColor.setNewPreviewColor(intColor);

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
        } else if (preference == mListViewAnimation) {
            int value = Integer.parseInt((String) objValue);
            int index = mListViewAnimation.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LISTVIEW_ANIMATION, value);
            mListViewAnimation.setSummary(mListViewAnimation.getEntries()[index]);
            mListViewInterpolator.setEnabled(value > 0);
            return true;
        } else if (preference == mListViewInterpolator) {
            int value = Integer.parseInt((String) objValue);
            int index = mListViewInterpolator.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LISTVIEW_INTERPOLATOR, value);
            mListViewInterpolator.setSummary(mListViewInterpolator.getEntries()[index]);
            return true;
        } else if (preference == mLowBatteryWarning) {
            int lowBatteryWarning = Integer.valueOf((String) objValue);
            int index = mLowBatteryWarning.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY,
                    lowBatteryWarning);
            mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarCustomHeader) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER, value ? 1 : 0);
        } else  if (preference == mBarOpaqueColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_OPAQUE_COLOR, intHex);
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
        } else if (preference == mCustomBarColor) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.CUSTOM_STATUS_BAR_COLOR,
            mCustomBarColor.isChecked() ? 1 : 0);
            Helpers.restartSystemUI();
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);

    }
}
