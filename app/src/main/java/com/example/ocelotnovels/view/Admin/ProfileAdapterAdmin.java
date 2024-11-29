package com.example.ocelotnovels.view.Admin;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;
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

        ImageView profilePicture = view.findViewById(R.id.iv_user_icon);

        TextView name = view.findViewById(R.id.tv_username);
        assert profile != null;
        String nameString = profile.getFirstName()+ " " + profile.getLastName();
        name.setText(nameString);
        Button detailsButton = view.findViewById(R.id.btn_details);

        return view;
    }
}
