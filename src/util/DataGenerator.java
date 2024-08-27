package util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import controller.DatabaseController;

public class DataGenerator {
    private Random random;

    public DataGenerator() {
        this.random = new Random();
    }

    // Método para gerar um conjunto de chaves únicas usando um intervalo muito grande
    public Set<Integer> generateUniqueKeys(int numberOfRecords, int min, int max) {
        Set<Integer> keys = new HashSet<>();
        while (keys.size() < numberOfRecords) {
            int key = random.nextInt(max - min + 1) + min;
            keys.add(key);
        }
        return keys;
    }

    // Método para gerar UUIDs como chaves
    public Set<Integer> generateUniqueUUIDs(int numberOfRecords) {
        Set<Integer> keys = new HashSet<>();
        while (keys.size() < numberOfRecords) {
            String uuid = UUID.randomUUID().toString();
            keys.add(uuid.hashCode());
        }
        return keys;
    }

    // Método para gerar chaves de forma ilimitada, salvando periodicamente
    public void generateUnlimitedRecords(DatabaseController dbController) {
        int count = 0;
        while (true) {
            int key = random.nextInt(1000); // Gera números entre 0 e 999
            dbController.createRecord(key);
            count++;
            if (count % 1000 == 0) {
                System.out.println(count + " registros gerados.");
                dbController.saveToFile("dados_incrementais.bin"); // Salvamento periódico
            }
        }
    }
}
