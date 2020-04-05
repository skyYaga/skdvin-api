package in.skdv.skdvinbackend.model.entity.settings;

import java.time.Duration;
import java.time.LocalTime;

public class AdminSettings {

    private LocalTime tandemsFrom;
    private LocalTime tandemsTo;
    private Duration interval;
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

    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
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
