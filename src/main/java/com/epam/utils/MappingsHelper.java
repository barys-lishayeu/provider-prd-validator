package com.epam.utils;

import com.epam.utils.objects.Mappings;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javafx.util.Pair;

import java.util.List;
import java.util.Set;

public class MappingsHelper {
    /**
     * 1st unic values, 2nd - duplicate
     *
     * @param mappings mappings
     * @return 1st unic values, 2nd - duplicate
     */
    protected Pair<Set<Mappings>, List<Mappings>> deduplicateMappings(ListMultimap<String, Mappings> mappings) {
        List<Mappings> duplicated = Lists.newArrayList();
        Set<Mappings> unicValues = Sets.newHashSet();

        for (String key : mappings.keySet()) {
            List<Mappings> list = mappings.get(key);
            unicValues.add(list.get(0));
            if (list.size() > 1) {
                int size = list.size();
                for (int i = size - 1; i > 0; i--) {
                    duplicated.add(list.remove(i));
                }
            }
        }
        return new Pair<>(unicValues, duplicated);
    }

}
