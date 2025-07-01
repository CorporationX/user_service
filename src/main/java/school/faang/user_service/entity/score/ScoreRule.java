package school.faang.user_service.entity.score;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.Role;

@Entity
@Getter
@Setter
@Table(name = "score_rule")
public class ScoreRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "score")
    private int score = 0;
}
