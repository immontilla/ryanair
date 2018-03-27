package eu.immontilla.ryanair;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.client.service.impl.ScheduleFinderServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleFinderServiceImplTest {
    private static final String MAD = "MAD";
    private static final String DUB = "DUB";

    @TestConfiguration
    static class ScheduleFinderServiceImplTestContextConfiguration {
        @Bean
        public ScheduleFinderService scheduleFinderService() {
            return new ScheduleFinderServiceImpl();
        }
    }

    @Autowired
    private ScheduleFinderService scheduleFinderService;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tomorrow = now.plusDays(1);

    @Test
    public void aSimpleTest() {
        int month = tomorrow.getMonthValue();
        int year = tomorrow.getYear();
        Schedule schedule = scheduleFinderService.get(MAD, DUB, month, year);
        assertEquals(schedule.getMonth(), month);
    }
}
