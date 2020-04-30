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
                "<h1>Appointment #0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Please click on the Link below to confirm your appointment.</p>\n" +
                "<a href=\"https://example.com\">https://example.com</a>\n" +
                "<p>IMPORTANT: If you do not confirm your appointment by clicking on the link above, it will be cancelled automatically after 24 hours!</p>\n" +
                "<p>Your Appointment data:</p>\n" +
                "<span>Date: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Picture or Video)</span><br/>\n" +
                "<span>(0 x Picture and Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Your data:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>email: max@example.com</span><br/>\n" +
                "<span>phone: 0987654</span><br/>\n" +
                "<span>ZIP / city: 12345 Foo City</span>\n" +
                "<p>Jumper&#39;s data:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
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
                "<h1>Termin #0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Bitte den Termin durch einen Klick auf folgenden Link bestätigen:</p>\n" +
                "<a href=\"https://example.com\">https://example.com</a>\n" +
                "<p>WICHTIG: Sollten Sie Ihren Termin nicht durch einen Klick auf den Link oben bestätigen, wird dieser nach 24 Stunden automatisch storniert!</p>\n" +
                "<p>Zur Überprüfung hier ihre Termindaten:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
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
                "<h1>Appointment #0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Thanks for your reservation. See the details below:</p>\n" +
                "<span>Date: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Picture or Video)</span><br/>\n" +
                "<span>(0 x Picture and Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Your data:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>email: max@example.com</span><br/>\n" +
                "<span>phone: 0987654</span><br/>\n" +
                "<span>ZIP / city: 12345 Foo City</span>\n" +
                "<p>Jumper&#39;s data:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Please come to our manifest at the time of your reservation (10:00).<br/>Your booked times are just an indication, there is no guarantee to get in the air at the time ouf your booking. Please bring a few hours of your time.<br/>If you have any further questions or if you have to cancel your booking, send an email to mail@fsz-hassfurt.de or call us (during the weekend): +49(0)9521-3375.</p>\n" +
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
                "<h1>Termin #0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Vielen Dank für Ihre Reservierung. Nachfolgend alle Details:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Melden Sie sich zum gebuchten Zeitpunkt (10:00 Uhr) einfach vor Ort bei uns am Manifest.<br/>Die angegebenen Zeiten sind lediglich Richtzeiten, es besteht keine Garantie zur gebuchten Uhrzeit in die Luft zu kommen. Bitte bringen Sie ein paar Stunden Zeit mit.<br/>Falls Sie noch Fragen haben oder Ihren Termin nicht wahrnehmen können, senden Sie eine Mail an mail@fsz-hassfurt.de oder melden Sie sich telefonisch (am Wochenende) unter 09521-3375.</p>\n" +
                "<p>Ihr Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUnconfirmedCancellationMail_EN() {
        Context ctx = new Context(Locale.US);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-unconfirmed-cancellation", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1><span>CANCELLATION</span> <span>Appointment #0</span></h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>As you have not confirmed your appointment, it has just been automatically cancelled.<br/>If you simply forgot to confirm it, you can book a new appointment on skdv.in.<br/><br/>The following appointment was deleted:</p>\n" +
                "<span>Date: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Picture or Video)</span><br/>\n" +
                "<span>(0 x Picture and Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Your data:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>email: max@example.com</span><br/>\n" +
                "<span>phone: 0987654</span><br/>\n" +
                "<span>ZIP / city: 12345 Foo City</span>\n" +
                "<p>Jumper&#39;s data:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Your Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUnconfirmedCancellationMail_DE() {
        Context ctx = new Context(Locale.GERMANY);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-unconfirmed-cancellation", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1><span>STORNIERUNG</span> <span>Termin #0</span></h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Da Sie Ihren Termin nicht bestätigt haben, wurde dieser soeben automatisch storniert.<br/>Wenn Sie einfach vergessen haben diesen zu bestätigen, können Sie auf skdv.in einen neuen Termin buchen.<br/><br/>Folgender Termin wurde gelöscht:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Ihr Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUpdatedMail_EN() {
        Context ctx = new Context(Locale.US);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-updated", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Appointment #0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Your booking was updated. See the details below:</p>\n" +
                "<span>Date: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Picture or Video)</span><br/>\n" +
                "<span>(0 x Picture and Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Your data:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>email: max@example.com</span><br/>\n" +
                "<span>phone: 0987654</span><br/>\n" +
                "<span>ZIP / city: 12345 Foo City</span>\n" +
                "<p>Jumper&#39;s data:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Please come to our manifest at the time of your reservation (10:00).<br/>Your booked times are just an indication, there is no guarantee to get in the air at the time ouf your booking. Please bring a few hours of your time.<br/>If you have any further questions or if you have to cancel your booking, send an email to mail@fsz-hassfurt.de or call us (during the weekend): +49(0)9521-3375.</p>\n" +
                "<p>Your Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUpdatedMail_DE() {
        Context ctx = new Context(Locale.GERMANY);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-updated", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Termin #0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Ihre Buchung wurde aktualisiert. Nachfolgend alle Details:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Melden Sie sich zum gebuchten Zeitpunkt (10:00 Uhr) einfach vor Ort bei uns am Manifest.<br/>Die angegebenen Zeiten sind lediglich Richtzeiten, es besteht keine Garantie zur gebuchten Uhrzeit in die Luft zu kommen. Bitte bringen Sie ein paar Stunden Zeit mit.<br/>Falls Sie noch Fragen haben oder Ihren Termin nicht wahrnehmen können, senden Sie eine Mail an mail@fsz-hassfurt.de oder melden Sie sich telefonisch (am Wochenende) unter 09521-3375.</p>\n" +
                "<p>Ihr Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentDeletedMail_EN() {
        Context ctx = new Context(Locale.US);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-deleted", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Appointment #0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Your booking was deteted. See the details of the deleted reservation below:</p>\n" +
                "<span>Date: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Picture or Video)</span><br/>\n" +
                "<span>(0 x Picture and Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Your data:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>email: max@example.com</span><br/>\n" +
                "<span>phone: 0987654</span><br/>\n" +
                "<span>ZIP / city: 12345 Foo City</span>\n" +
                "<p>Jumper&#39;s data:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Your Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentDeletedMail_DE() {
        Context ctx = new Context(Locale.GERMANY);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        ctx.setVariable("appointment", appointment);
        String htmlMail = emailTemplateEngine.process("html/appointment-deleted", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Termin #0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Ihr Termin wurde gelöscht. Nachfolgend die Details des gelöschten Termins:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Ihre Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Ihr Fallschirm-Sport-Zentrum Haßfurt e.V.</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }
}
