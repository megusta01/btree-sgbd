package util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import controller.DatabaseController;

public class DataGenerator {
    private Random random;
    private Object insertedKeys;

    // Gerar um conjunto de chaves únicas dentro de um intervalo
    public Set<Integer> generateUniqueKeys(int numberOfRecords, int min, int max) {
        Set<Integer> keys = new HashSet<>();
        while (keys.size() < numberOfRecords) {
            keys.add(random.nextInt(max - min + 1) + min);
        }
        return keys;
    }

    // Gerar UUIDs como chaves
    public Set<Integer> generateUniqueUUIDs(int numberOfRecords) {
        Set<Integer> keys = new HashSet<>();
        while (keys.size() < numberOfRecords) {
            keys.add(UUID.randomUUID().hashCode());
        }
        return keys;
    }

    // Gerar chaves ilimitadas, salvando periodicamente
    public void generateUnlimitedRecords(DatabaseController dbController) {
        int count = 0;
        while (true) {
            int key = random.nextInt(5000); 
            dbController.createRecord(key);
            count++;
            if (count % 5000 == 0) {
                System.out.println(count + " registros gerados.");
                dbController.saveToFile("dados_incrementais.bin");
            }
        }
    }


}
