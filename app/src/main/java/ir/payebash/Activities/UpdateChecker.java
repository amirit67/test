package ir.payebash.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Version 1.0 - For latest version check here: http://blog.fdev.eu/updatechecker/
 *
 * @author Francesco Capano
 */
public class UpdateChecker {
    private final String TAG_SUFFIX = "UpdateCheckerAsynkTask";
    public String curVersion, localApkName = "PayeBash.apk", alertTitle, alertMessage,
            alertTitleError, alertMessageError, progressMessage;
    private String version, remoteApkUrl, TAG = TAG_SUFFIX;
    private Handler mHandler = new Handler();
    private Context context;
    private Activity activity;
    private AlertDialog alertUpdate, alertError;
    private boolean enabled = true;
    private Thread checkUpdate;
    private Runnable showError = new Runnable() {
        public void run() {
            alertError = new AlertDialog.Builder(activity)
                    .setTitle(alertTitleError)
                    .setMessage(alertMessageError)
                    .setCancelable(false)
                    .setPositiveButton("باشــه", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    };
    /* This Runnable creates an Error message*/
    private Runnable showUpdate = new Runnable() {
        public void run() {


            new SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("بروزرسانی")
                    .setContentText(alertMessage)
                    .setConfirmText("بله")
                    .setCancelText("فعلا نه!خروج")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            if (!remoteApkUrl.toLowerCase().contains("cafebazaar")) {
                                Log.d(TAG, "TAG:Starting to download");
                                DownloadFilesTask downloadFile = new DownloadFilesTask();
                                downloadFile.execute(remoteApkUrl, localApkName);
                            } else {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=" + context.getPackageName()));
                                    intent.setPackage("com.farsitel.bazaar");
                                    activity.startActivity(intent);
                                } catch (Exception e) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cafebazaar.ir/app/" + context.getPackageName() + "/?l=fa"));
                                        intent.setPackage("com.farsitel.bazaar");
                                        activity.startActivity(intent);
                                    } catch (Exception e1) {
                                    }
                                }
                            }
                            sDialog.dismiss();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(a);
                            System.exit(0);
                            ((Activity) context).finish();
                        }
                    }).show();
        }
    };

    public UpdateChecker(Activity c, String version, String remoteApkUrl) {
        this.activity = c;
        this.context = c.getApplicationContext();
        this.version = version;
        this.remoteApkUrl = remoteApkUrl;

    }

    /**
     * Starts to check for updates
     */
    public void startUpdateChecker() {
        if (!enabled)
            return;
        if (checkUpdate == null || !checkUpdate.isAlive() || checkUpdate.isInterrupted()) {
            checkUpdate = new Thread() {
                public void run() {
                    checkupdate();
                }
            };
            checkUpdate.start();
        }
    }

    /**
     * Interrupts update check
     */
    public void stopUpdateChecker() {
        if (!enabled)
            return;
        if (checkUpdate.isAlive() && !checkUpdate.isInterrupted())
            try {
                checkUpdate.interrupt();
            } catch (Exception e) {
                Log.w(TAG, "checkUpdate.interrupt() exception");
            }
    }

    private void checkupdate() {
        if (alertUpdate != null && alertUpdate.isShowing()) { //(alertError!=null && alertError.isShowing()) ||
            //There is already an download message
            return;
        }
        Log.v(TAG, "Checking updates...");
        try {

            /* Convert the Bytes read to a String. */
            final String s = version;         
            
            /* Get current Version Number */
            String newVersion = String.valueOf(s);

            Log.d(TAG, "Current version is: " + curVersion + " and new one is: " + newVersion);
            /* Is a higher version than the current already out? */
            if (!curVersion.equals(newVersion)) {
                /* Post a Handler for the UI to pick up and open the Dialog */
                if (alertUpdate == null || !alertUpdate.isShowing()) {
                    if (alertError != null && alertError.isShowing())
                        alertError.dismiss();
                    mHandler.post(showUpdate);
                }
            } else
                Log.v(TAG, "The software is updated to the latest version: " + newVersion);
        } catch (Exception e) {
            e.printStackTrace();
            if (alertError == null || !alertError.isShowing())
                mHandler.post(showError);
        }
    }

    private void InstallFile(String fileName) {
        try {
            Log.d(TAG, "Installing file  " + fileName);
            File file = new File(context.getFilesDir(), fileName);
            file.setReadable(true, false);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public void setTagPrefix(String tagPrefix) {
        TAG = tagPrefix + " " + TAG_SUFFIX;
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog mProgressDialog;
        private String outFileName;

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setMessage(progressMessage);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            } catch (Exception e) {
            }
        }

        @Override
        protected Integer doInBackground(String... urls) {
            String inFileName = urls[0];
            outFileName = urls[1];
            try {
                //connecting to url
                URL u = new URL(inFileName);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                int lenghtOfFile = c.getContentLength();


                //file input is from the url
                InputStream in = new BufferedInputStream(u.openStream(), 8192);
                //this is where the file will be seen after the download
                OutputStream out = context.openFileOutput(outFileName, Context.MODE_WORLD_READABLE); //


                //here's the download code
                byte[] buffer = new byte[1024];
                int readLenght = 0;
                long total = 0;
                int lastProgress = 0;

                while ((readLenght = in.read(buffer)) > 0) {
                    total += readLenght;
                    int cProgress = (int) ((total * 100) / 500000);
                    if (cProgress != lastProgress) {
                        publishProgress((int) ((total * 100) / 500000));
                        lastProgress = cProgress;
                    }
                    out.write(buffer, 0, readLenght);
                }
                out.flush();
                out.close();
                in.close();
                Log.d(TAG, "Saved file with name: " + outFileName + " | Size: " + total);
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        }

        @Override
        public void onProgressUpdate(Integer... args) {
            mProgressDialog.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0)
                InstallFile(outFileName);
            else
                mHandler.post(showError);
        }

    }
}