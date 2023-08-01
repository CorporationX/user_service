package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 64, nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "country")
    private List<User> residents;

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", residentUserIds=" + (residents == null ? " " : residents.stream().map(User::getId).toList()) +
                '}';
    }
}
