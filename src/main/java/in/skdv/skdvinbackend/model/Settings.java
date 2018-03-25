package in.skdv.skdvinbackend.model;

import java.time.Duration;
import java.time.LocalTime;

public class Settings {

    public LocalTime tandemsFrom;
    public LocalTime tandemsTo;
    public Duration interval;
    public int tandemCount;
    public int videoCount;

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

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "tandemsFrom=" + tandemsFrom +
                ", tandemsTo=" + tandemsTo +
                ", interval=" + interval +
                ", tandemCount=" + tandemCount +
                ", videoCount=" + videoCount +
                '}';
    }
}
