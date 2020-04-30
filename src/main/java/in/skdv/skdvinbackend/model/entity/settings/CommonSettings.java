package in.skdv.skdvinbackend.model.entity.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommonSettings {

    private Dropzone dropzone;
    private List<Faq> faq = new ArrayList<>();

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
}
