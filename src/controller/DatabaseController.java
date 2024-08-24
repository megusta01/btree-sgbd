package controller;

import btree.BTree;
import btree.BTreeNode;

public class DatabaseController {
    private BTree bTree;

    // Construtor - inicializa a árvore B com o grau mínimo t
    public DatabaseController(int t) {
        this.bTree = new BTree(t);
    }

    // Função para inserir um novo registro no banco de dados
    public void createRecord(int key) {
        System.out.println("Inserindo registro com chave: " + key);
        bTree.insert(key);
    }

    // Função para buscar um registro no banco de dados
    public String readRecord(int key) {
        BTreeNode result = bTree.search(key);
        if (result != null) {
            return "Registro encontrado com a chave: " + key;
        } else {
            return "Registro não encontrado com a chave: " + key;
        }
    }

    // Função para atualizar um registro existente
    // Para simplificação, esse método remove o registro antigo e insere um novo
    public void updateRecord(int oldKey, int newKey) {
        System.out.println("Atualizando registro com chave: " + oldKey + " para nova chave: " + newKey);
        BTreeNode result = bTree.search(oldKey);
        if (result != null) {
            bTree.remove(oldKey);
            bTree.insert(newKey);
            System.out.println("Registro atualizado com sucesso!");
        } else {
            System.out.println("Registro não encontrado com a chave: " + oldKey);
        }
    }

    // Função para remover um registro do banco de dados
    public void deleteRecord(int key) {
        System.out.println("Removendo registro com chave: " + key);
        BTreeNode result = bTree.search(key);
        if (result != null) {
            bTree.remove(key);
            System.out.println("Registro removido com sucesso!");
        } else {
            System.out.println("Registro não encontrado com a chave: " + key);
        }
    }

    // Função para percorrer a árvore e exibir todos os registros (para fins de depuração)
    public void displayRecords() {
        System.out.println("Exibindo todos os registros:");
        bTree.traverse();
        System.out.println();
    }
}
