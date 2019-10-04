package ir.payebash.Activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;

import androidx.appcompat.widget.Toolbar;
import ir.payebash.Classes.HSH;
import ir.payebash.Fragments.CategoriesFilterFragment;
import ir.payebash.R;

public class CategoriesFilterDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter_categories);

        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        replaceFragment(new CategoriesFilterFragment());
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.layoutFragment, fragment).commit();
    }


    public void animateRevealClose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            HSH.hide(CategoriesFilterDialog.this, findViewById(R.id.ll_filter));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);

        } else {
            CategoriesFilterDialog.super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (getSupportFragmentManager()
                    .getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            else
                animateRevealClose();
        }
        return false;

    }
}