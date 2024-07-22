package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;

import java.util.Optional;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        Optional<Long> userId = userContext.getUserId();
        userId.ifPresent(value -> template.header("x-user-id", String.valueOf(value)));
    }
}