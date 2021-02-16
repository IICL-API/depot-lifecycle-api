package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateCancelRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Column(nullable = false, length = 16)
    String estimateNumber;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "depot_id", nullable = false)
    Party depot;
}
