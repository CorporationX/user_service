package school.faang.user_service.service;

public interface GeneratorPictureService {

    byte[] generatePicture(String seed);

    byte[] generateSmallPicture(String seed);

    String getSeed();
}
