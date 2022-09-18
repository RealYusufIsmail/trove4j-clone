/*
 * Copyright (c) 2022, Rob Eden, RealYusufIsmail All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ 
package gnu.trove.decorator;

import gnu.trove.TDecorators;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test the Object key/primitive value map decorators
 *
 * @author Eric D. Friedman
 * @author Robert D. Eden
 * @author Jeff Randall
 * @author Jim Davies
 * @author Yusuf A. Ismail
 */
public class TObjectPrimitiveMapDecoratorTest {

    @Test
    public void testConstructorWithNull() {
        boolean expectionThrown = false;
        try {
            TDecorators.wrap((TObjectIntMap<String>) null);
        } catch (NullPointerException ignored) {
            expectionThrown = true;
        }

        assertTrue(expectionThrown,
                "Wrapping a null value should result in an expection being thrown.");
    }

    @Test
    public void testConstructors() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            raw_map.put(keys[i], vals[i]);
        }
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        TObjectIntHashMap<String> raw_capacity = new TObjectIntHashMap<>(20);
        for (int i = 0; i < element_count; i++) {
            raw_capacity.put(keys[i], vals[i]);
        }
        Map<String, Integer> capacity = TDecorators.wrap(raw_capacity);
        assertEquals(raw_map, raw_capacity);
        assertEquals(map, capacity);

        TObjectIntHashMap<String> raw_cap_and_factor = new TObjectIntHashMap<>(20, 0.75f);
        for (int i = 0; i < element_count; i++) {
            raw_cap_and_factor.put(keys[i], vals[i]);
        }
        Map<String, Integer> cap_and_factor = TDecorators.wrap(raw_cap_and_factor);
        assertEquals(raw_map, raw_cap_and_factor);
        assertEquals(map, cap_and_factor);

        TObjectIntHashMap<String> raw_fully_specified =
                new TObjectIntHashMap<>(20, 0.75f, Integer.MIN_VALUE);
        Map<String, Integer> fully_specified = TDecorators.wrap(raw_fully_specified);

        for (int i = 0; i < element_count; i++) {
            fully_specified.put(keys[i], vals[i]);
        }
        assertEquals(map, fully_specified);

        TObjectIntHashMap<String> raw_copy = new TObjectIntHashMap<>(raw_map);
        Map<String, Integer> copy = TDecorators.wrap(raw_copy);
        assertEquals(raw_map, raw_copy);
        assertEquals(map, copy);
    }


    @Test
    public void testGetMap() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            raw_map.put(keys[i], vals[i]);
        }
        // noinspection MismatchedQueryAndUpdateOfCollection
        TObjectIntMapDecorator<String> map = new TObjectIntMapDecorator<String>(raw_map);

        assertEquals(raw_map, map.getMap());
    }



    @Test
    public void testContainsKey() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> map = new TObjectIntHashMap<>();
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }

        for (int i = 0; i < element_count; i++) {
            assertTrue(map.containsKey(keys[i]),
                    "Key should be present: " + keys[i] + ", map: " + map);
        }

        String key = "1138";
        assertFalse(map.containsKey(key), "Key should not be present: " + key + ", map: " + map);

        assertFalse(map.containsKey(new Object()),
                "Random object should not be present in map: " + map);
    }


    @Test
    public void testContainsValue() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> map = new TObjectIntHashMap<>();
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }

        for (int i = 0; i < element_count; i++) {
            assertTrue(map.containsValue(vals[i]),
                    "Value should be present: " + vals[i] + ", map: " + map);
        }

        int val = 1138;
        assertFalse(map.containsValue(val), "Key should not be present: " + val + ", map: " + map);
    }


    @Test
    public void testPutIfAbsent() {
        TObjectIntHashMap<String> map = new TObjectIntHashMap<>();

        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3);

        assertEquals(1, map.putIfAbsent("One", 2));
        assertEquals(1, map.get("One"));
        assertEquals(0, map.putIfAbsent("Nine", 9));
        assertEquals(9, map.get("Nine"));
    }


    @Test
    public void testRemove() {
        int element_count = 20;
        String[] keys = new String[element_count];
        Integer[] vals = new Integer[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }

        for (int i = 0; i < element_count; i++) {
            if (i % 2 == 1) {
                assertEquals(vals[i], map.remove(keys[i]),
                        "Remove should have modified map: " + keys[i] + ", map: " + map);
            }
        }

        for (int i = 0; i < element_count; i++) {
            if (i % 2 == 1) {
                assertNull(map.get(keys[i]),
                        "Removed key still in map: " + keys[i] + ", map: " + map);
            } else {
                assertEquals(vals[i], map.get(keys[i]),
                        "Key should still be in map: " + keys[i] + ", map: " + map);
            }
        }

        assertNull(map.get("1138"));
        // noinspection SuspiciousMethodCalls
        assertNull(map.get(Integer.valueOf(1138)));
        assertNull(map.get(null));

        map.put("null-value", null);
        assertEquals(raw_map.getNoEntryValue(), raw_map.get("null-value"));
        assertTrue(map.containsKey("null-value"));
        Integer value = map.get("null-value");
        assertNull(value, "value: " + value);
        assertNull(map.remove("null-value"));
    }


    @Test
    public void testPutAllMap() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> control = new TObjectIntHashMap<>();
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            control.put(keys[i], vals[i]);
        }

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        Map<String, Integer> source = new HashMap<String, Integer>();
        for (int i = 0; i < element_count; i++) {
            source.put(keys[i], vals[i]);
        }

        map.putAll(source);
        assertEquals(source, map);
        assertEquals(control, raw_map);
    }


    @Test
    public void testClear() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, raw_map.size());



        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertNull(map.get(keys[5]));
    }


    @Test
    public void testKeySet() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Set<String> keyset = map.keySet();
        for (int i = 0; i < keyset.size(); i++) {
            assertTrue(keyset.contains(keys[i]));
        }
        assertFalse(keyset.isEmpty());

        Object[] keys_obj_array = keyset.toArray();
        int count = 0;
        Iterator<String> iter = keyset.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            assertTrue(keyset.contains(key));
            assertEquals(keys_obj_array[count], key);
            count++;
        }

        // noinspection ToArrayCallWithZeroLengthArrayArgument
        String[] keys_array = keyset.toArray(new String[0]);
        count = 0;
        iter = keyset.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            assertTrue(keyset.contains(key));
            assertEquals(keys_array[count], key);
            count++;
        }

        keys_array = keyset.toArray(new String[keyset.size()]);
        count = 0;
        iter = keyset.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            assertTrue(keyset.contains(key));
            assertEquals(keys_array[count], key);
            count++;
        }

        keys_array = keyset.toArray(new String[keyset.size() * 2]);
        count = 0;
        iter = keyset.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            assertTrue(keyset.contains(key));
            assertEquals(keys_array[count], key);
            count++;
        }
        assertNull(keys_array[keyset.size()]);

        Set<String> other = new HashSet<String>(keyset);
        assertFalse(keyset.retainAll(other));
        other.remove(keys[5]);
        assertTrue(keyset.retainAll(other));
        assertFalse(keyset.contains(keys[5]));
        assertFalse(map.containsKey(keys[5]));

        keyset.clear();
        assertTrue(keyset.isEmpty());
    }


    @Test
    public void testKeySetAdds() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);
        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Set<String> keyset = map.keySet();
        for (int i = 0; i < keyset.size(); i++) {
            assertTrue(keyset.contains(keys[i]));
        }
        assertFalse(keyset.isEmpty());

        try {
            keyset.add("explosions!");
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }

        try {
            Set<String> test = new HashSet<String>();
            test.add("explosions!");
            keyset.addAll(test);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }
    }


    @Test
    public void testKeys() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        // No argument
        Object[] keys_object_array = map.keySet().toArray();
        assertEquals(element_count, keys_object_array.length);
        List<Object> keys_object_list = Arrays.asList(keys_object_array);
        for (int i = 0; i < element_count; i++) {
            assertTrue(keys_object_list.contains(keys[i]));
        }

        // Zero length array
        // noinspection ToArrayCallWithZeroLengthArrayArgument
        String[] keys_string_array = map.keySet().toArray(new String[0]);
        assertEquals(element_count, keys_string_array.length);
        List<String> keys_string_list = Arrays.asList(keys_string_array);
        for (int i = 0; i < element_count; i++) {
            assertTrue(keys_string_list.contains(keys[i]));
        }

        // appropriate length array
        keys_string_array = map.keySet().toArray(new String[map.size()]);
        assertEquals(element_count, keys_string_array.length);
        keys_string_list = Arrays.asList(keys_string_array);
        for (int i = 0; i < element_count; i++) {
            assertTrue(keys_string_list.contains(keys[i]));
        }

        // longer array
        keys_string_array = map.keySet().toArray(new String[element_count * 2]);
        assertEquals(element_count * 2, keys_string_array.length);
        keys_string_list = Arrays.asList(keys_string_array);
        for (int i = 0; i < element_count; i++) {
            assertTrue(keys_string_list.contains(keys[i]));
        }
        assertNull(keys_string_array[element_count]);
    }


    @Test
    public void testValueCollectionToArray() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map =
                new TObjectIntHashMap<>(element_count, 0.5f, Integer.MIN_VALUE);
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        Object[] values_array = collection.toArray();
        int count = 0;
        Iterator<Integer> iter = collection.iterator();
        while (iter.hasNext()) {
            int value = iter.next();
            assertTrue(collection.contains(value));
            assertEquals(values_array[count], value);
            count++;
        }

        // noinspection ToArrayCallWithZeroLengthArrayArgument
        values_array = collection.toArray(new Integer[0]);
        count = 0;
        iter = collection.iterator();
        while (iter.hasNext()) {
            int value = iter.next();
            assertTrue(collection.contains(value));
            assertEquals(values_array[count], value);
            count++;
        }

        values_array = collection.toArray(new Integer[collection.size()]);
        count = 0;
        iter = collection.iterator();
        while (iter.hasNext()) {
            int value = iter.next();
            assertTrue(collection.contains(value));
            assertEquals(values_array[count], value);
            count++;
        }

        values_array = collection.toArray(new Integer[collection.size() * 2]);
        count = 0;
        iter = collection.iterator();
        while (iter.hasNext()) {
            int value = iter.next();
            assertTrue(collection.contains(value));
            assertEquals(values_array[count], value);
            count++;
        }
        assertNull(values_array[collection.size()]);
        assertNull(values_array[collection.size()]);

        Collection<Integer> other = new ArrayList<Integer>(collection);
        assertFalse(collection.retainAll(other));
        other.remove(vals[5]);
        assertTrue(collection.retainAll(other));
        assertFalse(collection.contains(vals[5]));
        assertFalse(map.containsKey(keys[5]));

        collection.clear();
        assertTrue(collection.isEmpty());
    }


    @Test
    public void testValueCollectionAdds() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        try {
            collection.add(1138);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }

        try {
            Set<Integer> test = new HashSet<Integer>();
            test.add(Integer.valueOf(1138));
            collection.addAll(test);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }

        try {
            Collection<Integer> test = new ArrayList<Integer>();
            test.add(1138);
            collection.addAll(test);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }

        try {
            Integer[] integer_vals = new Integer[vals.length];
            for (int i = 0; i < vals.length; i++) {
                integer_vals[i] = Integer.valueOf(vals[i]);
            }
            collection.addAll(Arrays.asList(integer_vals));
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }
    }


    @Test
    public void testValueCollectionContainsAll() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        List<Integer> java_list = new ArrayList<Integer>();
        for (int value : vals) {
            java_list.add(value);
        }
        assertTrue(collection.containsAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);
        java_list.add(1138);
        assertFalse(collection.containsAll(java_list),
                "collection: " + collection + ", should not contain all in list: " + java_list);

        List<Number> number_list = new ArrayList<Number>();
        for (int value : vals) {
            if (value == 5) {
                number_list.add((long) value);
            } else {
                number_list.add(value);
            }
        }
        assertFalse(collection.containsAll(number_list),
                "collection: " + collection + ", should not contain all in list: " + java_list);

        Collection<Integer> other = new ArrayList<Integer>(collection);
        assertTrue(collection.containsAll(other),
                "collection: " + collection + ", should contain all in other: " + other);
        other.add(1138);
        assertFalse(collection.containsAll(other),
                "collection: " + collection + ", should not contain all in other: " + other);
    }


    @Test
    public void testValueCollectionRetainAllCollection() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        List<Integer> java_list = new ArrayList<Integer>();
        for (int value : vals) {
            java_list.add(value);
        }
        assertFalse(collection.retainAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);

        java_list.remove(5);
        assertTrue(collection.retainAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);
        assertFalse(collection.contains(vals[5]));
        assertFalse(map.containsKey(keys[5]));
        assertFalse(map.containsValue(vals[5]));
        assertTrue(collection.containsAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);
    }


    @Test
    public void testValueCollectionRetainAllTCollection() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        assertFalse(collection.retainAll(collection),
                "collection: " + collection + ", should be unmodified.");

        Collection<Integer> other = new ArrayList<Integer>(collection);
        assertFalse(collection.retainAll(other),
                "collection: " + collection + ", should be unmodified. other: " + other);

        other.remove(vals[5]);
        assertTrue(collection.retainAll(other),
                "collection: " + collection + ", should be modified. other: " + other);
        assertFalse(collection.contains(vals[5]));
        assertFalse(map.containsKey(keys[5]));
        assertFalse(map.containsValue(vals[5]));
        assertTrue(collection.containsAll(other),
                "collection: " + collection + ", should contain all in other: " + other);
    }


    @Test
    public void testValueCollectionRemoveAllCollection() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        List<Integer> java_list = new ArrayList<Integer>();
        assertFalse(collection.removeAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);

        java_list.add(vals[5]);
        assertTrue(collection.removeAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);
        assertFalse(collection.contains(vals[5]));
        assertFalse(map.containsKey(keys[5]));
        assertFalse(map.containsValue(vals[5]));

        java_list = new ArrayList<Integer>();
        for (int value : vals) {
            java_list.add(value);
        }
        assertTrue(collection.removeAll(java_list),
                "collection: " + collection + ", should contain all in list: " + java_list);
        assertTrue(collection.isEmpty());
    }


    @Test
    public void testValueCollectionRemoveAllTCollection() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> collection = map.values();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue(collection.contains(vals[i]));
        }
        assertFalse(collection.isEmpty());

        Collection<Integer> other = new ArrayList<Integer>();
        assertFalse(collection.removeAll(other),
                "collection: " + collection + ", should be unmodified.");

        other = new ArrayList<Integer>(collection);
        other.remove(vals[5]);
        assertTrue(collection.removeAll(other),
                "collection: " + collection + ", should be modified. other: " + other);
        assertEquals(1, collection.size());
        for (int i = 0; i < element_count; i++) {
            if (i == 5) {
                assertTrue(collection.contains(vals[i]));
                assertTrue(map.containsKey(keys[i]));
                assertTrue(map.containsValue(vals[i]));
            } else {
                assertFalse(collection.contains(vals[i]));
                assertFalse(map.containsKey(keys[i]));
                assertFalse(map.containsValue(vals[i]));
            }
        }

        assertFalse(collection.removeAll(other),
                "collection: " + collection + ", should be unmodified. other: " + other);

        assertTrue(collection.removeAll(collection),
                "collection: " + collection + ", should be modified. other: " + other);
        assertTrue(collection.isEmpty());
    }


    @Test
    public void testValueCollectionHashCode() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        TObjectIntMap<String> raw_map2 = new TObjectIntHashMap<>();
        Map<String, Integer> map2 = TDecorators.wrap(raw_map2);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
            map2.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Collection<Integer> values = map.values();
        Collection<Integer> other = map2.values();

        assertTrue(values.hashCode() == values.hashCode());
        assertTrue(other.hashCode() == other.hashCode());

        map2.put(String.valueOf(1138), 1138);
        assertFalse(values.hashCode() == other.hashCode());
    }


    @Test
    public void testValues() {
        int element_count = 20;
        String[] keys = new String[element_count];
        Integer[] vals = new Integer[element_count];

        TObjectIntMap<String> raw_map =
                new TObjectIntHashMap<>(element_count, 0.5f, Integer.MIN_VALUE);
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = Integer.valueOf(i + 1);
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        // No argument
        Collection<Integer> values_collection = map.values();
        assertEquals(element_count, values_collection.size());
        List<Integer> values_list = new ArrayList<Integer>(values_collection);
        for (int i = 0; i < element_count; i++) {
            assertTrue(values_list.contains(vals[i]));
        }
    }


    @Test
    public void testEntrySet() {
        int element_count = 20;
        String[] keys = new String[element_count];
        Integer[] vals = new Integer[element_count];

        TObjectIntMap<String> raw_map =
                new TObjectIntHashMap<>(element_count, 0.5f, Integer.MIN_VALUE);
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = Integer.valueOf(i + 1);
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        assertEquals(element_count, entries.size());
        assertFalse(entries.isEmpty());
        // noinspection unchecked
        Map.Entry<String, Integer>[] array = entries.toArray(new Map.Entry[entries.size()]);
        for (Map.Entry<String, Integer> entry : array) {
            assertTrue(entries.contains(entry));
        }
        assertFalse(entries.contains(null));

        assertEquals(array[0].hashCode(), array[0].hashCode());
        assertTrue(array[0].hashCode() != array[1].hashCode());

        assertTrue(array[0].equals(array[0]));
        assertFalse(array[0].equals(array[1]));
        String key = array[0].getKey();
        Integer old_value = array[0].getValue();
        assertEquals(old_value, array[0].setValue(old_value * 2));
        assertEquals(Integer.valueOf(old_value * 2), map.get(key));
        assertEquals(Integer.valueOf(old_value * 2), array[0].getValue());

        // Adds are not allowed
        Map.Entry<String, Integer> invalid_entry = new Map.Entry<String, Integer>() {
            public String getKey() {
                return null;
            }

            public Integer getValue() {
                return null;
            }

            public Integer setValue(Integer value) {
                return null;
            }
        };
        List<Map.Entry<String, Integer>> invalid_entry_list =
                new ArrayList<Map.Entry<String, Integer>>();
        invalid_entry_list.add(invalid_entry);

        try {
            entries.add(invalid_entry);
            fail("Expected OperationUnsupportedException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }

        try {
            entries.addAll(invalid_entry_list);
            fail("Expected OperationUnsupportedException");
        } catch (UnsupportedOperationException ex) {
            // Expected
        }

        assertFalse(entries.containsAll(invalid_entry_list));
        assertFalse(entries.removeAll(invalid_entry_list));

        List<Map.Entry<String, Integer>> partial_list = new ArrayList<Map.Entry<String, Integer>>();
        partial_list.add(array[3]);
        partial_list.add(array[4]);
        assertTrue(entries.removeAll(partial_list));
        assertEquals(element_count - 2, entries.size());
        assertEquals(element_count - 2, map.size());

        entries.clear();
        assertTrue(entries.isEmpty());
        assertTrue(map.isEmpty());
    }


    @Test
    public void testEquals() {
        int element_count = 20;
        String[] keys = new String[element_count];
        int[] vals = new int[element_count];

        TObjectIntMap<String> raw_map =
                new TObjectIntHashMap<>(element_count, 0.5f, Integer.MIN_VALUE);
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < element_count; i++) {
            keys[i] = Integer.toString(i + 1);
            vals[i] = i + 1;
            map.put(keys[i], vals[i]);
        }
        assertEquals(element_count, map.size());

        TObjectIntHashMap<String> raw_fully_specified =
                new TObjectIntHashMap<>(20, 0.75f, Integer.MIN_VALUE);
        Map<String, Integer> fully_specified = TDecorators.wrap(raw_fully_specified);

        for (int i = 0; i < element_count; i++) {
            fully_specified.put(keys[i], vals[i]);
        }
        assertEquals(map, fully_specified);

        assertFalse(map.equals(new Object()), "shouldn't equal random object");
        // noinspection ObjectEqualsNull
        assertFalse(map.equals(null), "shouldn't equal null");
    }


    @SuppressWarnings({"unchecked"})
    @Test
    public void testSerialize() throws Exception {
        Integer[] vals = {1138, 42, 86, 99, 101, 727, 117};
        String[] keys = new String[vals.length];

        TObjectIntMap<String> raw_map = new TObjectIntHashMap<>();
        Map<String, Integer> map = TDecorators.wrap(raw_map);

        for (int i = 0; i < keys.length; i++) {
            keys[i] = Integer.toString(vals[i] * 2);
            map.put(keys[i], vals[i]);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(map);

        ByteArrayInputStream bias = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bias);

        Map<String, Integer> deserialized = (Map<String, Integer>) ois.readObject();

        assertEquals(map, deserialized);
    }


    @Test
    public void testToString() {
        TObjectIntHashMap<String> m = new TObjectIntHashMap<>();
        m.put("One", 11);
        m.put("Two", 22);

        String to_string = m.toString();
        assertTrue(to_string.equals("{One=11,Two=22}") || to_string.equals("{Two=22,One=11}"),
                to_string);
    }

}
