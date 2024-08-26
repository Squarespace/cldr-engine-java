package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.calendars.DayOfWeek;

public class GregorianDateTest {

	private static final CLDR EN = CLDR.get("en");
	private static final String UTC = "Etc/UTC";
	private static final String NEW_YORK = "America/New_York";

	private static final long unixEpochFromJD(long jd, long msDay) {
		long days = jd - CalendarConstants.JD_UNIX_EPOCH;
		return days * CalendarConstants.ONE_DAY_MS + Math.round(msDay);
	}

	private static final CalendarDate make(long epoch, String zoneId) {
		return GregorianDate.fromUnixEpoch(epoch, zoneId, DayOfWeek.SUNDAY, 1);
	}

	@Test
	public void testMinMaxClamp() {
		CalendarDate d;

		long min = unixEpochFromJD(CalendarConstants.JD_MIN, 0);
		long max = unixEpochFromJD(CalendarConstants.JD_MAX, 0);

		d = make(min, "UTC");
		assertEquals(d.toString(), "Gregorian -4712-01-01 00:00:00.000 Etc/UTC");

		// Clamp to minimum date
		d = make(min - CalendarConstants.ONE_DAY_MS, "UTC");
		assertEquals(d.toString(), "Gregorian -4712-01-01 00:00:00.000 Etc/UTC");

		d = make(max, "UTC");
		assertEquals(d.toString(), "Gregorian 8652-12-31 00:00:00.000 Etc/UTC");

		d = make(max + CalendarConstants.ONE_DAY_MS, "UTC");
		assertEquals(d.toString(), "Gregorian 8652-12-31 00:00:00.000 Etc/UTC");
	}

}
