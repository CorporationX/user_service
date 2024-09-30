package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        if (shouldSkipInterceptor(template.url(), template.method())) {
            return;
        }

        template.header("x-user-id", String.valueOf(userContext.getUserId()));
    }

    private boolean shouldSkipInterceptor(String url, String method) {
        return url.contains("/svg") && "GET".equalsIgnoreCase(method);
    }
}
