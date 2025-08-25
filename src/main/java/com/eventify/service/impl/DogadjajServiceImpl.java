package com.eventify.service.impl;

import com.eventify.dto.DogadjajDTO;
import com.eventify.entity.Dogadjaj;
import com.eventify.entity.Kategorija;
import com.eventify.entity.Korisnik;
import com.eventify.exception.ForbiddenOperationException;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.repository.DogadjajRepository;
import com.eventify.repository.KategorijaRepository;
import com.eventify.repository.KorisnikRepository;
import com.eventify.service.DogadjajService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class DogadjajServiceImpl implements DogadjajService {

    private final DogadjajRepository dogadjajRepository;
    private final KorisnikRepository korisnikRepository;
    private final KategorijaRepository kategorijaRepository;

    @Override
    public DogadjajDTO createDogadjaj(DogadjajDTO dto, Long organizatorId) {
        Korisnik org = korisnikRepository.findById(organizatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizator nije pronađen"));

        Dogadjaj d = toEntity(dto);
        d.setOrganizator(org);

        if (dto.getKategorijaId() != null) {
            Kategorija kat = kategorijaRepository.findById(dto.getKategorijaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategorija nije pronađena"));
            d.setKategorija(kat);
        } else {
            d.setKategorija(null);
        }

        d = dogadjajRepository.save(d);
        return toDTO(d);
    }

    @Override
    public DogadjajDTO getDogadjajById(Long id) {
        Dogadjaj d = dogadjajRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj nije pronađen"));
        return toDTO(d);
    }

    @Override
    public List<DogadjajDTO> getAllDogadjaji() {
        return dogadjajRepository.findAll().stream().map(this::toDTO).collect(toList());
    }

    @Override
    public List<DogadjajDTO> searchDogadjaji(String grad, LocalDate datum, Long kategorijaId) {
        LocalDateTime start = datum != null ? datum.atStartOfDay() : null;
        LocalDateTime end = datum != null ? datum.plusDays(1).atStartOfDay() : null;
        return dogadjajRepository.search(grad, kategorijaId, start, end)
                .stream().map(this::toDTO).collect(toList());
    }

    @Override
    public DogadjajDTO updateDogadjaj(Long id, DogadjajDTO dto, Long requestingUserId, boolean isAdmin) {
        Dogadjaj d = dogadjajRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj nije pronađen"));

        if (!isAdmin && !d.getOrganizator().getId().equals(requestingUserId)) {
            throw new ForbiddenOperationException("Nije dozvoljeno menjati ovaj događaj.");
        }

        d.setNaziv(dto.getNaziv());
        d.setOpis(dto.getOpis());
        d.setDatumVreme(dto.getDatumVreme());
        d.setLokacijaNaziv(dto.getLokacijaNaziv());
        d.setLokacijaAdresa(dto.getLokacijaAdresa());
        d.setUkupanBrojUlaznica(dto.getUkupanBrojUlaznica());
        d.setCenaUlaznice(dto.getCenaUlaznice());
        d.setSlikaUrl(dto.getSlikaUrl());

        if (dto.getKategorijaId() != null) {
            Kategorija kat = kategorijaRepository.findById(dto.getKategorijaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategorija nije pronađena"));
            d.setKategorija(kat);
        } else {
            d.setKategorija(null);
        }

        return toDTO(dogadjajRepository.save(d));
    }

    @Override
    public void deleteDogadjaj(Long id, Long requestingUserId, boolean isAdmin) {
        Dogadjaj d = dogadjajRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj nije pronađen"));

        if (!isAdmin && !d.getOrganizator().getId().equals(requestingUserId)) {
            throw new ForbiddenOperationException("Nije dozvoljeno brisanje ovog događaja.");
        }
        dogadjajRepository.delete(d);
    }

    private DogadjajDTO toDTO(Dogadjaj d) {
        DogadjajDTO dto = new DogadjajDTO();
        dto.setId(d.getId());
        dto.setNaziv(d.getNaziv());
        dto.setOpis(d.getOpis());
        dto.setDatumVreme(d.getDatumVreme());
        dto.setLokacijaNaziv(d.getLokacijaNaziv());
        dto.setLokacijaAdresa(d.getLokacijaAdresa());
        dto.setUkupanBrojUlaznica(d.getUkupanBrojUlaznica());
        dto.setCenaUlaznice(d.getCenaUlaznice());
        dto.setSlikaUrl(d.getSlikaUrl());
        dto.setKategorijaId(d.getKategorija() != null ? d.getKategorija().getId() : null);
        return dto;
    }

    private Dogadjaj toEntity(DogadjajDTO dto) {
        return Dogadjaj.builder()
                .naziv(dto.getNaziv())
                .opis(dto.getOpis())
                .datumVreme(dto.getDatumVreme())
                .lokacijaNaziv(dto.getLokacijaNaziv())
                .lokacijaAdresa(dto.getLokacijaAdresa())
                .ukupanBrojUlaznica(dto.getUkupanBrojUlaznica())
                .cenaUlaznice(dto.getCenaUlaznice())
                .slikaUrl(dto.getSlikaUrl())
                .build();
    }
}