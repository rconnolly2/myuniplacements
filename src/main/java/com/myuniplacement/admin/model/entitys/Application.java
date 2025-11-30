package com.myuniplacement.admin.model.entitys;

import lombok.Data;

@Data
public class Application {
    private String id;
    private String placementId;
    private String userEmail;
    private String coverLetter;
    private String screenshotUrl;
    private long appliedDate;
    private String status;
}