package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private static final String INTERNAL_SERVICES_SUFFIX = "-service";

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        if (!template.feignTarget().name().contains(INTERNAL_SERVICES_SUFFIX)) {
            return;
        }
        template.header("x-user-id", String.valueOf(userContext.getUserId()));
    }
}
