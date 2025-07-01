package school.faang.user_service.entity.score;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.user.User;

@Entity
@Getter
@Setter
@Table(name = "user_score")
public class UserScore {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "score", nullable = false)
    private int score = 0;
}
