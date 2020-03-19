package mops.domain.services;

import java.util.List;
import mops.domain.database.dto.*;
import mops.domain.models.*;
import org.bouncycastle.math.raw.Mod;

public interface IDTOService {
    ModulDTO load(Modul modul);
    ModulAuswahlDTO load(ModulAuswahl modulAuswahl);
    BerufModulDTO load(BerufModul berufModul);
    AdresseDTO load(Adresse adresse);
    ImmartikulationsStatusDTO load(ImmartikulationsStatus immartikulationsStatus);
    StudiengangAbschlussDTO load(StudiengangAbschluss studiengangAbschluss);
    List<ModulAuswahlDTO> loadList(Praeferenzen praeferenzen);
    KarriereDTO load(Karriere karriere);
    PersonalienDTO load(Personalien personalien);
    PraeferenzenDTO load(Praeferenzen praeferenzen);
    BewerberDTO load(Bewerber bewerber);
}
