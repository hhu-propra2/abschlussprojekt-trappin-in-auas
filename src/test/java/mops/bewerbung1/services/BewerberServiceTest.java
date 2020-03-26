package mops.bewerbung1.services;

import mops.bewerbung1.testutils.ModelGenerator;
import mops.domain.database.dto.*;
import mops.domain.models.*;
import mops.domain.models.Beruf;
import mops.domain.repositories.BewerberRepository;
import mops.domain.repositories.ModulRepository;
import mops.services.BewerberService;
import mops.services.DTOService;
import mops.services.ModelService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BewerberServiceTest {

  @Mock
  private transient ModulRepository modulRepository;

  @Mock
  private transient BewerberRepository bewerberRepository;

  private transient BewerberService bewerberService;
  private transient DTOService dtoService;
  private transient ModelService modelService;
  private transient ModelGenerator modelGenerator;

  @BeforeEach
  void setUp() {
    ModulDTO propra1DTO = new ModulDTO("Propra I", "Jens Bendisposto", "jens@hhu.de");
    ModulDTO propra2DTO = new ModulDTO("Propra I", "Jens Bendisposto", "jens@hhu.de");
    ModulDTO propra3DTO = new ModulDTO("Propra I", "Jens Bendisposto", "jens@hhu.de");
    List<ModulDTO> offeneModule = Arrays.asList(propra1DTO, propra2DTO, propra3DTO);

    this.dtoService = new DTOService(modulRepository);
    this.bewerberService = new BewerberService(bewerberRepository, dtoService, modelService);
    this.modelGenerator = new ModelGenerator();

    when(modulRepository.findAll()).thenReturn(offeneModule);
    initModulReturns(offeneModule);
  }

  private void initModulReturns(List<ModulDTO> modulDTOs) {
    for (ModulDTO modulDTO : modulDTOs) {
      when(modulRepository.findByModulName(modulDTO.getModulName())).thenReturn(Optional.of(modulDTO));
      when(modulRepository.findByModulNameAndDozentMail(modulDTO.getModulName(), modulDTO.getDozentMail()))
          .thenReturn(modulDTOs.stream().filter(x -> x.getModulName().equals(modulDTO.getModulName())
              && x.getDozentMail().equals(modulDTO.getDozentMail())).collect(Collectors.toList()));
    }
  }

  @Test
  public void findAlleBewerberTest() {
    List<ModulAuswahl> modulAuswahlList1 = new LinkedList<ModulAuswahl>();

    Bewerber bewerber1 = modelGenerator.generateBewerber();
    bewerber1.getPraeferenzen().setModulAuswahl(modulAuswahlList1);
    bewerber1.getPersonalien().setVorname("John");

    bewerberService.addBewerber(bewerber1);
    List<BewerberDTO> bewerberDTOList = bewerberService.findAlleBewerber();

    assertNotNull(bewerberDTOList);
    assertEquals(true, bewerberDTOList.size() > 0);

    bewerberService.removeBewerber(bewerber1);
  }

  @Test
  public void findNichtVerteilteBewerberTest() {
    Modul modul1 = modelGenerator.generateModul();
    Modul modul2 = modelGenerator.generateModul();
    modulRepository.save(dtoService.load(modul1));
    modulRepository.save(dtoService.load(modul2));

    List<ModulAuswahl> modulAuswahlList1 = new LinkedList<ModulAuswahl>();
    modulAuswahlList1.add(new ModulAuswahl(modul1, 2, 2.3, Beruf.Korrektor));
    modulAuswahlList1.add(new ModulAuswahl(modul1, 4, 1.7, Beruf.Korrektor));

    Bewerber bewerber1 = modelGenerator.generateBewerber();
    bewerber1.getPraeferenzen().setModulAuswahl(modulAuswahlList1);

    List<ModulAuswahl> modulAuswahlList2 = new LinkedList<ModulAuswahl>();
    modulAuswahlList2.add(new ModulAuswahl(modul1, 1, 2.7, Beruf.Korrektor));
    modulAuswahlList2.add(new ModulAuswahl(modul2, 3, 3.0, Beruf.Korrektor));

    Bewerber bewerber2 = modelGenerator.generateBewerber();
    bewerber2.getPraeferenzen().setModulAuswahl(modulAuswahlList1);

    bewerberService.addBewerber(bewerber1);
    bewerberService.addBewerber(bewerber2);

    bewerberService.verteile(bewerber1.getKennung(),
        bewerber1.getPraeferenzen().getModulAuswahl().get(0).getModul().getDozent());

    List<BewerberDTO> bewerberDTOList = bewerberService.findAlleBewerber();
    List<BewerberDTO> nichtVerteilteBewerber = bewerberService.findAlleNichtVerteilteBewerber(bewerberDTOList);

    assertEquals(2, bewerberDTOList.size());
    assertEquals(1, nichtVerteilteBewerber.size());

    bewerberService.removeBewerber(bewerber1);
    bewerberService.removeBewerber(bewerber2);

  }

  @Test
  public void findAlleVerteilteBewerberTest() {
    Adresse adresse1 = new Adresse("40474", "Duesseldorf", "Amsterdamer Strasse", "2");
    Date geburtsdatum1 = new Date(1996, Calendar.JANUARY, 2);
    Personalien personalien1 = new Personalien(adresse1, "Mueller", "John", geburtsdatum1, 24, "Deutschland",
        "Deutsch");
    ImmartikulationsStatus immartikulationsStatus1 = new ImmartikulationsStatus(true, "Informatik");
    StudiengangAbschluss studiengangAbschluss1 = new StudiengangAbschluss("Informatik", "Bachelor", "HHU");
    Karriere karriere1 = new Karriere("bei Apple Store gearbeitet.", immartikulationsStatus1, studiengangAbschluss1);
    Modul modul1_1 = new Modul("propra2", new Dozent("jens@hhu.de", "Jens Bendisposto"));
    Modul modul2_1 = new Modul("Aldat", new Dozent("stephan@hhu.de", "Stephan Mueller"));
    Modul modul3_1 = new Modul("RDB", new Dozent("shoetner@hhu.de", "Michael Schoetner"));
    ModulAuswahl modulAuswahl1_1 = new ModulAuswahl(modul1_1, 2, 2.0, Beruf.Tutor);
    ModulAuswahl modulAuswahl2_1 = new ModulAuswahl(modul2_1, 1, 1.7, Beruf.Tutor);
    ModulAuswahl modulAuswahl3_1 = new ModulAuswahl(modul3_1, 3, 2.3, Beruf.Tutor);
    List<ModulAuswahl> modulAuswahlList1 = new LinkedList<ModulAuswahl>();
    modulAuswahlList1.add(modulAuswahl1_1);
    modulAuswahlList1.add(modulAuswahl2_1);
    modulAuswahlList1.add(modulAuswahl3_1);
    Praeferenzen praeferenzen1 = new Praeferenzen(6, 8, modulAuswahlList1, "No Comment", EinstiegTyp.NEUEINSTIEG,
        "Keine", TutorenSchulungTeilnahme.TEILNAHME);
    Bewerber bewerber1 = new Bewerber(karriere1, personalien1, praeferenzen1);

    Adresse adresse2 = new Adresse("40474", "Duesseldorf", "Opladener Strasse", "11");
    Date geburtsdatum2 = new Date(2001, Calendar.JANUARY, 23);
    Personalien personalien2 = new Personalien(adresse2, "Fischer", "Karl", geburtsdatum2, 19, "Deutschland",
        "Deutsch");
    ImmartikulationsStatus immartikulationsStatus2 = new ImmartikulationsStatus(false, "Informatik");
    StudiengangAbschluss studiengangAbschluss2 = new StudiengangAbschluss("Informatik", "Bachelor", "HHU");
    Karriere karriere = new Karriere("keine.", immartikulationsStatus2, studiengangAbschluss2);
    Modul modul1_2 = new Modul("propra2", new Dozent("jens@hhu.de", "Jens Bendisposto"));
    Modul modul2_2 = new Modul("Aldat", new Dozent("stephan@hhu.de", "Stephan Mueller"));
    Modul modul3_2 = new Modul("RDB", new Dozent("shoetner@hhu.de", "Michael Schoetner"));
    ModulAuswahl modulAuswahl1_2 = new ModulAuswahl(modul1_2, 1, 2.0, Beruf.Tutor);
    ModulAuswahl modulAuswahl2_2 = new ModulAuswahl(modul2_2, 2, 1.0, Beruf.Korrektor);
    ModulAuswahl modulAuswahl3_2 = new ModulAuswahl(modul3_2, 3, 2.0, Beruf.Tutor);
    List<ModulAuswahl> modulAuswahlList2 = new LinkedList<ModulAuswahl>();
    modulAuswahlList2.add(modulAuswahl1_2);
    modulAuswahlList2.add(modulAuswahl2_2);
    modulAuswahlList2.add(modulAuswahl3_2);
    Praeferenzen praeferenzen2 = new Praeferenzen(6, 8, modulAuswahlList2, "No Comment", EinstiegTyp.NEUEINSTIEG,
        "Keine", TutorenSchulungTeilnahme.TEILNAHME);
    Bewerber bewerber2 = new Bewerber(karriere, personalien2, praeferenzen2);

    bewerberService.addBewerber(bewerber1);
    bewerberService.addBewerber(bewerber2);
    BewerberDTO bewerberDTO1 = dtoService.load(bewerber1);
    BewerberDTO bewerberDTO2 = dtoService.load(bewerber2);
    VerteilungDTO verteilungDTO1 = new VerteilungDTO("Jens Bendisposto", "jens@hhu.de");
    List<VerteilungDTO> verteilungDTOList = new LinkedList<VerteilungDTO>();
    verteilungDTOList.add(verteilungDTO1);
    bewerberDTO1.setVerteiltAn(verteilungDTOList);
    List<BewerberDTO> bewerberDTOList = bewerberService.findAlleBewerber();
    List<BewerberDTO> verteilteBewerber = bewerberService.findAlleVerteilteBewerber(bewerberDTOList);

    assertEquals(2, bewerberDTOList.size());
    assertEquals(1, verteilteBewerber.size());
  }
/*
  @Test
  public void findNichtVerteiltTest() {
    Adresse adresse1 = new Adresse("40474", "Duesseldorf", "Amsterdamer Strasse", "2");
    Date geburtsdatum1 = new Date(1996, Calendar.JANUARY, 2);
    Personalien personalien1 = new Personalien(adresse1, "Mueller", "John", geburtsdatum1, 24, "Deutschland",
        "Deutsch");
    ImmartikulationsStatus immartikulationsStatus1 = new ImmartikulationsStatus(true, "Informatik");
    StudiengangAbschluss studiengangAbschluss1 = new StudiengangAbschluss("Informatik", "Bachelor", "HHU");
    Karriere karriere1 = new Karriere("bei Apple Store gearbeitet.", immartikulationsStatus1, studiengangAbschluss1);
    Modul modul1_1 = new Modul("propra2", new Dozent("jens@hhu.de", "Jens Bendisposto"));
    Modul modul2_1 = new Modul("Aldat", new Dozent("stephan@hhu.de", "Stephan Mueller"));
    Modul modul3_1 = new Modul("RDB", new Dozent("shoetner@hhu.de", "Michael Schoetner"));
    ModulAuswahl modulAuswahl1_1 = new ModulAuswahl(modul1_1, 2, 2.0, Beruf.Tutor);
    ModulAuswahl modulAuswahl2_1 = new ModulAuswahl(modul2_1, 1, 1.7, Beruf.Tutor);
    ModulAuswahl modulAuswahl3_1 = new ModulAuswahl(modul3_1, 3, 2.3, Beruf.Tutor);
    List<ModulAuswahl> modulAuswahlList1 = new LinkedList<ModulAuswahl>();
    modulAuswahlList1.add(modulAuswahl1_1);
    modulAuswahlList1.add(modulAuswahl2_1);
    modulAuswahlList1.add(modulAuswahl3_1);
    Praeferenzen praeferenzen1 = new Praeferenzen(6, 8, modulAuswahlList1, "No Comment", EinstiegTyp.NEUEINSTIEG,
        "Keine", TutorenSchulungTeilnahme.TEILNAHME);
    Bewerber bewerber1 = new Bewerber(karriere1, personalien1, praeferenzen1);
    bewerber1.setVerteiltAn(Arrays.asList(new Dozent("Jens Bendisposto", "jens@hhu.de")));

    Adresse adresse2 = new Adresse("40474", "Duesseldorf", "Opladener Strasse", "11");
    Date geburtsdatum2 = new Date(2001, Calendar.JANUARY, 23);
    Personalien personalien2 = new Personalien(adresse2, "Fischer", "Karl", geburtsdatum2, 19, "Deutschland",
        "Deutsch");
    ImmartikulationsStatus immartikulationsStatus2 = new ImmartikulationsStatus(false, "Informatik");
    StudiengangAbschluss studiengangAbschluss2 = new StudiengangAbschluss("Informatik", "Bachelor", "HHU");
    Karriere karriere = new Karriere("keine.", immartikulationsStatus2, studiengangAbschluss2);
    Modul modul1_2 = new Modul("propra2", new Dozent("jens@hhu.de", "Jens Bendisposto"));
    Modul modul2_2 = new Modul("Aldat", new Dozent("stephan@hhu.de", "Stephan Mueller"));
    Modul modul3_2 = new Modul("RDB", new Dozent("shoetner@hhu.de", "Michael Schoetner"));
    ModulAuswahl modulAuswahl1_2 = new ModulAuswahl(modul1_2, 1, 2.0, Beruf.Tutor);
    ModulAuswahl modulAuswahl2_2 = new ModulAuswahl(modul2_2, 2, 1.0, Beruf.Korrektor);
    ModulAuswahl modulAuswahl3_2 = new ModulAuswahl(modul3_2, 3, 2.0, Beruf.Tutor);
    List<ModulAuswahl> modulAuswahlList2 = new LinkedList<ModulAuswahl>();
    modulAuswahlList2.add(modulAuswahl1_2);
    modulAuswahlList2.add(modulAuswahl2_2);
    modulAuswahlList2.add(modulAuswahl3_2);
    Praeferenzen praeferenzen2 = new Praeferenzen(6, 8, modulAuswahlList2, "No Comment", EinstiegTyp.NEUEINSTIEG,
        "Keine", TutorenSchulungTeilnahme.TEILNAHME);
    Bewerber bewerber2 = new Bewerber(karriere, personalien2, praeferenzen2);

    bewerberService.addBewerber(bewerber1);
    bewerberService.addBewerber(bewerber2);
    BewerberDTO bewerberDTO1 = dtoService.load(bewerber1);
    BewerberDTO bewerberDTO2 = dtoService.load(bewerber2);

    VerteilungDTO verteilungDTO1 = new VerteilungDTO("Jens Bendisposto", "jens@hhu.de");
    List<VerteilungDTO> verteilungDTOList = new LinkedList<VerteilungDTO>();
    verteilungDTOList.add(verteilungDTO1);
    bewerberDTO1.setVerteiltAn(verteilungDTOList);
    // when(bewerberRepository.findByVerteiltAnIsNotNull()).thenReturn(Arrays.asList(bewerberDTO2));
    // when(modelService.loadBewerberList(Arrays.asList(bewerberDTO1,bewerberDTO2))).thenReturn(Arrays.asList(bewerber1));
    List<BewerberDTO> alleBewerberDTOList = bewerberService.findAlleBewerber();
    List<Bewerber> nichtVerteilteBewerber = bewerberService.findNichtVerteilt();
    assertEquals(2, alleBewerberDTOList.size());
    assertEquals(1, nichtVerteilteBewerber.size());
  }
  /*
   * @Test public void findVerteilt(){ Adresse adresse = new Adresse("40474",
   * "Duesseldorf", "Amsterdamer Strasse", "2"); Date geburtsdatum = new
   * Date(2000, Calendar.JANUARY, 2); Personalien personalien = new
   * Personalien(adresse, "jomu100", "Mueller", "John", geburtsdatum, 20,
   * "Deutschland", "Deutsch");
   * 
   * ImmartikulationsStatus immartikulationsStatus = new
   * ImmartikulationsStatus(true, "Informatik"); StudiengangAbschluss
   * studiengangAbschluss = new StudiengangAbschluss("Informatik", "Bachelor",
   * "HHU"); Karriere karriere = new Karriere("bei Apple Store gearbeitet.",
   * immartikulationsStatus, studiengangAbschluss);
   * 
   * Modul modul1 = new Modul("propra2" , new Dozent(
   * "jens@hhu.de","Jens Bendisposto")); Modul modul2 = new Modul("Aldat", new
   * Dozent( "stephan@hhu.de", "Stephan Mueller")); Modul modul3 = new
   * Modul("RDB", new Dozent("shoetner@hhu.de" ,"Michael Schoetner"));
   * 
   * ModulAuswahl modulAuswahl1 = new ModulAuswahl(modul1, 2, 2.0); ModulAuswahl
   * modulAuswahl2 = new ModulAuswahl(modul2, 1, 1.7); ModulAuswahl modulAuswahl3
   * = new ModulAuswahl(modul3, 3, 2.3); List<ModulAuswahl> modulAuswahlList = new
   * LinkedList<ModulAuswahl>(); modulAuswahlList.add(modulAuswahl1);
   * modulAuswahlList.add(modulAuswahl2); modulAuswahlList.add(modulAuswahl3);
   * 
   * BerufModul berufModul = new BerufModul(Beruf.Tutor, modul1);
   * 
   * Praeferenzen praeferenzen = new Praeferenzen(6, 8, modulAuswahlList,
   * "No Comment", EinstiegTyp.NEUEINSTIEG, "Keine", berufModul,
   * TutorenSchulungTeilnahme.TEILNAHME); List<Dozent> verteiltAn = new
   * LinkedList<Dozent>(); verteiltAn.add(new Dozent("aa","bb"));
   * 
   * Adresse adresse2 = new Adresse("40474", "Duesseldorf", "Uerdinger Strasse",
   * "3"); Date geburtsdatum2 = new Date(2001, Calendar.JANUARY, 4); Personalien
   * personalien2 = new Personalien(adresse2, "kafi100", "Fischer", "Karl",
   * geburtsdatum2, 20, "Deutschland", "Deutsch");
   * 
   * ImmartikulationsStatus immartikulationsStatus2 = new
   * ImmartikulationsStatus(true, "Informatik"); StudiengangAbschluss
   * studiengangAbschluss2 = new StudiengangAbschluss("Informatik", "Bachelor",
   * "HHU"); Karriere karriere2 = new Karriere("keine.", immartikulationsStatus2,
   * studiengangAbschluss2);
   * 
   * Modul modul1_2 = new Modul("propra2" , new Dozent(
   * "jens@hhu.de","Jens Bendisposto")); Modul modul2_2 = new Modul("Aldat", new
   * Dozent( "stephan@hhu.de", "Stephan Mueller")); Modul modul3_2 = new
   * Modul("RDB", new Dozent("shoetner@hhu.de" ,"Michael Schoetner"));
   * 
   * ModulAuswahl modulAuswahl1_2 = new ModulAuswahl(modul1_2, 3, 2.0);
   * ModulAuswahl modulAuswahl2_2 = new ModulAuswahl(modul2_2, 1, 1.0);
   * ModulAuswahl modulAuswahl3_2 = new ModulAuswahl(modul3_2, 3, 2.3);
   * List<ModulAuswahl> modulAuswahlList2 = new LinkedList<ModulAuswahl>();
   * modulAuswahlList2.add(modulAuswahl1_2);
   * modulAuswahlList2.add(modulAuswahl2_2);
   * modulAuswahlList2.add(modulAuswahl3_2);
   * 
   * BerufModul berufModul2 = new BerufModul(Beruf.Tutor, modul1_2);
   * 
   * Praeferenzen praeferenzen2 = new Praeferenzen(6, 8, modulAuswahlList,
   * "No Comment", EinstiegTyp.NEUEINSTIEG, "Keine", berufModul2,
   * TutorenSchulungTeilnahme.TEILNAHME); List<Dozent> verteiltAn2 = new
   * LinkedList<Dozent>(); verteiltAn2.add(new Dozent("aa","bb"));
   * 
   * Bewerber bewerber1 = new Bewerber(karriere2, personalien2, praeferenzen2,
   * "Golov", verteiltAn2); Bewerber bewerber2 = new Bewerber(karriere,
   * personalien, praeferenzen, "lars", null);
   * bewerberService.addBewerber(bewerber1);
   * bewerberService.addBewerber(bewerber2); BewerberDTO bewerberDTO1 =
   * dtoService.load(bewerber1); BewerberDTO bewerberDTO2 =
   * dtoService.load(bewerber2);
   * when(bewerberRepository.findAll()).thenReturn(Arrays.asList(bewerberDTO1,
   * bewerberDTO2));
   * when(bewerberRepository.findByVerteiltAnIsNull()).thenReturn(Arrays.asList(
   * bewerberDTO1)); List<BewerberDTO> bewerberDTOList =
   * bewerberService.findAlleBewerber(); List<BewerberDTO> verteilteBewerber =
   * bewerberService.findNichtVerteilt(); assertEquals(bewerberDTOList.size(), 2);
   * assertEquals(verteilteBewerber.size(), 1); }
   * 
   * @Test public void findBewerberFuerDozentTest(){ Adresse adresse = new
   * Adresse("40474", "Duesseldorf", "Amsterdamer Strasse", "2"); Date
   * geburtsdatum = new Date(2000, Calendar.JANUARY, 2); Personalien personalien =
   * new Personalien(adresse, "jomu100", "Mueller", "John", geburtsdatum, 20,
   * "Deutschland", "Deutsch");
   * 
   * ImmartikulationsStatus immartikulationsStatus = new
   * ImmartikulationsStatus(true, "Informatik"); StudiengangAbschluss
   * studiengangAbschluss = new StudiengangAbschluss("Informatik", "Bachelor",
   * "HHU"); Karriere karriere = new Karriere("bei Apple Store gearbeitet.",
   * immartikulationsStatus, studiengangAbschluss);
   * 
   * Modul modul1 = new Modul("propra2" , new Dozent(
   * "jens@hhu.de","Jens Bendisposto")); Modul modul2 = new Modul("Aldat", new
   * Dozent( "stephan@hhu.de", "Stephan Mueller")); Modul modul3 = new
   * Modul("RDB", new Dozent("shoetner@hhu.de" ,"Michael Schoetner"));
   * 
   * ModulAuswahl modulAuswahl1 = new ModulAuswahl(modul1, 2, 2.0); ModulAuswahl
   * modulAuswahl2 = new ModulAuswahl(modul2, 1, 1.7); ModulAuswahl modulAuswahl3
   * = new ModulAuswahl(modul3, 3, 2.3); List<ModulAuswahl> modulAuswahlList = new
   * LinkedList<ModulAuswahl>(); modulAuswahlList.add(modulAuswahl1);
   * modulAuswahlList.add(modulAuswahl2); modulAuswahlList.add(modulAuswahl3);
   * 
   * BerufModul berufModul = new BerufModul(Beruf.Tutor, modul1);
   * 
   * Praeferenzen praeferenzen = new Praeferenzen(6, 8, modulAuswahlList,
   * "No Comment", EinstiegTyp.NEUEINSTIEG, "Keine", berufModul,
   * TutorenSchulungTeilnahme.TEILNAHME); List<Dozent> verteiltAn = new
   * LinkedList<Dozent>(); verteiltAn.add(new Dozent("aa","bb")); Bewerber
   * bewerber1 = new Bewerber(karriere, personalien, praeferenzen, "Golov",
   * verteiltAn);
   * 
   * bewerberService.addBewerber(bewerber1); BewerberDTO bewerberDTO1 =
   * dtoService.load(bewerber1);
   * when(bewerberRepository.findAll()).thenReturn(Arrays.asList(bewerberDTO1));
   * List<BewerberDTO> bewerberDTOList = bewerberService.findAlleBewerber();
   * List<Bewerber> bewerberFuerDozent =
   * bewerberService.findBewerberFuerDozent("stephan@hhu.de");
   * assertEquals(bewerberDTOList.get(0).getPersonalien().getUnikennung(),
   * "jomu100");
   * assertEquals(bewerberFuerDozent.get(0).getPersonalien().getUnikennung(),
   * "jomu100"); }
   * 
   * @Test public void verteileTest(){ Adresse adresse = new Adresse("40474",
   * "Duesseldorf", "Amsterdamer Strasse", "2"); Date geburtsdatum = new
   * Date(2000, Calendar.JANUARY, 2); Personalien personalien = new
   * Personalien(adresse, "jomu100", "Mueller", "John", geburtsdatum, 20,
   * "Deutschland", "Deutsch");
   * 
   * ImmartikulationsStatus immartikulationsStatus = new
   * ImmartikulationsStatus(true, "Informatik"); StudiengangAbschluss
   * studiengangAbschluss = new StudiengangAbschluss("Informatik", "Bachelor",
   * "HHU"); Karriere karriere = new Karriere("bei Apple Store gearbeitet.",
   * immartikulationsStatus, studiengangAbschluss);
   * 
   * Modul modul1 = new Modul("propra2" , new Dozent(
   * "jens@hhu.de","Jens Bendisposto")); Modul modul2 = new Modul("Aldat", new
   * Dozent( "stephan@hhu.de", "Stephan Mueller")); Modul modul3 = new
   * Modul("RDB", new Dozent("shoetner@hhu.de" ,"Michael Schoetner"));
   * 
   * ModulAuswahl modulAuswahl1 = new ModulAuswahl(modul1, 2, 2.0); ModulAuswahl
   * modulAuswahl2 = new ModulAuswahl(modul2, 1, 1.7); ModulAuswahl modulAuswahl3
   * = new ModulAuswahl(modul3, 3, 2.3); List<ModulAuswahl> modulAuswahlList = new
   * LinkedList<ModulAuswahl>(); modulAuswahlList.add(modulAuswahl1);
   * modulAuswahlList.add(modulAuswahl2); modulAuswahlList.add(modulAuswahl3);
   * 
   * BerufModul berufModul = new BerufModul(Beruf.Tutor, modul1);
   * 
   * Praeferenzen praeferenzen = new Praeferenzen(6, 8, modulAuswahlList,
   * "No Comment", EinstiegTyp.NEUEINSTIEG, "Keine", berufModul,
   * TutorenSchulungTeilnahme.TEILNAHME); List<Dozent> verteiltAn = new
   * LinkedList<Dozent>(); Bewerber bewerber1 = new Bewerber(karriere,
   * personalien, praeferenzen, "Golov", verteiltAn); Dozent dozent = new
   * Dozent("jens@hhu.de","Jens Bendisposto"); String id = "jomu100";
   * 
   * bewerberService.addBewerber(bewerber1); BewerberDTO bewerberDTO1 =
   * dtoService.load(bewerber1);
   * when(bewerberRepository.findAll()).thenReturn(Arrays.asList(bewerberDTO1));
   * when(bewerberRepository.findById(id)).thenReturn(Optional.of(bewerberDTO1));
   * when(bewerberRepository.save(bewerberDTO1)).thenReturn(bewerberDTO1);
   * List<BewerberDTO> bewerberDTOList = bewerberService.findAlleBewerber();
   * 
   * bewerberService.verteile(id, dozent);
   * 
   * assertEquals(bewerberDTOList.get(0).getPersonalien().getUnikennung(),
   * "jomu100");
   * assertEquals(bewerberDTOList.get(0).getVerteiltAn().get(0).getDozentKennung()
   * , "jens@hhu.de"); }
   */
}
