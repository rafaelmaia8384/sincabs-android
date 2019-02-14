package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AdminActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public static DialogHelper dialogHelper;
    public static SincabsServer sincabsServer;

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutAdmin, new FragmentAdminActivityMain());
        ft.commitAllowingStateLoss();

        dialogHelper = new DialogHelper(AdminActivity.this);
        sincabsServer = new SincabsServer(AdminActivity.this);

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

        getMenuInflater().inflate(R.menu.admin, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_sair) {

            Intent i = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            super.onBackPressed();
        }
        else if (!exit) {

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

            Intent i = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }
    }

    public void buttonSolicitacoes(View view) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutAdmin, new FragmentAdminActivityAnalises());
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void buttonSugestoes(View view) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutAdmin, new FragmentAdminActivitySugestoes());
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void buttonDenunciasUsuarios(View view) {


    }

    public void buttonDenunciasSuspeitos(View view) {


    }

    public void buttonImagensAvaliacao(View view) {


    }

    public void buttonUsuariosBloqueados(View view) {


    }

    public void buttonLimparCadastros(View view) {


    }
}