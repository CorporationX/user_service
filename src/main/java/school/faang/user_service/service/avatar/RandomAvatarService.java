package school.faang.user_service.service.avatar;

import school.faang.user_service.entity.user.UserProfilePic;

/**
 * Сервис для работы с аватарками пользователей.
 * <p>
 * Позволяет генерировать случайные аватарки через DiceBear API и получать ссылки на разные размеры,
 * а также удалять аватарки из S3.
 */
public interface RandomAvatarService {

    /**
     * Генерирует случайную аватарку для пользователя и загружает её в Amazon S3.
     * <p>
     * Метод создаёт две версии изображения: {@code small} (64x64) и {@code medium} (128x128),
     * и возвращает ссылки на них. Если генерация или загрузка не удалась после нескольких попыток,
     * используется fallback-аватарка из статических ресурсов проекта.
     *
     * @param username имя пользователя, используется для формирования имени файла в S3 и логирования.
     * @return карта с ключами "small" и "medium" и значениями — URL соответствующих аватарок.
     * @throws IllegalStateException если не удалось обработать fallback-аватарку.
     */
    UserProfilePic generateRandomAvatarForUser(String username);
}