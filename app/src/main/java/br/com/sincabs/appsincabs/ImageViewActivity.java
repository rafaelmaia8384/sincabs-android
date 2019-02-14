package br.com.sincabs.appsincabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends AppCompatActivity {

    private DialogHelper dialogHelper;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);

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

        dialogHelper = new DialogHelper(ImageViewActivity.this);

        String imageName = getIntent().getExtras().getString("img_principal");

        final ImageView imageView = findViewById(R.id.imageView);

        ImageLoader.getInstance().displayImage(SincabsServer.getImageAddress(imageName), imageView, null, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.textFalha).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                findViewById(R.id.progressBar).setVisibility(View.GONE);

                imageView.setImageBitmap(loadedImage);

                image = loadedImage;

                PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

                attacher.setMinimumScale(1.0f);
                attacher.setMediumScale(1.5f);
                attacher.setMaximumScale(2.5f);

                findViewById(R.id.buttonShare).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void share(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(Intent.ACTION_SEND);

                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(image));

                startActivity(Intent.createChooser(i, "Compartilhar imagem"));
            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {

        Uri bmpUri = null;

        try {

            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "sincabs-share-" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        }
        catch (IOException e) { }

        return bmpUri;
    }

    public void finishActivity(View view) {

        finish();
    }
}
