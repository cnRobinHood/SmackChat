package com.robin.smackchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.robin.smackchat.utils.PreferenceUtils;
import com.robin.smackchat.xmpp.SmackHelper;
import com.squareup.picasso.Picasso;

public class StartupActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "StartupActivity";
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_SIGNUP = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("action") && "wipe_user".equals(intent.getExtras().getString("action"))) {
            Log.d(TAG, "onCreate: "+intent.getStringExtra("action"));
            PreferenceUtils.wipeUser(this);
        }
        if (PreferenceUtils.getUser(this) != null) {
            startMainActivity();
            return;
        } else {
            findViewById(R.id.ll_buttons_container).setVisibility(View.VISIBLE);
        }
        Picasso.get().load("http://cn.bing.com/th?id=OHR.BistiBadlands_ZH-CN5428677883_1920x1080.jpg&rf=NorthMale_1920x1081920x1080.jpg").resize(340, 216).into(((ImageView) findViewById(R.id.iv_image)));
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connectivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_server) {
            startActivity(new Intent(this, ServerSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
                break;

            case R.id.btn_signup:
                startActivityForResult(new Intent(this, SignupActivity.class), REQUEST_CODE_SIGNUP);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}