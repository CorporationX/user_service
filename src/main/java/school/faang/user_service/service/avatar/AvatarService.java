package school.faang.user_service.service.avatar;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public MultipartFile downloadSvgAsMultipartFile(String svgUrl) throws IOException, TranscoderException {
        byte[] svgBytes = downloadSvg(svgUrl);
        byte[] pngBytes = convertSvgToPng(svgBytes);
        return createMultipartFile(pngBytes);
    }


    private String getRandomName() {
        int index = RANDOM.nextInt(NAMES.size());
        return NAMES.get(index);
    }

    private String getRandomBackground() {
        int index = RANDOM.nextInt(BACKGROUNDS.size());
        return BACKGROUNDS.get(index);
    }

    private byte[] downloadSvg(String svgUrl) throws IOException {
        URL url = new URL(svgUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] svgBytes = baos.toByteArray();
            if (svgBytes.length == 0) {
                throw new IOException("No data received from URL: " + svgUrl);
            }

            return svgBytes;
        } finally {
            connection.disconnect();
        }
    }

    private byte[] convertSvgToPng(byte[] svgBytes) throws TranscoderException, IOException {
        try (ByteArrayInputStream svgInputStream = new ByteArrayInputStream(svgBytes);
             ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {

            PNGTranscoder transcoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(svgInputStream);
            TranscoderOutput output = new TranscoderOutput(pngOutputStream);

            transcoder.transcode(input, output);

            return pngOutputStream.toByteArray();
        }
    }

    private MultipartFile createMultipartFile(byte[] pngBytes) {
        return new MockMultipartFile("avatar", "avatar.png", "image/png", pngBytes);
    }
}
