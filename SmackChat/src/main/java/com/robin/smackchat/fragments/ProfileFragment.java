package com.robin.smackchat.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.robin.smackchat.R;
import com.robin.smackchat.model.LoginUserProfile;
import com.robin.smackchat.tasks.LoadProfileTask;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.PreferenceUtils;

public class ProfileFragment extends PreferenceFragment implements Listener<LoginUserProfile> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.profile_preferences);

        new LoadProfileTask(this, getActivity()).execute();
    }

    @Override
    public void onResponse(LoginUserProfile profile) {
        if (profile != null) {
            findPreference(PreferenceUtils.AVATAR).setIcon(new BitmapDrawable(getResources(), profile.getAvatar()));
            findPreference(PreferenceUtils.NICKNAME).setSummary(profile.getNickname());
        }
    }

    @Override
    public void onErrorResponse(Exception exception) {
    }
}