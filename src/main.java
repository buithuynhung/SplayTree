
public class main {
    public static void main(String[] args) {
        SplayTree<Integer> tree = new SplayTree<>();
        tree.add(10);
        tree.add(20);
        tree.add(15);
        System.out.println(tree.contains(15));
        System.out.println(tree.contains(23));
    }
}
