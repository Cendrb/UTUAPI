package com.farast.utuapi.util;

import com.farast.utuapi.data.interfaces.Identifiable;
import com.farast.utuapi.util.functional_interfaces.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public final class CollectionUtil {
    private CollectionUtil() {

    }

    public static <T> ArrayList<T> filter(Collection<T> collection, Predicate<T> predicate) {
        ArrayList<T> arrayList = new ArrayList<>(collection);
        Iterator<T> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!predicate.test(item))
                iterator.remove();
        }
        return arrayList;
    }

    public static <T extends Identifiable> T findById(Collection<T> collection, final int id) throws RecordNotFoundException, MultipleRecordsWithSameIdException {
        ArrayList<T> filtered = filter(collection, new Predicate<T>() {
            @Override
            public boolean test(T object) {
                return object.getId() == id;
            }
        });
        if (filtered.size() < 1)
            throw new RecordNotFoundException(id, collection);
        else if (filtered.size() > 1)
            throw new MultipleRecordsWithSameIdException(collection);
        else
            return filtered.get(0);
    }

    public static <T extends Identifiable> ArrayList<T> findByIds(Collection<T> collection, final Collection<Integer> ids) throws RecordNotFoundException, MultipleRecordsWithSameIdException {
        ArrayList<T> items = new ArrayList<>();
        for (Integer id : ids) {
            items.add(findById(collection, id));
        }

        return items;
    }

    public static class RecordNotFoundException extends RuntimeException {
        private final int recordId;
        private Collection collection;

        private RecordNotFoundException(int recordId, Collection collection) {
            this.recordId = recordId;
            this.collection = collection;
        }

        @Override
        public String getMessage() {
            return String.format("Record with id %1s doesn't exist in collection of type %2s", recordId, collection.getClass());
        }

        public int getRecordId() {
            return recordId;
        }

        public Collection getCollection() {
            return collection;
        }
    }

    public static class MultipleRecordsWithSameIdException extends RuntimeException {
        private Collection collection;

        private MultipleRecordsWithSameIdException(Collection collection) {
            this.collection = collection;
        }

        @Override
        public String getMessage() {
            return String.format("Collection of type %s contains multiple records with the same id", collection.getClass());
        }
    }
}
