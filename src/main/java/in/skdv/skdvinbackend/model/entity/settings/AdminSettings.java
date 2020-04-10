package in.skdv.skdvinbackend.model.entity.settings;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class AdminSettings {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime tandemsFrom;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime tandemsTo;
    private String interval;
    private int tandemCount;
    private int picOrVidCount;
    private int picAndVidCount;
    private int handcamCount;

    public LocalTime getTandemsFrom() {
        return tandemsFrom;
    }

    public void setTandemsFrom(LocalTime tandemsFrom) {
        this.tandemsFrom = tandemsFrom;
    }

    public LocalTime getTandemsTo() {
        return tandemsTo;
    }

    public void setTandemsTo(LocalTime tandemsTo) {
        this.tandemsTo = tandemsTo;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getTandemCount() {
        return tandemCount;
    }

    public void setTandemCount(int tandemCount) {
        this.tandemCount = tandemCount;
    }

    public int getPicOrVidCount() {
        return picOrVidCount;
    }

    public void setPicOrVidCount(int picOrVidCount) {
        this.picOrVidCount = picOrVidCount;
    }

    public int getPicAndVidCount() {
        return picAndVidCount;
    }

    public void setPicAndVidCount(int picAndVidCount) {
        this.picAndVidCount = picAndVidCount;
    }

    public int getHandcamCount() {
        return handcamCount;
    }

    public void setHandcamCount(int handcamCount) {
        this.handcamCount = handcamCount;
    }
}
