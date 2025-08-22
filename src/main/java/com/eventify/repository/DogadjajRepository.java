package com.eventify.repository;

import com.eventify.entity.Dogadjaj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DogadjajRepository extends JpaRepository<Dogadjaj, Long> {

    // Example search method (by location name and date after)
    List<Dogadjaj> findByLokacijaNazivContainingIgnoreCaseAndDatumVremeAfter(String lokacijaNaziv, LocalDateTime after);

    // Flexible search covering grad (in naziv/adresa), kategorija and date range
    @Query("""
           SELECT d FROM Dogadjaj d
           WHERE (:grad IS NULL OR LOWER(d.lokacijaAdresa) LIKE LOWER(CONCAT('%', :grad, '%'))
                                OR LOWER(d.lokacijaNaziv) LIKE LOWER(CONCAT('%', :grad, '%')))
             AND (:kategorijaId IS NULL OR d.kategorija.id = :kategorijaId)
             AND (:start IS NULL OR d.datumVreme >= :start)
             AND (:end IS NULL OR d.datumVreme <  :end)
           """)
    List<Dogadjaj> search(String grad, Long kategorijaId, LocalDateTime start, LocalDateTime end);
}