package school.faang.user_service.service.profile_picture;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private final Set<String> STYLES = Set.of("adventurer", "adventurer-neutral", "avataaars", "avataaars-neutral",
            "big-ears", "big-ears-neutral", "big-smile", "bottts", "bottts-neutral", "croodles", "croodles-neutral",
            "fun-emoji", "icons", "identicon", "initials", "lorelei", "lorelei-neutral", "micah", "miniavs", "notionists",
            "notionists-neutral", "open-peeps", "personas", "pixel-art", "pixel-art-neutral", "shapes", "thumbs");

    private final String DICE_BEAR_BASE_URL =
            String.format("https://api.dicebear.com/6.x/%s/svg?seed=", STYLES.iterator().next());

    public String generatePictureUrl(User user) throws IOException {
        String encodedUsername = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        String avatarUrl = DICE_BEAR_BASE_URL + encodedUsername;

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(avatarUrl);
        HttpResponse httpResponse = httpClient.execute(httpGet);

        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            return avatarUrl;
        } else {
            throw new IOException("Failed to generate the avatar URL for the user.");
        }
    }

    public byte[] downloadPicture(String pictureURL) throws IOException {
        URL url = new URL(pictureURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try (InputStream in = connection.getInputStream()) {
            return in.readAllBytes();
        } finally {
            connection.disconnect();
        }
    }
}
