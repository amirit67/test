package ir.payebash.Classes;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class BaseFragment extends Fragment {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = new Intent("PERMISSION_RECEIVER");
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("permissions", permissions);
        intent.putExtra("grantResults", grantResults);
        getActivity().sendBroadcast(intent);
    }
}
