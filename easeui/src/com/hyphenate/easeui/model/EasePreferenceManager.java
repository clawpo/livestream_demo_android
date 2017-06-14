package com.hyphenate.easeui.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.easeui.controller.EaseUI;

import java.util.Set;

public class EasePreferenceManager {
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private static final String KEY_AT_GROUPS = "AT_GROUPS";

    public static final String PREFERENCE_NAME = "saveInfo";
    private static SharedPreferences mUserSharedPreferences;
    private static SharedPreferences.Editor userEditor;
    private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
    private static String SHARED_KEY_CURRENTUSER_NICK = "SHARED_KEY_CURRENTUSER_NICK";
    private static String SHARED_KEY_CURRENTUSER_AVATAR = "SHARED_KEY_CURRENTUSER_AVATAR";
    private static String SHARED_KEY_SHOW_SEND_DIALOG = "SHARED_KEY_SHOW_SEND_DIALOG";
    
    private EasePreferenceManager(){
        mSharedPreferences = EaseUI.getInstance().getContext().getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        mUserSharedPreferences = EaseUI.getInstance().getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        userEditor = mUserSharedPreferences.edit();
    }
    private static EasePreferenceManager instance;
    
    public synchronized static EasePreferenceManager getInstance(){
        if(instance == null){
            instance = new EasePreferenceManager();
        }
        return instance;
        
    }
    
    
    public void setAtMeGroups(Set<String> groups) {
        editor.remove(KEY_AT_GROUPS);
        editor.commit();
        editor.putStringSet(KEY_AT_GROUPS, groups);
        editor.commit();
    }
    
    public Set<String> getAtMeGroups(){
        return mSharedPreferences.getStringSet(KEY_AT_GROUPS, null);
    }

    public void setCurrentUserNick(String nick) {
        userEditor.putString(SHARED_KEY_CURRENTUSER_NICK, nick);
        userEditor.apply();
    }

    public void setCurrentUserAvatar(String avatar) {
        userEditor.putString(SHARED_KEY_CURRENTUSER_AVATAR, avatar);
        userEditor.apply();
    }

    public void setCurrentUserName(String username){
        userEditor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username);
        userEditor.apply();
    }

    public void setShowDialog(boolean isShow){
        userEditor.putBoolean(SHARED_KEY_SHOW_SEND_DIALOG, isShow);
        userEditor.apply();
    }

    public boolean getShowDialog() {
        return mUserSharedPreferences.getBoolean(SHARED_KEY_SHOW_SEND_DIALOG,false);
    }
    public String getCurrentUserNick() {
        return mUserSharedPreferences.getString(SHARED_KEY_CURRENTUSER_NICK, null);
    }

    public String getCurrentUserAvatar() {
        return mUserSharedPreferences.getString(SHARED_KEY_CURRENTUSER_AVATAR, null);
    }


    public String getCurrentUsername(){
        return mUserSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
    }

    public void removeCurrentUserInfo() {
        userEditor.remove(SHARED_KEY_CURRENTUSER_NICK);
        userEditor.remove(SHARED_KEY_CURRENTUSER_AVATAR);
        userEditor.apply();
    }
    
}
