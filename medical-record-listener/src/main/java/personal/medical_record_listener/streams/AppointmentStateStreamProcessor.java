package personal.medical_record_listener.streams;

import java.util.function.BiFunction;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import personal.medical_record_listener.streams.model.AppointmentState;
import personal.shared.event.AppointmentCreatedEvent;
import personal.shared.event.AppointmentUpdateStatusEvent;

@Configuration
public class AppointmentStateStreamProcessor {

    @Bean
    public BiFunction<
            KStream<Long, AppointmentCreatedEvent>,
            KStream<Long, AppointmentUpdateStatusEvent>,
            KStream<Long, AppointmentState>> appointmentStateProcessor() {

        return (createdStream, updateStream) -> {

            /*
             * ============================================================
             * APPOINTMENT CREATED
             * ============================================================
             *
             * Creamos el estado completo de la cita.
             */

            KStream<Long, AppointmentState> createdStates =
                    createdStream
                            .filter((key, event) -> event != null)
                            .selectKey(
                                    (key, event) ->
                                            event.appointmentId()
                            )
                            .mapValues(event ->
                                    new AppointmentState(
                                            event.appointmentId(),
                                            event.appointmentType(),
                                            event.patientId(),
                                            event.patientName(),
                                            event.doctorId(),
                                            event.doctorName(),
                                            event.specialty(),
                                            event.scheduledAt(),
                                            event.reason(),
                                            event.status(),
                                            event.amount(),
                                            event.currency()
                                    )
                            );

            /*
             * ============================================================
             * APPOINTMENT UPDATE
             * ============================================================
             *
             * El UPDATE solamente contiene:
             *
             * appointmentId
             * status
             *
             * Necesitamos obtener el estado actual para conservar
             * todos los demás datos.
             */

            KStream<Long, AppointmentUpdateStatusEvent> updates =
                    updateStream
                            .filter((key, event) -> event != null)
                            .selectKey(
                                    (key, event) ->
                                            event.appointmentId()
                            );

            /*
             * ============================================================
             * STATE
             * ============================================================
             *
             * Primero convertimos CREATE en estado.
             *
             * Los UPDATE necesitan modificar el estado existente.
             *
             * Para esto usamos una tabla materializada.
             */

            return createdStates
                    .merge(
                            updates
                                    .join(
                                            createdStates.toTable(),
                                            (update, currentState) ->
                                                    currentState.withStatus(
                                                            update.status()
                                                    )
                                    )
                    )
                    .selectKey(
                            (key, state) ->
                                    state.appointmentId()
                    );
        };
    }
}