package com.myuniplacement.admin.model.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.myuniplacement.admin.model.entitys.Placement;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PlacementService {

    private final Firestore db = FirestoreClient.getFirestore();

    public List<Placement> getAllPlacements() throws Exception {
        List<Placement> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection("placements").get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(Placement.class));
        }
        return list;
    }

    public Placement get(String id) throws Exception {
        DocumentSnapshot doc = db.collection("placements").document(id).get().get();
        if (!doc.exists()) return null;
        return doc.toObject(Placement.class);
    }

    public void save(Placement p, MultipartFile file) throws Exception {
        Placement old = null;
        if (p.getId() != null && !p.getId().isBlank()) {
            DocumentSnapshot doc = db.collection("placements").document(p.getId()).get().get();
            if (doc.exists()) old = doc.toObject(Placement.class);
        }

        boolean isNew = (old == null);

        if (isNew) {
            p.setAddedDate(LocalDate.now().toString());
            p.setModifiedDate(LocalDate.now().toString());
            p.setCompanyLogo("");
        } else {
            p.setAddedDate(old.getAddedDate());
            p.setModifiedDate(LocalDate.now().toString());
        }

        if (file != null && !file.isEmpty()) {

            String original = file.getOriginalFilename();
            String ext = ".png";

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String fileName = "companyLogos/" + p.getId() + ext;

            StorageClient.getInstance()
                    .bucket()
                    .create(fileName, file.getBytes(), file.getContentType());

            String encoded = fileName.replace("/", "%2F");
            String url = "https://firebasestorage.googleapis.com/v0/b/myuniplacement.firebasestorage.app/o/"
                    + encoded + "?alt=media";

            p.setCompanyLogo(url);

        } else {
            if (!isNew) {
                p.setCompanyLogo(old.getCompanyLogo());
            }
        }

        db.collection("placements").document(p.getId()).set(p).get();
    }

    public void delete(String id) throws Exception {
        db.collection("placements").document(id).delete().get();
    }
}