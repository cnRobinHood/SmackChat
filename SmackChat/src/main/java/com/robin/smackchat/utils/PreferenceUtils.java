package com.robin.smackchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";
    public static final String NICKNAME = "nickname";
    public static final String AVATAR = "avatar";
    public static final String TRAFFIC_TRANSMITTED = "traffic_transmitted";
    public static final String TRAFFIC_RECEIVED = "traffic_received";
    public static final String SERVER_ADDRESS = "server_address";
    public static final String SECRET_KEY = "secret_key";

    public static final String AWS_SERVER_IP = "47.112.27.122";

    public static void setLoginUser(Context context, String user, String password, String nickname) {
        String encryptedUser = AESEncryption.encrypt(context, user);
        String encryptedPassword = AESEncryption.encrypt(context, password);
        getSharedPreferences(context).edit().putString(USERNAME, encryptedUser).putString(PASSWORD, encryptedPassword)
                .putString(NICKNAME, nickname).commit();
    }

    public static String getUser(Context context) {
        String encryptedUser = getSharedPreferences(context).getString(USERNAME, null);
        return encryptedUser != null ? AESEncryption.decrypt(context, encryptedUser) : null;
    }

    public static void wipeUser(Context context) {
        getSharedPreferences(context).edit().clear().commit();
    }

    public static String getPassword(Context context) {
        String encryptedPassword = getSharedPreferences(context).getString(PASSWORD, null);
        return encryptedPassword != null ? AESEncryption.decrypt(context, encryptedPassword) : null;
    }

    public static String getNickname(Context context) {
        return getSharedPreferences(context).getString(NICKNAME, null);
    }

    public static String getServerHost(Context context) {
        String serverHost = getSharedPreferences(context).getString(SERVER_ADDRESS, null);
        return serverHost == null ? AWS_SERVER_IP : serverHost;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}