package com.example.flora.Features.Project.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String createdAt;
    private List<String> devices = new ArrayList<>();
    private List<String> techs = new ArrayList<>();
    private String status = "PLANNING";

    public Project() {
    }

    public Project(String id, String name, String description, String ownerId, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public Project(String id, String name, String description, String ownerId,
                   String createdAt, List<String> devices, List<String> techs) {
        this(id, name, description, ownerId, createdAt);
        this.devices = devices != null ? devices : new ArrayList<>();
        this.techs = techs != null ? techs : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> d) {
        this.devices = d != null ? d : new ArrayList<>();
    }

    public List<String> getTechs() {
        return techs;
    }

    public void setTechs(List<String> t) {
        this.techs = t != null ? t : new ArrayList<>();
    }

    public String getStatus() {
        return status != null ? status : "PLANNING";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}