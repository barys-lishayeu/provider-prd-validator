package com.epam.utils.objects;

import java.util.ArrayList;
import java.util.Collections;

public class Mappings extends ArrayList<String> implements AsArray {

    public Mappings(String[] values) {
        Collections.addAll(this, values);
    }

    @Override
    public String[] asArray() {
        return this.toArray(new String[this.size()]);
    }
}
