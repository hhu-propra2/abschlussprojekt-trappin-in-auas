package mops.services;

import java.util.List;
import mops.domain.database.dto.DozentPraeferenzDTO;
import mops.domain.repositories.DozentPraeferenzRepo;
import mops.domain.repositories.ModulRepository;
import mops.domain.services.IDozentPraeferenzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DozentPraeferenzService implements IDozentPraeferenzService {

  @Autowired
  private transient DozentPraeferenzRepo dozentPraeferenzRepo;

  public DozentPraeferenzService(DozentPraeferenzRepo dozentPraeferenzRepo) {
    this.dozentPraeferenzRepo = dozentPraeferenzRepo;
  }

  @Override
  public void addPraeferenz(DozentPraeferenzDTO dozentPraeferenzDTO) {
    dozentPraeferenzRepo.save(dozentPraeferenzDTO);
  }

  @Override
  public void deletePraeferenz(String bewerber, String dozentMail) {

  }

  @Override
  public Integer getDozentPraeferenz(String bewerber, String dozentMail) {
    List<DozentPraeferenzDTO> matchingRows = dozentPraeferenzRepo.findByBewerberAndDozentMail(bewerber, dozentMail);

    if(matchingRows.isEmpty()){
      return 0;
    }
    return matchingRows.get(0).getPraeferenz();
  }
}
