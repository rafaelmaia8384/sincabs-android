package br.com.sincabs.appsincabs;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.afollestad.materialdialogs.MaterialDialog;
import com.snatik.storage.Storage;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class LoginActivity extends AppCompatActivity {

    public static SincabsServer sincabsServer;
    public static DialogHelper dialogHelper;
    public static Uri imagemUri;
    public static int selecionarImagem;
    public static Storage storage;
    public static final String LAST_LOGIN_FILE = "last_login.data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sincabsServer = new SincabsServer(LoginActivity.this);
        dialogHelper = new DialogHelper(LoginActivity.this);

        storage = new Storage(LoginActivity.this);

        setContentView(R.layout.activity_login);

        if (storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + LAST_LOGIN_FILE)) {

            Fragment fragment = new FragmentLoginActivityLogin();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

            ft.add(R.id.frameLayoutLoginActivity, fragment, null);
            ft.replace(R.id.frameLayoutLoginActivity, fragment);
            ft.commitAllowingStateLoss();
        }
        else {

            Fragment fragment = new FragmentLoginActivityBemvindo();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

            ft.add(R.id.frameLayoutLoginActivity, fragment, null);
            ft.replace(R.id.frameLayoutLoginActivity, fragment);
            ft.commitAllowingStateLoss();
        }
    }

    private boolean checkPermissionGranted(int targetSdkVersion, String permission) {

        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {

                result = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
            else {

                result = PermissionChecker.checkSelfPermission(LoginActivity.this, permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    public void button_back(View v) {

        onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            dialogHelper.dismissProgress();

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                imagemUri = result.getUri();

                if (selecionarImagem == 1) { // seleção da imagem do perfil

                    findViewById(R.id.fragment_loginactivity_cadastro_passo3_imagemPerfil).setTag("changed");
                    ((ImageView)findViewById(R.id.fragment_loginactivity_cadastro_passo3_imagemPerfil)).setImageURI(imagemUri);
                }
                else if (selecionarImagem == 2) { // seleção da imagem frontal da identidade funcional

                    findViewById(R.id.fragment_loginactivity_pos_cadastro_analise_documental2_imagemFrente).setTag("changed");
                    ((ImageView)findViewById(R.id.fragment_loginactivity_pos_cadastro_analise_documental2_imagemFrente)).setImageURI(imagemUri);
                }
                else if (selecionarImagem == 3) { // seleção da imagem do verso da identidade funcional

                    findViewById(R.id.fragment_loginactivity_pos_cadastro_analise_documental3_imagemVerso).setTag("changed");
                    ((ImageView)findViewById(R.id.fragment_loginactivity_pos_cadastro_analise_documental3_imagemVerso)).setImageURI(imagemUri);
                }
                else { // seleção da imagem para atualizar a imagem de perfil

                    findViewById(R.id.fragment_loginactivity_atualizar_imagem_perfil_imagemPerfil).setTag("changed");
                    ((ImageView)findViewById(R.id.fragment_loginactivity_atualizar_imagem_perfil_imagemPerfil)).setImageURI(imagemUri);
                }

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                dialogHelper.showError("Ocorreu um erro ao selecionar a imagem. Por favor, tente novamente.");
            }
        }
        else if (requestCode == 202) {

            int targedSkdVersion = 0;

            try {

                final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

                targedSkdVersion = info.applicationInfo.targetSdkVersion;
            }
            catch (Exception e) { }

            if (    checkPermissionGranted(targedSkdVersion, Manifest.permission.READ_PHONE_STATE) &&
                    checkPermissionGranted(targedSkdVersion, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    checkPermissionGranted(targedSkdVersion, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    checkPermissionGranted(targedSkdVersion, Manifest.permission.VIBRATE) &&
                    checkPermissionGranted(targedSkdVersion, Manifest.permission.CAMERA)) {

                FragmentManager fm = getSupportFragmentManager();

                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {

                    fm.popBackStack();
                }

                Fragment fragment = new FragmentLoginActivityBemvindo();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

                ft.add(R.id.frameLayoutLoginActivity, fragment, null);
                ft.replace(R.id.frameLayoutLoginActivity, fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected void onResume() {

        int targedSkdVersion = 0;

        try {

            final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

            targedSkdVersion = info.applicationInfo.targetSdkVersion;
        }
        catch (Exception e) { }

        if (    !checkPermissionGranted(targedSkdVersion, Manifest.permission.READ_PHONE_STATE) ||
                !checkPermissionGranted(targedSkdVersion, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                !checkPermissionGranted(targedSkdVersion, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !checkPermissionGranted(targedSkdVersion, Manifest.permission.VIBRATE) ||
                !checkPermissionGranted(targedSkdVersion, Manifest.permission.CAMERA)) {

            Fragment fragment = new FragmentLoginActivityPermissoes();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

            ft.add(R.id.frameLayoutLoginActivity, fragment, null);
            ft.replace(R.id.frameLayoutLoginActivity, fragment);
            ft.commitAllowingStateLoss();

            DataHolder.getInstance().logOut();
        }

        final Intent intent = getIntent();

        if (intent.hasExtra("SincabsMessage")) {

            final String msg = intent.getStringExtra("SincabsMessage");

            new MaterialDialog.Builder(LoginActivity.this)
                    .customView(R.layout.layout_mensagem_sincabs, true)
                    .positiveText("OK")
                    .showListener(new DialogInterface.OnShowListener() {

                        @Override
                        public void onShow(DialogInterface dialog) {

                            Dialog d = (Dialog) dialog;

                            ((TextView)d.findViewById(R.id.textMessage)).setText(msg);

                            intent.removeExtra("SincabsMessage");
                        }
                    })
                    .show();
        }

        super.onResume();
    }
}
