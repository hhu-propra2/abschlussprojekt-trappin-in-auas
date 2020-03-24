package mops.services;
import mops.domain.models.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import java.io.File;

import org.springframework.stereotype.Service;
@SuppressWarnings("PMD.UnusedLocalVariable")
@Service
public class PDFService {
  /*
  for (PDField field2 : pDAcroForm.getFields()){
    System.out.println(field2.getFullyQualifiedName());
  }
  */

  public String filedirectory(Bewerber bewerber) {
    if(bewerber.getKarriere().getFachAbschluss() == null) {
      return "../../../resources/static/321_Antrag_Beschaeftigung_stud_Hilfskraefte.pdf";
    }
    else {
      return "../../../resources/static/323_Antrag_Beschaeftigung_wiss_Hilfskraefte_mit_BA.pdf";
    }
  }

  public void checkEncryption(PDDocument pdDocument, String fileDirectory) throws Exception {
    if (pdDocument.isEncrypted()) {
      try {
        pdDocument.setAllSecurityToBeRemoved(true);
      } catch (Exception e) {
        throw new Exception("The document is encrypted, and we can't decrypt it.", e);
      }
    }
  }

  public void fillPDF(Bewerber bewerber, String fileDirectory) throws Exception {
    try (PDDocument pDDocument = PDDocument.load(new File(fileDirectory))) {
      try {
        checkEncryption(pDDocument, fileDirectory);
        loadPDFFelder(bewerber, pDDocument);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (hatAbschluss(bewerber)) {
          pDDocument.save("../../../resources/static//output2.pdf");
        } else {
          pDDocument.save("../../../resources/static//output.pdf");
        }
        pDDocument.close();
      }
    }
  }
  public void loadPDFFelder(Bewerber bewerber, PDDocument pdDocument) throws Exception{
    PDAcroForm pDAcroForm = pdDocument.getDocumentCatalog().getAcroForm();

    PDField field = pDAcroForm.getField("Vorname");
    field.setValue(bewerber.getPersonalien().getVorname());
    field = pDAcroForm.getField("Name");
    field.setValue(bewerber.getPersonalien().getName());
    field = pDAcroForm.getField("Geburtsdatum");
    field.setValue(bewerber.getPersonalien().getGeburtsdatum().toString());
    field = pDAcroForm.getField("Staatsangehörigkeit");
    field.setValue(bewerber.getPersonalien().getNationalitaet());
    field = pDAcroForm.getField("Anschrift (Straße)");
    field.setValue(bewerber.getPersonalien().getAdresse().getStrasse());
    field = pDAcroForm.getField("Anschrift (Hausnummer)");
    field.setValue(bewerber.getPersonalien().getAdresse().getHausnummer());
    field = pDAcroForm.getField("Anschrift (PLZ)");
    field.setValue(bewerber.getPersonalien().getAdresse().getPLZ());
    field = pDAcroForm.getField("Anschrift (ORT)");
    field.setValue(bewerber.getPersonalien().getAdresse().getWohnort());

    field = pDAcroForm.getField("Vertragsart");
    field.setValue(pruefeVertragsart(bewerber));

    field = pDAcroForm.getField("Stunden");
    field.setValue(
        bewerber.getPraeferenzen().getMinWunschStunden() + " - " + bewerber.getPraeferenzen()
            .getMaxWunschStunden());
    field = pDAcroForm.getField("Immatrikulation");
    field.setValue(pruefeStatus(bewerber));
    field = pDAcroForm.getField("Studiengang");
    field.setValue(bewerber.getKarriere().getImmartikulationsStatus().getFachrichtung());

    field = pDAcroForm.getField("Bemerkung zum Antrag");
    field.setValue("On");
    field = pDAcroForm.getField("Bemerkung zum Antrag1");
    field.setValue(
        bewerber.getKarriere().getArbeitserfahrung() + "/n" + bewerber.getPraeferenzen()
            .getKommentar() + "/n" + bewerber.getPraeferenzen().getEinschraenkungen());

    if(hatAbschluss(bewerber)) {
      field = pDAcroForm.getField("Schilderung der auszuübenden Tätigkeit");
      field.setValue(berufModul(bewerber));
    }
  }

  public boolean hatAbschluss(Bewerber bewerber) {
    return bewerber.getKarriere().getFachAbschluss() != null;
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public String berufModul(Bewerber bewerber){
    String berufModul = "";
    for (ModulAuswahl modulAuswahl : bewerber.getPraeferenzen().getModulAuswahl()) {
      berufModul =
          berufModul + " " + modulAuswahl.getModul().getModulName() + ": " + modulAuswahl.getBeruf().toString()
              + "\n";
    }
    return berufModul;
  }

  public String pruefeStatus(Bewerber bewerber){
    if(bewerber.getKarriere().getImmartikulationsStatus().isStatus()){
      return "On";
    }
    return "Off";
  }

  public String pruefeVertragsart(Bewerber bewerber) {
    if (bewerber.getPraeferenzen().getEinstiegTyp() == EinstiegTyp.WEITERBESCHAEFTIGUNG){
      return "Weiterbeschäftigung";
    }
    else if(bewerber.getPraeferenzen().getEinstiegTyp() == EinstiegTyp.NEUEINSTIEG){
      return "Einstellung";
    } else{
      return "Off";
    }
  }
}