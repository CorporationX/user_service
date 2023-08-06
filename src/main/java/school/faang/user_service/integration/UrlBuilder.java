package school.faang.user_service.integration;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class UrlBuilder {

    public URI buildUrl(String host, String port, String path) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(port)
                .path(path)
                .build();
        return uriComponents.toUri();
    }
}