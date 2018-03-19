package eu.immontilla.ryanair.client.service;

import eu.immontilla.ryanair.client.model.Schedule;

/**
 * Service to get the schedules from the Schedules API
 * 
 * @author immontilla
 */
public interface ScheduleFinderService {
    Schedule get(String from, String to, int month, int year);
}
