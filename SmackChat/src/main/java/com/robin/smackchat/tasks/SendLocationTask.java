package com.robin.smackchat.tasks;

import android.content.ContentValues;
import android.content.Context;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.databases.ChatMessageTableHelper;
import com.robin.smackchat.xmpp.SmackHelper;
import com.robin.smackchat.xmpp.UserLocation;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * Created by dilli on 11/27/2015.
 */
public class SendLocationTask extends SendMessageTask {
    protected PacketExtension packetExtension;
    private UserLocation location;

    public SendLocationTask(Response.Listener<Boolean> listener, Context context, String to, String nickname, UserLocation location) {
        super(listener, context, to, nickname, location.getName());

        this.location = location;
        packetExtension = location;
    }

    @Override
    protected ContentValues newMessage(long timeMillis) {
        return ChatMessageTableHelper.newLocationMessage(to, body, timeMillis, location, true);
    }

    @Override
    protected void doSend(Context context) throws SmackInvocationException {
        SmackHelper.getInstance(context).sendChatMessage(to, body, packetExtension);
    }
}