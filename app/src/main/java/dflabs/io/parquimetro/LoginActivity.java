package dflabs.io.parquimetro;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        ButterKnife.inject(this);
    }

    @OnClick(R.id.act_login_btn_login) void onLoginClick(View v){
        Intent i  = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
