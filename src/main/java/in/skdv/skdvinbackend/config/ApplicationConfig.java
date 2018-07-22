package in.skdv.skdvinbackend.config;

import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.impl.MongoAppointmentService;
import in.skdv.skdvinbackend.service.impl.MongoJumpdayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Bean
    public IAppointmentService getAppointmentService() {
        return new MongoAppointmentService(appointmentRepository);
    }

    @Bean
    public IJumpdayService getJumpdayService() {
        return new MongoJumpdayService(jumpdayRepository);
    }
}
