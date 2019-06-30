package core.tfg.sketchsearch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

//Class that contains the gallery
public class Gallery extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    try{
                        Log.i("OpenCV", "OpenCV loaded successfully");
                        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
                        recyclerView.setHasFixedSize(true);

                        //Positioning items on the view
                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
                        recyclerView.setLayoutManager(layoutManager);

                        //Prepare the images

                        final ArrayList<CreateList> createLists = prepareData();


                        MyAdapter adapter = new MyAdapter(getApplicationContext(), createLists);

                        //Click an image to open
                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Gallery.this, Image.class);
                                //Put the clicked image data
                                intent.putExtra("IMAGE_ID", createLists.get(recyclerView.getChildAdapterPosition(v)).getImage_id());

                                startActivities(new Intent[]{intent});

                            }
                        });


                        recyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "No couple found. Try again", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    //Function that prepare the images
    private ArrayList<CreateList> prepareData(){
        GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
        String kind = globalVariables.getKind();
        boolean window = globalVariables.isWindow_state();
        int distance = globalVariables.getORB_distance();
        int nbins_r = globalVariables.getNbins_r();
        int nbins_thate = globalVariables.getNbins_theta();
        Double r_inner = globalVariables.getR_innter();
        Double r_outter = globalVariables.getR_outter();
        int simpleto_shapeContext = globalVariables.getSimpleto_shapeContext();
        int simpleto_hausdorff = globalVariables.getSimpleto_hausdorff();
        int qlenght = globalVariables.getQlength();
        int window_mov = globalVariables.getWindow_mov();
        int window_height = globalVariables.getWindow_height();
        int window_width = globalVariables.getWindow_width();

        String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/sketchSearch";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        if (files.length == 0) {
            Toast.makeText(getApplicationContext(), "The gallery is empty", Toast.LENGTH_SHORT).show();
            finish();
        }
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }

        //Load the images
        String location = Environment.getRootDirectory().toString();
        File f = new File(location);
        File file[] = f.listFiles();
        Comparison comp = new Comparison(5, 12, 2.0, 0.1250, 100);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(Gallery.this.openFileInput("myImage"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Mat sketch = new Mat();
        Utils.bitmapToMat(bitmap, sketch);

        Map<Integer, Bitmap> dictionary_int = new HashMap<Integer, Bitmap>();
        Map<Double, Bitmap> dictionary_double = new HashMap<Double, Bitmap>();
        int value = 0;
        Double result;
        Mat copy = new Mat();
        Imgproc.cvtColor(sketch, copy, Imgproc.COLOR_BGR2HSV);
        int[][] des2 = comp.ShapeContext(copy, nbins_r, nbins_thate, simpleto_shapeContext, r_inner, r_outter);
        int[][] des1;

        ArrayList<CreateList> theimage = new ArrayList<>();
        for(int i = 0; i< files.length; i++){
            //Prepare the load image
            Bitmap mybitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath());
            //Bitmap mybitmap = BitmapFactory.decodeResource(getResources(), image_ids[i]);
            Mat color = new Mat();
            Utils.bitmapToMat(mybitmap, color);

            Mat draw = new Mat();
            draw = comp.Canny(color);
            System.out.println(kind);
            if (window) {
                switch (kind){
                    case ("ORB"):
                        value = comp.window_ORB(draw, sketch, window_mov, window_height, window_width, distance);
                        dictionary_int.put(value, mybitmap);
                        break;
                    case ("ShapeContext"):
                        result = comp.window_ShapeContext(draw, window_mov, window_height, window_width, nbins_r, nbins_thate, simpleto_shapeContext, r_inner, r_outter, qlenght, des2);
                        dictionary_double.put(result, mybitmap);
                        break;
                    case ("Hausdorff"):
                        result = comp.window_Hausdorff(draw, copy, window_mov, window_height, window_width, simpleto_hausdorff);
                        dictionary_double.put(result, mybitmap);
                        break;
                }
            } else {
                switch (kind){
                    case ("ORB"):
                        System.out.println("Esto es Shape ORB");
                        value = comp.ORB(sketch, draw, distance);
                        dictionary_int.put(value, mybitmap);
                        break;
                    case ("ShapeContext"):
                        System.out.println("Esto es Shape Context");
                        des1 = comp.ShapeContext(draw, nbins_r, nbins_thate, simpleto_shapeContext, r_inner, r_outter);
                        result = comp.diff(des1, des2, qlenght, nbins_r, nbins_thate);
                        dictionary_double.put(result, mybitmap);
                        break;
                    case ("Hausdorff"):
                        System.out.println("Esto es Shape Context");
                        result = comp.distanceHausdorff(draw, copy, simpleto_hausdorff);
                        dictionary_double.put(result, mybitmap);
                        break;
                }
            }
            System.out.println(dictionary_int);

        }
        switch (kind){
            case ("ORB"):
                SortedSet<Integer> keys = new TreeSet<>(dictionary_int.keySet());
                System.out.println(keys);
                for(Integer key : keys) {
                    System.out.println(key);

                    CreateList createList = new CreateList();

                    createList.setImage_bitmap(dictionary_int.get(key));

                    theimage.add(0, createList);
                }
                break;
            default:
                SortedSet<Double> keys_double = new TreeSet<>(dictionary_double.keySet());
                System.out.println(keys_double);
                for(Double key : keys_double) {
                    System.out.println(key);

                    CreateList createList = new CreateList();

                    createList.setImage_bitmap(dictionary_double.get(key));

                    theimage.add(createList);
                }
                break;
        }

        return theimage;
    }

    private Mat readImageFromResources(Integer id) {
        Mat img = null;
        /*try {
            img = Utils.loadResource(this, id);
            Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGRA);
        } catch (IOException e) {
            Log.e("Error",Log.getStackTraceString(e));
        }*/
        return img;
    }

    private void showImg(Mat img) {
        Bitmap bm = Bitmap.createBitmap(img.cols(), img.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, bm);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bm);
    }
}
