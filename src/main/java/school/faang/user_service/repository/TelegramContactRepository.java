package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.entity.TelegramContact;

import java.util.Optional;

@Repository
public interface TelegramContactRepository extends JpaRepository<TelegramContact, Long> {

    @Query("SELECT t FROM TelegramContact t WHERE t.telegramUserName = :telegramUserName")
    Optional<TelegramContact> findByTelegramUserName(String telegramUserName);
}
