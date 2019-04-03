package com.robin.smackchat.tasks;

import android.content.Context;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.AppLog;
import com.robin.smackchat.xmpp.SmackHelper;

public class LoadStatusTask extends BaseAsyncTask<Void, Void, String> {
    public LoadStatusTask(Listener<String> listener, Context context) {
        super(listener, context);
    }

    @Override
    protected Response<String> doInBackground(Void... params) {
        Context context = getContext();
        if (context != null) {
            try {
                return Response.success(SmackHelper.getInstance(context).loadStatus());
            } catch (SmackInvocationException e) {
                AppLog.e(String.format("get login user status error %s", e.toString()), e);

                return Response.error(e);
            }
        } else {
            return null;
        }
    }
}