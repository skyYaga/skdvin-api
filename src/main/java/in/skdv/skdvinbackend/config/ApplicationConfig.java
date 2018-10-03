package in.skdv.skdvinbackend.config;

import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISequenceService;
import in.skdv.skdvinbackend.service.impl.MongoAppointmentService;
import in.skdv.skdvinbackend.service.impl.MongoJumpdayService;
import in.skdv.skdvinbackend.service.impl.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class ApplicationConfig {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Bean
    public ISequenceService getSequenceService() {
        return new SequenceService();
    }

    @Bean
    @Autowired
    public IAppointmentService getAppointmentService(ISequenceService sequenceService) {
        return new MongoAppointmentService(appointmentRepository, sequenceService);
    }

    @Bean
    public IJumpdayService getJumpdayService() {
        return new MongoJumpdayService(jumpdayRepository);
    }
}
