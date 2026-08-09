package personal.appointment_ms.streams;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import personal.shared.event.AppointmentEvent;

@Component
@AllArgsConstructor
public class AppointmentPublisher {

    private final StreamBridge streamBridge;

    public void publishAppointmentScheduled(AppointmentEvent appointmentEvent) {
        streamBridge.send("appointment-created-out-0", appointmentEvent);
    }
    
    public void publishAppointmentConfirmed(AppointmentEvent appointmentEvent) {
        streamBridge.send("appointment-confirmed-out-0", appointmentEvent);
    }

    public void publishAppointmentCompleted(AppointmentEvent appointmentEvent) {
        streamBridge.send("appointment-completed-out-0", appointmentEvent);
    }

    public void publishAppointmentCanceled(AppointmentEvent appointmentEvent) {
        streamBridge.send("appointment-canceled-out-0", appointmentEvent);
    }
}