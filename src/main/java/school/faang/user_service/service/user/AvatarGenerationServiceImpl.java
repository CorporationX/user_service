package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@PropertySource("file:src/main/resources/properties/avatar_generation.properties")
public class AvatarGenerationServiceImpl implements AvatarGenerationService {

    @Value("${base_url}")
    private String baseUrl;

    @Value("${parameter_url_for_img_size}")
    private String parametrUrlForImgSize;

    @Value("${face_features}")
    private List<String> features;

    @Value("#{'${face_features_vatiants}'.split(';')}")
    private List<List<String>> featuresVariants;

    @Override
    public String generateAvatarUrl() {
        Random random = new Random();
        StringBuilder generatedAvatarUrl = new StringBuilder(baseUrl);
        for (int i = 0; i < features.size(); i++) {
            String feature = features.get(i);
            List<String> featureVariants = featuresVariants.get(i);
            int randomElementIndex = random.nextInt(featureVariants.size());
            generatedAvatarUrl.append(feature).append(featureVariants.get(randomElementIndex));
        }
        return generatedAvatarUrl.toString();
    }

    @Override
    public String setSizeToGeneratedAvatar(String generatedAvatarUrl, int requiredSize) {
        return generatedAvatarUrl + parametrUrlForImgSize + requiredSize;
    }
}
