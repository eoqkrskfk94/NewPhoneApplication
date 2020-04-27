package com.mj.newphoneapplication.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.mj.newphoneapplication.R;

import javax.annotation.Nullable;

public class SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;
    SwitchPreference vibrationPreference;
    SwitchPreference voicePreference;
    ListPreference levelPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);
        vibrationPreference = (SwitchPreference) findPreference("vibration_alarm");
        voicePreference = (SwitchPreference) findPreference("voice_alarm");
        levelPreference = (ListPreference) findPreference("level_list");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

//        if(!prefs.getString("voice_alarm","").equals("")){
//            voicePreference.setSummary(prefs.getString("voice_alarm"));
//        }
//
//
//        prefs.registerOnSharedPreferenceChangeListener(prefListener);



    }


}
