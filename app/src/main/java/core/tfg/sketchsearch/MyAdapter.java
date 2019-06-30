package core.tfg.sketchsearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<CreateList> galleryList;
    private Context context;
    private View.OnClickListener listener;

    public MyAdapter(Context context, ArrayList<CreateList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    //Set the ViewHolder
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Set the layout to every item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.image_view, viewGroup, false);

        view.setOnClickListener(this);

        return new ViewHolder(view);
    }

    //Specify the items position
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setImageBitmap((galleryList.get(i).getImage_bitmap()));
    }

    //Return the number of items
    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    //Set the click listener
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Set click action
    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}
