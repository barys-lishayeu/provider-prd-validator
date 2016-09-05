package com.tsm.prd.objects;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Airports {
    private static final ArrayListMultimap<String, String> AIRPORTS_GROUPS = ArrayListMultimap.create();
    private static final Map<String, String> GROUP_BY_NUMBER = new HashMap<>();

    static {
        GROUP_BY_NUMBER.put("2", "LON");
        GROUP_BY_NUMBER.put("3", "Midlands");
        GROUP_BY_NUMBER.put("4", "North East");
        GROUP_BY_NUMBER.put("5", "North West");
        GROUP_BY_NUMBER.put("6", "Northern Ireland");
        GROUP_BY_NUMBER.put("7", "Scotland");
        GROUP_BY_NUMBER.put("8", "South East");
        GROUP_BY_NUMBER.put("9", "South West and Wales");

        AIRPORTS_GROUPS.putAll("BAK", Lists.newArrayList("ZXT", "GYD"));
        AIRPORTS_GROUPS.putAll("BHZ", Lists.newArrayList("CNF", "PLU"));
        AIRPORTS_GROUPS.putAll("BJS", Lists.newArrayList("NAY", "PEK"));
        AIRPORTS_GROUPS.putAll("BUE", Lists.newArrayList("AEP", "EZE"));
        AIRPORTS_GROUPS.putAll("BUH", Lists.newArrayList("BBU", "OTP"));
        AIRPORTS_GROUPS.putAll("CHI", Lists.newArrayList("DPA", "GYY", "CGX", "MDW", "ORD", "RFD", "PWK"));
        AIRPORTS_GROUPS.putAll("DTT", Lists.newArrayList("DET", "YIP", "DTW"));
        AIRPORTS_GROUPS.putAll("IZM", Lists.newArrayList("IGL", "ADB"));
        AIRPORTS_GROUPS.putAll("JKT", Lists.newArrayList("HLP", "CGK"));
        AIRPORTS_GROUPS.putAll("LON", Lists.newArrayList("LTN", "LCY", "SEN", "STN", "LHR", "LGW"));
        AIRPORTS_GROUPS.putAll("MIL", Lists.newArrayList("MXP", "BGY", "LIN", "PMF"));
        AIRPORTS_GROUPS.putAll("MMA", Lists.newArrayList("JMM", "MMX"));
        AIRPORTS_GROUPS.putAll("MOW", Lists.newArrayList("BKA", "SVO", "DME", "VKO"));
        AIRPORTS_GROUPS.putAll("NYC", Lists.newArrayList("FLU", "JRA", "JRB", "JRE", "TSS", "EWR", "JFK", "LGA"));
        AIRPORTS_GROUPS.putAll("OSA", Lists.newArrayList("ITM", "KIX", "UKB"));
        AIRPORTS_GROUPS.putAll("PAR", Lists.newArrayList("LBG", "XCR", "POX", "JDP", "JPU", "VIY", "ORY", "CDG", "BVA"));
        AIRPORTS_GROUPS.putAll("REK", Lists.newArrayList("RKV", "KEF"));
        AIRPORTS_GROUPS.putAll("RIO", Lists.newArrayList("GIG", "SDU"));
        AIRPORTS_GROUPS.putAll("ROM", Lists.newArrayList("CIA", "FCO"));
        AIRPORTS_GROUPS.putAll("SAO", Lists.newArrayList("GRU", "VCP", "CGH"));
        AIRPORTS_GROUPS.putAll("SDZ", Lists.newArrayList("SCS", "LSI", "LWK"));
        AIRPORTS_GROUPS.putAll("SEL", Lists.newArrayList("SSN", "ICN", "GMP"));
        AIRPORTS_GROUPS.putAll("SPK", Lists.newArrayList("OKD", "CTS"));
        AIRPORTS_GROUPS.putAll("STO", Lists.newArrayList("VST", "BMA", "ARN", "NYO"));
        AIRPORTS_GROUPS.putAll("TCI", Lists.newArrayList("TFN", "TFS"));
        AIRPORTS_GROUPS.putAll("TYO", Lists.newArrayList("OKO", "NRT", "HND"));
        AIRPORTS_GROUPS.putAll("WAS", Lists.newArrayList("BOF", "JPN", "DCA", "IAD"));
        AIRPORTS_GROUPS.putAll("YEA", Lists.newArrayList("YED", "YXD", "YEG"));
        AIRPORTS_GROUPS.putAll("YMQ", Lists.newArrayList("YMX", "YUL", "YHU"));
        AIRPORTS_GROUPS.putAll("YTO", Lists.newArrayList("YKZ", "YTZ", "YHM", "YYZ", "YKF"));

        AIRPORTS_GROUPS.putAll("Midlands", Lists.newArrayList("BHX", "CVT", "EMA", "NWI"));
        AIRPORTS_GROUPS.putAll("North East", Lists.newArrayList("HUY", "LBA", "MME", "NCL"));
        AIRPORTS_GROUPS.putAll("North West", Lists.newArrayList("BLK", "LBA", "LPL", "MAN"));
        AIRPORTS_GROUPS.putAll("Northern Ireland", Lists.newArrayList("BFS", "BHD", "DUB"));
        AIRPORTS_GROUPS.putAll("Scotland", Lists.newArrayList("ABZ", "EDI", "GLA", "INV", "PIK"));
        AIRPORTS_GROUPS.putAll("South East", Lists.newArrayList("BOH", "LGW", "LHR", "LTN", "SOU", "STN"));
        AIRPORTS_GROUPS.putAll("South West and Wales", Lists.newArrayList("EXT", "BRS", "CWL"));

        AIRPORTS_GROUPS.putAll("NONE_GROUPS", Lists.newArrayList(""));
    }

    public static boolean isSameAirportGroupOrSameAirport(String departureOne, String departureTwo) {
        if (departureOne.equals(departureTwo)) {
            return true;
        }
        List<String> oneGroup = getAirportsGroupsName(departureOne);
        List<String> twoGroup = getAirportsGroupsName(departureTwo);
        return oneGroup.contains(departureTwo.toUpperCase()) || twoGroup.contains(departureOne.toUpperCase());
    }

    public static boolean isAirportGroup(final String airportForCheck) {
        for (final String airport : GROUP_BY_NUMBER.values()) {
            if (airport.equalsIgnoreCase(airportForCheck.trim())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAirportsGroupsByChild(String child) {
        checkNotNull(child);

        for (Map.Entry<String, String> entry : AIRPORTS_GROUPS.entries()) {
            if (entry.getValue().contains(child.toUpperCase())) {
                return AIRPORTS_GROUPS.get(entry.getKey());
            }
        }
        return Lists.newArrayList();
    }

    public static List<String> getAirportsGroupsName(String name) {
        checkNotNull(name);
        for (String groupName : AIRPORTS_GROUPS.keySet()) {
            if (groupName.equalsIgnoreCase(name)) {
                return AIRPORTS_GROUPS.get(groupName);
            }
        }
        return Collections.<String>emptyList();
    }

    public static List<String> getAirportsGroupsByNumber(String number) {
        checkNotNull(number);
        String airport = GROUP_BY_NUMBER.get(number);
        checkNotNull(airport);
        return getAirportsGroupsName(airport);
    }

    public static String getAirportByNumber(String number) {
        checkNotNull(number);
        return GROUP_BY_NUMBER.get(number);
    }
}
