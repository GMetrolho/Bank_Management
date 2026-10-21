package services;

import DAO.ContaDAO;
import models.Conta;
import models.ContaPoupanca;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Serviço de monitorização em segundo plano (background) que implementa a interface Runnable.
 * É responsável por realizar tarefas periódicas no sistema, como a aplicação automática
 * de juros às contas poupança.
 * 
 * O monitor executa a cada INTERVALO_EM_SEGUNDOS segundos.
 * 
 */
public class MonitorContas implements Runnable {
	/** Objeto Data Access Object (DAO) usado para aceder e atualizar dados da conta na base de dados. */
    private final ContaDAO contaDAO;
    /** Flag volátil para controlar o ciclo de execução do thread. */
    private volatile boolean running = true;
    /** Intervalo de tempo (em segundos) entre as execuções periódicas da lógica de monitorização. */
    private static final long INTERVALO_EM_SEGUNDOS = 90;

    
    /**
     * Construtor do Monitor de Contas.
     * Inicializa o ContaDAO necessário para aceder aos dados da conta.
     */
    public MonitorContas() {
        this.contaDAO = new ContaDAO();
        
       
    }

    
    /**
     * Define a flag de execução como false, sinalizando ao método run()
     * que deve terminar a sua execução no final do ciclo atual ou após despertar do sono.
     */
    public void stopRunning() {
        this.running = false;
    }

    
    
    /**
     * O ponto de entrada principal para a thread de monitorização.
     * Executa em loop até que o método stopRunning() seja chamado ou
     * o thread seja interrompido. A cada ciclo, aguarda o INTERVALO_EM_SEGUNDOS
     * de tempo e depois chama aplicarJurosContasPoupanca().
     */
    @Override
    public void run() {
        System.out.println("Monitor de Contas iniciado (Runnable). Processamento periódico a cada " + INTERVALO_EM_SEGUNDOS + " segundos.");


        while (running) {
            try {
            	// Aguarda o intervalo de tempo
                TimeUnit.SECONDS.sleep(INTERVALO_EM_SEGUNDOS);

             // Executa a lógica de negócio
                aplicarJurosContasPoupanca();

            } catch (InterruptedException e) {
            	// Interrompe o thread em caso de exceção de interrupção
                Thread.currentThread().interrupt();
                this.running = false;
            } catch (SQLException e) {
                System.err.println("ERRO no Monitor: Falha ao aceder à BD para aplicar juros: " + e.getMessage());
            }
        }
        System.out.println("Monitor de Contas encerrado.");
    }

    /**
     * Itera sobre todas as contas na base de dados, identifica as instâncias de
     * ContaPoupanca, aplica o cálculo de juros (calculoJuros()}) e,
     * se houver alteração no saldo, atualiza a base de dados e regista o evento
     * usando LogManager.logAplicacaoJuros().
     *
     * @throws SQLException Se ocorrer um erro ao aceder ou atualizar a base de dados.
     */
    private void aplicarJurosContasPoupanca() throws SQLException {

        List<Conta> todasContas = contaDAO.getAllContas();
        

		for (Conta conta : todasContas) {
			// Verifica se a conta é uma ContaPoupanca antes de fazer o casting
            if (conta instanceof ContaPoupanca) {
                ContaPoupanca cp = (ContaPoupanca) conta;

                double saldoAntigo = cp.getSaldo();
                cp.calculoJuros();
                double saldoNovo = cp.getSaldo();

                if (saldoNovo > saldoAntigo) {
                	// Atualiza o saldo na BD
                	contaDAO.update(cp);
                    contaDAO.save(cp);
                 // Regista o evento de aplicação de juros
                    LogManager.logAplicacaoJuros(cp.getId_conta(), saldoAntigo, saldoNovo);
                   
                }
            }
        }

       
        
    }
}
