package com.kerberos.breedhub.models;

public class RequestModel {
    private String id, rejectionReason, nextSteps, status;

    private PetModel cat1, cat2;

    private UserData user1, user2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PetModel getCat1() {
        return cat1;
    }

    public PetModel getCat2() {
        return cat2;
    }

    public void setCat1(PetModel cat1) {
        this.cat1 = cat1;
    }

    public void setCat2(PetModel cat2) {
        this.cat2 = cat2;
    }

    public UserData getUser1() {
        return user1;
    }

    public UserData getUser2() {
        return user2;
    }

    public void setUser1(UserData user1) {
        this.user1 = user1;
    }

    public void setUser2(UserData user2) {
        this.user2 = user2;
    }
}
