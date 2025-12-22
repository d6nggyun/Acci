package refresh.acci.domain.analysis.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "analysis")
public class Analysis {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column
    private Long userId;

    @Column(nullable = false)
    private Long accidentRate;

    @Enumerated(EnumType.STRING)
    @Column
    private AccidentType accidentType;

    @Column
    private String accidentImage;

    private Analysis(Long userId, Long accidentRate, AccidentType accidentType, String accidentImage) {
        this.userId = userId;
        this.accidentRate = accidentRate;
        this.accidentType = accidentType;
        this.accidentImage = accidentImage;
    }

    public static Analysis of(Long userId, Long accidentRate, AccidentType accidentType, String accidentImage) {
        return new Analysis(userId, accidentRate, accidentType, accidentImage);
    }
}
