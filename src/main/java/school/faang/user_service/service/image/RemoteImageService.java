package school.faang.user_service.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.httpResponse.HttpResponseData;
import school.faang.user_service.validator.httpResponse.HttpResponseValidator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:apis/remote-pictures.properties")
public class RemoteImageService {

    @Value("${api.picture.url}")
    private String apiUrl;

    @Value("${api.picture.style.name}")
    private String picStyle;

    @Value("${api.picture.format}")
    private String picFormat;

    private final HttpResponseValidator httpResponseValidator;

    public HttpResponseData getUserProfileImageFromRemoteService() throws IOException {
        URL url = new URL(apiUrl + picStyle + picFormat);
        log.info("Trying to get random profile picture from {}", url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        HttpResponseData responseData = new HttpResponseData();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            log.info("Successfully got response from {}", url);

            Map<String, List<String>> headers = connection.getHeaderFields();
            responseData.setHeaders(headers);

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] imageBytes = inputStream.readAllBytes();
                responseData.setContent(imageBytes);
            }
        } else {
            log.error("No response from {}", url);
            throw new IOException("Failed to fetch image");
        }

        httpResponseValidator.validateRequiredHeadersForS3PictureUploading(responseData);
        return responseData;
    }
}
