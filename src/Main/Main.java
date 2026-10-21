package Main;

import DAO.*;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import models.*;

import services.MonitorContas;

/**
 * Classe principal do sistema de gestão bancária.
 * Gere a interface de linha de comandos (CLI) e coordena as operações entre
 * os modelos, os DAOs (Data Access Objects) e os serviços de background.
 * @author Grupo: Gustavo Metrolho, Pedro Martins e Rodrigo Rocha
 * @version 1.0
 */
public class Main {
    /** Scanner para leitura de dados do teclado. */
    private static final Scanner scn = new Scanner(System.in);
    
    /** Thread responsável pela execução do serviço de monitorização. */
    private static Thread monitorThread;
    
    /** Instância do serviço que monitoriza as contas em segundo plano. */
    private static MonitorContas monitor;
  	
    /**
     * Solicita os dados ao utilizador e cria um novo registo de Cliente na base de dados.
     * Inclui validações de NIF (9 dígitos e unicidade) e idade mínima (18 anos).
     * Permite até 3 tentativas falhadas para a introdução do NIF.
     */
    public static void createClient() {
        ClienteDAO Cdao = new ClienteDAO();

        int nifErrorCount = 0;
        int id = -1;

        System.out.print("Nome: ");
        String nome = scn.nextLine();

        String nif;
        while (true) {

            if (nifErrorCount >= 3) {
                System.out.println("Atenção: Três tentativas falhadas de NIF. Ação cancelada.");
                return;
            }

            System.out.print("NIF (9 dígitos): ");
            nif = scn.nextLine().trim();
            if (!nif.matches("\\d{9}")) {
                System.out.println("NIF inválido. Deve conter exatamente 9 dígitos numéricos.");
                nifErrorCount++;
                continue;
            }

            // INFO: verificar se já existe outro cliente com este NIF
            try {
                Cliente existente = Cdao.lerPorNIF(nif);
                if (existente != null) {
                    System.out.println("Já existe um cliente com esse NIF. Insira outro NIF.");
                    nifErrorCount++;
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Aviso: não foi possível verificar NIF no BD: " + e.getMessage());
            }

            break;
        }

        System.out.print("Morada: ");
        String morada = scn.nextLine();

        int idade = -1;
        while (true) {
            try {
                System.out.print("Idade: ");
                idade = scn.nextInt();
                scn.nextLine();
                if (idade < 18) {
                    System.out.println("O cliente deve ter pelo menos 18 anos.");
                    continue;
                } else if (idade > 120) {
                    System.out.println("Idade inválida. Por favor, insira um valor realista.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Idade inválida. Insira um número inteiro.");
                scn.nextLine();
            }
        }

        Cliente cliente = new Cliente(id, nome, nif, morada, idade);

        try {
            Cdao.criar(cliente);
            System.out.println("Cliente criado com sucesso (id: " + cliente.getId_cliente() + ").");
        } catch (Exception e) {
            System.out.println("Erro ao criar cliente: " + e.getMessage());
        }
    }
    
    /**
     * Menu interativo para atualizar os dados pessoais de um cliente específico.
     * Permite alterar nome, NIF, morada e idade, persistindo as alterações na base de dados.
     * * @param cliente O objeto Cliente que será atualizado.
     */
    public static void updateInfoAccount(Cliente cliente) { // REVIEW - é preciso pedir para eles verem se esta tudo correto e de acordo com o que querem!

        ClienteDAO cDAO = new ClienteDAO();
        int optionMenuCliente = -1;
        boolean exit = false;
        int nifErrorCount = 0;

        do {
            try {
                System.out.println("|================== Atualizar Informação do Cliente ==================|");
                System.out.printf("| ID Cliente: %-55d |\n", cliente.getId_cliente());
                System.out.printf("| Nome: %-61s |\n", cliente.getNome());
                System.out.printf("| NIF: %-62s |\n", cliente.getNIF());
                System.out.printf("| Morada: %-59s |\n", cliente.getMorada());
                System.out.printf("| Idade: %-60d |\n", cliente.getIdade());
                System.out.println("|=====================================================================|");

                System.out.println("\nO que deseja atualizar?");
                System.out.println("[1] Nome");
                System.out.println("[2] NIF");
                System.out.println("[3] Morada");
                System.out.println("[4] Idade");
                System.out.println("[0] Sair e Guardar Alterações");
                System.out.println("Opção: ");

                optionMenuCliente = scn.nextInt();
                scn.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor insira um número para a opção");
                scn.nextLine();
                optionMenuCliente = -1;
            }

            switch (optionMenuCliente) {
                case 1:
                    System.out.print("Novo Nome: ");
                    String novoNome = scn.nextLine();
                    cliente.setNome(novoNome);
                    System.out.println("Nome atualizado com sucesso!");
                    break;

                case 2:
                while (true) {

                if (nifErrorCount >= 3) {
                        System.out.println("Atenção: Três tentativas falhadas de NIF. Ação cancelada.");
                return;
                }
                    System.out.print("NIF (9 dígitos): ");
                    String novoNif = scn.nextLine().trim();

                if (!novoNif.matches("\\d{9}")) {
                    System.out.println("NIF inválido. Deve conter exatamente 9 dígitos numéricos.");
                    nifErrorCount++;
                    continue;
                }

                if (cliente.getNIF().equals(novoNif)) {
                    System.out.println("NIF igual ao atual. Insira um NIF diferente.");
                    nifErrorCount++;
                    continue;
                }

                try {
                    Cliente outroCliente = cDAO.lerPorNIF(novoNif);
                    if (outroCliente != null) {
                        System.out.println("Já existe outro cliente com esse NIF. Insira outro NIF.");
                        nifErrorCount++;
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao verificar NIF no BD: " + e.getMessage());
                }

                cliente.setNIF(novoNif);
                System.out.println("NIF atualizado com sucesso!");
                break;
                }
                    break;

                case 3:
                    System.out.print("Nova Morada: ");
                    String novaMorada = scn.nextLine();
                    cliente.setMorada(novaMorada);
                    System.out.println("Morada atualizado com sucesso!");
                    break;

                case 4:
                    int novaIdade = -1;

                    while (true) {
                        try {
                            System.out.print("Nova Idade: ");
                            novaIdade = scn.nextInt();
                            scn.nextLine();

                            if (novaIdade < 18) {
                            System.out.println("O cliente deve ter pelo menos 18 anos.");
                            continue;
                            } else if (novaIdade > 120) {
                            System.out.println("Idade inválida. Por favor, insira um valor realista.");
                            continue;
                            }

                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Idade inválida. Insira um número inteiro.");
                            scn.nextLine();
                        }

                                        }
                    cliente.setIdade(novaIdade);
                    System.out.println("Idade atualizado com sucesso!");
                    break;
                case 0: 

                    try {
                        cDAO.atualizar(cliente);
                        System.out.println("Informação do cliente atualizada com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Erro ao atualizar informação do cliente: " + e.getMessage());
                    }
                    exit = true;
                    System.out.println("Voltando ao menu anterior...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.\n");
                    break;
            }

        } while (!exit);

    }

    /**
     * Apresenta no ecrã todas as contas bancárias associadas a um determinado cliente.
     * Lista o ID da conta, o tipo (Corrente/Poupança) e o respetivo saldo.
     * * @param cliente O cliente do qual se pretende listar as contas.
     */
    public static void showAccountsAndInfo(Cliente cliente) { 
        ContaDAO contaDAO = new ContaDAO();

        System.out.println("\n|================ Contas Associadas ================|");
        try {
            java.util.List<Conta> contas = contaDAO.getContasByCliente(cliente.getId_cliente());
            if (contas.isEmpty()) {
                System.out.println("|      Nenhuma conta associada a este cliente.      |");
                System.out.println("|===================================================|");

            } else {
                for (Conta conta : contas) {
                    System.out.printf("|ID Conta: %-2d | Tipo: %-10s | Saldo: %-10.2f|\n",
                            conta.getId_conta(),
                            (conta instanceof ContaCorrente) ? "Corrente" : "Poupança",
                            conta.getSaldo());
                }
            System.out.println("|===================================================|");
            }
        } catch (Exception e) {
            System.out.println("Erro ao obter contas do cliente: " + e.getMessage());
        }
    }
    
    /**
     * Menu de gestão de um cliente específico.
     * Requer autenticação por ID e NIF. Permite aceder à atualização de dados
     * ou visualização de informações de contas.
     */
    public static void manageClient() {

        ClienteDAO clienteDAO = new ClienteDAO();
        int idCliente = -1;
        String nif = "";
        Cliente cliente = null;
        int nifErrorCount = 0;

            try {
                System.out.print("Digite o ID de Cliente: ");
                idCliente = scn.nextInt();
                scn.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("ID de Cliente inválido.");
                scn.nextLine();
                return;
            }

            while (true) {

                if (nifErrorCount >= 3) {
                System.out.println("Atenção: Três tentativas falhadas de NIF. Ação cancelada.");
                return;
                }

                System.out.print("NIF (9 dígitos): ");
                nif = scn.nextLine().trim();

                if (!nif.matches("\\d{9}")) {
                    System.out.println("NIF inválido. Deve conter exatamente 9 dígitos numéricos.");
                    nifErrorCount++;
                    continue;
                }

                try {
                    cliente = clienteDAO.ler(idCliente);

                    if (cliente == null) {
                        System.out.println("Cliente não encontrado.");
                        return;
                    }


                    if (!cliente.getNIF().equals(nif)) {
                        System.out.println("NIF não corresponde ao cliente. Tente novamente.");
                        nifErrorCount++;
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao aceder ao BD: " + e.getMessage());
                    return;
                }

                break;
            }



        int optionMenu = -1;
        boolean exit = false;


        do {
            try {
                System.out.println("\n|=============== INFORMAÇÃO CLIENTE ===============|");
                System.out.printf("| Nome: %-42s |\n", cliente.getNome());
                System.out.printf("| ID: %-44d |\n", cliente.getId_cliente());
                System.out.printf("| NIF: %-43s |\n", cliente.getNIF());
                System.out.println("|================== MENU CLIENTE ==================|");
                System.out.println("| [1] Atualizar Dados do Cliente                   |");
                System.out.println("| [2] Informação da Conta                          |");
                System.out.println("| [0] Sair                                         |");
                System.out.println("|==================================================|\n");
                System.out.println("Opcao: ");
                optionMenu = scn.nextInt();
                scn.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor insira um número para a opção");
                scn.nextLine();
                optionMenu = -1;
            }


            switch (optionMenu) {
                case 1:
                    updateInfoAccount(cliente);
                    break;
                case 2:
                    showAccountsAndInfo(cliente);
                    break;
                case 0: // info: Sair
                    exit = true;
                    System.out.println("Voltar para o menu principal...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.\n");
            }
        } while (!exit);
    }

    /**
     * Inicia o processo de criação de uma nova conta bancária (Corrente ou Poupança).
     * Aplica a regra de negócio que limita a um máximo de 3 contas por cliente.
     * Valida limites de descoberto para contas correntes e taxas de juro para poupanças.
     */
    public static void createAccount() {
        ClienteDAO clienteDAO = new ClienteDAO();
        ContaDAO contaDAO = new ContaDAO();

        int nifErrorCount = 0;
        int idCliente = -1;
        String nif = "";

        System.out.println("\n========= Criar Conta =========");

            try {
                System.out.print("Digite o ID de Cliente: ");
                idCliente = scn.nextInt();
                scn.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("ID de Cliente inválido.");
                scn.nextLine();
                return;
            }


            while (true) {
                if (nifErrorCount >= 3) {
                System.out.println("Atenção: Três tentativas falhadas de NIF. Ação cancelada.");
                return;
                }
                System.out.print("NIF (9 dígitos): ");
                nif = scn.nextLine().trim();

                if (!nif.matches("\\d{9}")) {
                    System.out.println("NIF inválido. Deve conter exatamente 9 dígitos numéricos.");
                    nifErrorCount++;
                    continue;
                }

                try {
                    Cliente cliente = clienteDAO.ler(idCliente);

                    if (cliente == null) {
                        System.out.println("Cliente não encontrado.");
                        return;
                    }


                    if (!cliente.getNIF().equals(nif)) {
                        System.out.println("NIF não corresponde ao cliente. Tente novamente.");
                        nifErrorCount++;
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao aceder ao BD: " + e.getMessage());
                    return;
                }

                break;
            }

        // INFO: Limitar contas por pessoa a 3 contas
        try {
            java.util.List<Conta> contasExistentes = contaDAO.getContasByCliente(idCliente);
            if (contasExistentes != null && contasExistentes.size() >= 3) {
                System.out.println("O cliente já tem 3 contas. Não é possível criar mais.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar contas do cliente: " + e.getMessage());
            return;
        }

        // INFO: Tipo de Conta a ser criada (poupança ou corrente)
        int optionAccountType = -1;

        while (true) {
        System.out.println("\n|========= Tipo de Conta =========|");
        System.out.println("| [1] Conta Corrente              |");
        System.out.println("| [2] Conta Poupança              |");
        System.out.println("|=================================|");
        System.out.print("Opcao: ");

            try {
                optionAccountType = scn.nextInt();
                scn.nextLine();

                if (optionAccountType != 1 && optionAccountType != 2) {
                    System.out.println("Opção inválida. Escolha 1 ou 2.");
                    optionAccountType = -1;
                    continue;
                }
                break;

            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor insira um número para a opção");
                optionAccountType = -1;
                scn.nextLine();
            }
        }

        Conta novaConta = null;

        switch (optionAccountType) {
            case 1:
                System.out.println("\n========= Criar Conta Corrente =========");
                double limiteDescoberto = 0;
                while (true) {
                    try {
                        System.out.print("Limite de Descoberto: ");
                        limiteDescoberto = scn.nextDouble();
                        scn.nextLine();
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Limite inválido. Insira um número válido.");
                        scn.nextLine();
                    }
                }

                novaConta = new ContaCorrente(-1, idCliente, 0.0, limiteDescoberto);
                break;
            case 2:
                System.out.println("\n========= Criar Conta Poupança =========");
                double taxaJuro = 0;
                while (true) {
                    System.out.print("Taxa de Juro (% de 0.5 a 5.0): ");
                    try {
                        taxaJuro = scn.nextDouble();
                        scn.nextLine();

                        if (taxaJuro < 0.5 || taxaJuro > 5.0) {
                            System.out.println("A taxa de juro tem de ser entre 0.5 a 5.0. Tente novamente.");
                            continue;
                        }

                       
                        double taxaJuroDecimal = taxaJuro / 100.0;

                      
                        novaConta = new ContaPoupanca(-1, idCliente, 0.0, taxaJuroDecimal);
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Taxa inválida. Insira um número válido.");
                        scn.nextLine();
                    }
                }
                break;

            default:
                System.out.println("Opção inválida. Tipo de conta inválido.");
                return;
        }

        try {
            contaDAO.save(novaConta);
            System.out.println("Conta criada com sucesso (ID " + novaConta.getId_conta() + ") para o cliente " + clienteDAO.ler(idCliente).getNome() + " - ID " + idCliente + " NIF: " + nif);
        } catch (Exception e) {
            System.out.println("Erro ao criar conta: " + e.getMessage());
        }
    }

   
    /**
     * Realiza um depósito numa conta específica.
     * * @param idContaDestino O ID da conta onde o dinheiro será depositado.
     */
    public static void depositDinheiro(int idContaDestino) {
        ContaDAO contaDAO = new ContaDAO();
        double valor = -1;

        System.out.println("\n========= DEPOSITAR DINHEIRO =========");
        try {
            System.out.print("Valor a Depositar: ");
            valor = scn.nextDouble();
            scn.nextLine();


            contaDAO.depositar(idContaDestino, valor);

            System.out.printf("Depósito de %.2f€ na conta %d efetuado com sucesso.\n", valor, idContaDestino);

        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Digite um número para o valor.");
            scn.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Erro de Lógica: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro na Base de Dados: " + e.getMessage());
        }
    }

    /**
     * Realiza o levantamento de uma quantia de uma conta específica.
     * Valida se existe saldo ou limite disponível para a operação.
     * * @param idConta O ID da conta de onde o dinheiro será retirado.
     */
    public static void raiseMoney(int idConta) {
        ContaDAO contaDAO = new ContaDAO();
        double valor = -1;
        

        System.out.println("\n========= LEVANTAR DINHEIRO =========");
        System.out.printf("Conta de Origem: %d\n", idConta);

        try {
        	
            System.out.print("Valor a Levantar: ");
            valor = scn.nextDouble();
            scn.nextLine();
            
            Conta conta = contaDAO.read(idConta);

            if(conta == null) {
            	System.out.println("Erro: Conta não encontrada");
            	return;
            }
            
            conta.levantar(valor);
            
            contaDAO.update(conta);
            

        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Digite um número para o valor.");
            scn.nextLine();
        } catch (IllegalArgumentException | models.SaldoInsuficienteException e) {
           
            System.out.println("Erro de Lógica: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro na Base de Dados: " + e.getMessage());
        }
    }

    /**
     * Efetua uma transferência bancária entre duas contas.
     * Solicita o ID de destino, confirma o nome do titular da conta de destino
     * e valida a disponibilidade de fundos na conta de origem.
     * * @param idContaOrigem O ID da conta que envia o dinheiro.
     */
    public static void transferCash(int idContaOrigem) {
        ContaDAO contaDAO = new ContaDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        int idDestino = -1;
        double valor = -1;

        System.out.println("\n========= TRANSFERIR DINHEIRO =========");
        System.out.println("Conta de Origem: " + idContaOrigem);

        try {
           
            System.out.print("ID da Conta de Destino: ");
            idDestino = scn.nextInt();
            scn.nextLine(); // Consumir nova linha

            if (idContaOrigem == idDestino) {
                System.out.println("Erro: Não pode transferir dinheiro para a própria conta de origem.");
                return;
            }

            
            Conta contaDestino = contaDAO.read(idDestino);

            if (contaDestino == null) {
                System.out.println("Erro: Conta de destino não encontrada.");
                return;
            }

            
            Cliente clienteDestino = clienteDAO.ler(contaDestino.getId_cliente());

            if (clienteDestino == null) {
                System.out.println("Erro: Não foi possível encontrar o cliente associado à conta de destino.");
                return;
            }

           
            System.out.println("\n--- CONFIRMAÇÃO ---");
            System.out.println("Conta de Destino (ID: " + idDestino + ") pertence a: " + clienteDestino.getNome());
            System.out.print("Confirma que deseja enviar para " + clienteDestino.getNome() + "? (S/N): ");
            String confirmacao = scn.nextLine().trim().toUpperCase();

            if (!confirmacao.equals("S")) {
                System.out.println("Operação de transferência cancelada pelo utilizador.");
                return;
            }

            
            System.out.print("Valor a Transferir: ");
            valor = scn.nextDouble();
            scn.nextLine(); 

           
            contaDAO.transferir(idContaOrigem, idDestino, valor);

            System.out.printf("✅ Transferência de %.2f€ de %d para %d (%s) efetuada com sucesso.\n",
                valor, idContaOrigem, idDestino, clienteDestino.getNome());

        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Certifique-se de usar números para o ID e valor.");
            scn.nextLine();
        } catch (IllegalArgumentException | models.SaldoInsuficienteException e) {
            
            System.out.println("Erro de Lógica: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro na Base de Dados: " + e.getMessage());
        }
    }

    /**
     * Exibe o extrato de transações de uma conta.
     * Lista as transações por ordem cronológica inversa, calculando e exibindo
     * os saldos intermédios para cada movimento.
     * * @param idConta O ID da conta da qual se pretende visualizar o extrato.
     */
    public static void viewTransactions(int idConta) {
        TransacaoDAO transacaoDAO = new TransacaoDAO();
        ContaDAO contaDAO = new ContaDAO();

        System.out.println("\n|==================== EXTRATO DE TRANSAÇÕES (Conta " + idConta + ") =====================|");

        try {
            
            Conta conta = contaDAO.read(idConta);
            if (conta == null) {
                System.out.println("Erro: Conta não encontrada.");
                return;
            }

           
            java.util.List<Transacao> extrato = transacaoDAO.getTransacoesByConta(idConta);

            if (extrato.isEmpty()) {
                System.out.println("| Nenhuma transação encontrada.                                       |");
            } else {

               
                java.util.Collections.reverse(extrato);

              
                double saldoAtual = conta.getSaldo();

               
                System.out.println("| ID | Tipo           | Valor      | Saldo Antes | Saldo Depois | Data e Hora        |");
                System.out.println("|----|----------------|------------|-------------|--------------|--------------------|");

                for (Transacao t : extrato) {
                    // Formatar a data no formato legível
                    String dataFormatada = t.getData().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                  
                    double saldoDepois = saldoAtual;

                   
                    double valorMovimento = t.getValor();

                    if (t.getId_destino() == idConta) {
                       
                        saldoAtual -= valorMovimento;
                    } else {
                        
                        saldoAtual += valorMovimento;
                    }

                    double saldoAntes = saldoAtual; 

                  
                    System.out.printf("| %-2d | %-14s | %-10.2f | %-11.2f | %-12.2f | %-17s |\n",
                        t.getId_transacao(),
                        t.getTipo(),
                        valorMovimento,
                        saldoAntes,
                        saldoDepois,
                        dataFormatada
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao carregar o extrato da base de dados: " + e.getMessage());
        }

        System.out.println("|====================================================================================|");
    }

    /**
     * Encerra uma conta bancária.
     * A conta só pode ser encerrada se o saldo for exatamente zero.
     * Requer confirmação textual ("SIM") do utilizador.
     * * @param idConta O ID da conta a fechar.
     */
    public static void closeAccount(int idConta) {
    ContaDAO contaDAO = new ContaDAO();
    String confirmacao;

    System.out.println("\n|============= FECHAR CONTA " + idConta + " =============|");

    try {

        Conta conta = contaDAO.read(idConta);
        if (conta == null) {
            System.out.println("Erro: Conta não encontrada.");
            return;
        }

        double saldoAtual = conta.getSaldo();
       
        if (saldoAtual != 0) {
            System.out.printf("Erro: Saldo da conta não é zero (%.2f€). Não é possível fechar.\n", saldoAtual);
            System.out.println("Por favor, levante o valor ou transfira-o antes de fechar a conta.");
            return;
        }
         
        System.out.println("ATENÇÃO: Tem a certeza que deseja eliminar esta conta (ID " + idConta + ")?");
        System.out.print("Digite 'SIM' para confirmar: ");
        confirmacao = scn.nextLine().toUpperCase();

        if (!confirmacao.equals("SIM")) {
            System.out.println("Operação de fecho de conta cancelada.");
            return;
        }

       
         contaDAO.delete(idConta);

       
        System.out.printf("CONTA %d ELIMINADA COM SUCESSO.\n", idConta);

    } catch (SQLException e) {
         System.out.println("Erro na Base de Dados ao fechar a conta: " + e.getMessage());
    }
}

    /**
     * Menu secundário que disponibiliza as operações financeiras de uma conta.
     * Inclui depósito, levantamento, transferência, consulta de extrato e fecho de conta.
     * * @param conta O objeto Conta sobre o qual as operações serão realizadas.
     */
    public static void accountOperationsMenu(Conta conta) {
        ContaDAO contaDAO = new ContaDAO();

        int optionMenu = -1;
        boolean exit = false;

        do {
            try {
                System.out.println("\n|============= MENU OPERAÇÕES DA CONTA =============|");
                System.out.printf("| ID Conta: %-40d|\n", conta.getId_conta());
                System.out.printf("| Tipo: %-44s|\n", (conta instanceof ContaCorrente) ? "Corrente" : "Poupança");
                
                Conta contaAtualizada = contaDAO.read(conta.getId_conta());
                System.out.printf("| Saldo Atual: %-37.2f|\n", contaAtualizada.getSaldo());
                System.out.println("|===================================================|");

                System.out.println("| [1] Depositar Dinheiro                            |");
                System.out.println("| [2] Levantar Dinheiro                             |");
                System.out.println("| [3] Transferir Dinheiro                           |");
                System.out.println("| [4] Ver Extrato de Transações                     |");
                System.out.println("| [5] Fechar Conta                                  |");
                System.out.println("| [0] Sair                                          |");
                System.out.println("|===================================================|");
                System.out.print("Opção: ");

                optionMenu = scn.nextInt();
                scn.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor insira um número para a opção.");
                scn.nextLine();
                optionMenu = -1;
            } catch (SQLException e) {
                System.out.println("Erro ao carregar o saldo da conta: " + e.getMessage());
            }

            switch (optionMenu) {
                case 1:
                    depositDinheiro(conta.getId_conta());
                    break;
                case 2:
                    raiseMoney(conta.getId_conta());
                    break;
                case 3:
                    transferCash(conta.getId_conta());
                    break;
                case 4:
                    viewTransactions(conta.getId_conta());
                    break;
                case 5:
                    closeAccount(conta.getId_conta());
                    exit = true;
                    break;
                case 0:
                    exit = true;
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.\n");
                    break;
            }
        } while (!exit);
    }


    /**
     * Gere o processo de acesso (login) a uma conta.
     * O utilizador deve identificar-se com ID de Cliente e NIF. 
     * Após validação, apresenta as contas disponíveis para seleção e operação.
     */
    public static void manageAccount() {
        ContaDAO contaDAO = new ContaDAO();
        ClienteDAO clienteDAO = new ClienteDAO();

        
        int idCliente = -1;
        String nif = "";
        Cliente cliente = null;
        int nifErrorCount = 0;

        System.out.println("\n========= LOGIN DE CONTA =========");

        
        try {
            System.out.print("Digite o ID de Cliente: ");
            idCliente = scn.nextInt();
            scn.nextLine(); // Consumir a nova linha
        } catch (InputMismatchException e) {
            System.out.println("ID de Cliente inválido.");
            scn.nextLine();
            return;
        }

    
        while (true) {

            if (nifErrorCount >= 3) {
                System.out.println("Atenção: Três tentativas falhadas de NIF. Ação cancelada.");
                return;
            }

            System.out.print("NIF (9 dígitos): ");
            nif = scn.nextLine().trim();

            if (!nif.matches("\\d{9}")) {
                System.out.println("NIF inválido. Deve conter exatamente 9 dígitos numéricos.");
                nifErrorCount++;
                continue;
            }

            try {
                
                cliente = clienteDAO.ler(idCliente);

                if (cliente == null) {
                    System.out.println("Cliente não encontrado.");
                    return;
                }

                
                if (!cliente.getNIF().equals(nif)) {
                    System.out.println("NIF não corresponde ao cliente. Tente novamente.");
                    nifErrorCount++;
                    continue;
                }

                
                break;

            } catch (Exception e) { 
                System.out.println("Erro ao aceder ao BD: " + e.getMessage());
                return;
            }
        }

        

        try {
            
            java.util.List<Conta> contasDoCliente = contaDAO.getContasByCliente(cliente.getId_cliente());

            if (contasDoCliente.isEmpty()) {
                System.out.println("Erro: Cliente autenticado sem contas associadas.");
                return;
            }

            Conta contaSelecionada = null;
            int idContaSelecionada = -1;

          
            while (contaSelecionada == null) {

               
                System.out.println("\n|=============== SELEÇÃO DE CONTA ==================|");
                System.out.printf("| Cliente: %-40s |\n", cliente.getNome());
                System.out.println("|---------------------------------------------------|");


                
                for (Conta c : contasDoCliente) {
                    System.out.printf("| ID - %-4d | Tipo: %-10s | Saldo: %-11.2f |\n",
                        c.getId_conta(),
                        (c instanceof ContaCorrente) ? "Corrente" : "Poupança",
                        c.getSaldo());
                }
                System.out.println("| [0] Voltar ao Menu Principal                      |");
                System.out.println("|===================================================|");


                System.out.print("Digite o ID da Conta para operar ou 0 para sair: ");

                try {
                    idContaSelecionada = scn.nextInt();
                    scn.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Erro: Por favor insira um número inteiro para o ID da conta.");
                    scn.nextLine();
                    continue;
                }

                if (idContaSelecionada == 0) {
                    return; 
                }

               
                for (Conta c : contasDoCliente) {
                    if (c.getId_conta() == idContaSelecionada) {
                        contaSelecionada = c;
                        break;
                    }
                }

                if (contaSelecionada == null) {
                    System.out.println("ID de conta inválido ou não pertencente a este cliente. Tente novamente.");
                }
            }

            
            accountOperationsMenu(contaSelecionada);

        } catch (SQLException e) {
            System.out.println("Erro ao carregar contas do cliente: " + e.getMessage());
        }
    }

    /**
     * Menu principal da aplicação.
     * Controla o fluxo de navegação entre a criação de clientes, gestão de clientes,
     * criação de contas e gestão de contas.
     * Verifica dinamicamente se existem clientes na base de dados para habilitar
     * ou desabilitar certas opções.
     */
    public static void menu() {
        ClienteDAO clienteDAO = new ClienteDAO();
        boolean exit = false;
        int optionMenu = -1;

        do {
            boolean clientesExistem = false;
            try {
                clientesExistem = clienteDAO.existeAlgumCliente();
            } catch (SQLException e) {
                System.err.println("Erro ao verificar clientes na BD: " + e.getMessage());
            }

            try {
                System.out.println("\n|============== Escolhe uma opção ==============|");
                System.out.println("| [1] Criar Cliente                             |");

                if (clientesExistem) {
                    System.out.println("| [2] Gerir Cliente                             |");
                    System.out.println("| [3] Criar Conta                               |");
                    System.out.println("| [4] Gerir Conta                               |");
                }

                System.out.println("| [0] Sair                                      |");
                System.out.println("|===============================================|");
                System.out.print("Opção: ");
                optionMenu = scn.nextInt();
                scn.nextLine();

            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor insira um número para a opção");
                scn.nextLine();
                optionMenu = -1;
            }

            switch (optionMenu) {
                case 1:
                    createClient();
                    break;
                case 2: // Gerir Cliente
                    if (clientesExistem) {
                        manageClient();
                    } else {
                        System.out.println("Opção indisponível. Crie o primeiro cliente (Opção 1) para aceder.\n");
                    }
                    break;

                case 3: // Criar Conta
                    if (clientesExistem) {
                        createAccount();
                    } else {
                        System.out.println("Opção indisponível. Crie o primeiro cliente (Opção 1) para aceder.\n");
                    }
                    break;

                case 4: // Gerir Conta
                    if (clientesExistem) {
                        manageAccount();
                    } else {
                        System.out.println("Opção indisponível. Crie o primeiro cliente (Opção 1) para aceder.\n");
                    }
                    break;
                case 0: // info: Sair
                    exit = true;
                    System.out.println("Até a proxima!! Fico a tua espera :)");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                }
        } while (!exit);
    }
    
 
    /**
     * Inicializa os serviços de background.
     * Instancia e arranca a Thread de monitorização de contas (processamento de juros, etc.).
     */
    public static void iniciarMonitoramento() {
        System.out.println("----------------------------------------------");
        System.out.println(" A INICIALIZAR SERVIÇOS DE BACKGROUND...");
        
        // 1. Cria a instância do Runnable (MonitorContas)
        monitor = new MonitorContas();
        
        // 2. Cria a Thread e passa o Runnable
        monitorThread = new Thread(monitor);
        
        // 3. Inicia a Thread (chama o método run() de MonitorContas)
        monitorThread.start();
        System.out.println("Monitor de Contas iniciado em segundo plano.");
        System.out.println("----------------------------------------------");
    }
    
    /**
     * Finaliza os serviços de background de forma segura.
     * Sinaliza a paragem ao monitor e aguarda a conclusão da execução da thread (join).
     */
    public static void pararMonitoramento() {
        if (monitor != null) {
            // Sinaliza ao Runnable para parar o loop while
            monitor.stopRunning();
            
            // Interrompe a thread, tirando-a imediatamente do sleep()
            monitorThread.interrupt(); 
            
            try {
                // Espera a thread terminar a execução (clean shutdown)
                monitorThread.join();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Monitoramento de Contas encerrado com segurança.");
        }
    }

    /**
     * Ponto de entrada principal da aplicação.
     * Estabelece a ligação inicial à base de dados, cria as tabelas necessárias,
     * inicia a monitorização e abre o menu principal.
     * * @param args Argumentos da linha de comandos (não utilizados).
     * @throws Exception Caso ocorra um erro fatal na inicialização ou base de dados.
     */
    public static void main(String[] args) throws Exception {
        GestorBD.getConnectionAndCreateTables();
        iniciarMonitoramento();
        menu();
        pararMonitoramento();
        
        
    }
}