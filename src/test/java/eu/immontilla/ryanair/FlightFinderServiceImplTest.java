package eu.immontilla.ryanair;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.service.AvailableRouteService;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.service.FlightFinderService;
import eu.immontilla.ryanair.service.impl.FlightFinderServiceImpl;

@RunWith(SpringRunner.class)
public class FlightFinderServiceImplTest {
    private static final String MAD = "MAD";
    private static final String DUB = "DUB";
    private static final String LIS = "LIS";

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
        @Bean
        public FlightFinderService flightFinderService() {
            return new FlightFinderServiceImpl();
        }
    }

    @Autowired
    private FlightFinderService flightFinderService;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tomorrow = now.plusDays(1);
    LocalDateTime afterTomorrow = now.plusDays(2);

    @MockBean
    private AvailableRouteService availableRouteService;
    @MockBean
    private ScheduleFinderService scheduleFinderService;

    @Before
    public void setUp() {
        List<Route> routes = new ArrayList<Route>();
        routes.add(new Route("CRL", "PRG", null, false, false, "RYANAIR", "ETHNIC"));
        routes.add(new Route(MAD, LIS, null, false, false, "RYANAIR", "UKP"));
        routes.add(new Route("LGW", "DUB", null, false, false, "RYANAIR", "UKP"));
        routes.add(new Route("PSR", "SXF", "BGY", false, false, "RYANAIR", "GENERIC"));
        Mockito.when(availableRouteService.getAll()).thenReturn(routes);
    }

    @Test
    public void whenLookForFlightNotInRouteThenReturnEmptyList() {
        List<FlightResult> flights = flightFinderService.findFlights(MAD, DUB, tomorrow, afterTomorrow);
        assertTrue(flights.isEmpty());
    }

}
