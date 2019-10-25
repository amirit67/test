package ir.payebash.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.R;


public class NoConnectioonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connectioon);

        if (NetworkUtils.getConnectivity(NoConnectioonActivity.this) != false) {
            HSH.onOpenPage(NoConnectioonActivity.this, SplashActivity.class);
            finish();
        }
    }

    public void no_connection_retry_click(View v) {
        if (NetworkUtils.getConnectivity(NoConnectioonActivity.this) != false) {
            HSH.onOpenPage(NoConnectioonActivity.this, SplashActivity.class);
            finish();
        }
    }
}
