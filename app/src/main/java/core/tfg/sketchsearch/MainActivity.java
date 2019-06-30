package core.tfg.sketchsearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    //String of test images
    private final Integer image_ids[] = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img7,
            R.drawable.img8,
    };

    private final String image_name[] = {
            "img1",
            "img2",
            "img3",
            "img4",
            "img5",
            "img6",
            "img7",
            "img8",
    };

    //Touch the screen to start
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        startActivities(new Intent[]{new Intent(
                MainActivity.this, Sketch.class)});

        return true;
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

        }

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Pictures" + File.separator + "sketchSearch");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();

            for (int i = 0; i < image_ids.length; i++) {
                Bitmap mybitmap = BitmapFactory.decodeResource(getResources(), image_ids[i]);

                File img = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "sketchSearch" + File.separator + image_name[i] + ".jpg");

                try (FileOutputStream out = new FileOutputStream(img)) {
                    mybitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}



