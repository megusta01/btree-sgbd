package util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DataGenerator {
    private Random random;

    // Construtor
    public DataGenerator() {
        this.random = new Random();
    }

    // Função para gerar um conjunto de dados aleatórios com chaves únicas
    public Set<Integer> generateUniqueKeys(int numberOfRecords, int min, int max) {
        Set<Integer> keys = new HashSet<>();

        // Garante que as chaves são únicas
        while (keys.size() < numberOfRecords) {
            int key = random.nextInt(max - min + 1) + min;
            keys.add(key);
        }

        return keys;
    }

    // Função para gerar um único valor aleatório para simular dados associados a uma chave
    public String generateRandomValue() {
        // Gera um valor aleatório entre 1000 e 9999 (exemplo simples)
        int value = random.nextInt(9000) + 1000;
        return "Value-" + value;
    }

    // Função para gerar um conjunto de valores aleatórios para simular dados associados às chaves
    public String[] generateRandomValues(int numberOfRecords) {
        String[] values = new String[numberOfRecords];

        for (int i = 0; i < numberOfRecords; i++) {
            values[i] = generateRandomValue();
        }

        return values;
    }
}
