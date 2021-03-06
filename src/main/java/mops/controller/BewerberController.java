package mops.controller;

import static mops.authentication.account.keycloak.KeycloakRoles.*;

import java.util.stream.Collectors;

import javax.validation.Valid;

import mops.domain.models.*;
import mops.services.BewerberService;
import mops.services.ModulService;

import mops.services.ZyklusDirigentService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("")
@RequestMapping("/bewerbung1/bewerber")
public class BewerberController {

  @Autowired
  private transient BewerberService bewerberService;

  @Autowired
  private transient ModulService modulService;

  @Autowired
  private transient ZyklusDirigentService zyklusDirigentService;

  /**
   * Students dashboard. Login as "studentin" required.
   * @param model injected, Model for Thymeleaf interaction
   * @param token injected, present, if user is logged in
   * @return studentMainpage html template
   */
  @GetMapping("")
  @Secured({ ROLE_STUDENT })
  public String index(Model model, KeycloakAuthenticationToken token) {
    model.addAttribute("bewerbungExists", bewerberService.bewerbungExists(token.getName()));
    model.addAttribute("bewerberPhase", zyklusDirigentService.getBewerbungsPhase());
    return "student/bewerberuebersicht";
  }

  @GetMapping("/bewerbung")
  @Secured({ ROLE_STUDENT })
  public String bewirb(Model model, KeycloakAuthenticationToken token) {
    model.addAttribute("bewerber", bewerberService.initialiseBewerber());
    model.addAttribute("existingmodule", modulService.findAllModule());
    System.out.println("Module: " + modulService.findAllModule());
    return "student/main_min";
  }

  @GetMapping("/ex")
  @Secured({ ROLE_STUDENT })
  public String ex(Model model, KeycloakAuthenticationToken token){
    model.addAttribute("bewerber", bewerberService.initialiseEditBewerber(token.getName()));
    return "orga/dozent/bewerbungDetail";
  }

  @GetMapping("/editieren")
  @Secured({ ROLE_STUDENT })
  public String editieren(Model model, KeycloakAuthenticationToken token) {
    model.addAttribute("bewerber", bewerberService.initialiseEditBewerber(token.getName()));
    model.addAttribute("existingmodule", modulService.findAllModule());
    return "student/main_min";
  }

  @PostMapping("/bewerbungabschicken")
  @Secured({ ROLE_STUDENT })
  public String bewirbabschicken(Model model, @ModelAttribute @Valid Bewerber bewerber, BindingResult result, KeycloakAuthenticationToken token) {
    if(result.hasErrors()){
      System.out.println(result.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList()));
      System.out.println(result.getAllErrors().stream().map(x -> x.getObjectName()).collect(Collectors.toList()));
      model.addAttribute("existingmodule", modulService.findAllModule());
      return "student/main_min";
    }
    bewerberService.addBewerber(bewerber, token.getName());
    return "student/vielenDank";
  }
}
