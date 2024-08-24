package btree;

public class BTreeNode {
    int[] keys;  // Array de chaves
    int t;  // Grau mínimo
    BTreeNode[] children; // Array de filhos
    int numKeys;  // Número atual de chaves
    boolean isLeaf;  // É folha ou não

    // Construtor
    public BTreeNode(int t, boolean isLeaf) {
        this.t = t;
        this.isLeaf = isLeaf;
        this.keys = new int[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.numKeys = 0;
    }

    // Função para percorrer todos os nós de uma árvore
    public void traverse() {
        int i;
        for (i = 0; i < this.numKeys; i++) {
            // Se não for folha, percorre a sub-árvore antes de imprimir a chave
            if (!this.isLeaf) {
                if (this.children[i] != null) {
                    children[i].traverse();
                }
            }
            System.out.print(" " + keys[i]);
        }
    
        // Imprime a sub-árvore após a última chave
        if (!this.isLeaf) {
            if (this.children[i] != null) {
                children[i].traverse();
            }
        }
    }
    

    // Função para buscar uma chave neste nó
    public BTreeNode search(int key) {
        int i = 0;
        while (i < numKeys && key > keys[i]) {
            i++;
        }

        // Se a chave é encontrada neste nó
        if (keys[i] == key) {
            return this;
        }

        // Se a chave não é encontrada aqui e este é um nó folha
        if (isLeaf) {
            return null;
        }

        // Buscar na sub-árvore apropriada
        return children[i].search(key);
    }

    // Função para inserir uma nova chave neste nó. O nó deve ter espaço para a nova chave
    public void insertNonFull(int key) {
        int i = numKeys - 1;
    
        // Se este é um nó folha
        if (isLeaf) {
            // Encontra a localização da nova chave e move todas as chaves maiores para uma posição à frente
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                i--;
            }
    
            // Insere a nova chave
            keys[i + 1] = key;
            numKeys = numKeys + 1;
        } else {  // Se este nó não for folha
            // Encontra o filho que receberá a nova chave
            while (i >= 0 && keys[i] > key) {
                i--;
            }
    
            // Verifica se o filho encontrado está cheio
            if (children[i + 1] == null) {
                // Inicializa o filho se ele for nulo
                children[i + 1] = new BTreeNode(t, true);
            }
    
            if (children[i + 1].numKeys == 2 * t - 1) {
                // Se o filho estiver cheio, divida-o
                splitChild(i + 1, children[i + 1]);
    
                // Após a divisão, a chave do meio sobe para este nó e pode haver a necessidade de mover a nova chave para uma das duas crianças
                if (keys[i + 1] < key) {
                    i++;
                }
            }
            children[i + 1].insertNonFull(key);
        }
    }

    // Função para dividir a criança y de um nó. i é o índice da criança y em this.children. A criança y deve estar cheia quando esta função é chamada
    public void splitChild(int i, BTreeNode y) {
        // Cria um novo nó que armazenará t-1 chaves de y
        BTreeNode z = new BTreeNode(y.t, y.isLeaf);
        z.numKeys = t - 1;

        // Copia as últimas t-1 chaves de y para z
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
        }

        // Copia os últimos t filhos de y para z
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children[j + t] = y.children[j + t];
            }
        }

        // Reduz o número de chaves em y
        y.numKeys = t - 1;

        // Move os filhos de this para dar espaço ao novo filho z
        for (int j = numKeys; j >= i + 1; j--) {
            children[j + 1] = children[j];
        }

        // Liga o novo filho a this
        children[i + 1] = z;

        // Move as chaves de this para dar espaço à nova chave
        for (int j = numKeys - 1; j >= i; j--) {
            keys[j + 1] = keys[j];
        }

        // Copia a chave do meio de y para this
        keys[i] = y.keys[t - 1];

        // Incrementa o número de chaves em this
        numKeys = numKeys + 1;
    }

    // Função para remover uma chave deste nó
    public void remove(int key) {
        int idx = findKey(key);

        // Se a chave está presente neste nó
        if (idx < numKeys && keys[idx] == key) {
            // Se o nó é uma folha, remover diretamente
            if (isLeaf) {
                removeFromLeaf(idx);
            } else { // Se o nó não é uma folha, remova de um nó interno
                removeFromNonLeaf(idx);
            }
        } else {
            // Se este nó é uma folha, a chave não está nesta árvore
            if (isLeaf) {
                System.out.println("A chave " + key + " não está na árvore");
                return;
            }

            // Caso contrário, a chave a ser removida está na sub-árvore enraizada neste nó
            boolean flag = (idx == numKeys);

            // Se o filho onde a chave deveria estar tem menos de t chaves, preencha-o
            if (children[idx].numKeys < t) {
                fill(idx);
            }

            // Se o último filho foi fundido, recorra ao filho idx-1, caso contrário, ao filho idx
            if (flag && idx > numKeys) {
                children[idx - 1].remove(key);
            } else {
                children[idx].remove(key);
            }
        }
    }

    // Encontra o índice da primeira chave maior ou igual a key
    private int findKey(int key) {
        int idx = 0;
        while (idx < numKeys && keys[idx] < key) {
            idx++;
        }
        return idx;
    }

    // Função para remover a chave idx de um nó folha
    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < numKeys; ++i) {
            keys[i - 1] = keys[i];
        }
        numKeys--;
    }

    // Função para remover a chave idx de um nó não folha
    private void removeFromNonLeaf(int idx) {
        int key = keys[idx];

        // Se o filho à esquerda de idx (children[idx]) tem pelo menos t chaves, encontre o predecessor da chave
        // na sub-árvore children[idx]. Substitua key pelo predecessor. Remova o predecessor em children[idx]
        if (children[idx].numKeys >= t) {
            int pred = getPredecessor(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        } else if (children[idx + 1].numKeys >= t) { // Se o filho children[idx] tiver menos de t chaves
            int succ = getSuccessor(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ);
        } else { // Se ambos os filhos children[idx] e children[idx+1] tiverem menos de t chaves, funde-os
            merge(idx);
            children[idx].remove(key);
        }
    }

    // Função para obter o predecessor da chave[idx]
    private int getPredecessor(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.isLeaf) {
            cur = cur.children[cur.numKeys];
        }
        return cur.keys[cur.numKeys - 1];
    }

    // Função para obter o sucessor da chave[idx]
    private int getSuccessor(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.isLeaf) {
            cur = cur.children[0];
        }
        return cur.keys[0];
    }

    // Função para preencher o filho children[idx] que tem menos de t chaves
    private void fill(int idx) {
        // Se o filho anterior (children[idx-1]) tiver mais de t-1 chaves, empresta uma chave dele
        if (idx != 0 && children[idx - 1].numKeys >= t) {
            borrowFromPrev(idx);
        } else if (idx != numKeys && children[idx + 1].numKeys >= t) { // Se o filho próximo (children[idx+1]) tiver mais de t-1 chaves, empresta uma chave dele
            borrowFromNext(idx);
        } else { // Caso contrário, funde children[idx] com seu irmão
            if (idx != numKeys) {
                merge(idx);
            } else {
                merge(idx - 1);
            }
        }
    }

    // Função para emprestar uma chave de children[idx-1] e inseri-la em children[idx]
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

    // Função para emprestar uma chave de children[idx+1] e inseri-la em children[idx]
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

    // Função para fundir children[idx] com children[idx+1]
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
}
