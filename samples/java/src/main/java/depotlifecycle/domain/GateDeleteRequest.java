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
public class GateDeleteRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @JsonIgnore
    @Column(nullable = false, length = 16)
    String adviceNumber;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @JsonIgnore
    @Column(nullable = false, length = 11)
    String unitNumber;
}
