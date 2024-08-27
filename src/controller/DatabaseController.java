package controller;

import btree.BTree;
import btree.BTreeNode;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DatabaseController {
    private BTree bTree;
    private int insertCount = 0; // Contador de inserções
    private Set<Integer> insertedKeys; // Conjunto para rastrear chaves inseridas

    // Formatação para milissegundos com 4 casas decimais
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    public DatabaseController(int t) {
        this.bTree = new BTree(t);
        this.insertedKeys = new HashSet<>();
    }

    // Método para salvar a árvore em um arquivo
    public void saveToFile(String filename) {
        File directory = new File("files");
        if (!directory.exists()) {
            directory.mkdir(); // Cria a pasta "files" se ela não existir
        }

        File file = new File(directory, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(bTree);
            System.out.println("Dados salvos com sucesso em " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para carregar a árvore de um arquivo
    public void loadFromFile(String filename) {
        File file = new File("files", filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            bTree = (BTree) ois.readObject();
            System.out.println("Dados carregados com sucesso de " + file.getPath());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Método para inserir um novo registro no banco de dados
    public void createRecord(int key) {
        if (insertedKeys.contains(key)) {
            System.out.println("Chave duplicada detectada: " + key);
            return; // Ignora chaves duplicadas
        }

        long startTime = System.nanoTime();
        bTree.insert(key);
        long endTime = System.nanoTime();

        double durationInMs = (endTime - startTime) / 1_000_000.0; // Converte para milissegundos
        insertedKeys.add(key); // Adiciona a chave ao conjunto de chaves inseridas
        System.out.println(
                "Registro inserido: Chave = " + key + ", Tempo de execução = " + df.format(durationInMs) + " ms");

        // Incrementa o contador de inserções
        insertCount++;
        // Salva os dados periodicamente (a cada 1000 inserções)
        if (insertCount % 1000 == 0) {
            saveToFile("dados_incrementais.bin");
            System.out.println("Salvamento automático realizado após " + insertCount + " inserções.");
        }
    }

    // Método para buscar um registro no banco de dados
    public String readRecord(int key) {
        long startTime = System.nanoTime();
        BTreeNode result = bTree.search(key);
        long endTime = System.nanoTime();
        double durationInMs = (endTime - startTime) / 1_000_000.0;
        return result != null ? "Registro encontrado. Tempo de execução = " + df.format(durationInMs) + " ms"
                : "Registro não encontrado. Tempo de execução = " + df.format(durationInMs) + " ms";
    }

    // Método para atualizar um registro existente
    public void updateRecord(int oldKey, int newKey) {
        long startTime = System.nanoTime();
        BTreeNode result = bTree.search(oldKey);
        if (result != null) {
            bTree.remove(oldKey);
            bTree.insert(newKey);
            insertedKeys.remove(oldKey);
            insertedKeys.add(newKey);
            long endTime = System.nanoTime();
            double durationInMs = (endTime - startTime) / 1_000_000.0;
            System.out.println("Registro atualizado: Chave antiga = " + oldKey + ", Nova chave = " + newKey
                    + ", Tempo de execução = " + df.format(durationInMs) + " ms");
        } else {
            long endTime = System.nanoTime();
            double durationInMs = (endTime - startTime) / 1_000_000.0;
            System.out.println("Registro não encontrado. Tempo de execução = " + df.format(durationInMs) + " ms");
        }
    }

    // Método para remover um registro do banco de dados
    public void deleteRecord(int key) {
        long startTime = System.nanoTime();
        BTreeNode result = bTree.search(key);
        if (result != null) {
            bTree.remove(key);
            insertedKeys.remove(key);
            long endTime = System.nanoTime();
            double durationInMs = (endTime - startTime) / 1_000_000.0;
            System.out.println(
                    "Registro removido: Chave = " + key + ", Tempo de execução = " + df.format(durationInMs) + " ms");
        } else {
            long endTime = System.nanoTime();
            double durationInMs = (endTime - startTime) / 1_000_000.0;
            System.out.println("Registro não encontrado. Tempo de execução = " + df.format(durationInMs) + " ms");
        }
    }

    // Método para exibir todos os registros
    public void displayRecords() {
        bTree.printTreeByLevels();
    }

    // Método para gerar registros automaticamente
    public void generateRecordsAutomatically(int numberOfRecords) {
        Random random = new Random();
        long startTime = System.nanoTime();

        for (int i = 0; i < numberOfRecords; i++) {
            int key = random.nextInt(5000); // Gera números entre 0 e 999
            bTree.insert(key);
            insertedKeys.add(key);
        }

        long endTime = System.nanoTime();
        double durationInMs = (endTime - startTime) / 1_000_000.0;
        System.out.println(
                numberOfRecords + " registros inseridos. Tempo de execução: " + df.format(durationInMs) + " ms");
    }
}
