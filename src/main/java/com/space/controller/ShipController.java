package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipController {

    @Autowired
    private ShipService shipService;

    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "planet", required = false) String planet,
                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                  @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return shipService.getAllShips(Specification.where(shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet)))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByDate(after, before))
                .and(shipService.filterByUsage(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating(minRating, maxRating)), pageable)
                .getContent();
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    public Integer getCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "planet", required = false) String planet,
                            @RequestParam(value = "shipType", required = false) ShipType shipType,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                            @RequestParam(value = "minRating", required = false) Double minRating,
                            @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return shipService.getAllShips(Specification.where(shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet)))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByDate(after, before))
                .and(shipService.filterByUsage(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating(minRating, maxRating)), Pageable.unpaged()).getNumberOfElements();
    }

    @PostMapping(value = "/ships")
    public @ResponseBody Ship addShip(@RequestBody Ship ship) {
        try{
            shipService.addShip(ship);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        return ship;
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    public @ResponseBody Ship getShipById(@PathVariable("id") Long id) {
        Ship ship;
        if (!shipService.ifIdValid(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ID not correct = " + id);
        if (!shipService.ifIdExists(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found ID = " + id);
        try {
            ship = shipService.getShipById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        return ship;
    }

    @PostMapping(value = "/ships/{id}")
    public Ship updateShipById(@PathVariable("id") Long id, @RequestBody Ship ship){
        Ship updatedShip;
        if (!shipService.ifIdValid(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ID not correct = " + id);
        if (!shipService.ifIdExists(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found ID = " + id);
        try {
            updatedShip = shipService.editShip(id,ship);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        return updatedShip;
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public void deleteShipById(@PathVariable("id") Long id){
        if (!shipService.ifIdValid(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"ID not correct = " + id);
        if (!shipService.ifIdExists(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found ID = " + id);
        try {
            shipService.deleteShip(id);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
}
