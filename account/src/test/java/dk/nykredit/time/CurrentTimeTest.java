package dk.nykredit.time;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CurrentTimeTest {


    @Test
    public void testUnRealTime(){
        Instant i = Instant.ofEpochMilli(100000);
        ZonedDateTime zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        CurrentTime.setTime(i, true);
        assertEquals(zd, CurrentTime.nowAsZonedDateTime());
        assertNotEquals(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond(), CurrentTime.nowAsZonedDateTime().toEpochSecond());
    }

    @Test
    public void testStoppedTime() throws InterruptedException {
        Instant i = Instant.ofEpochMilli(100000);
        ZonedDateTime zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        CurrentTime.setTime(i, true);
        assertEquals(zd, CurrentTime.nowAsZonedDateTime());
        assertNotEquals(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond(), CurrentTime.nowAsZonedDateTime().toEpochSecond());
        Thread.sleep(5);
        assertEquals(zd, CurrentTime.nowAsZonedDateTime());
    }

    @Test
    public void testStartAndStoppedTime() throws InterruptedException {
        Instant i = Instant.ofEpochMilli(100000);
        ZonedDateTime zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        CurrentTime.setTime(i, true);
        assertEquals(zd, CurrentTime.nowAsZonedDateTime());
        assertNotEquals(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond(), CurrentTime.nowAsZonedDateTime().toEpochSecond());
        Thread.sleep(5);
        CurrentTime.stopTime();
        Instant instant = CurrentTime.now();
        Thread.sleep(5);
        assertEquals(instant.getEpochSecond(), CurrentTime.now().getEpochSecond());
        CurrentTime.startTime();
        Thread.sleep(5);
        assertTrue(zd.isBefore(CurrentTime.nowAsZonedDateTime()));
    }

    @Test
    public void testRunningTime() throws InterruptedException {
        Instant i = Instant.ofEpochMilli(100000);
        ZonedDateTime zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        CurrentTime.setTime(i, false);
        Thread.sleep(5);
        assertTrue(zd.isBefore(CurrentTime.nowAsZonedDateTime()));
    }

}
