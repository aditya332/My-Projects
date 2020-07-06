package com.somanibrothersservices.lokalmall;

public class AddressesModel {
    private Boolean selected;
    private String locality , flatNo , landmark , name , mobileNo , alternateMobileNo ;

    public AddressesModel(Boolean selected, String locality, String flatNo, String landmark, String name, String mobileNo, String alternateMobileNo) {
        this.selected = selected;
        this.locality = locality;
        this.flatNo = flatNo;
        this.landmark = landmark;
        this.name = name;
        this.mobileNo = mobileNo;
        this.alternateMobileNo = alternateMobileNo;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAlternateMobileNo() {
        return alternateMobileNo;
    }

    public void setAlternateMobileNo(String alternateMobileNo) {
        this.alternateMobileNo = alternateMobileNo;
    }
}
