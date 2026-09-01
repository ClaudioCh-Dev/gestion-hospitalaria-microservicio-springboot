package personal.billing_ms.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

import lombok.RequiredArgsConstructor;
import personal.shared.exception.BusinessException;
import personal.shared.exception.RemoteProblemDetail;


@RequiredArgsConstructor
public class GlobalFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        try {

            if (response.body() != null) {

                RemoteProblemDetail problem =
                        objectMapper.readValue(
                                response.body().asInputStream(),
                                RemoteProblemDetail.class
                        );

                if (problem.code() != null) {

                    return new BusinessException(
                            problem.code(),
                            problem.status(),
                            problem.detail()
                    );
                }
            }

        } catch (Exception ex) {

            // Si no se puede leer el ProblemDetail,
            // usamos el comportamiento por defecto de Feign.
        }

        return defaultErrorDecoder.decode(
                methodKey,
                response
        );
    }
}