package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {
    List<Ship> getAllShips(Specification<Ship> shipSpecification);
    Page<Ship> getAllShips(Specification<Ship> shipSpecification, Pageable pageable);
    Ship addShip(Ship ship);
    Ship editShip(Long id,Ship ship);
    void deleteShip(Long id);
    Ship getShipById(Long id);
    boolean ifIdExists(Long id);
    boolean ifIdValid(Long id);

    Specification<Ship> filterByName(String name);
    Specification<Ship> filterByPlanet(String planet);

    Specification<Ship> filterByShipType(ShipType shipType);
    Specification<Ship> filterByDate(Long from, Long to);
    Specification<Ship> filterByUsage(Boolean isUsed);
    Specification<Ship> filterBySpeed(Double min, Double max);
    Specification<Ship> filterByCrewSize(Integer min, Integer max);
    Specification<Ship> filterByRating(Double min, Double max);
}
