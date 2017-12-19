//класс, описывающий отдельную вершину
public class Node<T> {

    Node<T> left;
    Node<T> right;
    Node<T> parent;
    final T value;

    public Node(T value) {
        this.value = value;
    }
}
