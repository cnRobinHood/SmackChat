package com.robin.smackchat.tasks;

import android.content.ContentValues;
import android.content.Context;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.databases.ChatMessageTableHelper;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.xmpp.SmackHelper;

public class SendPlainTextTask extends SendMessageTask {
    public SendPlainTextTask(Listener<Boolean> listener, Context context, String to, String nickname, String body) {
        super(listener, context, to, nickname, body);
    }

    @Override
    protected ContentValues newMessage(long timeMillis) {
        return ChatMessageTableHelper.newPlainTextMessage(to, body, timeMillis, true);
    }

    @Override
    protected void doSend(Context context) throws SmackInvocationException {
        SmackHelper.getInstance(context).sendChatMessage(to, body, null);
    }
}