package btree;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class BTree implements Serializable {
    private static final long serialVersionUID = 1L;
    private BTreeNode root;
    private int t;

    public BTree(int t) {
        this.t = t;
    }

    public void insert(int key) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = key;
            root.numKeys = 1;
        } else {
            if (root.numKeys == 2 * t - 1) {
                BTreeNode newRoot = new BTreeNode(t, false);
                newRoot.children[0] = root;
                newRoot.splitChild(0, root);
                newRoot.children[(newRoot.keys[0] < key) ? 1 : 0].insertNonFull(key);
                root = newRoot;
            } else {
                root.insertNonFull(key);
            }
        }
    }

    public void remove(int key) {
        if (root == null) {
            System.out.println("A árvore está vazia");
            return;
        }
        root.remove(key);
        if (root.numKeys == 0) root = root.isLeaf ? null : root.children[0];
    }

    public BTreeNode search(int key) {
        return root == null ? null : root.search(key);
    }

    public void printTreeByLevels() {
        if (root == null) {
            System.out.println("Árvore está vazia.");
            return;
        }

        Queue<BTreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            while (levelSize-- > 0) {
                BTreeNode node = queue.poll();
                System.out.print(node.keysToString() + " ");
                for (int i = 0; i <= node.numKeys; i++) {
                    if (node.children[i] != null) queue.add(node.children[i]);
                }
            }
            System.out.println();
        }
    }
}
