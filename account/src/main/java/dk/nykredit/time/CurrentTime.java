package dk.nykredit.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * A very simple way of controlling current time.
 *
 * can be used in tests for avoiding time related challenges and thus remove false positives in test.
 * to be used remotely the remote instance must expose an endpoint which allows the support of this feature.
 */

public class CurrentTime {

    private static ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC"));

    protected CurrentTime() {
        // no construction necessary except from derived usages
    }

    /**
     * @return current time to in UTC time, the time can be real or set as "virtual" current time
     */
    public static ZonedDateTime now() {
        return time;
    }

    protected static void setTime(Instant instant) {
        time = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    protected static void reset() {
        time = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
