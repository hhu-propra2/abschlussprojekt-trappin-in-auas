package mops.bewerbung1.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import mops.domain.database.dto.*;
import mops.domain.models.*;
import mops.services.DTOService;
import mops.services.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ModelServiceTest {

  private transient DTOService mappingService;
  private transient ModelService mappingModelService;

  @BeforeEach
  void setUp() {
    mappingService = new DTOService();
    mappingModelService = new ModelService();
  }

  @Test
  public void personalienDTOZuPersonalienModel() {
    AdresseDTO adresseDTO = new AdresseDTO();
    adresseDTO.setHausnummer("11a");
    adresseDTO.setPLZ("40233");
    adresseDTO.setStraße("Simrockstr");
    adresseDTO.setWohnort("Düsseldorf");

    PersonalienDTO personalienDTO = new PersonalienDTO();
    personalienDTO.setAdresse(adresseDTO);
    personalienDTO.setAlter(20);
    try {
      personalienDTO.setGeburtsdatum(
          Date.from(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse("15.07.1999").toInstant()));
    } catch (Exception e) {
      personalienDTO.setGeburtsdatum(null);
    }
    personalienDTO.setGeburtsort("Swetlana");
    personalienDTO.setName("Wick");
    personalienDTO.setNationalitaet("Terminator");
    personalienDTO.setVorname("John");
    personalienDTO.setUnikennung("johwi200");

    Personalien personalien = mappingModelService.load(personalienDTO);

    Adresse adresse = personalien.getAdresse();

    assertNotNull(personalien);
    assertNotNull(adresse);

    assertEquals(adresseDTO.getHausnummer(), adresse.getHausnummer());
    assertEquals(adresseDTO.getPLZ(), adresse.getPLZ());
    assertEquals(adresseDTO.getStraße(), adresse.getStraße());
    assertEquals(adresseDTO.getWohnort(), adresse.getWohnort());

    assertEquals(personalienDTO.getAlter(), personalien.getAlter());
    assertEquals(personalienDTO.getGeburtsdatum(), personalien.getGeburtsdatum());
    assertEquals(personalienDTO.getGeburtsort(), personalien.getGeburtsort());
    assertEquals(personalienDTO.getName(), personalien.getName());
    assertEquals(personalienDTO.getNationalitaet(), personalien.getNationalitaet());
    assertEquals(personalienDTO.getUnikennung(), personalien.getUnikennung());
    assertEquals(personalienDTO.getVorname(), personalien.getVorname());
  }

  @Test
  public void personalienDTOIsNullMappingReturnsNull() {
    PersonalienDTO personalienDTO = null;
    Personalien personalien = mappingModelService.load(personalienDTO);

    assertNull(personalien);
  }

}