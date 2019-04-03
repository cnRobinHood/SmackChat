package com.robin.smackchat.tasks;

import android.content.Context;
import android.database.Cursor;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.databases.ChatContract.ContactTable;
import com.robin.smackchat.model.UserProfile;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.AppLog;
import com.robin.smackchat.utils.PreferenceUtils;
import com.robin.smackchat.xmpp.SmackHelper;

public class SearchUserTask extends BaseAsyncTask<Void, Void, UserProfile> {
    private String username;

    public SearchUserTask(Listener<UserProfile> listener, Context context, String username) {
        super(listener, context);

        this.username = username;
    }

    @Override
    protected Response<UserProfile> doInBackground(Void... params) {
        Context context = getContext();
        if (context != null) {
            try {
                UserProfile user = SmackHelper.getInstance(context).search(username);
                if (user != null) {
                    if (user.getUserName().equals(PreferenceUtils.getUser(context))) {
                        user.setType(UserProfile.TYPE_MYSELF);
                    } else {
                        Cursor c = context.getContentResolver().query(ContactTable.CONTENT_URI, new String[]{ContactTable._ID},
                                ContactTable.COLUMN_NAME_JID + " = ?", new String[]{user.getJid()}, null);
                        if (c.moveToFirst()) {
                            user.setType(UserProfile.TYPE_CONTACT);
                        } else {
                            user.setType(UserProfile.TYPE_NOT_CONTACT);
                        }
                    }
                }

                return Response.success(user);
            } catch (SmackInvocationException e) {
                AppLog.e(String.format("search user error %s", e.toString()), e);

                return Response.error(e);
            }
        } else {
            return null;
        }
    }
}