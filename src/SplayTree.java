import java.util.*;

public class SplayTree<T extends Comparable<T>> implements SortedSet<T> {

    private Node<T> root, left, right;
    private int size;

    //вспомогательные процедуры для работы с указателями на родителей
    private void setParent(Node<T> child, Node<T> parent) {
        if (child != null) {
            child.parent = parent;
        }
    }

    private void keepParent(Node<T> node) {
        setParent(node.left, node);
        setParent(node.right, node);
    }

    //Подъем вершины в короень через повороты вершин
    private void rotate(Node<T> parent, Node<T> child) {
        Node<T> gparent = parent.parent;
        if (gparent != null) {
            if (gparent.left == parent) {
                gparent.left = child;
            } else {
                gparent.right = child;
            }
        }
        if (parent.left == child) {
            parent.left = child.right;
            child.right = parent;
        } else {
            parent.right = child.left;
            child.left = parent;
        }
        keepParent(child);
        keepParent(parent);
        child.parent = gparent;
    }

    private Node<T> splay(Node<T> node) {
        if (node.parent == null) {
            return node;
        }
        Node<T> parent = node.parent;
        Node<T> gparent = parent.parent;
        if (gparent == null) {
            rotate(parent, node);
            return node;
        } else {
            boolean zigzig = (gparent.left == parent) == (parent.left == node);
            if (zigzig == true) {
                rotate(gparent, parent);
                rotate(parent, node);
            } else {
                rotate(parent, node);
                rotate(gparent, node);
            }
        }
        return splay(node);
    }

    private Node<T> findClosest(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        int comparison = value.compareTo(node.value);
        if (comparison == 0) {
            return splay(node);
        } else if (comparison < 0 && node.left != null) {
            return findClosest(node.left, value);
        } else if (comparison > 0 && node.right != null) {
            return findClosest(node.right, value);
        }
        return splay(node);
    }

    //для реализации вставки ключа
    private void split(Node<T> root, T value) {
        if (root == null) {
            left = null;
            right = null;
            return;
        }
        root = findClosest(root, value);
        int comparison = value.compareTo(root.value);
        if (comparison > 0) {
            right = root.right;
            root.right = null;
            setParent(right, null);
            left = root;
        } else {
            left = root.left;
            root.left = null;
            setParent(left, null);
            right = root;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(Object o) {
        if (root == null || o.getClass() != root.value.getClass()) {
            return false;
        }
        Node<T> node = find(root, (T) o);
        if (node == null) {
            return false;
        } else {
            root = node;
            return true;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new SplayTreeIterator();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {

        if (fromElement == null || toElement == null) throw new NullPointerException();
        if (fromElement.compareTo(toElement) > 0) throw new IllegalArgumentException();

        return subSet(fromElement, toElement, false);
    }

    private SortedSet<T> subSet(T fromElement, T toElement, boolean includeTo) {
        SortedSet<T> set = new TreeSet<>();
        subSet(root, set, fromElement, toElement, includeTo);
        return set;
    }

    private void subSet(Node<T> current, SortedSet<T> set, T fromElement, T toElement, boolean includeTo) {
        if (current == null)
            return;
        int compFrom = current.value.compareTo(fromElement);
        int compTo = current.value.compareTo(toElement);
        if (compFrom > 0)
            subSet(current.left, set, fromElement, toElement, includeTo);
        if (includeTo) {
            if (compFrom >= 0 && compTo <= 0)
                set.add(current.value);
        } else {
            if (compFrom >= 0 && compTo < 0)
                set.add(current.value);
        }
        if (compTo < 0)
            subSet(current.right, set, fromElement, toElement, includeTo);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        if (toElement.compareTo(first()) < 0) throw new IllegalArgumentException();
        return subSet(first(), toElement, true);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (fromElement.compareTo(last()) > 0) throw new IllegalArgumentException();
        return subSet(fromElement, last(), true);
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public class SplayTreeIterator implements Iterator<T> {

        private final List<T> list;
        private int index;

        public SplayTreeIterator() {
            list = new ArrayList<T>();
            if (root != null) {
                addToList(root);
            }
            index = 0;
        }

        private void addToList(Node<T> node) {
            if (node.right != null) {
                addToList(node.right);
            }
            list.add(node.value);
            if (node.left != null) {
                addToList(node.left);
            }
        }

        @Override
        public boolean hasNext() {
            return list.size() != index;
        }

        @Override
        public T next() {
            if (hasNext()) {
                return list.get(index++);
            } else throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (index <= 0) throw new IllegalStateException();
            list.remove(--index);
        }
    }

    @Override
    public Object[] toArray() {
        Iterator it = new SplayTreeIterator();
        Object[] a = new Object[size];
        int i = 0;
        while (it.hasNext()) {
            a[i] = it.next();
            i++;
        }
        return a;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Object[] elementData = toArray();
        if (a.length < size) {
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(T e) {
        if (contains(e)) {
            return false;
        }
        root = insert(e);
        size++;
        return true;
    }

    private Node<T> insert(T e) {
        split(root, e);
        root = new Node(e);
        root.left = left;
        root.right = right;
        keepParent(root);
        return root;
    }

    @Override
    public boolean remove(Object o) {
        if (root == null || o.getClass() != root.value.getClass()) {
            return false;
        }
        Node<T> node = remove(root, (T) o);
        if (node == null) {
            if (root == null) {
                size--;
                return true;
            }
            return false;
        }
        root = node;
        size--;
        return true;
    }

    private Node<T> remove(Node<T> node, T value) {
        node = find(node, value);
        if (node == null) {
            return null;
        }
        setParent(node.left, null);
        setParent(node.right, null);
        Node<T> newRoot = merge(node.left, node.right);
        if (newRoot == null) {
            root = null;
            return null;
        } else {
            return newRoot;
        }
    }

    private Node<T> merge(Node<T> left, Node<T> right) {
        if (right == null) {
            return left;
        }
        if (left == null) {
            return right;
        }
        right = findClosest(right, left.value);
        right.left = left;
        left.parent = right;
        return right;
    }

    private Node<T> find(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        int comparison = value.compareTo(node.value);
        if (comparison == 0) {
            return splay(node);
        } else if (comparison < 0) {
            return find(node.left, value);
        } else {
            return find(node.right, value);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Iterator it = c.iterator();
        Object o;
        if (it.hasNext()) {
            o = it.next();
            if (root == null || o.getClass() != root.value.getClass()) {
                return false;
            }
        } else {
            return false;
        }
        if (!contains((T) o)) {
            return false;
        }
        while (it.hasNext()) {
            o = it.next();
            if (!contains((T) o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        Iterator it = c.iterator();
        Object o;
        if (it.hasNext()) {
            o = it.next();
            if (root == null || o.getClass() != root.value.getClass()) {
                return false;
            }
        } else {
            return false;
        }
        boolean flag = false;
        if (add((T) o)) {
            flag = true;
        }
        while (it.hasNext()) {
            o = it.next();
            if (add((T) o)) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator it = c.iterator();
        Object o;
        if (it.hasNext()) {
            o = it.next();
            if (root == null || o.getClass() != root.value.getClass()) {
                return false;
            }
        } else {
            return false;
        }
        boolean flag = false;
        SplayTree<T> tree = new SplayTree<>();
        if (contains((T) o)) {
            tree.add((T) o);
            flag = true;
        }
        while (it.hasNext()) {
            o = it.next();
            if (contains((T) o)) {
                tree.add((T) o);
            } else {
                flag = true;
            }
        }
        root = tree.root;
        size = tree.size;
        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Iterator it = c.iterator();
        Object o;
        if (it.hasNext()) {
            o = it.next();
            if (root == null || o.getClass() != root.value.getClass()) {
                return false;
            }
        } else {
            return false;
        }
        boolean flag = false;
        if (remove((T) o)) {
            flag = true;
        }
        while (it.hasNext()) {
            o = it.next();
            if (remove((T) o)) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }
}

