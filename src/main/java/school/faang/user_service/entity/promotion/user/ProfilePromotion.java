package school.faang.user_service.entity.promotion.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.PromotionBase;

@Entity
@Table(name = "profile_promotion")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@DiscriminatorValue("PROFILE_PROMOTION")
@PrimaryKeyJoinColumn(name = "id")
public class ProfilePromotion extends PromotionBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private User profile;
}
