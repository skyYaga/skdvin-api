package in.skdv.skdvinbackend.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Appointment {

    @Id
    public int Id;
    public Customer customer;
    public LocalDateTime date;
    public int tandem;
    public int video;
    public AppointmentState state;
    public LocalDateTime createdOn;
    public String createdBy;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getTandem() {
        return tandem;
    }

    public void setTandem(int tandem) {
        this.tandem = tandem;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public AppointmentState getState() {
        return state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "Id=" + Id +
                ", customer=" + customer +
                ", date=" + date +
                ", tandem=" + tandem +
                ", video=" + video +
                ", state=" + state +
                ", createdOn=" + createdOn +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
