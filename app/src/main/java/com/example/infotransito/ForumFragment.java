package com.example.infotransito;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {

    private RecyclerView postViewList;
    private LinearLayoutManager manager;
    private ForumAdapter adapter;
    private FirebaseFirestore db;

    public ForumFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance() {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_forum, container, false);
        postViewList = root.findViewById(R.id.forum_postList);
        postViewList.setHasFixedSize(true);
        manager = new LinearLayoutManager(root.getContext());
        postViewList.setLayoutManager(manager);

        adapter = new ForumAdapter();
        postViewList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("posts").get().addOnCompleteListener(
                task -> {
                    for(DocumentSnapshot document : task.getResult().getDocuments()) {
                        Post post = document.toObject(Post.class);
                        adapter.addPost(post);
                    }
                }
        );

        return root;
    }
}