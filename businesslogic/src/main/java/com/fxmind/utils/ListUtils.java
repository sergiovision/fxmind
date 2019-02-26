package com.fxmind.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 */
public class ListUtils {


    /**
     * Filters any kinds of collections that implements.
     * interface
     * @param enumeration to be filtered; to obtain an enumeration,.
     *             use objectEnumerator() for the collections
     *             and use elements() for the Vector and Hashtable
     * @param qualifier to do the filtering
     * @return array of filtered results.
     */
    public static <T> List<T> filteredWithQualifierEvaluation(Iterator<T> enumeration, Comparable qualifier) {

        ArrayList<T> result = new ArrayList<T>();
        while (enumeration.hasNext()) {
            T object = enumeration.next();
            if (qualifier.compareTo(object) == 0)
               result.add(object);
        }
        return result;
    }
}
