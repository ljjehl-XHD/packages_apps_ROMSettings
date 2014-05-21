package com.aokp.romcontrol.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.VolumePanel;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.service.FlipService;
import com.aokp.romcontrol.service.HeadphoneService;
import com.aokp.romcontrol.widgets.AppSelectListPreference;

import java.net.URISyntaxException;

public class Sound extends AOKPPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String KEY_VOLUME_OVERLAY = "volume_overlay";
    private static final String PREF_HEADPHONES_PLUGGED_ACTION = "headphone_audio_mode";
    private static final String KEY_HEADSET_PLUG = "headset_plug";
    private static final String KEY_HEADSET_MUSIC_ACTIVE = "headset_plug_music_active";
    private static final String PREF_BT_CONNECTED_ACTION = "bt_audio_mode";
    private static final String PREF_FLIP_ACTION = "flip_mode";
    private static final String PREF_USER_TIMEOUT = "user_timeout";
    private static final String PREF_USER_DOWN_MS = "user_down_ms";
    private static final String PREF_PHONE_RING_SILENCE = "phone_ring_silence";
    private static final String PREF_LESS_NOTIFICATION_SOUNDS = "less_notification_sounds";

    SharedPreferences prefs;
    ListPreference mVolumeOverlay;
    ListPreference mHeadphonesPluggedAction;
    ListPreference mBTPluggedAction;
    ListPreference mFlipAction;
    ListPreference mUserDownMS;
    ListPreference mFlipScreenOff;
    ListPreference mPhoneSilent;
    ListPreference mAnnoyingNotifications;
    private AppSelectListPreference mHeadsetPlug;
    private CheckBoxPreference mHeadsetMusicActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_sound);
        addPreferencesFromResource(R.xml.prefs_sound);
        PreferenceManager.setDefaultValues(mContext, R.xml.prefs_sound, true);
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        mVolumeOverlay = (ListPreference) findPreference(KEY_VOLUME_OVERLAY);
        mVolumeOverlay.setOnPreferenceChangeListener(this);
        int volumeOverlay = Settings.System.getIntForUser(mContentRes,
                Settings.System.MODE_VOLUME_OVERLAY, VolumePanel.VOLUME_OVERLAY_EXPANDABLE,
                UserHandle.USER_CURRENT);
        mVolumeOverlay.setValue(Integer.toString(volumeOverlay));
        mVolumeOverlay.setSummary(mVolumeOverlay.getEntry());

        mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);
        mAnnoyingNotifications.setValue(Integer.toString(Settings.System.getInt(mContentRes,
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, 0)));

        mFlipAction = (ListPreference) findPreference(PREF_FLIP_ACTION);
        mFlipAction.setOnPreferenceChangeListener(this);
        mFlipAction.setValue((prefs.getString(PREF_FLIP_ACTION, "-1")));

        mUserDownMS = (ListPreference) findPreference(PREF_USER_DOWN_MS);
        mUserDownMS.setEnabled(Integer.parseInt(prefs.getString(PREF_FLIP_ACTION, "-1")) != -1);

        mFlipScreenOff = (ListPreference) findPreference(PREF_USER_TIMEOUT);
        mFlipScreenOff.setEnabled(Integer.parseInt(prefs.getString(PREF_FLIP_ACTION, "-1")) != -1);

        mPhoneSilent = (ListPreference) findPreference(PREF_PHONE_RING_SILENCE);
        mPhoneSilent.setValue((prefs.getString(PREF_PHONE_RING_SILENCE, "0")));
        mPhoneSilent.setOnPreferenceChangeListener(this);

        mHeadsetPlug = (AppSelectListPreference) findPreference(KEY_HEADSET_PLUG);
        mHeadsetPlug.setOnPreferenceChangeListener(this);
        mHeadsetMusicActive = (CheckBoxPreference) findPreference(KEY_HEADSET_MUSIC_ACTIVE);
        updateHeadsetPlugSummary();

        if (!hasPhoneAbility(mContext)) {
            getPreferenceScreen().removePreference(mPhoneSilent);
        }

        if (HeadphoneService.DEBUG)
            mContext.startService(new Intent(mContext, HeadphoneService.class));

        if (FlipService.DEBUG)
            mContext.startService(new Intent(mContext, FlipService.class));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void toggleFlipService() {
        if (FlipService.isStarted()) {
            mContext.stopService(new Intent(mContext, FlipService.class));
        }
        mContext.startService(new Intent(mContext, FlipService.class));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFlipAction) {
            int val = Integer.parseInt((String) newValue);
            if (val != -1) {
                mUserDownMS.setEnabled(true);
                mFlipScreenOff.setEnabled(true);
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle(getResources().getString(R.string.flip_dialog_title));
                ad.setMessage(getResources().getString(R.string.flip_dialog_msg));
                ad.setPositiveButton(
                        getResources().getString(R.string.flip_action_positive),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                ad.show();
                toggleFlipService();
            } else {
                mUserDownMS.setEnabled(false);
                mFlipScreenOff.setEnabled(false);
            }
            return true;

        } else if (preference == mAnnoyingNotifications) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, val);
            return true;

        } else if (preference == mPhoneSilent) {
            int val = Integer.parseInt((String) newValue);
            if (val != 0) {
                toggleFlipService();
            }
            return true;
        } else if (preference == mHeadsetPlug) {
           String value = (String) newValue;
           Settings.System.putString(getContentResolver(),
                    Settings.System.HEADSET_PLUG_ENABLED, value);	
           updateHeadsetPlugSummary();
        } else if (preference == mHeadsetPlug) {
           Settings.System.putInt(getContentResolver(),
                    Settings.System.HEADSET_PLUG_MUSIC_ACTIVE, 
                    mHeadsetMusicActive.isChecked()? 1:0);
        } else if (preference == mVolumeOverlay) {
            int value = Integer.valueOf((String) newValue);
            int index = mVolumeOverlay.findIndexOfValue((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MODE_VOLUME_OVERLAY, value);
            mVolumeOverlay.setSummary(mVolumeOverlay.getEntries()[index]);
        }
        return false;
    }

    private void updateHeadsetPlugSummary(){
        final PackageManager packageManager = getPackageManager();

        mHeadsetPlug.setSummary(getResources().getString(R.string.headset_plug_positive_title));
        mHeadsetMusicActive.setEnabled(false);

        String headSetPlugIntentUri = Settings.System.getString(getContentResolver(), Settings.System.HEADSET_PLUG_ENABLED);
        if (headSetPlugIntentUri != null) {
            if(headSetPlugIntentUri.equals(Settings.System.HEADSET_PLUG_SYSTEM_DEFAULT)) {
                 mHeadsetPlug.setSummary(getResources().getString(R.string.headset_plug_neutral_summary));
                 mHeadsetMusicActive.setEnabled(true);
            } else {
                Intent headSetPlugIntent = null;
                try {
                    headSetPlugIntent = Intent.parseUri(headSetPlugIntentUri, 0);
                } catch (URISyntaxException e) {
                    headSetPlugIntent = null;
                }

                if(headSetPlugIntent != null) {
                    ResolveInfo info = packageManager.resolveActivity(headSetPlugIntent, 0);
                    if (info != null) {
                        mHeadsetPlug.setSummary(info.loadLabel(packageManager));
                        mHeadsetMusicActive.setEnabled(true);
                    }
                }
            }
        }
    }
}
