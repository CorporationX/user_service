package school.faang.user_service.validator.httpResponse;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.httpResponse.HttpResponseData;

@Component
public class HttpResponseValidator {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    public void validateRequiredHeadersForS3PictureUploading(HttpResponseData httpResponseData) {
        if (!(httpResponseData.getHeaders().containsKey(CONTENT_TYPE) &&
                httpResponseData.getHeaders().containsKey(CONTENT_LENGTH))) {
            throw new ValidationException("httpResponse doesn't contains required headers");
        }
    }
}
