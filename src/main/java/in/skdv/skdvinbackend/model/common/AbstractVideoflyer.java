package in.skdv.skdvinbackend.model.common;

import javax.validation.constraints.NotNull;

public class AbstractVideoflyer {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String email;
    private String tel;
    private boolean picAndVid;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPicAndVid() {
        return picAndVid;
    }

    public void setPicAndVid(boolean picAndVid) {
        this.picAndVid = picAndVid;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
