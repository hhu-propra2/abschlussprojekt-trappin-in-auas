package mops.controller;

import static mops.authentication.account.keycloak.KeycloakRoles.ROLE_VERTEILER;

import mops.domain.models.Bewerber;
import mops.services.*;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bewerbung1/verteiler")
public class VerteilerController {

  private transient BewerberService bewerberService;
  private transient ModulService modulService;
  private transient DozentPraeferenzService dozentPraeferenzService;
  private transient ZyklusDirigentService zyklusDirigentService;
  private transient VerteilerService verteilerService;

  public VerteilerController(BewerberService bewerberService, ModulService modulService,
      DozentPraeferenzService dozentPraeferenzService, ZyklusDirigentService zyklusDirigentService, VerteilerService verteilerService) {
    this.bewerberService = bewerberService;
    this.modulService = modulService;
    this.dozentPraeferenzService = dozentPraeferenzService;
    this.zyklusDirigentService = zyklusDirigentService;
    this.verteilerService = verteilerService;
  }

  @Secured(ROLE_VERTEILER)
  @GetMapping("/uebersicht")
  public String verteilen(Model model, KeycloakAuthenticationToken token) {
    List<Bewerber> offeneBewerbungen = bewerberService.findAlleNichtVerteilteBewerber();
    List<Bewerber> zugewieseneBewerbungen = bewerberService.findAlleVerteilteBewerber();
    List<Bewerber> offeneBewerbungenPreview = offeneBewerbungen.stream().limit(5).collect(Collectors.toList());

    model.addAttribute("anzahlOffeneBewerbungen", offeneBewerbungen.size());
    model.addAttribute("anzahlZugewieseneBewerbungen", zugewieseneBewerbungen.size());
    model.addAttribute("anzuzeigende", offeneBewerbungenPreview);

    model.addAttribute("alleModule", modulService.findAllModule());

    model.addAttribute("anzeigeModus", "uebersicht");
    model.addAttribute("verteilerPhase", zyklusDirigentService.getVerteilerPhase());
    model.addAttribute("dozentPhase", zyklusDirigentService.getDozentenPhase());
    model.addAttribute("bewerberPhase", zyklusDirigentService.getBewerbungsPhase());

    model.addAttribute("modulMitZugewiesende", verteilerService.getListModulMitAnzahlVerteilten(modulService.findAllModule()));

    return "verteiler/verteiler";
  }

  @Secured(ROLE_VERTEILER)
  @GetMapping("/verteilte")
  public String showVerteilteBewerber(Model model, KeycloakAuthenticationToken token) {
    List<Bewerber> offeneBewerbungen = bewerberService.findAlleNichtVerteilteBewerber();
    List<Bewerber> zugewieseneBewerbungen = bewerberService.findAlleVerteilteBewerber();

    model.addAttribute("anzahlOffeneBewerbungen", offeneBewerbungen.size());
    model.addAttribute("anzahlZugewieseneBewerbungen", zugewieseneBewerbungen.size());
    model.addAttribute("anzuzeigende", zugewieseneBewerbungen);

    model.addAttribute("alleModule", modulService.findAllModule());

    model.addAttribute("anzeigeModus", "verteilte");
    model.addAttribute("verteilerPhase", zyklusDirigentService.getVerteilerPhase());
    model.addAttribute("dozentPhase", zyklusDirigentService.getDozentenPhase());
    model.addAttribute("bewerberPhase", zyklusDirigentService.getBewerbungsPhase());

    model.addAttribute("modulMitZugewiesende", verteilerService.getListModulMitAnzahlVerteilten(modulService.findAllModule()));


    return "verteiler/verteiler";
  }

  @Secured(ROLE_VERTEILER)
  @GetMapping("/offene")
  public String showOffeneBewerber(Model model, KeycloakAuthenticationToken token) {
    List<Bewerber> offeneBewerbungen = bewerberService.findAlleNichtVerteilteBewerber();
    List<Bewerber> zugewieseneBewerbungen = bewerberService.findAlleVerteilteBewerber();

    model.addAttribute("anzahlOffeneBewerbungen", offeneBewerbungen.size());
    model.addAttribute("anzahlZugewieseneBewerbungen", zugewieseneBewerbungen.size());
    model.addAttribute("anzuzeigende", offeneBewerbungen);

    model.addAttribute("alleModule", modulService.findAllModule());

    model.addAttribute("anzeigeModus", "offene");
    model.addAttribute("verteilerPhase", zyklusDirigentService.getVerteilerPhase());
    model.addAttribute("dozentPhase", zyklusDirigentService.getDozentenPhase());
    model.addAttribute("bewerberPhase", zyklusDirigentService.getBewerbungsPhase());

    model.addAttribute("modulMitZugewiesende", verteilerService.getListModulMitAnzahlVerteilten(modulService.findAllModule()));


    return "verteiler/verteiler";
  }

  @Secured(ROLE_VERTEILER)
  @GetMapping("/details/{kennung}")
  public String detailansicht(Model model, @PathVariable String kennung) {
    model.addAttribute("bewerber", bewerberService.findBewerberModelByKennung(kennung));
    model.addAttribute("phase", zyklusDirigentService.getVerteilerPhase());
    return "bewerbungsdetails/details";
  }

  @Secured(ROLE_VERTEILER)
  @PostMapping("/verteile")
  public String verteile(Model m, @RequestParam String bewerberKennung, @RequestParam String modulName) {
    bewerberService.verteile(bewerberKennung, modulService.findModulByModulName(modulName).getDozent());
    return "redirect:/bewerbung1/verteiler/uebersicht";
  }

  @Secured(ROLE_VERTEILER)
  @PostMapping("/verteilungentfernen")
  public String verteilungEntfernen(Model m, String bewerber, String dozentKennung) {
    bewerberService.verteilungEntfernen(bewerber, dozentKennung);
    return "redirect:/bewerbung1/verteiler/verteilte";
  }

  @Secured(ROLE_VERTEILER)
  @PostMapping("/phasesetzen/{phase}")
  public String phaseSetzen(Model m, @PathVariable String phase) {
    if (phase.equals("bewerbung")) {
      zyklusDirigentService.bewerbungsPhaseBeginnen();
    } else if (phase.equals("dozent")) {
      zyklusDirigentService.dozentBewertungsphaseBeginnen();
    } else if (phase.equals("verteiler")) {
      zyklusDirigentService.verteilungsPhaseBeginnen();
    }
    return "redirect:/bewerbung1/verteiler/uebersicht";
  }
}