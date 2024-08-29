package btree;

import java.io.Serializable;
import java.util.List;

public class BTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    int[] keys;
    int t;
    BTreeNode[] children;
    int numKeys;
    boolean isLeaf;

    public BTreeNode(int t, boolean isLeaf) {
        this.t = t;
        this.isLeaf = isLeaf;
        this.keys = new int[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.numKeys = 0;
    }

    public void traverse() {
        int i;
        for (i = 0; i < this.numKeys; i++) {
            if (!this.isLeaf) {
                children[i].traverse();
            }
            System.out.print(" " + keys[i]);
        }
        if (!this.isLeaf) {
            children[i].traverse();
        }
    }

    public BTreeNode search(int key) {
        int i = 0;
        while (i < numKeys && key > keys[i]) {
            i++;
        }
        if (i < numKeys && keys[i] == key) {
            return this;
        }
        if (isLeaf) {
            return null;
        }
        return children[i].search(key);
    }

    public void insertNonFull(int key) {
        int i = numKeys - 1;
        if (isLeaf) {
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            numKeys = numKeys + 1;
        } else {
            while (i >= 0 && keys[i] > key) {
                i--;
            }
            if (children[i + 1].numKeys == 2 * t - 1) {
                splitChild(i + 1, children[i + 1]);
                if (keys[i + 1] < key) {
                    i++;
                }
            }
            children[i + 1].insertNonFull(key);
        }
    }

    public void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.isLeaf);
        z.numKeys = t - 1;
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
        }
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
            }
        }
        y.numKeys = t - 1;
        for (int j = numKeys; j >= i + 1; j--) {
            children[j + 1] = children[j];
        }
        children[i + 1] = z;
        for (int j = numKeys - 1; j >= i; j--) {
            keys[j + 1] = keys[j];
        }
        keys[i] = y.keys[t - 1];
        numKeys = numKeys + 1;
    }

    public void remove(int key) {
        int idx = findKey(key);
        if (idx < numKeys && keys[idx] == key) {
            if (isLeaf) {
                removeFromLeaf(idx);
            } else {
                removeFromNonLeaf(idx);
            }
        } else {
            if (isLeaf) {
                System.out.println("A chave " + key + " não está na árvore");
                return;
            }
            boolean flag = (idx == numKeys);
            if (children[idx].numKeys < t) {
                fill(idx);
            }
            if (flag && idx > numKeys) {
                children[idx - 1].remove(key);
            } else {
                children[idx].remove(key);
            }
        }
    }

    private int findKey(int key) {
        int idx = 0;
        while (idx < numKeys && keys[idx] < key) {
            idx++;
        }
        return idx;
    }

    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < numKeys; ++i) {
            keys[i - 1] = keys[i];
        }
        numKeys--;
    }

    private void removeFromNonLeaf(int idx) {
        int key = keys[idx];
        if (children[idx].numKeys >= t) {
            int pred = getPredecessor(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        } else if (children[idx + 1].numKeys >= t) {
            int succ = getSuccessor(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ);
        } else {
            merge(idx);
            children[idx].remove(key);
        }
    }

    private int getPredecessor(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.isLeaf) {
            cur = cur.children[cur.numKeys];
        }
        return cur.keys[cur.numKeys - 1];
    }

    private int getSuccessor(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.isLeaf) {
            cur = cur.children[0];
        }
        return cur.keys[0];
    }

    private void fill(int idx) {
        if (idx != 0 && children[idx - 1].numKeys >= t) {
            borrowFromPrev(idx);
        } else if (idx != numKeys && children[idx + 1].numKeys >= t) {
            borrowFromNext(idx);
        } else {
            if (idx != numKeys) {
                merge(idx);
            } else {
                merge(idx - 1);
            }
        }
    }

    private void borrowFromPrev(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];
        for (int i = child.numKeys - 1; i >= 0; --i) {
            child.keys[i + 1] = child.keys[i];
        }
        if (!child.isLeaf) {
            for (int i = child.numKeys; i >= 0; --i) {
                child.children[i + 1] = child.children[i];
            }
        }
        child.keys[0] = keys[idx - 1];
        if (!child.isLeaf) {
            child.children[0] = sibling.children[sibling.numKeys];
        }
        keys[idx - 1] = sibling.keys[sibling.numKeys - 1];
        child.numKeys += 1;
        sibling.numKeys -= 1;
    }

    private void borrowFromNext(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];
        child.keys[child.numKeys] = keys[idx];
        if (!child.isLeaf) {
            child.children[child.numKeys + 1] = sibling.children[0];
        }
        keys[idx] = sibling.keys[0];
        for (int i = 1; i < sibling.numKeys; ++i) {
            sibling.keys[i - 1] = sibling.keys[i];
        }
        if (!sibling.isLeaf) {
            for (int i = 1; i <= sibling.numKeys; ++i) {
                sibling.children[i - 1] = sibling.children[i];
            }
        }
        child.numKeys += 1;
        sibling.numKeys -= 1;
    }

    private void merge(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];
        child.keys[t - 1] = keys[idx];
        for (int i = 0; i < sibling.numKeys; ++i) {
            child.keys[i + t] = sibling.keys[i];
        }
        if (!child.isLeaf) {
            for (int i = 0; i <= sibling.numKeys; ++i) {
                child.children[i + t] = sibling.children[i];
            }
        }
        for (int i = idx + 1; i < numKeys; ++i) {
            keys[i - 1] = keys[i];
        }
        for (int i = idx + 2; i <= numKeys; ++i) {
            children[i - 1] = children[i];
        }
        child.numKeys += sibling.numKeys + 1;
        numKeys--;
        sibling = null;
    }

    public String keysToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < numKeys; i++) {
            sb.append(keys[i]);
            if (i < numKeys - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    public void collectKeys(List<Integer> allKeys) {
    for (int i = 0; i < numKeys; i++) {
        if (!isLeaf) {
            children[i].collectKeys(allKeys);
        }
        allKeys.add(keys[i]);
    }
    if (!isLeaf) {
        children[numKeys].collectKeys(allKeys);
    }
}

}
