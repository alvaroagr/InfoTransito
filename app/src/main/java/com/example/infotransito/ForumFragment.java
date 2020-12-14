package com.example.infotransito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {

    private MainActivity host;

    private ListenerRegistration subscription;

    private RecyclerView postViewList;
    private LinearLayoutManager manager;
    private ForumAdapter adapter;
    private FirebaseFirestore db;

//    private Button forum_addBtn;
    private ImageButton forum_addBtn;

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
    public void onAttach(@NonNull Context context) {
        host = (MainActivity) getActivity();
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        host = null;
        super.onDetach();
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

        forum_addBtn = root.findViewById(R.id.forum_addBtn);
        forum_addBtn.setOnClickListener(
                v -> {
                    Intent i = new Intent(host, NewPostActivity.class);
                    i.putExtra("userId", host.getMyUser().getId());
                    i.putExtra("username", host.getMyUser().getName());
                    startActivity(i);
                    host.overridePendingTransition(R.anim.enter, R.anim.stay);
                }
        );

        db = FirebaseFirestore.getInstance();

//        db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(
//                task -> {
//                    for(DocumentSnapshot document : task.getResult().getDocuments()) {
//                        Post post = document.toObject(Post.class);
//                        adapter.addPost(post);
//                    }
//                }
//        );

        subscribeToPosts();

        return root;
    }

    public void subscribeToPosts(){
        Query ref = db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
        subscription = ref.addSnapshotListener(
                (data, error) -> {
                    adapter.clearPosts();

                    for(DocumentSnapshot doc : data.getDocuments()){
                        Post post = doc.toObject(Post.class);
                        adapter.addPost(post);
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        subscription.remove();
        super.onDestroy();
    }
}