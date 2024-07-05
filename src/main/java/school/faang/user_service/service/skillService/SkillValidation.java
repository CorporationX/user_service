package school.faang.user_service.service.skillService;

public interface SkillValidation {

    boolean titleIsValid(String title);

    boolean existByTitle(String title);
}
