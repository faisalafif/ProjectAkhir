package com.faislll.projectakhir.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.adapter.PostinganAdapter;
import com.faislll.projectakhir.model.Postingan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {
    private static final String TAG = "FragmentHome";
    private RecyclerView recyclerView;
    private PostinganAdapter adapter;
    private List<Postingan> postinganList;
    private ProgressBar loading;

    private List<String> listFollowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_home, container, false);
                recyclerView = view.findViewById(R.id.recycler_view_home);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                postinganList = new ArrayList<>();
                adapter = new PostinganAdapter(this.getContext(), postinganList);
                recyclerView.setAdapter(adapter);
                loading = view.findViewById(R.id.loading_home);

        cekFollowing();
        return view;
    }

    private void tampilkanPostingan() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("photos");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                postinganList.clear();
                for (DocumentSnapshot snapshot : value) {
                    Postingan postingan = snapshot.toObject(Postingan.class);
                    for (String id : listFollowing) {
                        if (postingan.getPengapload().equals(id)) {
                            postinganList.add(postingan);
                        }
                    }
                }
                loading.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void cekFollowing() {
        listFollowing = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("follow")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("following");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listFollowing.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    listFollowing.add(snapshot.getId());
                }
                tampilkanPostingan();
            }
        });
    }
}