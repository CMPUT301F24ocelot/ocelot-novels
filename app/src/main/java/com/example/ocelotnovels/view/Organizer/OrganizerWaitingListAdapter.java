package com.example.ocelotnovels.view.Organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;

import java.util.List;

/**
 * OrganizerWaitingListAdapter is a RecyclerView adapter used to display a list of users
 * in the organizer's waiting list. Each item in the list includes the user's full name
 * and email address. The adapter binds user data to the respective views in the waiting
 * list layout.
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *     <li>Inflating the waiting list item layout</li>
 *     <li>Binding user data to the item views</li>
 *     <li>Providing the item count for the RecyclerView</li>
 * </ul>
 */
public class OrganizerWaitingListAdapter extends RecyclerView.Adapter<OrganizerWaitingListAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    /**
     * Constructs an OrganizerWaitingListAdapter with the specified context and user list.
     *
     * @param context  the application or activity context
     * @param userList the list of users to display in the waiting list
     */
    public OrganizerWaitingListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    /**
     * Creates a new ViewHolder instance for an item in the waiting list.
     *
     * @param parent   the parent ViewGroup to which the new view will be attached
     * @param viewType the type of the new view (not used here as all views are the same type)
     * @return a new ViewHolder containing the inflated layout for the waiting list item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waiting_list_user, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds user data to the item views in the waiting list layout.
     *
     * @param holder   the ViewHolder containing the item views
     * @param position the position of the user in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // Combine first and last name for display
        String fullName = user.getFirstName() + " " + user.getLastName();
        holder.textName.setText(fullName);
        holder.textEmail.setText(user.getEmail());
    }

    /**
     * Returns the total number of items in the waiting list.
     *
     * @return the number of users in the list
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class for holding references to the views in each item of the waiting list.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textEmail;

        /**
         * Constructs a ViewHolder for the specified item view.
         *
         * @param itemView the view representing an item in the waiting list
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
        }
    }
}
