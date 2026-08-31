package personal.medical_record_listener.streams;

import java.util.function.BiFunction;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import personal.shared.event.AppointmentUpdateStatusEvent;
import personal.shared.event.EnumStatusAppointment;
import personal.shared.event.MedicalRecordReadyEvent;
import personal.shared.event.PaymentStatus;
import personal.shared.event.PaymentUpdateStatus;

@Configuration
public class MedicalRecordStreamProcessor {

    @Bean
    public BiFunction<
            KStream<Long, AppointmentUpdateStatusEvent>,
            KStream<Long, PaymentUpdateStatus>,
            KStream<Long, MedicalRecordReadyEvent>> medicalRecordProcessor() {

        return (appointmentStream, paymentStream) -> {

            /*
             * ============================================================
             * APPOINTMENT COMPLETED
             * ============================================================
             *
             * Solo dejamos pasar citas que estén COMPLETED.
             */
            KStream<Long, AppointmentUpdateStatusEvent> appointments =
                    appointmentStream
                            .filter((key, event) ->
                                    event != null
                                            && event.status()
                                                    == EnumStatusAppointment.COMPLETED
                            );

            /*
             * ============================================================
             * PAYMENT PAID
             * ============================================================
             *
             * Solo dejamos pasar pagos que estén PAID.
             */
            KStream<Long, PaymentUpdateStatus> payments =
                    paymentStream
                            .filter((key, event) ->
                                    event != null
                                            && event.status()
                                                    == PaymentStatus.PAID
                            );

            /*
             * ============================================================
             * APPOINTMENT TABLE
             * ============================================================
             *
             * La clave será appointmentId.
             */
            KTable<Long, AppointmentUpdateStatusEvent> appointmentTable =
                    appointments
                            .selectKey(
                                    (key, event) ->
                                            event.appointmentId()
                            )
                            .toTable();

            /*
             * ============================================================
             * PAYMENT TABLE
             * ============================================================
             *
             * La clave también será appointmentId.
             */
            KTable<Long, PaymentUpdateStatus> paymentTable =
                    payments
                            .selectKey(
                                    (key, event) ->
                                            event.appointmentId()
                            )
                            .toTable();

            /*
             * ============================================================
             * JOIN
             * ============================================================
             *
             * Se genera el MedicalRecordReadyEvent únicamente
             * cuando existe:
             *
             * appointment = COMPLETED
             * payment     = PAID
             *
             * con el mismo appointmentId.
             */
            return appointmentTable
                    .join(
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
                                            "COMPLETED",
                                            payment.amount()
                                    )
                    )
                    .toStream();
        };
    }
}