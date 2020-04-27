package com.mj.newphoneapplication.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;

import com.mj.newphoneapplication.MainActivity;
import com.mj.newphoneapplication.R;

import javax.annotation.Nullable;

import static android.content.Context.POWER_SERVICE;

public class SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;
    ListPreference levelPreference;
    Preference batteryPreference;
    Preference overlayPreference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);

        levelPreference = (ListPreference) findPreference("level_list");
        batteryPreference = (Preference) findPreference("battery");
        overlayPreference = (Preference) findPreference("overlay");

        if(MainActivity.getInstace().getBattery()){
            batteryPreference.setSummary("ON");
        }else{
            batteryPreference.setSummary("OFF");
        }

        if(MainActivity.getInstace().getOverlay()){
            overlayPreference.setSummary("ON");
        }else{
            overlayPreference.setSummary("OFF");
        }

        //SharedPreference객체를 참조하여 설정상태에 대한 제어 가능..
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());



        levelPreference.setSummary(prefs.getString("level_list", "약"));

        prefs.registerOnSharedPreferenceChangeListener(listener);


    }


    @Override
    public void onResume() {
        super.onResume();

//설정값 변경리스너..등록
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();

        prefs.unregisterOnSharedPreferenceChangeListener(listener);

    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("vibration_alarm")){
                boolean vibrate = prefs.getBoolean("vibration_alarm", false);

            }

            if(key.equals("voice_alarm")){
                boolean voice = prefs.getBoolean("voice_alarm", false);

            }

            if(key.equals("level_list")){
                String level = prefs.getString("level_list", "");
                levelPreference.setSummary(level);
            }

        }
    };





}
