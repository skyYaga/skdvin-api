package in.skdv.skdvinbackend.model.entity.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommonSettings {

    private Dropzone dropzone;
    private List<Faq> faq = new ArrayList<>();
    private String homepageHint = "";
    private String homepageHintTitle = "";
    private String bccMail = "";
    private String additionalReminderHint = "";
    private SelfAssignmentMode selfAssignmentMode = SelfAssignmentMode.WRITE_DELETE;


    public Dropzone getDropzone() {
        return dropzone;
    }

    public void setDropzone(Dropzone dropzone) {
        this.dropzone = dropzone;
    }

    public List<Faq> getFaq() {
        return faq.stream().sorted(Comparator.comparing(Faq::getId)).collect(Collectors.toList());
    }

    public void setFaq(List<Faq> faq) {
        this.faq = faq;
    }

    public String getHomepageHint() {
        return homepageHint;
    }

    public void setHomepageHint(String homepageHint) {
        this.homepageHint = homepageHint;
    }

    public String getHomepageHintTitle() {
        return homepageHintTitle;
    }

    public void setHomepageHintTitle(String homepageHintTitle) {
        this.homepageHintTitle = homepageHintTitle;
    }

    public String getBccMail() {
        return bccMail;
    }

    public void setBccMail(String bccMail) {
        this.bccMail = bccMail;
    }

    public String getAdditionalReminderHint() {
        return additionalReminderHint;
    }

    public void setAdditionalReminderHint(String additionalReminderHint) {
        this.additionalReminderHint = additionalReminderHint;
    }

    public SelfAssignmentMode getSelfAssignmentMode() {
        return selfAssignmentMode;
    }

    public void setSelfAssignmentMode(SelfAssignmentMode selfAssignmentMode) {
        this.selfAssignmentMode = selfAssignmentMode;
    }
}
