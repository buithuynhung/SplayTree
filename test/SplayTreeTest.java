import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SplayTreeTest {

    SplayTree<Integer> tree = new SplayTree<>();
    SplayTree<Integer> test = new SplayTree<>();

    void add() {
        tree.add(20);
        tree.add(30);
        tree.add(25);
        tree.add(40);
    }

    @Test
    void size() {
        add();
        System.out.println("Size test");
        assertEquals(4, tree.size());
        tree.remove(20);
        assertEquals(3, tree.size());
    }

    @Test
    void isEmpty() {
        System.out.println("IsEmpty test");
        assertTrue(tree.isEmpty());
        add();
        tree.remove(20);
        tree.remove(30);
        tree.remove(25);
        assertFalse(tree.isEmpty());
        tree.remove(40);
        assertTrue(tree.isEmpty());
    }

    @Test
    void contains() {
        add();
        System.out.println("Contains test");
        assertTrue(tree.contains(20));
        assertFalse(tree.contains(50));
        assertTrue(tree.contains(25));
        assertTrue(tree.contains(30));
        assertTrue(tree.contains(40));
        tree.remove(30);
        assertFalse(tree.contains(30));

    }

    @Test
    void toArray() {
        add();
        System.out.println("ToArray() test");
        Integer[] exp = new Integer[]{20, 25, 30, 40};
        assertArrayEquals(tree.toArray(), exp);
    }

    @Test
    void toArray1() {
        add();
        System.out.println("ToArray(a) test");
        Integer a[] = new Integer[]{15, 30};
        Integer exp[] = new Integer[]{20, 25, 30, 40};
        assertArrayEquals(tree.toArray(a), exp);
    }

    @Test
    void containsAll() {
        add();
        System.out.println("ContainsAll test");
        test.add(20);
        test.add(40);
        assertTrue(tree.containsAll(test));
        test.add(50);
        assertFalse(tree.containsAll(test));
    }

    @Test
    void addAll() {
        add();
        System.out.println("AddAll test");
        Set set = new HashSet();
        set.add(10);
        set.add(50);
        set.add(35);
        test.addAll(set);
        tree.add(10);
        tree.add(35);
        tree.add(50);
        assertTrue(tree.containsAll(test));

    }

    @Test
    void retainAll() {
        add();
        System.out.println("RetainAll test");
        test.add(25);
        test.add(40);
        tree.retainAll(test);
        assertArrayEquals(test.toArray(), tree.toArray());
        test.add(50);
        Integer[] exp = new Integer[]{25, 40};
        assertArrayEquals(exp, tree.toArray());
    }

    @Test
    void removeAll() {
        add();
        System.out.println("removeAll test");
        test.add(25);
        test.add(40);
        tree.removeAll(test);
        Integer exp[] = new Integer[]{20, 30};
        assertArrayEquals(exp, tree.toArray());
    }

    @Test
    void clear() {
        add();
        test.add(20);
        test.add(25);
        test.add(30);
        test.add(40);
        tree.removeAll(test);
        SplayTree spl = new SplayTree();
        assertArrayEquals(spl.toArray(), tree.toArray());
    }

}