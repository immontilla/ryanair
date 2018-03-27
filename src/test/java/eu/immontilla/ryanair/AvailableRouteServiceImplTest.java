package eu.immontilla.ryanair;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.service.AvailableRouteService;
import eu.immontilla.ryanair.client.service.impl.AvailableRouteServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AvailableRouteServiceImplTest {

    @TestConfiguration
    static class AvailableRouteServiceImplImplTestContextConfiguration {
        @Bean
        public AvailableRouteServiceImpl availableRouteServiceImpl() {
            return new AvailableRouteServiceImpl();
        }
    }

    @Autowired
    private AvailableRouteService availableRouteService;

    @Test
    public void whenLookingForRoutesThenReturnANonEmptyList() {
        List<Route> routes = availableRouteService.getAll();
        assertEquals(routes.isEmpty(), false);
    }
    
    
    
}
