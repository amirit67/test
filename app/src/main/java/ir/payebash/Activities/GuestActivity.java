package ir.payebash.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import ir.payebash.Classes.HSH;
import ir.payebash.Fragments.HomeFragment;
import ir.payebash.Interfaces.TitleMain;
import ir.payebash.R;

import static ir.payebash.Classes.HSH.openFragment;

public class GuestActivity extends AppCompatActivity implements TitleMain {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.btn_new_event).setBackgroundResource(R.mipmap.ic_signup);
        findViewById(R.id.btn_new_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSH.onOpenPage(GuestActivity.this, RegisterActivity.class);
            }
        });

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
