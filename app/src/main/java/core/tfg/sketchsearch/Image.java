package core.tfg.sketchsearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        //Get the image data
        Integer id = getIntent().getExtras().getInt("IMAGE_ID");
        /*Intent intent = getIntent();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(Image.this.openFileInput("myImage"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);*/

        //Change the image
        ((ImageView)findViewById(R.id.imageView)).setImageResource(id);
    }
}
