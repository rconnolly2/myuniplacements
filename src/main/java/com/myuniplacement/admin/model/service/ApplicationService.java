package com.myuniplacement.admin.model.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.myuniplacement.admin.model.entitys.Application;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    private final Firestore db = FirestoreClient.getFirestore();

    public List<Application> getApplicationsForUser(String email) throws Exception {
        List<Application> list = new ArrayList<>();

        ApiFuture<QuerySnapshot> future =
                db.collection("applications")
                        .whereEqualTo("userEmail", email)
                        .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(Application.class));
        }

        return list;
    }
}