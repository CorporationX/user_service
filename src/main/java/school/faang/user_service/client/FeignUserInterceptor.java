package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.externalApis.ExternalApisProperties;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;
    private final ExternalApisProperties externalApisProperties;

    @Override
    public void apply(RequestTemplate template) {
        if (!shouldSkipInterceptor(template.feignTarget().url())) {
            template.header("x-user-id", String.valueOf(userContext.getUserId()));
        }
    }

    private boolean shouldSkipInterceptor(String url) {
        return externalApisProperties.getImagegenerator().get("endpoint").contains(url);
    }
}
