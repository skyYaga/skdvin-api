package in.skdv.skdvinbackend.model.entity.settings;

import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
@Builder
public class LanguageSettings {

    private Dropzone dropzone;
    private List<Faq> faq;
    private String homepageHint;
    private String homepageHintTitle;
    private String additionalReminderHint;

    public List<Faq> getFaq() {
        return faq.stream().sorted(Comparator.comparing(Faq::getId)).toList();
    }
}
