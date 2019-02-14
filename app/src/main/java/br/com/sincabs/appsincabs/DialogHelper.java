package br.com.sincabs.appsincabs;

import android.app.Activity;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Timer;
import java.util.TimerTask;

public class DialogHelper {

    private Activity activity;

    private MaterialDialog progressDialog;
    private MaterialDialog errorDialog;
    private MaterialDialog successDialog;

    public DialogHelper(Activity activity) {

        this.activity = activity;

        progressDialog = new MaterialDialog.Builder(activity)
                .content("Aguarde...")
                .progress(true, 0)
                .cancelable(false)
                .build();


        successDialog = new MaterialDialog.Builder(activity)
                .title("Sucesso")
                .positiveText("OK")
                .positiveColor(Color.parseColor("#FF444444"))
                .build();

        errorDialog = new MaterialDialog.Builder(activity)
                .title("Desculpe")
                .positiveText("OK")
                .positiveColor(Color.parseColor("#FF444444"))
                .build();
    }

    public void showProgress() {

        progressDialog.show();
    }

    public void dismissProgress() {

        progressDialog.dismiss();
    }

    public void showProgressDelayed(final int millisec, final Runnable runnable) {

        showProgress();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                dismissProgress();

                try {

                    runnable.run();
                }
                catch (Exception e) { }
            }
        }, millisec);
    }

    public void showSuccess(String text) {

        successDialog.setContent(text);
        successDialog.show();
    }

    public void showError(String text) {

        errorDialog.setContent(text);
        errorDialog.show();
    }

    public void showList(String title, String[] list, MaterialDialog.ListCallback listCallBack) {

        new MaterialDialog.Builder(activity)
                .title(title)
                .items(list)
                .itemsCallback(listCallBack)
                .show();
    }

    public void inputDialog(String title, String text, int inputType, MaterialDialog.InputCallback inputCallback) {

        new MaterialDialog.Builder(activity)
                .title(title)
                .content(text)
                .inputType(inputType)
                .input("", "", inputCallback)
                .show();
    }

    public void confirmDialog(boolean cancelable, String title, String text, String negativeText, MaterialDialog.SingleButtonCallback inputCallback) {

        new MaterialDialog.Builder(activity)
                .title(title)
                .content(text)
                .positiveText("OK")
                .negativeText(negativeText)
                .onPositive(inputCallback)
                .cancelable(cancelable)
                .show();
    }
}
