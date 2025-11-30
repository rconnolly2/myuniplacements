package com.myuniplacement.admin.model.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private final Firestore db = FirestoreClient.getFirestore();

    public DocumentSnapshot getPlacement(String id) throws ExecutionException, InterruptedException {
        return db.collection("placements").document(id).get().get();
    }
}

