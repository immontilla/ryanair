package eu.immontilla.ryanair.client.service;

import java.util.List;

import eu.immontilla.ryanair.client.model.Route;

/**
 * Service to get the availables routes from the Routes API.
 * API response is cached. 
 * Cache configuration file: src/main/resources/ehcache.xml
 * 
 * @author immontilla
 */
public interface AvailableRouteService {
    List<Route> getAll();
}
