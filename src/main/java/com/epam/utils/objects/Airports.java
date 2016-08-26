package com.epam.utils.objects;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Airports {
    private static final ArrayListMultimap<String, List<String>> AIRPORTS_GROUPS = ArrayListMultimap.create();
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

        AIRPORTS_GROUPS.put("BAK", Lists.newArrayList("ZXT", "GYD"));
        AIRPORTS_GROUPS.put("BHZ", Lists.newArrayList("CNF", "PLU"));
        AIRPORTS_GROUPS.put("BJS", Lists.newArrayList("NAY", "PEK"));
        AIRPORTS_GROUPS.put("BUE", Lists.newArrayList("AEP", "EZE"));
        AIRPORTS_GROUPS.put("BUH", Lists.newArrayList("BBU", "OTP"));
        AIRPORTS_GROUPS.put("CHI", Lists.newArrayList("DPA", "GYY", "CGX", "MDW", "ORD", "RFD", "PWK"));
        AIRPORTS_GROUPS.put("DTT", Lists.newArrayList("DET", "YIP", "DTW"));
        AIRPORTS_GROUPS.put("IZM", Lists.newArrayList("IGL", "ADB"));
        AIRPORTS_GROUPS.put("JKT", Lists.newArrayList("HLP", "CGK"));
        AIRPORTS_GROUPS.put("LON", Lists.newArrayList("LTN", "LCY", "SEN", "STN", "LHR", "LGW"));
        AIRPORTS_GROUPS.put("MIL", Lists.newArrayList("MXP", "BGY", "LIN", "PMF"));
        AIRPORTS_GROUPS.put("MMA", Lists.newArrayList("JMM", "MMX"));
        AIRPORTS_GROUPS.put("MOW", Lists.newArrayList("BKA", "SVO", "DME", "VKO"));
        AIRPORTS_GROUPS.put("NYC", Lists.newArrayList("FLU", "JRA", "JRB", "JRE", "TSS", "EWR", "JFK", "LGA"));
        AIRPORTS_GROUPS.put("OSA", Lists.newArrayList("ITM", "KIX", "UKB"));
        AIRPORTS_GROUPS.put("PAR", Lists.newArrayList("LBG", "XCR", "POX", "JDP", "JPU", "VIY", "ORY", "CDG", "BVA"));
        AIRPORTS_GROUPS.put("REK", Lists.newArrayList("RKV", "KEF"));
        AIRPORTS_GROUPS.put("RIO", Lists.newArrayList("GIG", "SDU"));
        AIRPORTS_GROUPS.put("ROM", Lists.newArrayList("CIA", "FCO"));
        AIRPORTS_GROUPS.put("SAO", Lists.newArrayList("GRU", "VCP", "CGH"));
        AIRPORTS_GROUPS.put("SDZ", Lists.newArrayList("SCS", "LSI", "LWK"));
        AIRPORTS_GROUPS.put("SEL", Lists.newArrayList("SSN", "ICN", "GMP"));
        AIRPORTS_GROUPS.put("SPK", Lists.newArrayList("OKD", "CTS"));
        AIRPORTS_GROUPS.put("STO", Lists.newArrayList("VST", "BMA", "ARN", "NYO"));
        AIRPORTS_GROUPS.put("TCI", Lists.newArrayList("TFN", "TFS"));
        AIRPORTS_GROUPS.put("TYO", Lists.newArrayList("OKO", "NRT", "HND"));
        AIRPORTS_GROUPS.put("WAS", Lists.newArrayList("BOF", "JPN", "DCA", "IAD"));
        AIRPORTS_GROUPS.put("YEA", Lists.newArrayList("YED", "YXD", "YEG"));
        AIRPORTS_GROUPS.put("YMQ", Lists.newArrayList("YMX", "YUL", "YHU"));
        AIRPORTS_GROUPS.put("YTO", Lists.newArrayList("YKZ", "YTZ", "YHM", "YYZ", "YKF"));

        AIRPORTS_GROUPS.put("Midlands", Lists.newArrayList("BHX", "CVT", "EMA", "NWI"));
        AIRPORTS_GROUPS.put("North East", Lists.newArrayList("HUY", "LBA", "MME", "NCL"));
        AIRPORTS_GROUPS.put("North West", Lists.newArrayList("BLK", "LBA", "LPL", "MAN"));
        AIRPORTS_GROUPS.put("Northern Ireland", Lists.newArrayList("BFS"));
        AIRPORTS_GROUPS.put("Scotland", Lists.newArrayList("ABZ", "EDI", "GLA", "INV", "PIK"));
        AIRPORTS_GROUPS.put("South East", Lists.newArrayList("BOH", "LGW", "LHR", "LTN", "SOU", "STN"));
        AIRPORTS_GROUPS.put("South West and Wales", Lists.newArrayList(""));

        AIRPORTS_GROUPS.put("NONE_GROUPS", Lists.newArrayList("EXT"));
    }

    public static List<String> getAirportsGroupsByChild(String child) {
        checkNotNull(child);

        for (Map.Entry<String, List<String>> entry : AIRPORTS_GROUPS.entries()) {
            if (entry.getValue().contains(child.toUpperCase())) {
                return entry.getValue();
            }
        }
        return Lists.newArrayList();
    }

    public static List<String> getAirportsGroups(String key) {
        checkNotNull(key);
        List<List<String>> result = AIRPORTS_GROUPS.get(key.toUpperCase());
        return (result == null || result.isEmpty()) ? Collections.<String>emptyList() : result.get(0);
    }

    public static List<String> getAirportsGroupsByNumber(String number) {
        checkNotNull(number);
        String airport = GROUP_BY_NUMBER.get(number);
        checkNotNull(airport);
        return getAirportsGroups(airport);
    }

    public static String getAirportByNumber(String number) {
        checkNotNull(number);
        return GROUP_BY_NUMBER.get(number);
    }
}
