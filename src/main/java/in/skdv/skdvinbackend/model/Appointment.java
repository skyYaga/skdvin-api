package in.skdv.skdvinbackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document
public class Appointment {

    @Id
    private int Id;

    @NotNull
    private Customer customer;

    @NotNull
    private LocalDateTime date;

    @NotNull
    @Min(1)
    private int tandem;

    @NotNull
    private int video;

    private AppointmentState state;

    @NotNull
    private LocalDateTime createdOn;

    @NotNull
    private String createdBy;

    @NotNull
    private String clientId;

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
