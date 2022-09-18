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
package gnu.trove.set.hash;

import gnu.trove.map.hash.ArrayHashingStrategy;
import gnu.trove.strategy.HashingStrategy;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 *
 */
public class TCustomHashSetTest {
    @Test
    public void testArray() {
        char[] foo = new char[] {'a', 'b', 'c'};
        char[] bar = new char[] {'a', 'b', 'c'};

        assertFalse(Arrays.hashCode(foo) == Arrays.hashCode(bar));
        // noinspection ArrayEquals
        assertFalse(foo.equals(bar));

        HashingStrategy<char[]> strategy = new ArrayHashingStrategy();
        assertTrue(strategy.computeHashCode(foo) == strategy.computeHashCode(bar));
        assertTrue(strategy.equals(foo, bar));

        Set<char[]> set = new TCustomHashSet<char[]>(strategy);
        set.add(foo);
        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));

        set.remove(bar);

        assertTrue(set.isEmpty());
    }


    @Test
    public void testSerialization() throws Exception {
        char[] foo = new char[] {'a', 'b', 'c'};
        char[] bar = new char[] {'a', 'b', 'c'};

        HashingStrategy<char[]> strategy = new ArrayHashingStrategy();
        Set<char[]> set = new TCustomHashSet<char[]>(strategy);

        set.add(foo);

        // Make sure it still works after being serialized
        ObjectOutputStream oout = null;
        ByteArrayOutputStream bout = null;
        ObjectInputStream oin = null;
        ByteArrayInputStream bin = null;
        try {
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);

            oout.writeObject(set);

            bin = new ByteArrayInputStream(bout.toByteArray());
            oin = new ObjectInputStream(bin);

            set = (Set<char[]>) oin.readObject();
        } finally {
            if (oin != null)
                oin.close();
            if (bin != null)
                bin.close();
            if (oout != null)
                oout.close();
            if (bout != null)
                bout.close();
        }

        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));

        set.remove(bar);

        assertTrue(set.isEmpty());
    }
}
