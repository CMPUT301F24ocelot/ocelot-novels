package com.example.ocelotnovels.view.Organizer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;

import java.util.List;

public class OrganiserSelectedListAdapter extends RecyclerView.Adapter<OrganiserSelectedListAdapter.ViewHolder> {
    private final List<User> selectedUsersList;
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onCancelEntrant(User user);
    }

    public OrganiserSelectedListAdapter(List<User> selectedUsersList, OnUserActionListener listener) {
        this.selectedUsersList = selectedUsersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = selectedUsersList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return selectedUsersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName;
        private final TextView textEmail;
        private final Button cancelButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            cancelButton = itemView.findViewById(R.id.organiser_cancel_button);
        }

        void bind(final User user) {
            Log.d("USERAD", String.valueOf(user.getDevice_ID()));
            if (user != null) {
                textName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                textEmail.setText(user.getEmail() != null ? user.getEmail() : "");

                cancelButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCancelEntrant(user);
                    }
                });
            }
        }
    }
}