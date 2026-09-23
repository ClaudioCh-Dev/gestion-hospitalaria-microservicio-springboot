package personal.appointment_ms.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import personal.shared.exception.BusinessException;
import personal.shared.exception.RemoteProblemDetail;

@Slf4j
@RequiredArgsConstructor
public class GlobalFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        log.warn(
                ">>> ERROR DECODER EJECUTADO method={} status={}",
                methodKey,
                response.status());

        try {
            if (response.body() != null) {

                RemoteProblemDetail problem = objectMapper.readValue(
                        response.body().asInputStream(),
                        RemoteProblemDetail.class);

                log.warn(
                        ">>> REMOTE ERROR code={} status={} detail={}",
                        problem.code(),
                        problem.status(),
                        problem.detail());

                if (problem.code() != null) {
                    return new BusinessException(
                            problem.code(),
                            problem.status(),
                            problem.detail());
                }
            }
        } catch (Exception ex) {
            log.error("Error leyendo ProblemDetail remoto", ex);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}