/*
 * Copyright (C) 2012 ParanoidAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aokp.romcontrol.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.AbstractAsyncSuCMDProcessor;
import com.aokp.romcontrol.util.Helpers;
import com.aokp.romcontrol.widgets.AlphaSeekBar;
import com.aokp.romcontrol.widgets.SeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class Pie extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PIE_CONTROLS = "pie_controls";
    private static final String PIE_GRAVITY = "pie_gravity";
    private static final String PIE_MODE = "pie_mode";
    private static final String PIE_SIZE = "pie_size";
    private static final String PIE_TRIGGER = "pie_trigger";
    private static final String PIE_GAP = "pie_gap";
    private static final String PIE_CENTER = "pie_center";
    private static final String PIE_STICK = "pie_stick";
    private static final String PIE_NOTIFICATIONS = "pie_notifications";
    private static final String PIE_LASTAPP = "pie_lastapp";
    private static final String PIE_KILLTASK = "pie_killtask";
    private static final String PIE_APPWINDOW = "pie_appwindow";
    private static final String PIE_MENU = "pie_menu";
    private static final String PIE_SEARCH = "pie_search";
    private static final String PIE_RESTART = "pie_restart_launcher";

    ListPreference mPieMode;
    ListPreference mPieSize;
    ListPreference mPieGravity;
    ListPreference mPieTrigger;
    ListPreference mPieGap;
    CheckBoxPreference mPieCenter;
    CheckBoxPreference mPieNotifi;
    CheckBoxPreference mPieControls;
    CheckBoxPreference mPieLastApp;
    CheckBoxPreference mPieKillTask;
    CheckBoxPreference mPieAppWindow;
    CheckBoxPreference mPieMenu;
    CheckBoxPreference mPieSearch;
    CheckBoxPreference mPieStick;
    CheckBoxPreference mPieRestart;

    private Context mContext;
    private int mAllowedLocations;

    protected Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pie_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        mContext = getActivity().getApplicationContext();
        ContentResolver resolver = mContext.getContentResolver();

        mPieControls = (CheckBoxPreference) findPreference(PIE_CONTROLS);
        mPieControls.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1);

        mPieGravity = (ListPreference) prefSet.findPreference(PIE_GRAVITY);
        int pieGravity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_GRAVITY, 3);
        mPieGravity.setValue(String.valueOf(pieGravity));
        mPieGravity.setOnPreferenceChangeListener(this);

        mPieMode = (ListPreference) prefSet.findPreference(PIE_MODE);
        int pieMode = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_MODE, 2);
        mPieMode.setValue(String.valueOf(pieMode));
        mPieMode.setOnPreferenceChangeListener(this);

        mPieSize = (ListPreference) prefSet.findPreference(PIE_SIZE);
        mPieTrigger = (ListPreference) prefSet.findPreference(PIE_TRIGGER);
        try {
            float pieSize = Settings.System.getFloat(mContext.getContentResolver(),
                    Settings.System.PIE_SIZE, 0.9f);
            mPieSize.setValue(String.valueOf(pieSize));
  
            float pieTrigger = Settings.System.getFloat(mContext.getContentResolver(),
                    Settings.System.PIE_TRIGGER);
            mPieTrigger.setValue(String.valueOf(pieTrigger));
        } catch(Settings.SettingNotFoundException ex) {
            // So what
        }

        mPieSize.setOnPreferenceChangeListener(this);
        mPieTrigger.setOnPreferenceChangeListener(this);

        mPieCenter = (CheckBoxPreference) prefSet.findPreference(PIE_CENTER);
        mPieCenter.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_CENTER, 1) == 1);

        mPieStick = (CheckBoxPreference) prefSet.findPreference(PIE_STICK);
        mPieStick.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_STICK, 1) == 1);

        mPieRestart = (CheckBoxPreference) prefSet.findPreference(PIE_RESTART);
        mPieRestart.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.EXPANDED_DESKTOP_RESTART_LAUNCHER, 1) == 1);

        mPieGap = (ListPreference) prefSet.findPreference(PIE_GAP);
        int pieGap = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_GAP, 3);
        mPieGap.setValue(String.valueOf(pieGap));
        mPieGap.setOnPreferenceChangeListener(this);

        mPieNotifi = (CheckBoxPreference) prefSet.findPreference(PIE_NOTIFICATIONS);
        mPieNotifi.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.PIE_NOTIFICATIONS, 0) == 1));

        mPieLastApp = (CheckBoxPreference) prefSet.findPreference(PIE_LASTAPP);
        mPieLastApp.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_LAST_APP, 0) == 1);
                
        mPieKillTask = (CheckBoxPreference) prefSet.findPreference(PIE_KILLTASK);
        mPieKillTask.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_KILL_TASK, 1) == 1);

        mPieAppWindow = (CheckBoxPreference) prefSet.findPreference(PIE_APPWINDOW);
        mPieAppWindow.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_APP_WINDOW, 1) == 1);

        mPieMenu = (CheckBoxPreference) prefSet.findPreference(PIE_MENU);
        mPieMenu.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_MENU, 1) == 1);

        mPieSearch = (CheckBoxPreference) prefSet.findPreference(PIE_SEARCH);
        mPieSearch.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_SEARCH, 1) == 1);

        checkControls();
    }

    private void checkControls() {
        boolean pieCheck = mPieControls.isChecked();
        mPieGravity.setEnabled(pieCheck);
        mPieMode.setEnabled(pieCheck);
        mPieSize.setEnabled(pieCheck);
        mPieTrigger.setEnabled(pieCheck);
        mPieGap.setEnabled(pieCheck);
        mPieNotifi.setEnabled(pieCheck);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mPieControls) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.PIE_CONTROLS, mPieControls.isChecked() ? 1 : 0);
            checkControls();
            Helpers.restartSystemUI();
        } else if (preference == mPieNotifi) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.PIE_NOTIFICATIONS, mPieNotifi.isChecked() ? 1 : 0);
        } else if (preference == mPieLastApp) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_LAST_APP, mPieLastApp.isChecked() ? 1 : 0);
        } else if (preference == mPieKillTask) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_KILL_TASK, mPieKillTask.isChecked() ? 1 : 0);
        } else if (preference == mPieAppWindow) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_APP_WINDOW, mPieAppWindow.isChecked() ? 1 : 0);
        } else if (preference == mPieMenu) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_MENU, mPieMenu.isChecked() ? 1 : 0);
        } else if (preference == mPieSearch) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_SEARCH, mPieSearch.isChecked() ? 1 : 0);
        } else if (preference == mPieCenter) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_CENTER, mPieCenter.isChecked() ? 1 : 0);
        } else if (preference == mPieStick) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_STICK, mPieStick.isChecked() ? 1 : 0);
        } else if (preference == mPieRestart) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_RESTART_LAUNCHER, mPieRestart.isChecked() ? 1 : 0);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPieMode) {
            int pieMode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_MODE, pieMode);
            return true;
        } else if (preference == mPieSize) {
            float pieSize = Float.valueOf((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.PIE_SIZE, pieSize);
            return true;
        } else if (preference == mPieGravity) {
            int pieGravity = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_GRAVITY, pieGravity);
            return true;
        } else if (preference == mPieGap) {
            int pieGap = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_GAP, pieGap);
            return true;
        } else if (preference == mPieTrigger) {
            float pieTrigger = Float.valueOf((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.PIE_TRIGGER, pieTrigger);
            return true;
        }
        return false;
    }
}
