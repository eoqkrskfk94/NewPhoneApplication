package com.mj.newphoneapplication.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.mj.newphoneapplication.R;

import javax.annotation.Nullable;

public class SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;
    ListPreference levelPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);

        levelPreference = (ListPreference) findPreference("level_list");

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
                Toast.makeText(getActivity(), "진동 : "+ vibrate, Toast.LENGTH_SHORT).show();

            }

            if(key.equals("voice_alarm")){
                boolean voice = prefs.getBoolean("voice_alarm", false);
                Toast.makeText(getActivity(), "음성 : "+ voice, Toast.LENGTH_SHORT).show();

            }

            if(key.equals("level_list")){
                String level = prefs.getString("level_list", "");
                levelPreference.setSummary(level);
                Toast.makeText(getActivity(), "강도 : "+ level, Toast.LENGTH_SHORT).show();
            }

        }
    };


}
