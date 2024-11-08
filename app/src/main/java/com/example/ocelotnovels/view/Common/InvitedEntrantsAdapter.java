//package com.example.ocelotnovels.view.Common;
//
//// InvitedEntrantsAdapter.java
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ocelotnovels.R;
//import com.google.firebase.firestore.auth.User;
//
//import java.util.List;
//
//public class InvitedEntrantsAdapter extends RecyclerView.Adapter<InvitedEntrantsAdapter.EntrantViewHolder> {
//    private final List<User> entrants;
//    private final Context context;
//
//    public InvitedEntrantsAdapter(Context context, List<User> entrants) {
//        this.context = context;
//        this.entrants = entrants;
//    }
//
//    @NonNull
//    @Override
//    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_entrant, parent, false);
//        return new EntrantViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
//        User user = entrants.get(position);
//        holder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());
//        holder.emailTextView.setText(user.getEmail());
//        // If you have profile pictures
//        // Glide.with(context).load(user.getPhotoUrl()).into(holder.profilePicImageView);
//    }
//
//    @Override
//    public int getItemCount() {
//        return entrants.size();
//    }
//
//    class EntrantViewHolder extends RecyclerView.ViewHolder {
//        TextView nameTextView;
//        TextView emailTextView;
//        // ImageView profilePicImageView;
//
//        EntrantViewHolder(View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.entrant_name);
//            emailTextView = itemView.findViewById(R.id.entrant_email);
//            // profilePicImageView = itemView.findViewById(R.id.profile_pic);
//        }
//    }
//}
//
