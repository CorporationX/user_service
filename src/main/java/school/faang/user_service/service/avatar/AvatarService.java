package school.faang.user_service.service.avatar;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

@Service
public class AvatarService {
    private final String DICEBEAR_API_URL = "https://api.dicebear.com/9.x/avataaars/svg";
    private static final List<String> NAMES = List.of(
            "Felix", "Aneka", "Fluffy", "Whiskers", "Sadie", "Annie", "Cuddles", "Willow",
            "Lola", "Trouble", "Boots", "Ginger", "Sassy", "Bubba", "Zoe", "Zoey", "Mia",
            "Sammy", "Snuggles", "Harley", "Simon", "Toby"
    );
    private static final List<String> BACKGROUNDS = List.of(
            "b6e3f4", "c0aede", "d1d4f9", "ffd5dc", "ffdfbf"
    );
    private static final Random RANDOM = new Random();

    public String getRandomAvatarUrl() {
        String encodedSeed = URLEncoder.encode(getRandomName(), StandardCharsets.UTF_8);
        String encodedBackground = URLEncoder.encode(getRandomBackground(), StandardCharsets.UTF_8);
        return DICEBEAR_API_URL + "?seed=" + encodedSeed + "&backgroundColor=" + encodedBackground;
    }

    private String getRandomName() {
        int index = RANDOM.nextInt(NAMES.size());
        return NAMES.get(index);
    }

    private String getRandomBackground() {
        int index = RANDOM.nextInt(BACKGROUNDS.size());
        return BACKGROUNDS.get(index);
    }

}
