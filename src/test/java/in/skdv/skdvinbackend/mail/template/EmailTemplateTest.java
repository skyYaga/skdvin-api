package in.skdv.skdvinbackend.mail.template;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailTemplateTest extends AbstractSkdvinTest {

    private static final String BASE_URL = "https://example.com";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    private TemplateEngine emailTemplateEngine;

    @Test
    public void testAppointmentVerificationMail_US() {
        Context ctx = new Context(Locale.US);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        ctx.setVariable("tokenurl", BASE_URL);
        String htmlMail = emailTemplateEngine.process("html/appointment-verification", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Appointment 0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Please click on the Link below to confirm your appointment.</p>\n" +
                "<a href=\"https://example.com\">https://example.com</a>\n" +
                "<p>IMPORTANT: If you do not confirm your appointment by clicking on the link above, it will be cancelled automatically after 24 hours!</p>\n" +
                "<p>Your Appointment data:</p>\n" +
                "<p>" + appointment.getDate().format(formatter) + " / 10:00</p>\n" +
                "<p>1 x Tandem</p>\n" +
                "<p>(1 x Picture or Video)</p>\n" +
                "<p>(0 x Picture and Video)</p>\n" +
                "<p>(0 x Handcam)</p>\n" +
                "<p>Your data:</p>\n" +
                "<p>Max Mustermann</p>\n" +
                "<p>email: max@example.com</p>\n" +
                "<p>phone: 0987654</p>\n" +
                "<p>ZIP / city: 12345 Foo City</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980 / 70kg)</li>\n" +
                "</ul>\n" +
                "<p>Your Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentVerificationMail_DE() {
        Context ctx = new Context(Locale.GERMANY);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        ctx.setVariable("tokenurl", BASE_URL);
        String htmlMail = emailTemplateEngine.process("html/appointment-verification", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Termin 0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Bitte den Termin durch einen Klick auf folgenden Link bestätigen:</p>\n" +
                "<a href=\"https://example.com\">https://example.com</a>\n" +
                "<p>WICHTIG: Sollten Sie Ihren Termin nicht durch einen Klick auf den Link oben bestätigen, wird dieser nach 24 Stunden automatisch storniert!</p>\n" +
                "<p>Zur Überprüfung hier ihre Termindaten:</p>\n" +
                "<p>" + appointment.getDate().format(formatter) + " / 10:00</p>\n" +
                "<p>1 x Tandem</p>\n" +
                "<p>(1 x Foto oder Video)</p>\n" +
                "<p>(0 x Foto und Video)</p>\n" +
                "<p>(0 x Handcam)</p>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<p>Max Mustermann</p>\n" +
                "<p>E-Mail: max@example.com</p>\n" +
                "<p>Telefon: 0987654</p>\n" +
                "<p>PLZ / Wohnort: 12345 Foo City</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980 / 70kg)</li>\n" +
                "</ul>\n" +
                "<p>Ihr Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }
    @Test
    public void testAppointmentConfirmationMail_US() {
        Context ctx = new Context(Locale.US);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-confirmation", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Appointment 0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Thanks for your reservation. See the details below:</p>\n" +
                "<p>" + appointment.getDate().format(formatter) + " / 10:00</p>\n" +
                "<p>1 x Tandem</p>\n" +
                "<p>(1 x Picture or Video)</p>\n" +
                "<p>(0 x Picture and Video)</p>\n" +
                "<p>(0 x Handcam)</p>\n" +
                "<p>Your data:</p>\n" +
                "<p>Max Mustermann</p>\n" +
                "<p>email: max@example.com</p>\n" +
                "<p>phone: 0987654</p>\n" +
                "<p>ZIP / city: 12345 Foo City</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980 / 70kg)</li>\n" +
                "</ul>\n" +
                "<p>Please come to our manifest at the time of your reservation (10:00).\n" +
                "Your booked times are just an indication, there is no guarantee to get in the air at the time ouf your booking. Please bring a few hours of your time.\n" +
                "If you have any further questions or if you have to cancel your booking, send an email to mail@fsz-hassfurt.de or call us (during the weekend): +49(0)9521-3375.</p>\n" +
                "<p>Your Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentConfirmationMail_DE() {
        Context ctx = new Context(Locale.GERMANY);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-confirmation", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Termin 0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Vielen Dank für Ihre Reservierung. Nachfolgend alle Details:</p>\n" +
                "<p>" + appointment.getDate().format(formatter) + " / 10:00</p>\n" +
                "<p>1 x Tandem</p>\n" +
                "<p>(1 x Foto oder Video)</p>\n" +
                "<p>(0 x Foto und Video)</p>\n" +
                "<p>(0 x Handcam)</p>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<p>Max Mustermann</p>\n" +
                "<p>E-Mail: max@example.com</p>\n" +
                "<p>Telefon: 0987654</p>\n" +
                "<p>PLZ / Wohnort: 12345 Foo City</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980 / 70kg)</li>\n" +
                "</ul>\n" +
                "<p>Melden Sie sich zum gebuchten Zeitpunkt (10:00 Uhr) einfach vor Ort bei uns am Manifest.\n" +
                "Die angegebenen Zeiten sind lediglich Richtzeiten, es besteht keine Garantie zur gebuchten Uhrzeit in die Luft zu kommen. Bitte bringen Sie ein paar Stunden Zeit mit.\n" +
                "Falls Sie noch Fragen haben oder Ihren Termin nicht wahrnehmen können, senden Sie eine Mail an mail@fsz-hassfurt.de oder melden Sie sich telefonisch (am Wochenende) unter 09521-3375.</p>\n" +
                "<p>Ihr Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }
}
