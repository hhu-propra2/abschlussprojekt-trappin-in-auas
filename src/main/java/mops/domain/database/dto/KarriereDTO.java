package mops.domain.database.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table(name = "karriere")
@Entity
public class KarriereDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String arbeitserfahrung;
    @Embedded
    private ImmartikulationsStatus immartikulationsStatus;
    @Embedded
    private StudiengangAbschluss fachAbschluss;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "karriere")
    private List<BestandeneModuleDTO> bestandendeModule;

}