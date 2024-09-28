package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import school.faang.user_service.config.context.UserContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;
    private final Map<String, List<String>> endpointRequiredHeaders;
    private final Map<String, Supplier<String>> headerMapping = new HashMap<>();

    public FeignUserInterceptor(UserContext userContext, Map<String, List<String>> endpointRequiredHeaders) {
        this.userContext = userContext;
        this.endpointRequiredHeaders = endpointRequiredHeaders;
        initializeHeaderMapping();
    }

    @Override
    public void apply(RequestTemplate template) {
        if (endpointRequiredHeaders.containsKey(template.feignTarget().url()) &&
                endpointRequiredHeaders.get(template.feignTarget().url()) != null &&
                !endpointRequiredHeaders.get(template.feignTarget().url()).isEmpty()) {
            addHeaderToTemplate(template, endpointRequiredHeaders.get(template.feignTarget().url()));
        }
    }

    private void initializeHeaderMapping() {
        headerMapping.put("x-user-id", () -> String.valueOf(userContext.getUserId()));
    }

    private void addHeaderToTemplate(RequestTemplate template, List<String> headers) {
        headers.forEach(header -> {
            Supplier<String> supplier = headerMapping.get(header);
            if (supplier != null) {
                template.header(header, supplier.get());
            }
        });
    }
}
