package com.myuniplacement.admin.model.entitys;

import lombok.Data;

@Data
public class Announcement {
    private String id;
    private String title;
    private String content;
    private String image;
    private String addedDate;
    private String modifiedDate;
}