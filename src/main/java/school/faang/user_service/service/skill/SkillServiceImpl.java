package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService{
    private final SkillRepository skillRepository;

}
