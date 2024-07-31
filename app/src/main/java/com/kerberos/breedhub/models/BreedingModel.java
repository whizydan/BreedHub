package com.kerberos.breedhub.models;

public class BreedingModel {
    private String id, userId, userIdSecond, petId, petIdSecond, clinicId, reason;
    private boolean approved;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIdSecond() {
        return userIdSecond;
    }

    public void setUserIdSecond(String userIdSecond) {
        this.userIdSecond = userIdSecond;
    }

    public String getPetId() {
        return petId;
    }

    public String getPetIdSecond() {
        return petIdSecond;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public void setPetIdSecond(String petIdSecond) {
        this.petIdSecond = petIdSecond;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

}
