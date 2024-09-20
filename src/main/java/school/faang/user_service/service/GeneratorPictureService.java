package school.faang.user_service.service;

import java.util.List;

public interface GeneratorPictureService {

    List<byte[]> getProfilePictures(String seed);
}
