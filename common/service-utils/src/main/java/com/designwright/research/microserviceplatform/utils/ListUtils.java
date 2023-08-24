package com.designwright.research.microserviceplatform.utils;

public class ListUtils {

    private ListUtils() {

    }

    public static boolean isEmpty(Iterable<?> iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }
}
