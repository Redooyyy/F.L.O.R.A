package com.example.flora.Features.Bug.model;


public class Bug {

    private String id;
    private String projectId;
    private String title;
    private BugStatus status;
    private String fixingUserId;


    public Bug() {}

    public Bug(String id, String projectId, String title,
               BugStatus status, String fixingUserId) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.status = status;
        this.fixingUserId = fixingUserId;
    }

    public static Bug open(String projectId, String title) {
        return new Bug(null, projectId, title, BugStatus.OPEN, null);
    }


    public String getId(){
        return id;
    }
    public String getProjectId(){
        return projectId;
    }
    public String getTitle(){
        return title;
    }
    public BugStatus getStatus(){
        return status;
    }
    public String getFixingUserId(){
        return fixingUserId;
    }


    public void setId(String id) {
        this.id = id;
    }
    public void setProjectId(String projectId){
        this.projectId = projectId;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setStatus(BugStatus status){
        this.status = status;
    }
    public void setFixingUserId(String userId){
        this.fixingUserId = userId;
    }


    public boolean isUnclaimed() {
        return fixingUserId == null || fixingUserId.isBlank();
    }

    public boolean isClosed() {
        return status == BugStatus.CLOSED;
    }
}