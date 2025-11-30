package com.myuniplacement.admin.model.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.myuniplacement.admin.model.entitys.Student;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    private final Firestore db = FirestoreClient.getFirestore();

    public List<Student> getAllStudents() throws Exception {
        List<Student> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection("users").get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(Student.class));
        }
        return list;
    }

    public Student get(String email) throws Exception {
        DocumentSnapshot doc = db.collection("users").document(email).get().get();
        if (!doc.exists()) return null;
        return doc.toObject(Student.class);
    }
}