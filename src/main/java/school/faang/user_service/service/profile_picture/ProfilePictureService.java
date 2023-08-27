package school.faang.user_service.service.profile_picture;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.s3.S3Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    private final S3Client s3Client;

    private final int WIDTH = 180;
    private final int HEIGHT = 180;

    private final String ORIGINAL_EXTENSION = ".svg";
    private final String SMALL_EXTENSION = "_small.jpg";

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

    public byte[] resizePicture(byte[] pictureData) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(pictureData));
        BufferedImage resizedImage = new BufferedImage(WIDTH, HEIGHT, originalImage.getType());

        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, WIDTH, HEIGHT, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }

    public void setProfilePicture(User user) throws IOException {
        String pictureURL = generatePictureUrl(user);

        byte[] picture = downloadPicture(pictureURL);
        byte[] resizedPicture = resizePicture(picture);

        s3Client.upload(user, picture, ORIGINAL_EXTENSION);
        s3Client.upload(user, resizedPicture, SMALL_EXTENSION);

        UserProfilePic profilePic = new UserProfilePic();

        profilePic.setFileId(s3Client.getURLById(user.getId(), ORIGINAL_EXTENSION));
        profilePic.setSmallFileId(s3Client.getURLById(user.getId(), SMALL_EXTENSION));

        user.setUserProfilePic(profilePic);
    }
}
