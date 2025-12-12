package school.faang.user_service.entity.recommendation;

import jakarta.persistence.*;
import lombok.*;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "skill_offer")
public class SkillOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    public Skill skill;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "recommendation_id", nullable = false)
    private Recommendation recommendation;

    @ManyToOne
    @JoinColumn(name = "offered_by_user_id")
    private User offeredBy;
}