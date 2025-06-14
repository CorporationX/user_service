package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "education")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_from", nullable = false)
    private Integer yearFrom;

    @Column(name = "year_to")
    private Integer yearTo;

    @Column(name = "institution", nullable = false)
    private String institution; // название учебного заведения (школа, университет, колледж)

    @Column(name = "education_level")
    private String educationLevel; // полученное образование (среднее, высшее, неоконченное высшее и т.п.)

    @Column(name = "specialization")
    private String specialization; // направление обучения, специализация

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
