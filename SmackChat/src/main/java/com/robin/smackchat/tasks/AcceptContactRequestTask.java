package com.robin.smackchat.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.databases.ChatContract.ContactRequestTable;
import com.robin.smackchat.model.UserProfile;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.AppLog;
import com.robin.smackchat.utils.ProviderUtils;
import com.robin.smackchat.xmpp.SmackHelper;
import com.robin.smackchat.xmpp.SmackVCardHelper;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.lang.ref.WeakReference;

public class AcceptContactRequestTask extends BaseAsyncTask<Void, Void, UserProfile> {
    private WeakReference<Uri> uriWrapper;

    public AcceptContactRequestTask(Listener<UserProfile> listener, Context context, Uri uri) {
        super(listener, context);
        uriWrapper = new WeakReference<Uri>(uri);
    }

    @Override
    protected Response<UserProfile> doInBackground(Void... params) {
        Uri requestUri = uriWrapper.get();
        Context context = getContext();
        if (requestUri != null && context != null) {
            Cursor cursor = context.getContentResolver().query(requestUri,
                    new String[]{ContactRequestTable.COLUMN_NAME_NICKNAME, ContactRequestTable.COLUMN_NAME_JID},
                    null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    String jid = cursor.getString(cursor.getColumnIndex(ContactRequestTable.COLUMN_NAME_JID));
                    String nickname = cursor.getString(cursor.getColumnIndex(ContactRequestTable.COLUMN_NAME_NICKNAME));

                    SmackHelper smackHelper = SmackHelper.getInstance(context);
                    // 1. grant subscription to initiator, and request subscription afterwards
                    smackHelper.approveSubscription(jid, nickname, true);

                    // 2. load VCard
                    VCard vCard = smackHelper.loadVCard(jid);

                    // 3. save new contact into db
                    ProviderUtils.addNewContact(context, jid, nickname, vCard.getField(SmackVCardHelper.FIELD_STATUS));

                    return Response.success(new UserProfile(jid, vCard, UserProfile.TYPE_CONTACT));
                }
            } catch (SmackInvocationException e) {
                AppLog.e(String.format("accept contact request error %s", e.toString()), e);

                return Response.error(e);
            } finally {
                cursor.close();
            }
        }

        return null;
    }
}