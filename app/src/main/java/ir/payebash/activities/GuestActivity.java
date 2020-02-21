package ir.payebash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import ir.payebash.classes.HSH;
import ir.payebash.fragments.HomeFragment;
import ir.payebash.fragments.registerUser.RegisterActivity;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;

import static ir.payebash.classes.HSH.openFragment;

public class GuestActivity extends AppCompatActivity implements IWebservice.TitleMain {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.btn_new_event).setBackgroundResource(R.mipmap.ic_signup);
        findViewById(R.id.btn_new_event).setOnClickListener(view -> HSH.onOpenPage(GuestActivity.this, RegisterActivity.class));

        HomeFragment home_fragment = new HomeFragment();
        openFragment(GuestActivity.this, home_fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void FragName(String name) {
        ((TextView) findViewById(R.id.txt_title)).setText(name);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        return false;

    }
}
