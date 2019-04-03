package com.robin.smackchat.tasks;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.util.Log;

import com.robin.smackchat.R;
import com.robin.smackchat.SmackInvocationException;
import com.robin.smackchat.databases.ChatContract;
import com.robin.smackchat.databases.ChatDbHelper;
import com.robin.smackchat.databases.ContactTableHelper;
import com.robin.smackchat.providers.DatabaseContentProvider;
import com.robin.smackchat.tasks.Response.Listener;
import com.robin.smackchat.utils.AppLog;
import com.robin.smackchat.utils.PreferenceUtils;
import com.robin.smackchat.xmpp.SmackHelper;

import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.List;

public class LoginTask extends BaseAsyncTask<Void, Void, Boolean> {
    private static final String TAG = "LoginTask";
    private String username;
    private String password;

    private ProgressDialog dialog;

    public LoginTask(Listener<Boolean> listener, Context context, String username, String password) {
        super(listener, context);

        this.username = username;
        this.password = password;

        dialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.login));
    }

    @Override
    public Response<Boolean> doInBackground(Void... params) {
        Context context = getContext();
        if (context != null) {
            try {
                SmackHelper smackHelper = SmackHelper.getInstance(context);

                smackHelper.login(username, password);
                ChatDbHelper chatDbHelper = ChatDbHelper.getInstance(context);
                SQLiteDatabase db = chatDbHelper.getWritableDatabase();
                Cursor cursor = db.query("contact", null, null, null, null, null, null);
                if (cursor==null||cursor.getCount()==0){
                    List<RosterEntry> rosterEntries = smackHelper.getRosterEntries();
                    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                    for (RosterEntry r : rosterEntries
                    ) {
                        operations.add(ContentProviderOperation.newInsert(ChatContract.ContactTable.CONTENT_URI).
                                withValues(ContactTableHelper.newContentValues(r.getUser(), r.getName(), "")).build());
                        Log.d(TAG, "doInBackground: user = " + r.getUser() + " name = " + r.getName() + " status = " + r.getStatus());
                    }
                    try {
                        context.getContentResolver().applyBatch(DatabaseContentProvider.AUTHORITY, operations);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }

                PreferenceUtils.setLoginUser(context, username, password, smackHelper.getLoginUserNickname());

                return Response.success(true);
            } catch (SmackInvocationException e) {
                AppLog.e(String.format("login error %s", username), e);
                return Response.error(e);
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response<Boolean> response) {
        dialog.dismiss();

        super.onPostExecute(response);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        dismissDialog();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void dismissDialogAndCancel() {
        dismissDialog();
        cancel(false);
    }
}