package in.skdv.skdvinbackend.mail.template;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    private TemplateEngine emailTemplateEngine;
    private final Appointment appointment = ModelMockHelper.createSingleAppointment();
    private final CommonSettings settings = ModelMockHelper.createCommonSettings();

    @Test
    public void testAppointmentVerificationMail_US() {
        Context ctx = createContext(Locale.US);
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
                "<p>Your Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentVerificationMail_DE() {
        Context ctx = createContext(Locale.GERMANY);
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
                "<p>WICHTIG: Solltest Du deinen Termin nicht durch einen Klick auf den Link oben bestätigen, wird dieser nach 24 Stunden automatisch storniert!</p>\n" +
                "<p>Zur Überprüfung hier deine Termindaten:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Deine Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Dein Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentConfirmationMail_US() {
        Context ctx = createContext(Locale.US);
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
                "<p>Please come to our manifest at the time of your reservation (10:00).<br/>" +
                "Your booked times are just an indication, there is no guarantee to get in the air at the time ouf your booking. " +
                "Please bring a few hours of your time.<br/>" +
                "Please bring comfortable sportswear and make sure that we can reach you at your telephone number on the day of the jump.<br/>" +
                "If you have any further questions or if you have to cancel your booking, " +
                "send an email to dz@example.com or call us on 015112345678 or during operation on 0987654321.</p>\n" +
                "<p>Your Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentConfirmationMail_DE() {
        Context ctx = createContext(Locale.GERMANY);
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
                "<p>Vielen Dank für deine Reservierung. Nachfolgend alle Details:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Deine Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Melde dich zum gebuchten Zeitpunkt (10:00 Uhr) einfach vor Ort bei uns am Manifest.<br/>" +
                "Die angegebenen Zeiten sind lediglich Richtzeiten, es besteht keine Garantie zur gebuchten Uhrzeit in die Luft zu kommen. " +
                "Bitte nehme dir ein paar Stunden Zeit.<br/>" +
                "Bringe bitte bequeme Sportkleidung mit und achte darauf, " +
                "dass wir dich am Sprungtag unter deiner angegebenen Telefonnummer erreichen können.<br/>" +
                "Falls Du noch Fragen hast oder deinen Termin nicht wahrnehmen kannst, sende bitte eine Mail an dz@example.com " +
                "oder melde dich telefonisch unter 015112345678 oder bei Sprungbetrieb unter 0987654321.</p>\n" +
                "<p>Dein Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUnconfirmedCancellationMail_EN() {
        Context ctx = createContext(Locale.US);
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
                "<p>As you have not confirmed your appointment, it has just been automatically cancelled.<br/>" +
                "If you simply forgot to confirm it, you can book a new appointment on " + BASE_URL + ".<br/><br/>" +
                "The following appointment was deleted:</p>\n" +
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
                "<p>Your Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUnconfirmedCancellationMail_DE() {
        Context ctx = createContext(Locale.GERMANY);
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
                "<p>Da Du deinen Termin nicht bestätigt hast, wurde dieser soeben automatisch storniert.<br/>" +
                "Wenn Du einfach vergessen hast diesen zu bestätigen, kannst Du auf " + BASE_URL + " einen neuen Termin buchen.<br/><br/>" +
                "Folgender Termin wurde gelöscht:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Deine Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Dein Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUpdatedMail_EN() {
        Context ctx = createContext(Locale.US);
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
                "<p>Please come to our manifest at the time of your reservation (10:00).<br/>" +
                "Your booked times are just an indication, there is no guarantee to get in the air at the time ouf your booking. " +
                "Please bring a few hours of your time.<br/>" +
                "Please bring comfortable sportswear and make sure that we can reach you at your telephone number on the day of the jump.<br/>" +
                "If you have any further questions or if you have to cancel your booking, " +
                "send an email to dz@example.com or call us on 015112345678 or during operation on 0987654321.</p>\n" +
                "<p>Your Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentUpdatedMail_DE() {
        Context ctx = createContext(Locale.GERMANY);
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
                "<p>Deine Buchung wurde aktualisiert. Nachfolgend alle Details:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Deine Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Melde dich zum gebuchten Zeitpunkt (10:00 Uhr) einfach vor Ort bei uns am Manifest.<br/>" +
                "Die angegebenen Zeiten sind lediglich Richtzeiten, es besteht keine Garantie zur gebuchten Uhrzeit in die Luft zu kommen. " +
                "Bitte nehme dir ein paar Stunden Zeit.<br/>" +
                "Bringe bitte bequeme Sportkleidung mit und achte darauf, " +
                "dass wir dich am Sprungtag unter deiner angegebenen Telefonnummer erreichen können.<br/>" +
                "Falls Du noch Fragen hast oder deinen Termin nicht wahrnehmen kannst, sende bitte eine Mail an dz@example.com " +
                "oder melde dich telefonisch unter 015112345678 oder bei Sprungbetrieb unter 0987654321.</p>\n" +
                "<p>Dein Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentDeletedMail_EN() {
        Context ctx = createContext(Locale.US);
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
                "<p>Your Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentDeletedMail_DE() {
        Context ctx = createContext(Locale.GERMANY);
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
                "<p>Dein Termin wurde gelöscht. Nachfolgend die Details des gelöschten Termins:</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Deine Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Dein Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentReminderMail_EN() {
        Context ctx = createContext(Locale.US);
        String htmlMail = emailTemplateEngine.process("html/appointment-reminder", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Appointment #0</h1>\n" +
                "<p>Hello Max!</p>\n" +
                "<p>Not long until your skydive at Example DZ.</p>\n" +
                "<p><p>This is a additional hint</p><ul><li>Hint 1</li><li>Hint 2</li></ul></p>\n" +
                "<p>Please come to our manifest at the time of your reservation (10:00).<br/>" +
                "Your booked times are just an indication, there is no guarantee to get in the air at the time ouf your booking. " +
                "Please bring a few hours of your time.<br/>" +
                "Please bring comfortable sportswear and make sure that we can reach you at your telephone number on the day of the jump.<br/>" +
                "If you have any further questions or if you have to cancel your booking, " +
                "send an email to dz@example.com or call us on 015112345678 or during operation on 0987654321.</p>\n" +
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
                "<p>Your Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    @Test
    public void testAppointmentReminderMail_DE() {
        Context ctx = createContext(Locale.GERMANY);
        String htmlMail = emailTemplateEngine.process("html/appointment-reminder", ctx);
        assertEquals("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    \n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Termin #0</h1>\n" +
                "<p>Hallo Max!</p>\n" +
                "<p>Bald ist es soweit: Dein Tandemsprung bei Example DZ.</p>\n" +
                "<p><p>This is a additional hint</p><ul><li>Hint 1</li><li>Hint 2</li></ul></p>\n" +
                "<p>Melde dich zum gebuchten Zeitpunkt (10:00 Uhr) einfach vor Ort bei uns am Manifest.<br/>" +
                "Die angegebenen Zeiten sind lediglich Richtzeiten, es besteht keine Garantie zur gebuchten Uhrzeit in die Luft zu kommen. " +
                "Bitte nehme dir ein paar Stunden Zeit.<br/>" +
                "Bringe bitte bequeme Sportkleidung mit und achte darauf, " +
                "dass wir dich am Sprungtag unter deiner angegebenen Telefonnummer erreichen können.<br/>" +
                "Falls Du noch Fragen hast oder deinen Termin nicht wahrnehmen kannst, sende bitte eine Mail an dz@example.com " +
                "oder melde dich telefonisch unter 015112345678 oder bei Sprungbetrieb unter 0987654321.</p>\n" +
                "<span>Datum: " + appointment.getDate().format(formatter) + " / 10:00</span><br/>\n" +
                "<span>1 x Tandem</span><br/>\n" +
                "<span>(1 x Foto oder Video)</span><br/>\n" +
                "<span>(0 x Foto und Video)</span><br/>\n" +
                "<span>(0 x Handcam)</span>\n" +
                "<p>Deine Daten:</p>\n" +
                "<span>Max Mustermann</span><br/>\n" +
                "<span>E-Mail: max@example.com</span><br/>\n" +
                "<span>Telefon: 0987654</span><br/>\n" +
                "<span>PLZ / Wohnort: 12345 Foo City</span>\n" +
                "<p>Springerdaten:</p>\n" +
                "<ul>\n" +
                "    <li>first0 last0 (01.01.1980)</li>\n" +
                "</ul>\n" +
                "<p>Dein Example DZ</p>\n" +
                "</body>\n" +
                "</html>", htmlMail);
    }

    private Context createContext(Locale locale) {
        Context ctx = new Context(locale);
        ctx.setVariable("appointment", appointment);
        ctx.setVariable("settings", settings);
        ctx.setVariable("baseurl", BASE_URL);
        return ctx;
    }
}
