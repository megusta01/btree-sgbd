package btree;

import java.io.Serializable;
import java.util.Arrays;

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

    // public void traverse() {
    //     for (int i = 0; i < numKeys; i++) {
    //         if (!isLeaf) children[i].traverse();
    //         System.out.print(" " + keys[i]);
    //     }
    //     if (!isLeaf) children[numKeys].traverse();
    // }

    public BTreeNode search(int key) {
        int i = 0;
        while (i < numKeys && key > keys[i]) i++;
        return (i < numKeys && keys[i] == key) ? this : (isLeaf ? null : children[i].search(key));
    }

    public void insertNonFull(int key) {
        int i = numKeys - 1;
        if (isLeaf) {
            while (i >= 0 && keys[i] > key) keys[i + 1] = keys[i--];
            keys[++i] = key;
            numKeys++;
        } else {
            while (i >= 0 && keys[i] > key) i--;
            if (children[i + 1].numKeys == 2 * t - 1) {
                splitChild(i + 1, children[i + 1]);
                if (keys[i + 1] < key) i++;
            }
            children[i + 1].insertNonFull(key);
        }
    }

    public void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.isLeaf);
        z.numKeys = t - 1;
        System.arraycopy(y.keys, t, z.keys, 0, t - 1);
        if (!y.isLeaf) System.arraycopy(y.children, t, z.children, 0, t);
        y.numKeys = t - 1;
        System.arraycopy(children, i + 1, children, i + 2, numKeys - i);
        children[i + 1] = z;
        System.arraycopy(keys, i, keys, i + 1, numKeys - i);
        keys[i] = y.keys[t - 1];
        numKeys++;
    }

    public void remove(int key) {
        int idx = findKey(key);
        if (idx < numKeys && keys[idx] == key) {
            if (isLeaf) removeFromLeaf(idx);
            else removeFromNonLeaf(idx);
        } else {
            if (isLeaf) {
                System.out.println("Chave " + key + " não encontrada");
                return;
            }
            boolean flag = (idx == numKeys);
            if (children[idx].numKeys < t) fill(idx);
            (flag && idx > numKeys ? children[idx - 1] : children[idx]).remove(key);
        }
    }

    private int findKey(int key) {
        return Arrays.binarySearch(keys, 0, numKeys, key) < 0 ? Math.abs(Arrays.binarySearch(keys, 0, numKeys, key) + 1) : Arrays.binarySearch(keys, 0, numKeys, key);
    }

    private void removeFromLeaf(int idx) {
        System.arraycopy(keys, idx + 1, keys, idx, numKeys - idx - 1);
        numKeys--;
    }

    private void removeFromNonLeaf(int idx) {
        int key = keys[idx];
        if (children[idx].numKeys >= t) {
            keys[idx] = getPredecessor(idx);
            children[idx].remove(keys[idx]);
        } else if (children[idx + 1].numKeys >= t) {
            keys[idx] = getSuccessor(idx);
            children[idx + 1].remove(keys[idx]);
        } else {
            merge(idx);
            children[idx].remove(key);
        }
    }

    private int getPredecessor(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.isLeaf) cur = cur.children[cur.numKeys];
        return cur.keys[cur.numKeys - 1];
    }

    private int getSuccessor(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.isLeaf) cur = cur.children[0];
        return cur.keys[0];
    }

    private void fill(int idx) {
        if (idx != 0 && children[idx - 1].numKeys >= t) borrowFromPrev(idx);
        else if (idx != numKeys && children[idx + 1].numKeys >= t) borrowFromNext(idx);
        else merge(idx != numKeys ? idx : idx - 1);
    }

    private void borrowFromPrev(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];
        System.arraycopy(child.keys, 0, child.keys, 1, child.numKeys);
        if (!child.isLeaf) System.arraycopy(child.children, 0, child.children, 1, child.numKeys + 1);
        child.keys[0] = keys[idx - 1];
        if (!child.isLeaf) child.children[0] = sibling.children[sibling.numKeys];
        keys[idx - 1] = sibling.keys[sibling.numKeys - 1];
        child.numKeys++;
        sibling.numKeys--;
    }

    private void borrowFromNext(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];
        child.keys[child.numKeys] = keys[idx];
        if (!child.isLeaf) child.children[child.numKeys + 1] = sibling.children[0];
        keys[idx] = sibling.keys[0];
        System.arraycopy(sibling.keys, 1, sibling.keys, 0, sibling.numKeys - 1);
        if (!sibling.isLeaf) System.arraycopy(sibling.children, 1, sibling.children, 0, sibling.numKeys);
        child.numKeys++;
        sibling.numKeys--;
    }

    private void merge(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];
        child.keys[t - 1] = keys[idx];
        System.arraycopy(sibling.keys, 0, child.keys, t, sibling.numKeys);
        if (!child.isLeaf) System.arraycopy(sibling.children, 0, child.children, t, sibling.numKeys + 1);
        System.arraycopy(keys, idx + 1, keys, idx, numKeys - idx - 1);
        System.arraycopy(children, idx + 2, children, idx + 1, numKeys - idx - 1);
        child.numKeys += sibling.numKeys + 1;
        numKeys--;
    }

    public String keysToString() {
        return Arrays.toString(Arrays.copyOf(keys, numKeys));
    }
}
