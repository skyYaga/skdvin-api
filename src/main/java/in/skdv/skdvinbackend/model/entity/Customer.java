package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.util.ValidEmail;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Customer {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String tel;
    @NotNull
    @ValidEmail
    private String email;
    @NotNull
    private String zip;
    @NotNull
    private String city;
    @NotNull
    private List<Jumper> jumpers;

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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Jumper> getJumpers() {
        return jumpers;
    }

    public void setJumpers(List<Jumper> jumpers) {
        this.jumpers = jumpers;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
