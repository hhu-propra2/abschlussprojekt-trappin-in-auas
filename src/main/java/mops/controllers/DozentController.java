package mops.controllers;

import java.util.List;

import mops.domain.database.dto.BewerberDTO;
import mops.domain.models.Bewerber;
import mops.services.BewerberService;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dozent") // this site is specific to the logged in dozent
public class DozentController {

  private transient BewerberService bewerberService;
  private transient final String orgaRole = "ROLE_orga";

  public DozentController(BewerberService bewerberService) {
    this.bewerberService = bewerberService;
  }

  @Secured({ orgaRole })
  @GetMapping("/uebersicht")
  public String verteilen(Model model, KeycloakAuthenticationToken token) {
    List<Bewerber> meineBewerber = bewerberService.findBewerberFuerDozent(token.getName());
    /*
      TODO: anzahl bewerbungen ohne prio und anzahl bewerbungen mit prio

    */
    model.addAttribute("bewerber", meineBewerber);
    return "orga/dozent/ubersicht";
  }

  //TODO: DozentPraeferenz hinzufügen

  @Secured({ orgaRole })
  @GetMapping("/uebersicht/offene")
  public String offeneUebersicht(Model model, KeycloakAuthenticationToken token) {
    List<BewerberDTO> offeneBewerbungen = bewerberService.findNichtVerteilt();

    model.addAttribute("offene", offeneBewerbungen);

    return "";
  }

  @Secured({ orgaRole })
  @GetMapping("/uebersicht/zugewiesene")
  public String zugewieseneUebersicht(Model model, KeycloakAuthenticationToken token) {
    List<BewerberDTO> zugewiesene = bewerberService.findVerteilt();

    model.addAttribute(zugewiesene);

    return "";
  }

  @Secured({ orgaRole })
  @GetMapping("/uebersicht/detail/{kennung}")
  public String detailAnsicht(Model model, KeycloakAuthenticationToken token, @PathVariable String kennung) {
    BewerberDTO bewerber = bewerberService.findBewerberByKennung(kennung);

    model.addAttribute("bewerber", bewerber);
    return "";
  }
}