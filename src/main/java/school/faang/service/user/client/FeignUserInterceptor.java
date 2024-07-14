package school.faang.service.user.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.service.user.config.context.UserContext;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-user-id", String.valueOf(userContext.getUserId()));
    }
}
