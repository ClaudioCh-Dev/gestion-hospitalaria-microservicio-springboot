package personal.appointment_ms.streams;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AppointmentPublisher {

    private final StreamBridge streamBridge;

    /*
     * Topic name / Binding -> appointment-created
     */
    public void publishAppointmentCreated(Object appointmentData) {
        streamBridge.send("appointment-created-out-0", appointmentData);
    }

    public void publishAppointmentCompleted(Object appointmentData) {
        streamBridge.send("appointment-completed-out-0", appointmentData);
    }

    public void publishAppointmentCanceled(Object appointmentData) {
        streamBridge.send("appointment-canceled-out-0", appointmentData);
    }
}