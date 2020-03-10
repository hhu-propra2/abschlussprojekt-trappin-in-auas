package mops.domain.database.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BestandeneModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private Modul modul;
    private double note;

}
