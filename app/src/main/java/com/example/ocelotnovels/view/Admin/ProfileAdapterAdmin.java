/**
 * This class is responsible for providing an adapter to display user profiles in a ListView for administrators.
 * The adapter inflates a custom layout for each profile in the list and binds profile data to the UI components.
 * Administrators can view details of individual profiles by clicking a "Details" button.
 *
 * Key features:
 * - Displays user profile pictures using Glide for image loading.
 * - Shows the user's first and last name.
 * - Provides a button to navigate to a detailed profile view of the selected user.
 *
 * Dependencies:
 * - Glide library for image loading.
 * - Firebase for profile picture URLs.
 * - Custom layout resource `R.layout.waiting_list_item` for displaying each profile.
 * - `EntrantProfileAdminView` activity for detailed profile view navigation.
 */

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

    /**
     * Provides a View for an AdapterView (ListView) to display user profiles.
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
                Intent toProfile = new Intent(ProfileAdapterAdmin.this.getContext(), EntrantProfileAdminView.class);
                toProfile.putExtra("User",profile);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("User", profile);
                startActivity(ProfileAdapterAdmin.this.getContext(),toProfile,null);
            }
        });

        return view;
    }
}
