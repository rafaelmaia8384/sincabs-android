package br.com.sincabs.appsincabs;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;

public class SincabsActivity extends AppCompatActivity {

    private static long lastActivity = 0;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FrameLayout frameLayoutSuspeitos;
    private FrameLayout frameLayoutUsuarios;

    private static boolean mensagemCompartilharMostrada = false;
    private static final String AVISO_COMPARTILHAR = "msg-aviso-compartilhar.data";
    private static final String AVISO_CADASTRO = "aviso-regras-cadastro.data";

    public static DialogHelper dialogHelper;
    public static SincabsServer sincabsServer;

    private Fragment lastFragmentSuspeitos;
    private Fragment lastFragmentUsuarios;

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        lastActivity = System.currentTimeMillis();

        if (!DataHolder.getInstance().isLoggedIn()) {

            Intent intent = getIntent();

            Intent i = new Intent(SincabsActivity.this, LoginActivity.class);

            if (intent.hasExtra("SincabsMessage")) {

                i.putExtra("SincabsMessage", intent.getStringExtra("SincabsMessage"));
            }

            startActivity(i);

            finish();

            return;
        }

        setContentView(R.layout.activity_sincabs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        frameLayoutSuspeitos = (FrameLayout) findViewById(R.id.frameLayoutSuspeitos);
        frameLayoutUsuarios = (FrameLayout) findViewById(R.id.frameLayoutUsuarios);

        setSupportActionBar(toolbar);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addTab(tabLayout.newTab().setText("Suspeitos"));
        tabLayout.addTab(tabLayout.newTab().setText("Usuários"));

        lastFragmentSuspeitos = new FragmentSincabsActivitySuspeitos();
        lastFragmentUsuarios = new FragmentSincabsActivityUsuarios();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab LayoutTab) {

                if (LayoutTab.getPosition() == 0) {

                    frameLayoutSuspeitos.animate().alpha(1.0f);
                    frameLayoutUsuarios.animate().alpha(0.0f);

                    frameLayoutSuspeitos.setVisibility(View.VISIBLE);
                    frameLayoutUsuarios.setVisibility(View.GONE);
                }
                else if (LayoutTab.getPosition() == 1) {

                    frameLayoutSuspeitos.animate().alpha(0.0f);
                    frameLayoutUsuarios.animate().alpha(1.0f);

                    frameLayoutSuspeitos.setVisibility(View.GONE);
                    frameLayoutUsuarios.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab LayoutTab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab LayoutTab) {

            }
        });

        //iniciar já carregando o layout dos suspeitos

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutSuspeitos, lastFragmentSuspeitos);
        ft.commitAllowingStateLoss();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutUsuarios, lastFragmentUsuarios);
        ft.commitAllowingStateLoss();

        frameLayoutUsuarios.animate().alpha(0.0f);

        dialogHelper = new DialogHelper(SincabsActivity.this);
        sincabsServer = new SincabsServer(SincabsActivity.this);

        if (!ImageLoader.getInstance().isInited()) {

            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(imageOptions)
                    .diskCacheExtraOptions(640, 640, null)
                    .memoryCacheSize(16 * 1024 * 1024) // 16MB
                    .diskCacheSize(128 * 1024 * 1024)  // 128MB
                    .imageDownloader(new SincabsImageDownloader())
                    .build();

            ImageLoader.getInstance().init(config);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_sugestoes_e_criticas) {

            dialogHelper.showProgressDelayed(500, new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(SincabsActivity.this, SugestoesECriticasActivity.class);
                    startActivity(i);
                }
            });
        }
        else if (id == R.id.menu_termos_e_condicoes) {

            dialogHelper.showProgressDelayed(500, new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(SincabsActivity.this, TermosECondicoesDeUsoActivity.class);
                    startActivity(i);
                }
            });
        }
        else if (id == R.id.menu_sobre_o_sincabs) {

            dialogHelper.showProgressDelayed(500, new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(SincabsActivity.this, SobreActivity.class);
                    startActivity(i);
                }
            });
        }
        else if (id == R.id.menu_desconectar) {

            Intent i = new Intent(SincabsActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        if (System.currentTimeMillis() - lastActivity > (long) 1000*60*15) { // 15 minutos para expirar a conexão

            DataHolder.getInstance().setLoggedIn(false);

            Intent i = new Intent(SincabsActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        lastActivity = System.currentTimeMillis();

        super.onStop();
    }

    public void buttonBuscarSuspeito(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SincabsActivity.this, BuscarSuspeitoActivity.class);
                startActivityForResult(i, 100);
            }
        });
    }

    public void buttonCadastrarSuspeito(View view) {

        final Storage storage = new Storage(SincabsActivity.this);

        if (!storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + AVISO_CADASTRO)) {

            MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    if (((CheckBox)dialog.findViewById(R.id.checkNaoMostrarMais)).isChecked()) {

                        storage.createFile(storage.getInternalFilesDirectory() + File.separator + AVISO_CADASTRO, "{ok}");
                    }

                    buttonBuscarSuspeito(null);
                }
            };

            MaterialDialog.SingleButtonCallback negativeCallback = new MaterialDialog.SingleButtonCallback() {

                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    if (((CheckBox)dialog.findViewById(R.id.checkNaoMostrarMais)).isChecked()) {

                        storage.createFile(storage.getInternalFilesDirectory() + File.separator + AVISO_CADASTRO, "{ok}");
                    }

                    abrirActivityCadastrarSuspeito();
                }
            };

            new MaterialDialog.Builder(SincabsActivity.this)
                    .title("Atenção")
                    .customView(R.layout.layout_aviso_cadastrar_suspeito, true)
                    .positiveText("Fazer busca")
                    .negativeText("Depois")
                    .onPositive(positiveCallback)
                    .onNegative(negativeCallback)
                    .show();
        }
        else {

            abrirActivityCadastrarSuspeito();
        }
    }

    private void abrirActivityCadastrarSuspeito() {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SincabsActivity.this, CadastrarSuspeitoActivity.class);
                startActivityForResult(i, 300);
            }
        });
    }

    public void buttonBuscarUsuario(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SincabsActivity.this, BuscarUsuarioActivity.class);
                startActivityForResult(i, 200);
            }
        });
    }

    public void buttonResultadoDaBuscaVoltarSuspeitos(View view) {

        lastFragmentSuspeitos = new FragmentSincabsActivitySuspeitos();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutSuspeitos, lastFragmentSuspeitos);
        ft.commitAllowingStateLoss();
    }

    public void buttonResultadoDaBuscaVoltarUsuarios(View view) {

        lastFragmentUsuarios = new FragmentSincabsActivityUsuarios();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutUsuarios, lastFragmentUsuarios);
        ft.commitAllowingStateLoss();
    }

    public void buttonAtualizarPaginaSuspeitos(View view) {

        lastFragmentSuspeitos = new FragmentSincabsActivitySuspeitos();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutSuspeitos, lastFragmentSuspeitos);
        ft.commitAllowingStateLoss();
    }

    public void buttonAtualizarPaginaUsuarios(View view) {

        lastFragmentUsuarios = new FragmentSincabsActivityUsuarios();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutUsuarios, lastFragmentUsuarios);
        ft.commitAllowingStateLoss();
    }

    public void buttonVerMeuPerfil(View view) {

        dialogHelper.showProgress();

        sincabsServer.perfilUsuario(DataHolder.getInstance().getLoginData("id_usuario"), new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                DataHolder.getInstance().setPerfilUsuarioData(extra);

                Intent i = new Intent(SincabsActivity.this, PerfilUsuarioActivity.class);

                startActivity(i);
            }

            @Override
            void onResponseError(String error) {

                dialogHelper.showError(error);
            }

            @Override
            void onNoResponse(String error) {

                dialogHelper.showError(error);
            }

            @Override
            void onPostResponse() {

                dialogHelper.dismissProgress();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) { //activity Buscar Suspeito

            if (resultCode == 100) {

                lastFragmentSuspeitos = new FragmentSincabsActivityResultadoDaBuscaSuspeitos();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayoutSuspeitos, lastFragmentSuspeitos);
                ft.commitAllowingStateLoss();
            }
        }
        else if (requestCode == 200) { //activity Buscar Usuário

            if (resultCode == 200) {

                lastFragmentUsuarios = new FragmentSincabsActivityResultadoDaBuscaUsuarios();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayoutUsuarios, lastFragmentUsuarios);
                ft.commitAllowingStateLoss();
            }
        }
        else if (requestCode == 300) { //cadastrar suspeito

            if (resultCode == 300) { //se concluir o cadastro

                lastFragmentSuspeitos = new FragmentSincabsActivitySuspeitos();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayoutSuspeitos, lastFragmentSuspeitos);
                ft.commitAllowingStateLoss();
            }
        }
        else if (requestCode == 400) { //abrir perfil de suspeito

            if (resultCode == 400) { //se deletar o suspeito

                lastFragmentSuspeitos = new FragmentSincabsActivitySuspeitos();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayoutSuspeitos, lastFragmentSuspeitos);
                ft.commitAllowingStateLoss();
            }
        }
        else if (requestCode == 500) { //abrir perfil de usuário

            if (resultCode == 500) { //se deletar um suspeito no perfil do usuário

                //lastFragmentUsuarios = new FragmentSincabsActivityUsuarios();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (!exit) {

            exit = true;

            Snackbar.make(findViewById(android.R.id.content), "Pressione novamente para sair", 1000).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    exit = false;
                }
            }, 1000);
        }
        else {

            DataHolder.getInstance().setLoggedIn(false);

            Intent i = new Intent(SincabsActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }
    }

    private boolean checkPermissionGranted(int targetSdkVersion, String permission) {

        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {

                result = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
            else {

                result = PermissionChecker.checkSelfPermission(SincabsActivity.this, permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
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

            Intent i = new Intent(SincabsActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }

        if (!mensagemCompartilharMostrada) {

            mensagemCompartilharMostrada = true;

            final Storage storage = new Storage(SincabsActivity.this);

            if (!storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + AVISO_COMPARTILHAR)) {

                MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (((CheckBox)dialog.findViewById(R.id.checkNaoMostrarMais)).isChecked()) {

                            storage.createFile(storage.getInternalFilesDirectory() + File.separator + AVISO_COMPARTILHAR, "{ok}");
                        }
                    }
                };

                new MaterialDialog.Builder(SincabsActivity.this)
                        .title("Bem vindo")
                        .customView(R.layout.layout_mensagem_inicial, true)
                        .positiveText("OK")
                        .onPositive(positiveCallback)
                        .show();
            }
        }

        final Intent intent = getIntent();

        if (intent.hasExtra("SincabsMessage")) {

            final String msg = intent.getStringExtra("SincabsMessage");

            new MaterialDialog.Builder(SincabsActivity.this)
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
