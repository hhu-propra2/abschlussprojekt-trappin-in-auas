package mops.domain.models;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Karriere {

  private String arbeitserfahrung;

  @Valid
  private ImmartikulationsStatus immartikulationsStatus;

  @Valid
  private StudiengangAbschluss fachAbschluss;

}
