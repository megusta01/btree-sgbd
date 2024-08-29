package App;

import controller.DatabaseController;
import util.MemoryUtil;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            DatabaseController dbController = new DatabaseController(3); // Inicializa a árvore B com grau mínimo 3

            while (true) {
                System.out.println("\n--- Menu ---");
                System.out.println("1. Inserir registro");
                System.out.println("2. Buscar registro");
                System.out.println("3. Atualizar registro");
                System.out.println("4. Remover registro");
                System.out.println("5. Exibir todos os registros");
                System.out.println("6. Carregar dados de arquivo");
                System.out.println("7. Salvar dados em arquivo");
                System.out.println("8. Gerar registros automaticamente");
                System.out.println("9. Remover registros automaticamente");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                int option = scanner.nextInt();
                switch (option) {
                    case 1: // Inserir registro
                        long memoryBeforeInsert = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória antes da inserção: " + memoryBeforeInsert + " KB");

                        System.out.print("Digite a chave a ser inserida: ");
                        int insertKey = scanner.nextInt();
                        dbController.createRecord(insertKey);

                        long memoryAfterInsert = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória após a inserção: " + memoryAfterInsert + " KB");
                        System.out.println("Consumo de memória durante a inserção: " +
                                (memoryAfterInsert - memoryBeforeInsert) + " KB");
                        break;
                    case 2: // Buscar registro
                        long memoryBeforeSearch = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória antes da busca: " + memoryBeforeSearch + " KB");

                        System.out.print("Digite a chave a ser buscada: ");
                        int searchKey = scanner.nextInt();
                        System.out.println(dbController.readRecord(searchKey));

                        long memoryAfterSearch = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória após a busca: " + memoryAfterSearch + " KB");
                        System.out.println("Consumo de memória durante a busca: " +
                                (memoryAfterSearch - memoryBeforeSearch) + " KB");
                        break;
                    case 3: // Atualizar registro
                        long memoryBeforeUpdate = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória antes da atualização: " + memoryBeforeUpdate + " KB");

                        System.out.print("Digite a chave do registro a ser atualizado: ");
                        int oldKey = scanner.nextInt();
                        System.out.print("Digite a nova chave: ");
                        int newKey = scanner.nextInt();
                        dbController.updateRecord(oldKey, newKey);

                        long memoryAfterUpdate = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória após a atualização: " + memoryAfterUpdate + " KB");
                        System.out.println("Consumo de memória durante a atualização: " +
                                (memoryAfterUpdate - memoryBeforeUpdate) + " KB");
                        break;
                    case 4: // Remover registro
                        long memoryBeforeDelete = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória antes da remoção: " + memoryBeforeDelete + " KB");

                        System.out.print("Digite a chave do registro a ser removido: ");
                        int deleteKey = scanner.nextInt();
                        dbController.deleteRecord(deleteKey);

                        long memoryAfterDelete = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória após a remoção: " + memoryAfterDelete + " KB");
                        System.out.println("Consumo de memória durante a remoção: " +
                                (memoryAfterDelete - memoryBeforeDelete) + " KB");
                        break;
                    case 5: // Exibir todos os registros
                        dbController.displayRecords();
                        break;
                    case 6: // Carregar dados de arquivo
                        System.out.print("Digite o nome do arquivo para carregar os dados: ");
                        String loadFilename = scanner.next();
                        dbController.loadFromFile(loadFilename);
                        break;
                    case 7: // Salvar dados em arquivo
                        System.out.print("Digite o nome do arquivo para salvar os dados: ");
                        String saveFilename = scanner.next();
                        dbController.saveToFile(saveFilename);
                        break;
                    case 8: // Gerar registros automaticamente
                        long memoryBeforeGenerate = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória antes da geração automática: " + memoryBeforeGenerate + " KB\n");

                        System.out.print("Digite o número de registros a serem gerados: ");
                        int numberOfRecords = scanner.nextInt();
                        dbController.generateRecordsAutomatically(numberOfRecords); // Gera e insere registros
                                                                                    // automaticamente
                        System.out.println("\nRegistros gerados automaticamente...");

                        long memoryAfterGenerate = MemoryUtil.measureMemoryInKB();
                        System.out.println("Memória após a geração automática: " + memoryAfterGenerate + " KB\n");
                        System.out.println("Consumo de memória durante a geração automática: " +
                                (memoryAfterGenerate - memoryBeforeGenerate) + " KB");

                        break;
                    case 9: // Remover registros aleatórios de um arquivo
                        System.out.print("Digite o nome do arquivo para remover os registros: ");
                        String removeFilename = scanner.next();
                        System.out.print("Digite o número de registros a serem removidos aleatoriamente: ");
                        int numberOfRecordsToRemove = scanner.nextInt();
                        dbController.removeRandomRecordsFromFile(removeFilename, numberOfRecordsToRemove);
                        break;
                    case 0: // Sair
                        System.out.println("Saindo...");
                        System.exit(0);
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
