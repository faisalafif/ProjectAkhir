package com.faislll.projectakhir.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faislll.projectakhir.R;
import com.faislll.projectakhir.aktivitas.MainActivity;
import com.faislll.projectakhir.model.Comment;
import com.faislll.projectakhir.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context context;
    private List<Comment> comments;
    public static final String ID_PUBRISHER = "idpengapload";

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    private FirebaseUser user;

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);

        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = comments.get(position);

        holder.textViewText.setText(comment.getText());
        getUserInfo(holder.imageViewPp, holder.textViewMrComment, comment.getMrcomment());

        holder.textViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(ID_PUBRISHER, comment.getMrcomment());
                context.startActivity(intent);
            }
        });
        holder.imageViewPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(ID_PUBRISHER, comment.getMrcomment());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPp;
        public TextView textViewMrComment, textViewText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPp = itemView.findViewById(R.id.image_view_pp_item_comment);
            textViewMrComment = itemView.findViewById(R.id.text_view_username_commentar_item);
            textViewText = itemView.findViewById(R.id.text_view_item_commentar);

        }
    }

    private void getUserInfo(final ImageView imageViewPp, final TextView mrComment, String pengapload) {
        FirebaseFirestore.getInstance().collection("users").document(pengapload)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        UserData userData = value.toObject(UserData.class);
                        Glide.with(context).load(userData.getImageUrl()).into(imageViewPp);
                        mrComment.setText(userData.getUsername());
                    }
                });
    }
}
