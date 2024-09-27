package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.api.ApiProperties;
import school.faang.user_service.config.context.UserContext;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;
    private final List<ApiProperties> apiProperties;

    @Override
    public void apply(RequestTemplate template) {
        Optional<ApiProperties> optionalApiProperty = getApiProperty(template.feignTarget().url());

        if (optionalApiProperty.isPresent() &&
                optionalApiProperty.get().getHeaders() != null &&
                !optionalApiProperty.get().getHeaders().isEmpty()) {
            addHeaderToTemplate(template, optionalApiProperty.get().getHeaders());
        }
    }

    private void addHeaderToTemplate(RequestTemplate template, List<String> headers) {
        headers.forEach(header -> template.header(header, String.valueOf(userContext.getUserId())));
    }

    private Optional<ApiProperties> getApiProperty(String url) {
        return apiProperties.stream()
                .filter(api -> api.getEndpoint().contains(url))
                .findFirst();
    }
}
