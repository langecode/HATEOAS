package dk.nykredit.api.capabilities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.StringTokenizer;

import dk.nykredit.time.CurrentTime;

/**
 * A time interval with a start and end time.
 * The interval will always start before it ends. The start is exclusive and the end is inclusive.
 * <p>
 * Temporal aspects are handled using the interval Query Parameter.
 *
 * The syntax is:
 * {@literal(interval="<now/from/to/at/::+/-/#d/#/now>|
 * <now/from/to/at/::+/-/#d/#>")}
 * </p>
 *
 * <p>
 * Example:
 * <code>https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="from::-14d|to::now"</code>
 * </p>
 * which returns the transactions from a specific account within the last 14 days
 * Another example:
 * <code>
 * https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="from::1476449846|to::now"
 *
 * https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="from::1476449846"
 *
 * https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="at::1476449846"
 * </code>
 *
 * <p>
 * The latter three return the transactions from a specific account
 * within the last day assuming now is Friday the 14th of October 2016 UTC
 * </p>
 * This supports the use of CurrentTime and its virtual time set.
 */
public class Interval {

    private static final int DEFAULT_TIME_SPAN = 4;
    private final ZonedDateTime start;
    private final ZonedDateTime end;

    private Interval(ZonedDateTime start) {
        this(start, CurrentTime.nowAsZonedDateTime());
    }

    private Interval(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @return a time in UTC as the starting time of the interval
     *
     */
    public ZonedDateTime getStart() {
        return start;
    }

    /**
     * @return a time in UTC as the endtime of the interval
     *
     */
    public ZonedDateTime getEnd() {
        return end;
    }

    /**
     * Creates an Interval having a start and an end.
     *
     * The interval is constructed from a syntax like:
     *
     * {@literal(interval="<from/at/::+/-/#d/#/now/yesterday/tomorrow>|<to/::+/-/#d/#/now/yesterday/tomorrow>")}
     * the regexp is:
     * <code>"^(from::|at::)?(-|\\+)?(\\d+d?|now|yesterday|tomorrow)?(\\|)?(to::)?(-|\\+)?(\\d+d?|now|yesterday|tomorrow)?"</code>
     *
     * @param interval containing a time or a starting and an ending point in time according to the regexp above
     * @return an Interval with a start and an end, is the values are nor valid a NILL instance is returned
     */
    public static Optional<Interval> getInterval(String interval) {
        if (null == interval) {
            return Optional.empty();
        }
        if (!interval.matches("^(from::|at::)?(-|\\+)?(\\d+d?|now|yesterday|tomorrow)?(\\|)?(to::)?(-|\\+)?(\\d+d?|now|yesterday|tomorrow)?")) {
            return Optional.empty();
        }
        String result = Sanitizer.sanitize(interval, true, true);
        StringTokenizer timePoints = new StringTokenizer(result, "|", false);
        int pit = timePoints.countTokens();
        if (invalidIntervalInput(pit)) {
            return Optional.empty();
        }
        return createValidInterval(timePoints);
    }

    private static Optional<Interval> createValidInterval(StringTokenizer timePoints) {
        String startPoint = timePoints.nextToken();
        String start = getAttribute(startPoint);
        ZonedDateTime zds = getZonedDateTime(startPoint, start, startPoint.contains("at::"));
        if (null == zds) {
            return Optional.empty();
        }
        if (timePoints.hasMoreTokens()) {
            String endPoint = timePoints.nextToken();
            String end = getValue(endPoint);
            ZonedDateTime zde = getZonedDateTime(endPoint, end, startPoint.contains("at::"), zds);
            return Optional.of(new Interval(zds, zde));
        } else {
            if (startPoint.contains("from::"))
                return Optional.of(new Interval(zds, CurrentTime.nowAsZonedDateTime()));
            if (startPoint.contains("at::"))
                return Optional.of(new Interval(zds, zds.plusHours(DEFAULT_TIME_SPAN)));
        }
        return Optional.empty();
    }

    private static ZonedDateTime getZonedDateTime(String startPoint, String start, boolean relative, ZonedDateTime zds) {
        ZonedDateTime zd = zds;
        if (!"".equals(start)) {
            String time = getValue(startPoint);
            zd = ifSignedFormat(zd, time, relative);
            zd = ifUnsignedFormat(zd, time, relative);
            zd = ifLongNumber(zd, time);
            zd = ifTextual(zd, time);
        }
        return zd;
    }

    private static ZonedDateTime ifUnsignedFormat(ZonedDateTime zd, String time, boolean relative) {
        if (time.matches("^\\d+d")) {
            int len = time.length() - 1;
            long offset = Integer.parseInt(time.substring(0, len));
            zd = relative ? zd.withZoneSameInstant(ZoneId.of("UTC")).plusDays(offset) : CurrentTime.nowAsZonedDateTime().plusDays(offset);
        }
        return zd;
    }

    private static ZonedDateTime ifSignedFormat(ZonedDateTime zd, String time, boolean relative) {
        if (time.matches("^[-|\\+]+\\d+d")) {
            int len = time.length() - 1;
            long offset = Integer.parseInt(time.substring(1, len));
            ZonedDateTime utc = relative ? zd.withZoneSameInstant(ZoneId.of("UTC")) : CurrentTime.nowAsZonedDateTime();
            if ((time.charAt(0) == '-') && (time.charAt(len) == 'd')) {
                zd = utc.minusDays(offset);
            } else {
                zd = utc.plusDays(offset);
            }
        }
        return zd;
    }

    private static ZonedDateTime ifLongNumber(ZonedDateTime zd, String time) {
        if (time.matches("^\\d+")) {
            int len = time.length();
            long offset = Long.parseLong(time.substring(0, len));
            Instant i = Instant.ofEpochMilli(offset);
            zd = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"));
        }
        return zd;
    }

    private static ZonedDateTime ifTextual(ZonedDateTime zd, String time) {
        if (time.matches("now|tomorrow|yesterday")) {
            if (time.contains("now")) {
                zd = CurrentTime.nowAsZonedDateTime();
            }
            if (time.contains("tomorrow")) {
                zd = CurrentTime.nowAsZonedDateTime().plusDays(1);
            }
            if (time.contains("yesterday")) {
                zd = CurrentTime.nowAsZonedDateTime().minusDays(1);
            }
        }
        return zd;
    }

    private static String getValue(String timePoint) {
        int startsAt = timePoint.indexOf("::") + "::".length();
        int endsAt = timePoint.indexOf('|') > 0 ? timePoint.indexOf('|') : timePoint.length();
        return timePoint.substring(startsAt, endsAt);

    }

    private static String getAttribute(String timePoint) {
        int end = timePoint.indexOf(':');
        if (end > 0) {
            return timePoint.substring(0, end);
        }
        return "";
    }

    private static ZonedDateTime getZonedDateTime(String startPoint, String start, boolean relative) {
        return getZonedDateTime(startPoint, start, relative, null);
    }

    private static boolean invalidIntervalInput(int pit) {
        return (pit < 1) || (pit > 2);
    }

}
