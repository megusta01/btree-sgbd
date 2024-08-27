package App;

import controller.DatabaseController;

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
                System.out.println("6. Gerar registros automaticamente");
                System.out.println("7. Salvar dados em arquivo");
                System.out.println("8. Carregar dados de arquivo");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                int option = scanner.nextInt();
                switch (option) {
                    case 1: // Inserir registro
                        System.out.print("Digite a chave a ser inserida: ");
                        int insertKey = scanner.nextInt();
                        dbController.createRecord(insertKey);
                        break;
                    case 2: // Buscar registro
                        System.out.print("Digite a chave a ser buscada: ");
                        int searchKey = scanner.nextInt();
                        System.out.println(dbController.readRecord(searchKey));
                        break;
                    case 3: // Atualizar registro
                        System.out.print("Digite a chave do registro a ser atualizado: ");
                        int oldKey = scanner.nextInt();
                        System.out.print("Digite a nova chave: ");
                        int newKey = scanner.nextInt();
                        dbController.updateRecord(oldKey, newKey);
                        break;
                    case 4: // Remover registro
                        System.out.print("Digite a chave do registro a ser removido: ");
                        int deleteKey = scanner.nextInt();
                        dbController.deleteRecord(deleteKey);
                        break;
                    case 5: // Exibir todos os registros
                        dbController.displayRecords();
                        break;
                    case 6: // Gerar registros automaticamente
                        System.out.print("Digite o número de registros a serem gerados: ");
                        int numberOfRecords = scanner.nextInt();
                        dbController.generateRecordsAutomatically(numberOfRecords); // Gera e insere registros
                                                                                    // automaticamente
                        System.out.println("Registros gerados automaticamente.");
                        break;
                    case 7: // Salvar dados em arquivo
                        System.out.print("Digite o nome do arquivo para salvar os dados: ");
                        String saveFilename = scanner.next();
                        dbController.saveToFile(saveFilename);
                        break;
                    case 8: // Carregar dados de arquivo
                        System.out.print("Digite o nome do arquivo para carregar os dados: ");
                        String loadFilename = scanner.next();
                        dbController.loadFromFile(loadFilename);
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
