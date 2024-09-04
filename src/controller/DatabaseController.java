package controller;

import btree.BTree;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DatabaseController {
    private BTree bTree;
    private int insertCount = 0; // Contador de inserções
    private final Set<Integer> insertedKeys = new HashSet<>(); // Conjunto para rastrear chaves inseridas
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    public DatabaseController(int t) {
        this.bTree = new BTree(t);
    }

    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("files", filename)))) {
            oos.writeObject(bTree);
            System.out.println("Dados salvos com sucesso em " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("files", filename)))) {
            bTree = (BTree) ois.readObject();
            System.out.println("Dados carregados com sucesso de " + filename);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createRecord(int key) {
        if (!insertedKeys.add(key)) {
            System.out.println("Chave duplicada detectada: " + key);
            return;
        }
        double durationInMs = timeExecution(() -> bTree.insert(key));
        System.out.println("Registro inserido: Chave = " + key + ", Tempo de execução = " + df.format(durationInMs) + " ms");

        if (++insertCount % 1000 == 0) {
            saveToFile("dados_incrementais.bin");
            System.out.println("Salvamento automático após " + insertCount + " inserções.");
        }
    }

    public String readRecord(int key) {
        double durationInMs = timeExecution(() -> bTree.search(key));
        return bTree.search(key) != null ? "Registro encontrado" : "Registro não encontrado"
               + ". Tempo de execução = " + df.format(durationInMs) + " ms";
    }

    public void updateRecord(int oldKey, int newKey) {
        double durationInMs = timeExecution(() -> {
            if (bTree.search(oldKey) != null) {
                bTree.remove(oldKey);
                bTree.insert(newKey);
                insertedKeys.remove(oldKey);
                insertedKeys.add(newKey);
                System.out.println("Registro atualizado: Chave antiga = " + oldKey + ", Nova chave = " + newKey);
            } else {
                System.out.println("Registro não encontrado.");
            }
        });
        System.out.println("Tempo de execução = " + df.format(durationInMs) + " ms");
    }

    public void deleteRecord(int key) {
        double durationInMs = timeExecution(() -> {
            if (bTree.search(key) != null) {
                bTree.remove(key);
                insertedKeys.remove(key);
                System.out.println("Registro removido: Chave = " + key);
            } else {
                System.out.println("Registro não encontrado.");
            }
        });
        System.out.println("Tempo de execução = " + df.format(durationInMs) + " ms");
    }

    public void displayRecords() {
        bTree.printTreeByLevels();
    }

    public void generateRecordsAutomatically(int numberOfRecords) {
        Random random = new Random();
        double durationInMs = timeExecution(() -> {
            for (int i = 0; i < numberOfRecords; i++) {
                int key = random.nextInt(5000);
                if (insertedKeys.add(key)) {
                    bTree.insert(key);
                }
            }
        });
        System.out.println(numberOfRecords + " registros inseridos. Tempo de execução: " + df.format(durationInMs) + " ms");
    }

    private double timeExecution(Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        return (System.nanoTime() - startTime) / 1_000_000.0;
    }
}
