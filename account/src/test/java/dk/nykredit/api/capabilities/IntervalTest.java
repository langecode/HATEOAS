package dk.nykredit.api.capabilities;


import dk.nykredit.time.CurrentTime;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntervalTest {


    @Test
    public void testNoInterval() {
        Optional<Interval> interval = Interval.getInterval(null);
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("");
        assertEquals(Optional.empty(), interval);
    }

    @Test
    public void testIllegalInterval() {
        Optional<Interval> interval = Interval.getInterval("from::then|to::now");
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("from::then|to::sometime");
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("from::-0");
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("f|om::-0");
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("f'om::-0");
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("f%om::-0");
        assertEquals(Optional.empty(), interval);
        interval = Interval.getInterval("from::-0|to:45");
        assertEquals(Optional.empty(), interval);
    }

    @Test
    public void testInterval() {
        VirtualCurrentTime.adjustTime(Instant.ofEpochMilli(1481987229L), true);

        Optional<Interval> interval = Interval.getInterval("from::-14d|to::now");
        ZonedDateTime zd = CurrentTime.nowAsZonedDateTime().minusDays(14);
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth());

        zd = CurrentTime.nowAsZonedDateTime();
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth());


        interval = Interval.getInterval("from::1476449846|to::now");

        Instant i = Instant.ofEpochMilli(1476449846L);
        zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth());

        zd = CurrentTime.nowAsZonedDateTime();
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        interval = Interval.getInterval("from::1481987229|to::+10d");
        i = Instant.ofEpochMilli(1481987229L);
        zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = CurrentTime.nowAsZonedDateTime().plusDays(10);
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        interval = Interval.getInterval("at::1476449846|to::1d");
        i = Instant.ofEpochMilli(1476449846L);
        zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = zd.plusDays(1);
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        interval = Interval.getInterval("from::yesterday|to::tomorrow");
        zd = CurrentTime.nowAsZonedDateTime().minusDays(1);
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = CurrentTime.nowAsZonedDateTime().plusDays(1);
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        interval = Interval.getInterval("from::yesterday|to::now");
        zd = CurrentTime.nowAsZonedDateTime().minusDays(1);
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = CurrentTime.nowAsZonedDateTime();
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        interval = Interval.getInterval("from::-12d|to::+2d");
        zd = CurrentTime.nowAsZonedDateTime().minusDays(12);
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = CurrentTime.nowAsZonedDateTime().plusDays(2);
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight


        interval = Interval.getInterval("from::1481987229");
        i = Instant.ofEpochMilli(1481987229L);
        zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = CurrentTime.nowAsZonedDateTime();
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        interval = Interval.getInterval("from::0");
        i = Instant.ofEpochMilli(0L);
        zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        assertEquals(zd.getYear(), interval.get().getStart().getYear());
        assertEquals(zd.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        zd = CurrentTime.nowAsZonedDateTime();
        assertEquals(zd.getYear(), interval.get().getEnd().getYear());
        assertEquals(zd.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zd.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        LocalDateTime ldt = LocalDateTime.of(2016, 5, 29, 12, 0, 0);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
        long millis = zdt.toInstant().toEpochMilli();
        interval = Interval.getInterval("at::" + String.valueOf(millis));

        assertEquals(zdt.getYear(), interval.get().getStart().getYear());
        assertEquals(zdt.getMonth(), interval.get().getStart().getMonth());
        assertEquals(zdt.getDayOfMonth(), interval.get().getStart().getDayOfMonth()); //may be troublesome when run over midnight

        assertEquals(zdt.getYear(), interval.get().getEnd().getYear());
        assertEquals(zdt.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zdt.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight
    }

    @Test
    public void testTheVirtualTimeCrossYear(){
        VirtualCurrentTime.adjustTime(CurrentTime.now(),true);
        ZonedDateTime zdnow = CurrentTime.nowAsZonedDateTime();
        Optional<Interval> interval = Interval.getInterval("from::-14d|to::now");
        assertEquals(zdnow.getYear(), interval.get().getEnd().getYear());
        assertEquals(zdnow.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zdnow.getDayOfMonth(), interval.get().getEnd().getDayOfMonth()); //may be troublesome when run over midnight

        assertTrue(zdnow.isEqual(interval.get().getEnd()));
        assertTrue(zdnow.minusDays(14).isEqual(interval.get().getStart()));

        LocalDateTime ldt = LocalDateTime.of(2016, 1, 6, 8, 5, 10, 0);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
        VirtualCurrentTime.adjustTime(zdt.toInstant(), true);
        interval = Interval.getInterval("from::-14d|to::now");

        assertTrue(zdt.isEqual(interval.get().getEnd()));
        assertTrue(zdt.minusDays(14).isEqual(interval.get().getStart()));
        assertEquals(2015, interval.get().getStart().getYear());
        assertEquals(2016, interval.get().getEnd().getYear());
        assertEquals(12, interval.get().getStart().getMonthValue());
        assertEquals(1, interval.get().getEnd().getMonthValue());
        assertEquals(23, interval.get().getStart().getDayOfMonth());
        assertEquals(6, interval.get().getEnd().getDayOfMonth());
        VirtualCurrentTime.stop();

        zdnow = CurrentTime.nowAsZonedDateTime();
        interval = Interval.getInterval("from::-14d|to::now");
        assertEquals(zdnow.getYear(), interval.get().getEnd().getYear());
        assertEquals(zdnow.getMonth(), interval.get().getEnd().getMonth());
        assertEquals(zdnow.getDayOfMonth(), interval.get().getEnd().getDayOfMonth());
        assertTrue(zdnow.toEpochSecond() - interval.get().getEnd().toEpochSecond() < 1000);
        assertTrue(zdnow.minusDays(14).toEpochSecond() - interval.get().getStart().toEpochSecond() < 1000);
    }

}

class VirtualCurrentTime extends CurrentTime {

    static void adjustTime(Instant instant, boolean stopTime){
        CurrentTime.setTime(instant, stopTime);
    }

    static void stop() {
        CurrentTime.reset();
    }

}