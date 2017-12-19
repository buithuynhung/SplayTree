import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class SplayTree<T extends Comparable<T>> implements Set<T> {

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
//для реализации вставки и удаления ключа
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

    public class SplayTreeIterator implements Iterator<T> {

        private final Stack<T> stack;

        public SplayTreeIterator() {
            stack = new Stack<>();
            if (root != null) {
                addToStack(root);
            }
        }

        private void addToStack(Node<T> node) {
            if (node.right != null) {
                addToStack(node.right);
            }
            stack.push(node.value);
            if (node.left != null) {
                addToStack(node.left);
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.empty();
        }

        @Override
        public T next() {
            return stack.pop();
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
        Iterator it = new SplayTreeIterator();
        while (it.hasNext()) {
            remove((T) it.next());
        }
    }

}