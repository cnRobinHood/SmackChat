package com.robin.smackchat.xmpp;

import android.content.Context;
import android.content.Intent;

import com.robin.smackchat.service.MessageService;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

public class PresencePacketListener implements PacketListener {
    public static final String EXTRA_DATA_NAME_TYPE = "com.robin.letschat.Type";
    public static final String EXTRA_DATA_NAME_STATUS = "com.robin.letschat.Status";

    private Context context;

    public PresencePacketListener(Context context) {
        this.context = context;
    }

    @Override
    public void processPacket(Packet packet) {
        Presence presence = (Presence) packet;
        Presence.Type presenceType = presence.getType();

        Intent intent = new Intent(MessageService.ACTION_PRESENCE_RECEIVED, null, context, MessageService.class);
        intent.putExtra(MessageService.EXTRA_DATA_NAME_FROM, StringUtils.parseBareAddress(presence.getFrom()));
        intent.putExtra(EXTRA_DATA_NAME_TYPE, presenceType.ordinal());
        String status = presence.getStatus();
        if (status != null) {
            intent.putExtra(EXTRA_DATA_NAME_STATUS, presence.getStatus());
        }
        context.startService(intent);
    }
}