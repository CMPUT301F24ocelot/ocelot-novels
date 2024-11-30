package com.example.ocelotnovels.view.Admin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
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
import com.example.ocelotnovels.model.User;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * This is the class that is used to display the profiles in a ListView for the admin to browse
 */
public class ProfileAdapterAdmin extends ArrayAdapter<User> {

    /**
     * This will be called when the ProfileAdapterAdmin
     * @param context
     * @param profiles an Arraylist of profiles for the admin to be able to look through.
     */
    public ProfileAdapterAdmin(Context context, ArrayList<User> profiles) {
        super(context, 0, profiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.waiting_list_item,parent
                    ,false);
        }else{
            view = convertView;
        }
        User profile = getItem(position);
        assert profile != null;
        ImageView profilePicture = view.findViewById(R.id.iv_user_icon);
        String profilePicUrl = profile.getProfilePicture();
        Glide.with(this.getContext())
                    .load(profilePicUrl)
                    .placeholder(R.drawable.ic_image_placeholder) // Optional
                    .error(R.drawable.ic_image_placeholder) // Optional
                    .into(profilePicture);
        profilePicture.setImageResource(R.drawable.ic_image_placeholder);
        TextView name = view.findViewById(R.id.tv_username);
        String nameString = profile.getFirstName()+ " " + profile.getLastName();
        name.setText(nameString);
        Button detailsButton = view.findViewById(R.id.btn_details);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Admin","clicked");
                Intent toProfile = new Intent(ProfileAdapterAdmin.this.getContext(), EntrantProfileAdminView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", profile);
                startActivity(ProfileAdapterAdmin.this.getContext(),toProfile,bundle);
            }
        });

        return view;
    }
}
