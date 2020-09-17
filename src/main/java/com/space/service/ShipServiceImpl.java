package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public List<Ship> getAllShips(Specification<Ship> shipSpecification) {
        return shipRepository.findAll();
    }

    @Override
    public Page<Ship> getAllShips(Specification<Ship> shipSpecification, Pageable pageable) {
        return shipRepository.findAll(shipSpecification, pageable);
    }

    @Override
    public Ship addShip(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null)
            throw new RuntimeException("One of Ship params is null");
        String err = checkShipParams(ship);
        if (!err.isEmpty()) throw new RuntimeException(err);
        if (ship.getUsed() == null)
            ship.setUsed(false);
        Double raiting = calculateRating(ship);
        ship.setRating(raiting);
        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Ship editShip(Long id, Ship ship) {
        String err = checkShipParams(ship);
        if (!err.isEmpty()) throw new RuntimeException(err);
        if (!ifIdExists(id)) throw new RuntimeException("Ship not found");
        Ship editedShip = shipRepository.findById(id).get();

        if (ship.getName() != null) editedShip.setName(ship.getName());
        if (ship.getPlanet() != null) editedShip.setPlanet(ship.getPlanet());
        if (ship.getShipType() != null) editedShip.setShipType(ship.getShipType());
        if (ship.getProdDate() != null) editedShip.setProdDate(ship.getProdDate());
        if (ship.getSpeed() != null) editedShip.setSpeed(ship.getSpeed());
        if (ship.getUsed() != null) editedShip.setUsed(ship.getUsed());
        if (ship.getCrewSize() != null) editedShip.setCrewSize(ship.getCrewSize());

        Double rating = calculateRating(editedShip);
        editedShip.setRating(rating);

        return shipRepository.save(editedShip);
    }

    @Override
    public void deleteShip(Long id) {
        if (!ifIdExists(id)) throw new RuntimeException("Ship not found");
        shipRepository.deleteById(id);
    }

    @Override
    public Ship getShipById(Long id) {
        if (!ifIdExists(id)) throw new RuntimeException("Ship not found");
        return shipRepository.findById(id).get();
    }

    @Override
    public boolean ifIdExists(Long id) {
        return shipRepository.existsById(id);
    }

    @Override
    public boolean ifIdValid(Long id) {
        return id > 0;
    }

    @Override
    public Specification<Ship> filterByName(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"),"%" + name + "%");
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {
        return (root, query, criteriaBuilder) -> planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, criteriaBuilder) -> shipType == null ? null : criteriaBuilder.equal(root.get("shipType"), shipType);
    }

    @Override
    public Specification<Ship> filterByDate(Long from, Long to) {
        return (root, query, criteriaBuilder) -> {
            if (from == null && to == null) return null;
            if (from == null) return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), new Date(to));
            if (to == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), new Date(from));
            return criteriaBuilder.between(root.get("prodDate"), new Date(from), new Date(to));
        };
    }

    @Override
    public Specification<Ship> filterByUsage(Boolean isUsed) {
        return (root, query, criteriaBuilder) -> {
            if (isUsed == null)
                return null;
            if (isUsed) return criteriaBuilder.isTrue(root.get("isUsed"));
            return criteriaBuilder.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), min);
            return criteriaBuilder.between(root.get("speed"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), min);
            return criteriaBuilder.between(root.get("crewSize"), min, max);
        };
    }

    @Override
    public Specification<Ship> filterByRating(Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), min);
            return criteriaBuilder.between(root.get("rating"), min, max);
        };
    }

    private Double calculateRating(Ship ship) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        int year = cal.get(Calendar.YEAR);
        BigDecimal raiting = BigDecimal.valueOf((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));
        raiting = raiting.setScale(2, RoundingMode.HALF_UP);
        return raiting.doubleValue();
    }

    private String checkShipParams(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            return "Incorrect Ship.name";
        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            return "Incorrect Ship.planet";
        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            return "Incorrect Ship.crewSize";
        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            return "Incorrect Ship.speed";
        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
                return "Incorrect Ship.date";
        }
        return "";
    }
}
