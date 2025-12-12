package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

/**
 * Сервис для управления навыками пользователей.
 * Предоставляет операции создания, получения и принятия (acquire) навыков.
 */
public interface SkillService {

    /**
     * Создает новый навык на основе переданных данных.
     *
     * @param skillDto объект {@link CreateSkillDto} с данными для создания навыка
     * @return созданный объект {@link SkillDto}
     */
    SkillDto create(CreateSkillDto skillDto);

    /**
     * Возвращает список навыков, принадлежащих указанному пользователю.
     *
     * @param userId идентификатор пользователя
     * @return список объектов {@link SkillDto} пользователя
     */
    List<SkillDto> getByUserId(Long userId);

    /**
     * Возвращает список навыков, рекомендованных или предложенных указанному пользователю.
     * Например, это могут быть навыки, которые система предлагает на основе профиля.
     *
     * @param userId идентификатор пользователя
     * @return список объектов {@link SkillCandidateDto} — кандидатов навыков
     */
    List<SkillCandidateDto> getOfferedSkills(Long userId);

    /**
     * Пользователь принимает предложенный навык (acquire).
     * Метод переводит навык из состояния предложения в состояние у пользователя.
     *
     * @param skillId идентификатор принимаемого навыка
     * @param userId  идентификатор пользователя, который принимает навык
     */
    void acquireSkillFromOffers(Long skillId, Long userId);
}