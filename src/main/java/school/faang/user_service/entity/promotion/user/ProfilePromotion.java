package school.faang.user_service.entity.promotion.user;

import jakarta.persistence.*;
import lombok.*;
import school.faang.user_service.entity.promotion.PromotionBase;
import school.faang.user_service.entity.User;

@Entity
@Table(name = "profile_promotion")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@NamedEntityGraph(
        name = "ProfilePromotion.withUserAndProfile",
        attributeNodes = {
                @NamedAttributeNode("client"),
                @NamedAttributeNode("profile")
        }
)
@DiscriminatorValue("PROFILE_PROMOTION")
@PrimaryKeyJoinColumn(name = "id")
public class ProfilePromotion extends PromotionBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private User profile;
}
