package com.myuniplacement.admin.model.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.myuniplacement.admin.model.entitys.Announcement;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementService {

    private final Firestore db = FirestoreClient.getFirestore();

    public List<Announcement> getAll() throws Exception {
        List<Announcement> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection("announcements").get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(Announcement.class));
        }
        return list;
    }

    public Announcement get(String id) throws Exception {
        DocumentSnapshot doc = db.collection("announcements").document(id).get().get();
        if (!doc.exists()) return null;
        return doc.toObject(Announcement.class);
    }

    public void save(Announcement a, MultipartFile file) throws Exception {

        Announcement old = null;

        if (a.getId() != null && !a.getId().isBlank()) {
            DocumentSnapshot doc = db.collection("announcements").document(a.getId()).get().get();
            if (doc.exists()) old = doc.toObject(Announcement.class);
        }

        boolean isNew = (old == null);

        if (isNew) {
            a.setAddedDate(LocalDate.now().toString());
            a.setModifiedDate(LocalDate.now().toString());
            a.setImage("");
        } else {
            a.setAddedDate(old.getAddedDate());
            a.setModifiedDate(LocalDate.now().toString());
        }

        if (file != null && !file.isEmpty()) {

            String original = file.getOriginalFilename();
            String ext = ".png";

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String fileName = "announcementImages/" + a.getId() + ext;

            StorageClient.getInstance()
                    .bucket()
                    .create(fileName, file.getBytes(), file.getContentType());

            String encoded = fileName.replace("/", "%2F");
            String url = "https://firebasestorage.googleapis.com/v0/b/myuniplacement.firebasestorage.app/o/"
                    + encoded + "?alt=media";

            a.setImage(url);

        } else {
            if (!isNew) {
                a.setImage(old.getImage());
            }
        }

        db.collection("announcements").document(a.getId()).set(a).get();
    }

    public void delete(String id) throws Exception {
        db.collection("announcements").document(id).delete().get();
    }
}