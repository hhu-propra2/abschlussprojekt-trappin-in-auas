package mops.domain.services;

import mops.domain.models.Bewerber;

public interface IBewerberService {
    void addBewerber(Bewerber b);

    Bewerber findBewerberByKennung(String kennung);
}