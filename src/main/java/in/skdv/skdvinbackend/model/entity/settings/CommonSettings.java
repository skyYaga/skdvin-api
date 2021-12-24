package in.skdv.skdvinbackend.model.entity.settings;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommonSettings {

    private Dropzone dropzone;
    private List<Faq> faq = new ArrayList<>();
    private String homepageHint = "";
    private String homepageHintTitle = "";
    private String bccMail = "";
    private String additionalReminderHint = "";
    private SelfAssignmentMode selfAssignmentMode = SelfAssignmentMode.WRITE_DELETE;


    public List<Faq> getFaq() {
        return faq.stream().sorted(Comparator.comparing(Faq::getId)).collect(Collectors.toList());
    }
}
