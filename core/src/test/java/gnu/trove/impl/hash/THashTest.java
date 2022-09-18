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
package gnu.trove.impl.hash;

import gnu.trove.impl.PrimeFinder;
import gnu.trove.set.hash.TByteHashSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import static org.junit.jupiter.api.Assertions.*;


/**
 * tests that need access to internals of THash or THashSet
 */
public class THashTest {
    private final String name;

    public THashTest(String name) {
        this.name = name;
    }


    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    @Test
    public void testNormalLoad() throws Exception {
        THashSet<Integer> set = new THashSet<>(11, 0.5f);
        assertEquals(set._maxSize, 11);
        for (int i = 0; i < 12; i++) {
            set.add(i);
        }
        assertTrue(set._maxSize > 12);
    }


    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    @Test
    public void testMaxLoad() throws Exception {
        THashSet<Integer> set = new THashSet<>(11, 1.0f);
        assertEquals(10, set._maxSize);
        for (int i = 0; i < 12; i++) {
            set.add(i);
        }
        assertTrue(set._maxSize > 12);
    }

    @Test
    public void testNegativeCapacity() throws Exception {
        try {
            new THashSet<Integer>(-1);
            fail("expected an illegal argument exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testLargeCapacity() {
        final int twentyFourBitPrime = PrimeFinder.nextPrime(1 << 24);
        TByteHashSet set = new TByteHashSet(twentyFourBitPrime + 1, 1.0f);

        assertTrue(set.capacity() > twentyFourBitPrime,
                "capacity was not large enough to hold desired elements");
    }

    @Test
    public void testReusesRemovedSlotsOnCollision() {
        THashSet<Object> set = new THashSet<Object>(11, 0.5f);

        class Foo {

            public int hashCode() {
                return 4;
            }
        }

        Foo f1 = new Foo();
        Foo f2 = new Foo();
        Foo f3 = new Foo();
        set.add(f1);

        int idx = set.insertKey(f2);
        set.add(f2);
        assertEquals(f2, set._set[idx]);
        set.remove(f2);
        assertEquals(THashSet.REMOVED, set._set[idx]);
        assertEquals(idx, set.insertKey(f3));
        set.add(f3);
        assertEquals(f3, set._set[idx]);
    }

    @Test
    public void testCompact() throws Exception {
        THashMap<Integer, Integer> map = new THashMap<>();

        Integer[] data = new Integer[1000];

        for (int i = 0; i < 1000; i++) {
            data[i] = i;
            map.put(data[i], data[i]);
        }
        assertTrue(map._maxSize > 1000);
        for (int i = 0; i < 1000; i += 2) {
            // try {
            map.remove(data[i]);
            // }
            // catch( RuntimeException ex ) {
            // System.err.println("Error on i: " + i);
            // System.out.println("Hash codes:");
            // for( int j = 0 ; j < data.length; j++ ) {
            // if ( ( j % 8 ) == 0 ) {
            // System.out.println(",");
            // }
            // else System.out.print(",");
            // System.out.print(map._hashingStrategy.computeHashCode(data[j]));
            // }
            //
            //
            // System.out.println("Remove:");
            // for( int j = 0 ; j <= i; j+=2 ) {
            // if ( ( j % 8 ) == 0 ) {
            // System.out.println(",");
            // }
            // else System.out.print(",");
            // System.out.print(map._hashingStrategy.computeHashCode(data[j]));
            // }
            // throw ex;
            // }
        }
        assertEquals(500, map.size());
        map.compact();
        assertEquals(500, map.size());
        assertTrue(map._maxSize < 1000);
    }


    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    @Test
    public void testTPHashMapConstructors() {

        int cap = 20;

        THashMap cap_and_factor = new THashMap(cap, 0.75f);
        assertTrue(cap <= cap_and_factor.capacity(),
                "capacity not sufficient: " + cap + ", " + cap_and_factor.capacity());
        assertEquals(0.75f, cap_and_factor._loadFactor);
    }

    @Test
    public void testTPrimitivePrimitveHashMapConstructors() {

        int cap = 20;

        TIntLongMap cap_and_factor = new TIntLongHashMap(cap, 0.75f);
        TPrimitiveHash cap_and_factor_hash = (TPrimitiveHash) cap_and_factor;
        assertTrue(cap <= cap_and_factor_hash.capacity(),
                "capacity not sufficient: " + cap + ", " + cap_and_factor_hash.capacity());
        assertEquals(0.75f, cap_and_factor_hash._loadFactor);

        TIntLongMap fully_specified =
                new TIntLongHashMap(cap, 0.5f, Integer.MIN_VALUE, Long.MIN_VALUE);
        TPrimitiveHash fully_specified_hash = (TPrimitiveHash) fully_specified;
        assertTrue(cap <= fully_specified_hash.capacity(),
                "capacity not sufficient: " + cap + ", " + fully_specified_hash.capacity());
        assertEquals(0.5f, fully_specified_hash._loadFactor);
        assertEquals(Integer.MIN_VALUE, fully_specified.getNoEntryKey());
        assertEquals(Long.MIN_VALUE, fully_specified.getNoEntryValue());
    }


    // test all the way up the chain to THash
    @Test
    public void testTPrimitivePrimitveHashMapSerialize() throws Exception {
        int[] keys = {1138, 42, 86, 99, 101, 727, 117};
        long[] vals = new long[keys.length];

        TIntLongMap original_map =
                new TIntLongHashMap(200, 0.75f, Integer.MIN_VALUE, Long.MIN_VALUE);
        for (int i = 0; i < keys.length; i++) {
            vals[i] = keys[i] * 2;
            original_map.put(keys[i], vals[i]);
        }

        THash original_hash = (THash) original_map;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original_map);

        ByteArrayInputStream bias = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bias);

        TIntLongMap deserialized_map = (TIntLongMap) ois.readObject();
        THash deserialized_hash = (THash) deserialized_map;

        assertEquals(original_map, deserialized_map);
        assertEquals(original_map.getNoEntryKey(), deserialized_map.getNoEntryKey());
        assertEquals(original_map.getNoEntryValue(), deserialized_map.getNoEntryValue());
        assertEquals(original_hash._loadFactor, deserialized_hash._loadFactor);
    }


    // test all the way up the chain to THash
    @Test
    public void testTPrimitiveObjectHashMapSerialize() throws Exception {
        int[] keys = {1138, 42, 86, 99, 101, 727, 117};
        String[] vals = new String[keys.length];

        TIntObjectMap<String> original_map =
                new TIntObjectHashMap<String>(200, 0.75f, Integer.MIN_VALUE);
        for (int i = 0; i < keys.length; i++) {
            vals[i] = String.valueOf(keys[i] * 2);
            original_map.put(keys[i], vals[i]);
        }

        THash original_hash = (THash) original_map;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original_map);

        ByteArrayInputStream bias = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bias);

        TIntObjectMap deserialized_map = (TIntObjectMap) ois.readObject();
        THash deserialized_hash = (THash) deserialized_map;

        assertEquals(original_map, deserialized_map);
        assertEquals(original_map.getNoEntryKey(), deserialized_map.getNoEntryKey());
        assertEquals(original_hash._loadFactor, deserialized_hash._loadFactor);
    }


    // test all the way up the chain to THash
    @Test
    public void testTObjectPrimitiveHashMapSerialize() throws Exception {
        int[] vals = {1138, 42, 86, 99, 101, 727, 117};
        String[] keys = new String[vals.length];


        TObjectIntMap<String> original_map =
                new TObjectIntHashMap<String>(200, 0.75f, Integer.MIN_VALUE);
        for (int i = 0; i < keys.length; i++) {
            keys[i] = String.valueOf(vals[i] * 2);
            original_map.put(keys[i], vals[i]);
        }

        THash original_hash = (THash) original_map;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original_map);

        ByteArrayInputStream bias = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bias);

        TObjectIntMap deserialized_map = (TObjectIntMap) ois.readObject();
        THash deserialized_hash = (THash) deserialized_map;

        assertEquals(original_map, deserialized_map);
        assertEquals(original_map.getNoEntryValue(), deserialized_map.getNoEntryValue());
        assertEquals(original_hash._loadFactor, deserialized_hash._loadFactor);
    }

}
