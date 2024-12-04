package com.example.ocelotnovels.view.Admin;

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
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;

import java.util.ArrayList;

public class ProfileAdapterAdmin extends ArrayAdapter<User> {
    public ProfileAdapterAdmin(Context context, ArrayList<User> profiles) {
        super(context, 0, profiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.waiting_list_item, parent, false);
        } else {
            view = convertView;
        }

        User profile = getItem(position);
        if (profile == null) return view;

        ImageView profilePicture = view.findViewById(R.id.iv_user_icon);
        String profilePicUrl = profile.getProfilePicture();

        // Remove the setImageResource before Glide loading
        Glide.with(getContext())
                .load(profilePicUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(profilePicture);

        TextView name = view.findViewById(R.id.tv_username);
        String nameString = profile.getFirstName() + " " + profile.getLastName();
        name.setText(nameString);

        Button detailsButton = view.findViewById(R.id.btn_details);
        detailsButton.setOnClickListener(view1 -> {
            Intent toProfile = new Intent(getContext(), EntrantProfileAdminView.class);
            toProfile.putExtra("User", profile);
            ContextCompat.startActivity(getContext(), toProfile, null);
        });

        return view;
    }
}