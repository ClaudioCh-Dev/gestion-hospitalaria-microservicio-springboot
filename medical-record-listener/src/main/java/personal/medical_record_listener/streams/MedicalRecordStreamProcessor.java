package personal.medical_record_listener.streams;
import java.util.function.BiFunction;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import personal.medical_record_listener.streams.model.AppointmentState;
import personal.shared.event.MedicalRecordReadyEvent;
import personal.shared.event.PaymentUpdateStatus;
import personal.shared.event.status.StatusAppointment;
import personal.shared.event.status.StatusPayment;

@Configuration
public class MedicalRecordStreamProcessor {

    @Bean
    public BiFunction<
            KStream<Long, AppointmentState>,
            KStream<Long, PaymentUpdateStatus>,
            KStream<Long, MedicalRecordReadyEvent>> medicalRecordProcessor() {

        return (appointmentStream, paymentStream) -> {

            /*
             * ============================================================
             * APPOINTMENT COMPLETED
             * ============================================================
             *
             * Solo nos interesan citas COMPLETED.
             */

            KTable<Long, AppointmentState> appointmentTable =
                    appointmentStream
                            .filter(
                                    (key, event) ->
                                            event != null
                                                    && event.status()
                                                    == StatusAppointment.COMPLETED
                            )
                            .selectKey(
                                    (key, event) ->
                                            event.appointmentId()
                            )
                            .toTable();

            /*
             * ============================================================
             * PAYMENT PAID
             * ============================================================
             */

            KTable<Long, PaymentUpdateStatus> paymentTable =
                    paymentStream
                            .filter(
                                    (key, event) ->
                                            event != null
                                                    && event.status()
                                                    == StatusPayment.PAID
                            )
                            .selectKey(
                                    (key, event) ->
                                            event.appointmentId()
                            )
                            .toTable();

            /*
             * ============================================================
             * MATCH
             * ============================================================
             *
             * appointment = COMPLETED
             * payment     = PAID
             *
             * mismo appointmentId.
             */

            return appointmentTable.join(
                    paymentTable,

                    (appointment, payment) ->
                            new MedicalRecordReadyEvent(

                                    appointment.appointmentId(),

                                    appointment.appointmentType(),

                                    appointment.patientId(),
                                    appointment.patientName(),

                                    appointment.doctorId(),
                                    appointment.doctorName(),

                                    appointment.specialty(),

                                    appointment.scheduledAt(),

                                    appointment.reason(),

                                    appointment.status().name(),

                                    payment.amount()
                            )

            ).toStream();
        };
    }
}