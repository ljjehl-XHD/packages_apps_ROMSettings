package com.aokp.romcontrol.fragments;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class Keyboard extends AOKPPreferenceFragment implements OnPreferenceChangeListener {
        
    public static final String TAG = "Keyboard";   
        
    private static final String KEYBOARD_ROTATION_TOGGLE = "keyboard_rotation_toggle";
    private static final String KEYBOARD_ROTATION_TIMEOUT = "keyboard_rotation_timeout";
    private static final String PREF_DISABLE_FULLSCREEN_KEYBOARD = "disable_fullscreen_keyboard";
    private static final String SHOW_ENTER_KEY = "show_enter_key";
    private static final String VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
    private static final String KEY_IME_SWITCHER = "status_bar_ime_switcher";

    private static final int KEYBOARD_ROTATION_TIMEOUT_DEFAULT = 5000; // 5s
    
    private static ContentResolver mContentResolver;
    
    CheckBoxPreference mKeyboardRotationToggle;
    ListPreference mKeyboardRotationTimeout;
    CheckBoxPreference mDisableFullscreenKeyboard;
    CheckBoxPreference mShowEnterKey;
    CheckBoxPreference mStatusBarImeSwitcher;
    ListPreference mVolumeKeyCursorControl;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.keyboard_title);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_keyboard);

        PreferenceScreen prefs = getPreferenceScreen();
        PreferenceScreen prefSet = getPreferenceScreen();
		ContentResolver cr = mContext.getContentResolver();
		mContentResolver = getContentResolver();

		mKeyboardRotationToggle = (CheckBoxPreference) findPreference(KEYBOARD_ROTATION_TOGGLE);
        mKeyboardRotationToggle.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.KEYBOARD_ROTATION_TIMEOUT, 0) > 0);

        mKeyboardRotationTimeout = (ListPreference) findPreference(KEYBOARD_ROTATION_TIMEOUT);
        mKeyboardRotationTimeout.setOnPreferenceChangeListener(this);
        updateRotationTimeout(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.KEYBOARD_ROTATION_TIMEOUT, KEYBOARD_ROTATION_TIMEOUT_DEFAULT));
                    
        mDisableFullscreenKeyboard = (CheckBoxPreference) findPreference(PREF_DISABLE_FULLSCREEN_KEYBOARD);
        mDisableFullscreenKeyboard.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.DISABLE_FULLSCREEN_KEYBOARD, 0) == 1);
                
        mShowEnterKey = (CheckBoxPreference) findPreference(SHOW_ENTER_KEY);
        mShowEnterKey.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.FORMAL_TEXT_INPUT, 0) == 1);

        // Enable or disable mStatusBarImeSwitcher based on boolean value: config_show_cmIMESwitcher
        final Preference keyImeSwitcherPref = findPreference(KEY_IME_SWITCHER);
        if (keyImeSwitcherPref != null) {
            if (!getResources().getBoolean(com.android.internal.R.bool.config_show_IMESwitcher)) {
                getPreferenceScreen().removePreference(keyImeSwitcherPref);
            } else {
                mStatusBarImeSwitcher = (CheckBoxPreference) keyImeSwitcherPref;
                mStatusBarImeSwitcher.setOnPreferenceChangeListener(this);
            }
        }

        mVolumeKeyCursorControl = (ListPreference) findPreference(VOLUME_KEY_CURSOR_CONTROL);
        if(mVolumeKeyCursorControl != null) {
            mVolumeKeyCursorControl.setValue(Integer.toString(Settings.System.getInt(
                    getContentResolver(), Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0)));
            mVolumeKeyCursorControl.setSummary(mVolumeKeyCursorControl.getEntry());
            mVolumeKeyCursorControl.setOnPreferenceChangeListener(this);
        }
    }
    
    public void updateRotationTimeout(int timeout) {
        if (timeout == 0)
            timeout = KEYBOARD_ROTATION_TIMEOUT_DEFAULT;
        mKeyboardRotationTimeout.setValue(Integer.toString(timeout));
        mKeyboardRotationTimeout.setSummary(getString(R.string.keyboard_rotation_timeout_summary, mKeyboardRotationTimeout.getEntry()));
    }
    
    public void mKeyboardRotationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.keyboard_rotation_dialog);
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(com.android.internal.R.string.ok), null);
        AlertDialog alert = builder.create();
        alert.show();
    }
    
     @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    
		if (preference == mKeyboardRotationToggle) {
            boolean isAutoRotate = (Settings.System.getInt(getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
            if (isAutoRotate && mKeyboardRotationToggle.isChecked())
                mKeyboardRotationDialog();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYBOARD_ROTATION_TIMEOUT,
                    mKeyboardRotationToggle.isChecked() ? KEYBOARD_ROTATION_TIMEOUT_DEFAULT : 0);
            updateRotationTimeout(KEYBOARD_ROTATION_TIMEOUT_DEFAULT);
            return true;
            } else if (preference == mDisableFullscreenKeyboard) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.DISABLE_FULLSCREEN_KEYBOARD, checked ? 1 : 0);
            return true;
            } else if (preference == mShowEnterKey) {
            Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.FORMAL_TEXT_INPUT, mShowEnterKey.isChecked() ? 1 : 0);
            return true;
       }
      	return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    
    if (preference == mKeyboardRotationTimeout) {
            int timeout = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYBOARD_ROTATION_TIMEOUT, timeout);
            updateRotationTimeout(timeout);
            return true;
        } else if (preference == mStatusBarImeSwitcher) {
            Settings.System.putInt(getContentResolver(),
                Settings.System.STATUS_BAR_IME_SWITCHER, (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mVolumeKeyCursorControl) {
            String volumeKeyCursorControl = (String) newValue;
            int val = Integer.parseInt(volumeKeyCursorControl);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, val);
            int index = mVolumeKeyCursorControl.findIndexOfValue(volumeKeyCursorControl);
            mVolumeKeyCursorControl.setSummary(mVolumeKeyCursorControl.getEntries()[index]);
            return true;
        }
        return false;
    }
}   
