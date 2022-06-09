package in.skdv.skdvinbackend.model.entitiy.settings;

import in.skdv.skdvinbackend.model.entity.settings.Faq;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageSettingsTest {

    @Test
    void testOrder() {

        Faq faq1 = createFaq(1);
        Faq faq2 = createFaq(2);
        Faq faq3 = createFaq(3);

        ArrayList<Faq> list = new ArrayList<>();
        list.add(faq2);
        list.add(faq3);
        list.add(faq1);

        LanguageSettings languageSettings = LanguageSettings.builder().
                faq(list).build();

        assertEquals(1, languageSettings.getFaq().get(0).getId());
        assertEquals(2, languageSettings.getFaq().get(1).getId());
        assertEquals(3, languageSettings.getFaq().get(2).getId());
    }

    private Faq createFaq(int i) {
        Faq faq = new Faq();
        faq.setId(i);
        faq.setQuestion("Q");
        faq.setAnswer("A");
        return faq;
    }
}
