package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String serviceName;
    private String globalMessage;
    private Map<String, String> fieldErrors;
    private int status;
}
