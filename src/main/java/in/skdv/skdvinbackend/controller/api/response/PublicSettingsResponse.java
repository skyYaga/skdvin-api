package in.skdv.skdvinbackend.controller.api.response;

import in.skdv.skdvinbackend.model.entity.settings.Dropzone;
import in.skdv.skdvinbackend.model.entity.settings.Faq;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class PublicSettingsResponse {
    // CommonSettings
    SelfAssignmentMode selfAssignmentMode;
    boolean picAndVidEnabled;

    // LanguageSettings
    Dropzone dropzone;
    List<Faq> faq = new ArrayList<>();
    String homepageHint = "";
    String homepageHintTitle = "";
    String additionalReminderHint = "";
}
