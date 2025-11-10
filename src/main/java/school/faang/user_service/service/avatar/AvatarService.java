package school.faang.user_service.service.avatar;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;

/**
 * Сервис для управления аватарами пользователей.
 */
public interface AvatarService {

    /**
     * Загружает аватар для указанного пользователя.
     * <p>
     * Метод выполняет валидацию файла (размер, тип), сжимает изображение до двух размеров (1080px и 170px),
     * загружает обе версии в S3 хранилище и сохраняет полученные ключи в профиль пользователя.
     *
     * @param userId ID пользователя, для которого загружается аватар.
     * @param file   Объект MultipartFile, представляющий загружаемый файл изображения.
     * @return Объект {@link UserProfilePic}, содержащий ключи (fileId) для большой и маленькой версий аватара в S3.
     * @throws DataValidationException если файл не прошел валидацию (слишком большой, не является изображением)
     * @throws RuntimeException        если произошла ошибка при загрузке файла в S3 или при его обработке.
     */
    UserProfilePic uploadAvatar(long userId, MultipartFile file);

    /**
     * Скачивает аватар пользователя в виде массива байт.
     * <p>
     * Метод находит ключ файла аватара в профиле пользователя и загружает соответствующий файл из S3 хранилища.
     * Скачивается только основная (большая) версия аватара.
     *
     * @param userId ID пользователя, чей аватар необходимо скачать.
     * @return Массив байт {@code byte[]}, представляющий изображение аватара в формате PNG.
     * @throws school.faang.user_service.exception.EntityNotFoundException если аватар для пользователя не найден.
     * @throws DataValidationException если у пользователя установлен аватар по умолчанию, который нельзя скачать.
     * @throws RuntimeException если произошла ошибка при скачивании файла из S3.
     */
    byte[] downloadAvatar(long userId);

    /**
     * Удаляет текущий аватар пользователя и устанавливает аватар по умолчанию.
     * <p>
     * Метод удаляет оба файла (большой и маленький) из S3 хранилища, если они были загружены пользователем.
     * Затем генерирует URL для аватара по умолчанию (с помощью сервиса Dicebear) и сохраняет этот URL
     * в качестве нового аватара пользователя.
     *
     * @param userId ID пользователя, чей аватар необходимо удалить.
     * @return {@code String}, содержащий URL нового аватара по умолчанию.
     */
    String deleteAvatar(long userId);
}