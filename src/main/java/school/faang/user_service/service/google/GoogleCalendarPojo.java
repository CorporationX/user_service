package school.faang.user_service.service.google;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GoogleCalendarPojo {
    @Value("${spring.google.calendar.application-name}")
    private String applicationName;
    @Value("${spring.google.calendar.credentials-file-path}")
    private String credentialsFile;
    @Value("${spring.google.calendar.calendar-id}")
    private String calendarId;
    @Value("${spring.google.calendar.redirect-uri}")
    private String redirectUri;
    @Value("${spring.google.calendar.scopes}")
    private List<String> scopes;
    @Value("${spring.google.calendar.access-type}")
    private String accessType;
    private JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
}
