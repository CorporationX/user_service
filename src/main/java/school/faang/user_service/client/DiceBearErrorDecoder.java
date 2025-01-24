package school.faang.user_service.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DiceBearErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        switch (status) {
            case NOT_FOUND:
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found on DiceBear");
            case INTERNAL_SERVER_ERROR:
                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error on DiceBear");
            default:
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error calling DiceBear API");
        }
    }
}