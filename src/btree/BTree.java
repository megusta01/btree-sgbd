package btree;

public class BTree {
    private BTreeNode root; // Raiz da árvore
    private int t; // Grau mínimo (define o intervalo para o número de chaves)

    // Construtor
    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    // Função para percorrer a árvore
    public void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    // Função para buscar uma chave na árvore
    public BTreeNode search(int key) {
        return (root == null) ? null : root.search(key);
    }

    // Função para inserir uma nova chave na árvore
    public void insert(int key) {
        // Se a árvore estiver vazia
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = key;  // Insira a chave
            root.numKeys = 1;  // Atualize o número de chaves no nó
        } else {
            // Se a raiz estiver cheia, a árvore precisa crescer em altura
            if (root.numKeys == 2 * t - 1) {
                // Aloca uma nova raiz
                BTreeNode newRoot = new BTreeNode(t, false);

                // A antiga raiz se torna filha da nova raiz
                newRoot.children[0] = root;

                // Divide a raiz antiga e move uma chave para a nova raiz
                newRoot.splitChild(0, root);

                // A nova raiz tem duas crianças agora, insira a nova chave no nó correto
                int i = (newRoot.keys[0] < key) ? 1 : 0;
                newRoot.children[i].insertNonFull(key);

                // Atualiza a raiz
                root = newRoot;
            } else {
                // Se a raiz não estiver cheia, insira a nova chave na raiz
                root.insertNonFull(key);
            }
        }
    }

    // Função para remover uma chave da árvore
    public void remove(int key) {
        if (root == null) {
            System.out.println("A árvore está vazia");
            return;
        }

        root.remove(key);

        // Se a raiz tiver 0 chaves após a remoção, faça o filho primeiro como a nova raiz, se existir
        if (root.numKeys == 0) {
            BTreeNode temp = root;
            if (root.isLeaf) {
                root = null;
            } else {
                root = root.children[0];
            }
        }
    }
}
