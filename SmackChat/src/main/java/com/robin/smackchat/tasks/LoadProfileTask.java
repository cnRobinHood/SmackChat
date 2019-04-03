package com.robin.smackchat.tasks;

import android.content.Context;
import android.graphics.Bitmap;

import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.bitmapcache.BitmapUtils;
import com.robin.smackchat.bitmapcache.ImageCache;
import com.robin.smackchat.model.LoginUserProfile;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.PreferenceUtils;
import com.robin.smackchat.xmpp.SmackHelper;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

public class LoadProfileTask extends BaseAsyncTask<Void, Void, LoginUserProfile> {
    public LoadProfileTask(Listener<LoginUserProfile> listener, Context context) {
        super(listener, context);
    }

    @Override
    protected Response<LoginUserProfile> doInBackground(Void... params) {
        Context context = getContext();
        if (context != null) {
            try {
                String user = PreferenceUtils.getUser(context);

                // first check cache file to find avatar, and if not existing, load vcard from server
                Bitmap avatar = ImageCache.getAvatarFromFile(context, user);
                if (avatar == null) {
                    VCard vcard = SmackHelper.getInstance(context).loadVCard();
                    if (vcard != null) {
                        byte[] data = vcard.getAvatar();
                        if (data != null) {
                            avatar = BitmapUtils.decodeSampledBitmapFromByteArray(data, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
                        }
                    }

                    if (avatar != null) {
                        ImageCache.addAvatarToFile(context, user, avatar);
                    }
                }

                LoginUserProfile result = new LoginUserProfile();
                result.setAvatar(avatar);
                result.setNickname(PreferenceUtils.getNickname(context));

                return Response.success(result);
            } catch (SmackInvocationException e) {
                return Response.error(e);
            }
        }

        return null;
    }
}
