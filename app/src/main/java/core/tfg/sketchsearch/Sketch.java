package core.tfg.sketchsearch;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Sketch extends AppCompatActivity
{
    Tactil myDrawView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        // myDrawView = new MyDrawView(this, null);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDrawView = (Tactil)findViewById(R.id.draw);
        Button done = (Button)findViewById(R.id.done);
        Button clear = (Button)findViewById(R.id.clear);
        done.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Bitmap well = myDrawView.getBitmap();
                Bitmap save = Bitmap.createBitmap(320, 480, Config.ARGB_8888);
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                Canvas now = new Canvas(save);
                now.drawRect(new Rect(0,0,320,480), paint);
                now.drawBitmap(well, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,320,480), null);

                createImageFromBitmap(save);

                myDrawView.clear();

                startActivities(new Intent[]{new Intent(Sketch.this, Gallery.class)});
            }
        });
        clear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                myDrawView.clear();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configuration:
                startActivities(new Intent[]{new Intent(Sketch.this, Configuration.class)});
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

}