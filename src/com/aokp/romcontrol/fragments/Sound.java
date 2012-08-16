
package com.aokp.romcontrol.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class Sound extends AOKPPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String PREF_LESS_NOTIFICATION_SOUNDS = "less_notification_sounds";

    SharedPreferences prefs;
    ListPreference mAnnoyingNotifications;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_ui);
        PreferenceManager.setDefaultValues(mContext, R.xml.prefs_ui, true);
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);
        mAnnoyingNotifications.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD,
                0)));
    }

    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	if (preference == mAnnoyingNotifications) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, val);
            return true;
        }
        return false;
    }
}
