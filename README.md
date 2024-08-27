# Sistema Gerenciador de Banco de Dados Simples Utilizando Árvores B

Este projeto implementa um Sistema Gerenciador de Banco de Dados (SGBD) simples utilizando a estrutura de dados Árvore B para realizar as operações básicas de CRUD (Create, Read, Update, Delete). O objetivo é demonstrar o uso prático de árvores B na indexação de dados e avaliar o desempenho do sistema em diferentes cenários.

## Funcionalidades

- **Inserção de Registros (Create):** Adiciona novos registros ao banco de dados, indexando-os na Árvore B.
- **Busca de Registros (Read):** Realiza consultas no banco de dados utilizando a Árvore B para localizar registros específicos.
- **Atualização de Registros (Update):** Permite modificar registros existentes no banco de dados.
- **Remoção de Registros (Delete):** Remove registros do banco de dados e atualiza a Árvore B para manter a consistência.
- **Geração Automática de Registros:** Gera um conjunto de registros aleatórios para testar o sistema.
- **Persistência de Dados:** Salva e carrega o estado do banco de dados em arquivos, permitindo a continuidade entre execuções.

## Estrutura do Projeto

- **`BTree.java:`** Implementa a estrutura da Árvore B e suas operações principais.
- **`BTreeNode.java:`** Define os nós da Árvore B e suas operações internas.
- **`DatabaseController.java:`** Gerencia as operações CRUD, geração de registros, e persistência de dados.
- **`DataGenerator.java:`** Gera registros aleatórios para teste.
- **`Main.java:`** Interface de linha de comando para interação com o sistema.
