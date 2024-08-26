package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.client.annotation.SkipUserIdInterceptor;
import school.faang.user_service.config.context.UserContext;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        if (template.feignTarget().type().isAnnotationPresent(SkipUserIdInterceptor.class)) {
            return;
        }
        template.header("x-user-id", String.valueOf(userContext.getUserId()));
    }
}
