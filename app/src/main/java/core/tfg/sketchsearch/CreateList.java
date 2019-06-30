package core.tfg.sketchsearch;

import android.graphics.Bitmap;

//list of images
public class CreateList {

    private Integer image_id;
    private Bitmap image_bitmap;

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }

    public void setImage_bitmap(Bitmap image_bitmap) {
        this.image_bitmap = image_bitmap;
    }

    public Integer getImage_id() {
        return image_id;
    }

    public void setImage_id(Integer image_id) {
        this.image_id = image_id;
    }
}
