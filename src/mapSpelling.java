/****************************************************************************
 Sahand Karimi
 February 13, 2021

 File Name:      mapSpelling.java
 Description:    Takes a file filled with words, and runs it over
 the spellchecker code to output for any potential
 errors. Writes out the total number of words in the list.

 ****************************************************************************/
import java.io.*;

public class mapSpelling {


    private table[] table;

    private int size;

    private int max_size;

    // load factor
    private final float lf = 0.75f;


    public mapSpelling() {
        max_size = (int)(16 * lf);
        table = new table[16];
    }


    private final static int hash_function(final int h) {
        return ((h >>> 21) ^ (h >>> 12)) ^ (h >>> 7) ^ (h >>> 4);
    }


    private final static int check_idx(final int h, final int length) {
        return h & (length-1);
    }


    public final int map_size() {
        return size;
    }


    public final short get_key(final String key) {
        if (key == null) {
            return (short) 0;
        }
        int hash_function = hash_function(key.hashCode());
        table e = table[check_idx(hash_function, table.length)];
        while (e != null) {
       String k;
            if (e.hash_function != hash_function || ((k = e.key_tbl) != key && !key.equals(k))) {
                e = e.nxt_tbl;
            } else {
                return e.val_tbl;
            }
        }
        return (short) 0;
    }


    public final boolean contains(final String key) {
        if (gettable(key) != null) return true;
        else return false;
    }


    private final table gettable(final String key) {
        int hash_function = (key == null) ? 0 : hash_function(key.hashCode());
        String k;
        for (table e = table[check_idx(hash_function, table.length)];
             e != null;
             e = e.nxt_tbl) {
            if (e.hash_function == hash_function &&
                ((k = e.key_tbl) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }



    public final short put(final String key, final short value) {
        int hash_function = hash_function(key.hashCode());
        int i = check_idx(hash_function, table.length);
        String k;
        table e = table[i];
        while (e != null) {
            if (e.hash_function == hash_function && ((k = e.key_tbl) == key || key.equals(k))) {
                short oldValue = e.val_tbl;
                e.val_tbl = value;
                return oldValue;
            }
            e = e.nxt_tbl;
        }

        add_table(hash_function, key, value, i);
        return 0;
    }


    private final void resize(final int newCapacity) {
        table[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity != 1 << 30) {
            table[] newTable = new table[newCapacity];
            transfer(newTable);
            table = newTable;
            max_size = (int) (newCapacity * lf);
        } else {
            max_size = Integer.MAX_VALUE;
            return;
        }

    }


    private final void transfer(final table[] newTable) {
        table[] src = table;
        int newCapacity = newTable.length;
        int j = 0;
        while (j < src.length) {
            table e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    table next = e.nxt_tbl;
                    int i = check_idx(e.hash_function, newCapacity);
                    e.nxt_tbl = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
            j++;
        }
    }

    static final class table {
        final String key_tbl;
        short val_tbl;
        table nxt_tbl;
        final int hash_function;


        table(final int h, String k, final short v, final table n) {
            key_tbl = k;
            val_tbl = v;
            nxt_tbl = n;
            hash_function = h;
        }


        public final String getKey() {
            return key_tbl;
        }
        public final int getValue() {
            return val_tbl;
        }


    }


    private final void add_table(int hash_function, String key, short value, int bucketIndex) {
	table e = table[bucketIndex];
        table[bucketIndex] = new table(hash_function, key, value, e);
        if (size++ < max_size) {
            return;
        }
        resize(2 * table.length);
    }

}
