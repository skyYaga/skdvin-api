package in.skdv.skdvinbackend.model.entitiy.settings;

import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Faq;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CommonSettingsTest {

    @Test
    public void testOrder() {
        CommonSettings commonSettings = new CommonSettings();
        Faq faq1 = createFaq(1);
        Faq faq2 = createFaq(2);
        Faq faq3 = createFaq(3);

        ArrayList<Faq> list = new ArrayList<>();
        list.add(faq2);
        list.add(faq3);
        list.add(faq1);

        commonSettings.setFaq(list);

        Assert.assertEquals(1, commonSettings.getFaq().get(0).getId());
        Assert.assertEquals(2, commonSettings.getFaq().get(1).getId());
        Assert.assertEquals(3, commonSettings.getFaq().get(2).getId());
    }

    private Faq createFaq(int i) {
        Faq faq = new Faq();
        faq.setId(i);
        faq.setQuestion("Q");
        faq.setAnswer("A");
        return faq;
    }
}
