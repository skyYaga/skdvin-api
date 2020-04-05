package in.skdv.skdvinbackend.model.entity.settings;

import java.util.ArrayList;
import java.util.List;

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
        return faq;
    }

    public void setFaq(List<Faq> faq) {
        this.faq = faq;
    }
}
