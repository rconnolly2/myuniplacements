package com.myuniplacement.admin.model.entitys;

import lombok.Data;

@Data
public class Placement {
    private String id;
    private String title;
    private String company;
    private String companyLogo;
    private String description;
    private String location;
    private String addedDate;
    private String modifiedDate;
    private String placementUrl;
}