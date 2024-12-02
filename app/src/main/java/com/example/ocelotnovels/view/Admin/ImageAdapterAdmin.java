package com.example.ocelotnovels.view.Admin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Image;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;

import java.util.ArrayList;

/**
 * This is the adapter class that will be used to put the images in the admin browser for the admin to review
 */
public class ImageAdapterAdmin extends ArrayAdapter<Image> {
    private ArrayList<Image> images;
    /**
     * The constructor for the ImageAdapterAdmin
     *
     * @param context
     * @param images
     */
    public ImageAdapterAdmin(@NonNull Context context, ArrayList<Image> images) {
        super(context, 0, images);
        this.images = images;
    }

    /**
     * Provides a View for an AdapterView (ListView) to display Images.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that this view will be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_view_admin, parent
                    , false);
        } else {
            view = convertView;
        }
        Image image = getItem(position);
        assert image != null;
        ImageView imageView = view.findViewById(R.id.image);
        String imageUrl = image.getUrl();
        Glide.with(this.getContext())
                .load(imageUrl)
                //.placeholder(R.drawable.ic_image_placeholder) // Optional
                //.error(R.drawable.ic_image_placeholder) // Optional
                .into(imageView);
        //imageView.setImageResource(R.drawable.ic_image_placeholder);
        Button detailsButton = view.findViewById(R.id.delete_button);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtils firebaseUtils = FirebaseUtils.getInstance(ImageAdapterAdmin.this.getContext());
                firebaseUtils.deleteImage(ImageAdapterAdmin.this.getContext(),images.get(position));
                images.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
