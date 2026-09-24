package personal.billing_ms.docs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import personal.billing_ms.dto.BillingRecordResponse;
import personal.billing_ms.dto.BillingSummaryResponse;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingStatus;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de BillingRecordController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Facturación", description = "Registros de facturación de las citas y su pago")
public interface BillingRecordApiDocs {

    @Operation(summary = "Crear registro de facturación",
            description = "Genera la factura de una cita existente (se consulta appointment-ms). Requiere BILLING_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateBillingRequest.class),
                            examples = @ExampleObject(value = BillingExamples.CREATE_BILLING_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Registro de facturación creado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BillingRecordResponse.class),
                    examples = @ExampleObject(value = BillingExamples.BILLING_RECORD_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "404", description = "La cita no existe",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BillingExamples.APPOINTMENT_NOT_FOUND)))
    @ApiResponse(responseCode = "503", description = "appointment-ms no disponible",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BillingExamples.APPOINTMENT_SERVICE_UNAVAILABLE)))
    ResponseEntity<BillingRecordResponse> createBilling(CreateBillingRequest request);

    @Operation(summary = "Facturas de un paciente", description = "Devuelve las facturas del paciente paginadas. Requiere BILLING_READ_BY_PATIENT.")
    @Parameter(name = "patientId", in = ParameterIn.PATH, description = "ID del paciente", example = "1")
    @ApiResponse(responseCode = "200", description = "Página de facturas del paciente",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = BillingExamples.BILLING_RECORD_PAGE)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<Page<BillingRecordResponse>> getBillingByPatient(Long patientId, Pageable pageable);

    @Operation(summary = "Listar facturas", description = "Devuelve las facturas paginadas, con filtros opcionales. Requiere BILLING_READ.")
    @Parameter(name = "status", in = ParameterIn.QUERY, description = "Filtro opcional por estado", example = "PENDING")
    @Parameter(name = "search", in = ParameterIn.QUERY, description = "Número de factura, cita o paciente (billing-ms no guarda nombres)", example = "12")
    @Parameter(name = "patientIds", in = ParameterIn.QUERY, description = "IDs de pacientes; se usa para buscar por nombre resolviendo antes los IDs en patient-ms", example = "1,2,3")
    @ApiResponse(responseCode = "200", description = "Página de facturas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = BillingExamples.BILLING_RECORD_PAGE)))
    ResponseEntity<Page<BillingRecordResponse>> getBillings(
            BillingStatus status,
            String search,
            List<Long> patientIds,
            Pageable pageable);

    @Operation(summary = "Resumen de facturación", description = "Cantidad y monto por estado, y monto cobrado en el mes actual. Requiere BILLING_READ.")
    @ApiResponse(responseCode = "200", description = "Resumen de facturación",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BillingSummaryResponse.class)))
    ResponseEntity<BillingSummaryResponse> getSummary();

    @Operation(summary = "Pagar factura",
            description = "Marca la factura como PAID y publica el evento payment-update-status. Requiere BILLING_PAY.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del registro de facturación", example = "5")
    @ApiResponse(responseCode = "200", description = "Factura pagada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BillingRecordResponse.class),
                    examples = @ExampleObject(value = BillingExamples.BILLING_RECORD_PAID)))
    @ApiResponse(responseCode = "400", description = "La factura ya está pagada o cancelada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Ya pagada", value = BillingExamples.BILLING_RECORD_ALREADY_PAID),
                            @ExampleObject(name = "Cancelada", value = BillingExamples.BILLING_RECORD_ALREADY_CANCELLED)
                    }))
    @ApiResponse(responseCode = "404", description = "Registro de facturación no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = BillingExamples.BILLING_RECORD_NOT_FOUND)))
    ResponseEntity<BillingRecordResponse> payBilling(Long id);
}
