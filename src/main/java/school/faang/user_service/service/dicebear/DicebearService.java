package school.faang.user_service.service.dicebear;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DicebearService {

    private final DicebearClient dicebearClient;

    @Value("${services.dicebear.style}")
    private String style;

    public byte[] getImage(String format, int size) {
        String seed = String.valueOf(System.currentTimeMillis());
        return dicebearClient.getImage(style, format, seed, size);
    }
}
