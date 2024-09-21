package school.faang.user_service.dto.httpResponse;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponseData {
    private Map<String, List<String>> headers;
    private byte[] content;
}
