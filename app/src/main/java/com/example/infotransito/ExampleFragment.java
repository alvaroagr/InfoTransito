package com.example.infotransito;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExampleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExampleFragment extends Fragment {

    private MainActivity host;

    private ShapeableImageView profileIV;
    private TextView nameTV;
    private Button editBtn;

    private RecyclerView postViewList;
    private LinearLayoutManager manager;
    private ForumAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration subscription;

    public ExampleFragment() {
        // Required empty public constructor
    }


    public static ExampleFragment newInstance() {
        ExampleFragment fragment = new ExampleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_example, container, false);
        profileIV = root.findViewById(R.id.profileIV);
        nameTV = root.findViewById(R.id.nameIV);
        editBtn = root.findViewById(R.id.editBtn);

        postViewList = root.findViewById(R.id.forum_postList);
        postViewList.setHasFixedSize(true);
        manager = new LinearLayoutManager(root.getContext());
        postViewList.setLayoutManager(manager);

        adapter = new ForumAdapter();
        postViewList.setAdapter(adapter);

        nameTV.setText(host.getMyUser().getName());
        editBtn.setOnClickListener(
                v -> {
                    Toast.makeText(host, "This btn works", Toast.LENGTH_LONG).show();
                }
        );

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        subscribeToPosts();

        // Load profile picture
        if(host.getMyUser().getImg().contains("https://")){
            Glide.with(profileIV).load(host.getMyUser().getImg()).into(profileIV);
        } else {
            FirebaseStorage.getInstance().getReference()
                    .child("profile_images")
                    .child(host.getMyUser().getImg())
                    .getDownloadUrl()
                    .addOnCompleteListener(
                            task -> {
                                if(task.isSuccessful()){
                                    String url = task.getResult().toString();
                                    Glide.with(profileIV).load(url).into(profileIV);
                                }
                            }
                    );
        }



        return root;
    }

    private void subscribeToPosts() {
        Log.e(">>>" ,auth.getUid());
        Query ref = db.collection("posts").whereEqualTo("userId", auth.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);
        subscription = ref.addSnapshotListener(
                (data, error) -> {
                    adapter.clearPosts();
                    if(data != null) {
                        for(DocumentSnapshot doc : data.getDocuments()){
                            Post post = doc.toObject(Post.class);
                            adapter.addPost(post);
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        subscription.remove();
        super.onDestroy();
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
}