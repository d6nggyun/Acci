package refresh.acci.domain.repair.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "repair_estimate")
public class RepairEstimate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "vehicle_manufacturer", nullable = false)
    private String vehicleManufacturer;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "panel_beating_cost", nullable = false)
    private Long panelBeatingCost;

    @Column(name = "part_replacement_cost", nullable = false)
    private Long partReplacementCost;

    @Column(name = "total_repair_cost", nullable = false)
    private Long totalRepairCost;


}
