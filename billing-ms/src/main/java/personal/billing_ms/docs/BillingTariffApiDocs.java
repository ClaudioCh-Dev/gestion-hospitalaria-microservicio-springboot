package personal.billing_ms.docs;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import personal.billing_ms.dto.BillingTariffResponse;
import personal.billing_ms.dto.CreateBillingTariffRequest;
import personal.billing_ms.dto.UpdateBillingTariffRequest;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de BillingTariffController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Tarifas", description = "Precio de cada tipo de cita")
public interface BillingTariffApiDocs {

    @Operation(summary = "Crear tarifa", description = "Registra el precio de un tipo de cita. Requiere BILLING_TARIFF_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateBillingTariffRequest.class),
                            examples = @ExampleObject(value = BillingExamples.CREATE_TARIFF_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Tarifa creada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BillingTariffResponse.class),
                    examples = @ExampleObject(value = BillingExamples.TARIFF_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Ya existe una tarifa para ese tipo de cita, o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Tarifa duplicada", value = BillingExamples.BILLING_TARIFF_ALREADY_EXISTS),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    ResponseEntity<BillingTariffResponse> createTariff(CreateBillingTariffRequest request);

    @Operation(summary = "Actualizar tarifa", description = "Cambia el precio de un tipo de cita. Requiere BILLING_TARIFF_UPDATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateBillingTariffRequest.class),
                            examples = @ExampleObject(value = BillingExamples.UPDATE_TARIFF_REQUEST))))
    @Parameter(name = "appointmentTypeId", in = ParameterIn.PATH, description = "ID del tipo de cita", example = "2")
    @ApiResponse(responseCode = "200", description = "Tarifa actualizada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BillingTariffResponse.class),
                    examples = @ExampleObject(value = BillingExamples.TARIFF_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_REQUEST_BODY)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BillingExamples.BILLING_TARIFF_NOT_FOUND)))
    ResponseEntity<BillingTariffResponse> updateTariff(Long appointmentTypeId, UpdateBillingTariffRequest request);

    @Operation(summary = "Listar tarifas", description = "Requiere BILLING_TARIFF_READ.")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BillingTariffResponse.class)),
                    examples = @ExampleObject(value = BillingExamples.TARIFF_LIST)))
    ResponseEntity<List<BillingTariffResponse>> getTariffs();

    @Operation(summary = "Obtener tarifa de un tipo de cita", description = "Requiere BILLING_TARIFF_READ.")
    @Parameter(name = "appointmentTypeId", in = ParameterIn.PATH, description = "ID del tipo de cita", example = "2")
    @ApiResponse(responseCode = "200", description = "Tarifa encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BillingTariffResponse.class),
                    examples = @ExampleObject(value = BillingExamples.TARIFF_RESPONSE)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BillingExamples.BILLING_TARIFF_NOT_FOUND)))
    ResponseEntity<BillingTariffResponse> getTariff(Long appointmentTypeId);

    @Operation(summary = "Precio de un tipo de cita", description = "Devuelve solo el precio. Lo usa appointment-ms al programar citas. Requiere BILLING_TARIFF_READ.")
    @Parameter(name = "appointmentTypeId", in = ParameterIn.PATH, description = "ID del tipo de cita", example = "2")
    @ApiResponse(responseCode = "200", description = "Precio de la tarifa",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BigDecimal.class),
                    examples = @ExampleObject(value = BillingExamples.PRICE_RESPONSE)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BillingExamples.BILLING_TARIFF_NOT_FOUND)))
    ResponseEntity<BigDecimal> getPriceByAppointmentTypeId(Long appointmentTypeId);
}
