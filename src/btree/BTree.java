package btree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BTree implements Serializable {
    private static final long serialVersionUID = 1L;
    private BTreeNode root;
    private int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    // Método para inserir uma nova chave na árvore
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
                int i = (newRoot.keys[0] < key) ? 1 : 0;
                newRoot.children[i].insertNonFull(key);
                root = newRoot;
            } else {
                root.insertNonFull(key);
            }
        }
    }

    // Método para remover uma chave da árvore
    public void remove(int key) {
        if (root == null) {
            System.out.println("A árvore está vazia");
            return;
        }
        root.remove(key);
        if (root.numKeys == 0) {
            if (root.isLeaf) {
                root = null;
            } else {
                root = root.children[0];
            }
        }
    }

    // Método para buscar uma chave na árvore
    public BTreeNode search(int key) {
        return (root == null) ? null : root.search(key);
    }

    // Método para exibir a árvore B por níveis
    public void printTreeByLevels() {
        if (root == null) {
            System.out.println("Árvore está vazia.");
            return;
        }

        Queue<BTreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            while (levelSize > 0) {
                BTreeNode node = queue.poll();
                System.out.print(node.keysToString() + " ");
                for (int i = 0; i <= node.numKeys; i++) {
                    if (node.children[i] != null) {
                        queue.add(node.children[i]);
                    }
                }
                levelSize--;
            }
            System.out.println();
        }
    }

    public List<Integer> getAllKeys() {
    List<Integer> allKeys = new ArrayList<>();
    if (root != null) {
        root.collectKeys(allKeys);
    }
    return allKeys;
}

}
