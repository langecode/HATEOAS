package dk.nykredit.time;


import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CurrentTimeTest {


    @Test
    public void testUnRealTime(){
        Instant i = Instant.ofEpochMilli(100000);
        ZonedDateTime zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        CurrentTime.setTime(i);
        assertEquals(zd, CurrentTime.now());
        assertNotEquals(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond(), CurrentTime.now().toEpochSecond());
   }

   @Test
    public void testApproximatelyNearRealtime (){
       Instant i = Instant.ofEpochMilli(100000);
       ZonedDateTime zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
       assertTrue(CurrentTime.now().toEpochSecond() - zd.toEpochSecond() < 1000 );
       assertNotEquals(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond(), CurrentTime.now().toEpochSecond());
   }

}
