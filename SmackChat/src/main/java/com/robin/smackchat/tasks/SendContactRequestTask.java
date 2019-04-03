package com.robin.smackchat.tasks;

import android.content.Context;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.model.UserProfile;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.AppLog;
import com.robin.smackchat.xmpp.SmackHelper;

import java.lang.ref.WeakReference;
//发送添加好友请求
public class SendContactRequestTask extends BaseAsyncTask<Void, Void, Boolean> {
    private WeakReference<UserProfile> userProfileWrapper;

    public SendContactRequestTask(Listener<Boolean> listener, Context context, UserProfile userProfile) {
        super(listener, context);

        userProfileWrapper = new WeakReference<UserProfile>(userProfile);
    }

    @Override
    protected Response<Boolean> doInBackground(Void... params) {
        Context context = getContext();
        UserProfile userProfile = userProfileWrapper.get();
        if (context != null && userProfile != null) {
            try {
                SmackHelper.getInstance(context).requestSubscription(userProfile.getJid(), userProfile.getNickname());
                return Response.success(true);
            } catch (SmackInvocationException e) {
                AppLog.e(String.format("send contact request to %s error", userProfile.getJid()), e);
                return Response.error(e);
            }
        } else {
            return null;
        }
    }
}