package controller;

import btree.BTree;
import util.MemoryUtil;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    // Método para remover registros aleatórios de um arquivo

    public void removeRandomRecordsFromFile(String filename, int numberOfRecordsToRemove) {
    File file = new File("files", filename);
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        BTree loadedTree = (BTree) ois.readObject();
        List<Integer> allKeys = loadedTree.getAllKeys();

        if (allKeys.size() < numberOfRecordsToRemove) {
            System.out.println("O arquivo contém menos registros do que o número solicitado para remoção.");
            return;
        }

        Collections.shuffle(allKeys);
        List<Integer> keysToRemove = allKeys.subList(0, numberOfRecordsToRemove);


        long memoryBeforeRemoval = MemoryUtil.measureMemoryInKB();
        long startTime = System.nanoTime();

        for (int key : keysToRemove) {
            loadedTree.remove(key);
            insertedKeys.remove(key);
        }

        long endTime = System.nanoTime();
        long memoryAfterRemoval = MemoryUtil.measureMemoryInKB();
        double durationInMs = (endTime - startTime) / 1_000_000.0;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(loadedTree);
            System.out.println(numberOfRecordsToRemove + " registros removidos aleatoriamente do arquivo " + file.getPath());
        }

        System.out.println("Memória antes da remoção: " + memoryBeforeRemoval + " KB");
        System.out.println("Memória após a remoção: " + memoryAfterRemoval + " KB");
        System.out.println("Consumo de memória durante a remoção: " + (memoryAfterRemoval - memoryBeforeRemoval) + " KB");
        System.out.println("Tempo de execução da remoção: " + durationInMs + " ms");

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}

public void updateRandomRecordsFromFile(String filename, int numberOfRecordsToUpdate) {
    File file = new File("files", filename);
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        BTree loadedTree = (BTree) ois.readObject();
        List<Integer> allKeys = loadedTree.getAllKeys();

        if (allKeys.size() < numberOfRecordsToUpdate) {
            System.out.println("O arquivo contém menos registros do que o número solicitado para atualização.");
            return;
        }

        // Embaralhar as chaves para selecionar aleatoriamente
        Collections.shuffle(allKeys);
        List<Integer> keysToUpdate = allKeys.subList(0, numberOfRecordsToUpdate);

        // Medição de memória e tempo antes da atualização
        long memoryBeforeUpdate = MemoryUtil.measureMemoryInKB();
        long startTime = System.nanoTime();

        // Atualizar as chaves selecionadas
        for (int oldKey : keysToUpdate) {
            int newKey = generateNewKey(oldKey); // Método para gerar uma nova chave
            loadedTree.remove(oldKey);
            loadedTree.insert(newKey);
            insertedKeys.remove(oldKey);
            insertedKeys.add(newKey);
        }

        // Medição de memória e tempo após a atualização
        long endTime = System.nanoTime();
        long memoryAfterUpdate = MemoryUtil.measureMemoryInKB();
        double durationInMs = (endTime - startTime) / 1_000_000.0;

        // Salva a árvore B de volta para o arquivo após a atualização
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(loadedTree);
            System.out.println(numberOfRecordsToUpdate + " registros atualizados aleatoriamente no arquivo " + file.getPath());
        }

        // Exibir informações de desempenho
        System.out.println("Memória antes da atualização: " + memoryBeforeUpdate + " KB");
        System.out.println("Memória após a atualização: " + memoryAfterUpdate + " KB");
        System.out.println("Consumo de memória durante a atualização: " + (memoryAfterUpdate - memoryBeforeUpdate) + " KB");
        System.out.println("Tempo de execução da atualização: " + durationInMs + " ms");

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}

// Método auxiliar para gerar uma nova chave (garantir que não exista duplicata)
private int generateNewKey(int oldKey) {
    Random random = new Random();
    int newKey;
    do {
        newKey = random.nextInt(5000); // ou outro intervalo adequado
    } while (insertedKeys.contains(newKey)); // Garante que a nova chave não seja duplicada
    return newKey;
}

 // Método para inserir registros aleatórios de um arquivo

public void insertRandomRecordsFromFile(String filename, int numberOfRecordsToInsert) {
    File file = new File("files", filename);
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        BTree loadedTree = (BTree) ois.readObject();

        // Medição de memória e tempo antes da inserção
        long memoryBeforeInsert = MemoryUtil.measureMemoryInKB();
        long startTime = System.nanoTime();

        // Inserir registros aleatórios
        Random random = new Random();
        for (int i = 0; i < numberOfRecordsToInsert; i++) {
            int key = random.nextInt(5000); // ou outro intervalo adequado
            loadedTree.insert(key);
            insertedKeys.add(key);
        }

        // Medição de memória e tempo após a inserção
        long endTime = System.nanoTime();
        long memoryAfterInsert = MemoryUtil.measureMemoryInKB();
        double durationInMs = (endTime - startTime) / 1_000_000.0;

        // Salva a árvore B de volta para o arquivo após a inserção
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(loadedTree);
            System.out.println(numberOfRecordsToInsert + " registros inseridos aleatoriamente no arquivo " + file.getPath());
        }

        // Exibir informações de desempenho
        System.out.println("Memória antes da inserção: " + memoryBeforeInsert + " KB");
        System.out.println("Memória após a inserção: " + memoryAfterInsert + " KB");
        System.out.println("Consumo de memória durante a inserção: " + (memoryAfterInsert - memoryBeforeInsert) + " KB");
        System.out.println("Tempo de execução da inserção: " + durationInMs + " ms");

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }

}
}
