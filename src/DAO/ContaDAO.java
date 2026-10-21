package DAO;


import models.*;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;



/**
 * ContaDAO é a classe de Objeto de Acesso a Dados (DAO)
 * responsável por todas as operações de persistência e de negócio relacionadas
 * à entidade Conta e às subclasses ContaCorrente, ContaPoupanca.
 * Implementa métodos de CRUD, bem como operações transacionais
 * como depósito, levantamento e transferência, garantindo a integridade dos dados
 * através de transações de base de dados.
 *
 */
public class ContaDAO {

    // --- SQL QUERIES ---
	
	
	/** Query SQL para inserir uma nova conta na tabela 'conta'. */
    private static final String SQL_INSERT = "INSERT INTO conta (id_cliente, saldo, tipo, limite_descoberto, taxa_juro) VALUES (?, ?, ?, ?, ?)";
    /** Query SQL para ler uma conta pelo seu ID único. */
    private static final String SQL_READ_BY_ID = "SELECT * FROM conta WHERE id_conta = ?";
    /** Query SQL para apagar (deletar) uma conta pelo seu ID. */
    private static final String SQL_DELETE = "DELETE FROM conta WHERE id_conta = ?";
    /** Query SQL para buscar todas as contas associadas a um determinado cliente. */
    private static final String SQL_READ_BY_CLIENT = "SELECT * FROM conta WHERE id_cliente = ?";
    /** Query SQL para buscar todas as contas na base de dados. */
    private static final String SQL_READ_ALL = "SELECT * FROM conta";
    /** Query SQL para atualizar o saldo e os atributos específicos (limite ou taxa de juro) de uma conta. */
    private static final String SQL_UPDATE = "UPDATE conta SET saldo = ?, limite_descoberto = ?, taxa_juro = ? WHERE id_conta = ?";
    /** Instância de TransacaoDAO utilizada para persistir registos de transações. */
    private final TransacaoDAO transacaoDAO = new TransacaoDAO();

   

    
    /**
	 * Persiste uma nova conta (insert) ou atualiza uma conta existente (update) na base de dados.
	 * O comportamento é decidido pelo id_conta, se for -1, é uma nova conta.
	 *
	 * @param conta  - O objeto Conta (ou subclasse) a ser guardado.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
   public void save(Conta conta) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = GestorBD.getConnection();

            if (conta.getId_conta() == -1) {

                stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

                stmt.setInt(1, conta.getId_cliente());

               
                if (conta instanceof ContaPoupanca) {
                    stmt.setString(3, "POUPANCA");
                } else {
                    stmt.setString(3, "CORRENTE");
                }

                stmt.setDouble(2, conta.getSaldo());

               
                if (conta instanceof ContaCorrente cc) {
                    stmt.setDouble(4, cc.getLimiteDescoberto());
                    stmt.setNull(5, Types.REAL);
                } else if (conta instanceof ContaPoupanca cp) {
                    stmt.setNull(4, Types.REAL);
                    stmt.setDouble(5, cp.getTaxaJuro());
                } else {
                    stmt.setNull(4, Types.REAL);
                    stmt.setNull(5, Types.REAL);
                }

                stmt.executeUpdate();

                
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    conta.setId_conta(idGerado);
                    System.out.println("Conta inserida com sucesso! Novo ID: " + idGerado);
                }
            } else {
                
                stmt = conn.prepareStatement(SQL_UPDATE);

                // 1. saldo
                stmt.setDouble(1, conta.getSaldo());

                
                if (conta instanceof ContaCorrente cc) {
                    stmt.setDouble(2, cc.getLimiteDescoberto());
                    stmt.setNull(3, Types.REAL);
                } else if (conta instanceof ContaPoupanca cp) {
                    stmt.setNull(2, Types.REAL);
                    stmt.setDouble(3, cp.getTaxaJuro());
                } else {
                    stmt.setNull(2, Types.REAL);
                    stmt.setNull(3, Types.REAL);
                }

                
                stmt.setInt(4, conta.getId_conta());

                stmt.executeUpdate();
            }

            
            GestorBD.commit(conn);

        } catch (SQLException e) {
           
            GestorBD.rollback(conn);
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            GestorBD.closeConnection(conn);
        }
    }


   
   /**
	 * Busca e carrega uma conta da base de dados pelo seu ID, instanciando
	 * a subclasse correta ContaPoupanca ou ContaCorrente com base no campo 'tipo'.
	 *
	 * @param id - O ID único da conta a ser lida.
	 * @return O objeto Conta completo, ou null se a conta não for encontrada.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public Conta read(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Conta conta = null;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_READ_BY_ID);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
               
                int idCliente = rs.getInt("id_cliente");
                double saldo = rs.getDouble("saldo");
                String tipo = rs.getString("tipo");

                
                if (tipo.equals("POUPANCA")) {
                    double taxaJuro = rs.getDouble("taxa_juro");
                    conta = new ContaPoupanca(id, idCliente, saldo, taxaJuro);

                } else {
                    double limiteDescoberto = rs.getDouble("limite_descoberto");
                    conta = new ContaCorrente(id, idCliente, saldo, limiteDescoberto);
                }
            }

        } finally {
            GestorBD.closeConnection(conn);
        }

        return conta;
    }


    
    /**
	 * Apaga o registo de uma conta na base de dados pelo seu ID.
	 *
	 * @param id - O ID da conta a ser apagada.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public void delete(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            GestorBD.commit(conn);
        } catch (SQLException e) {
            GestorBD.rollback(conn);
            throw e;
        } finally {
            GestorBD.closeConnection(conn);
        }
    }


    
    /**
	 * Obtém todas as contas associadas a um cliente específico, dadas pelo seu ID.
	 *
	 * @param id_cliente - O ID do cliente cujas contas devem ser buscadas.
	 * @return Uma Listde objetos Conta (ou subclasses) pertencentes ao cliente.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public List<Conta> getContasByCliente(int id_cliente) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Conta> contas = new ArrayList<>();

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_READ_BY_CLIENT);
            stmt.setInt(1, id_cliente);
            rs = stmt.executeQuery();

            
            while (rs.next()) {
               
                int idConta = rs.getInt("id_conta");
                double saldo = rs.getDouble("saldo");
                String tipo = rs.getString("tipo");

                Conta conta;

                if (tipo.equals("POUPANCA")) {
                    double taxaJuro = rs.getDouble("taxa_juro");
                    conta = new ContaPoupanca(idConta, id_cliente, saldo, taxaJuro);
                } else {
                    double limiteDescoberto = rs.getDouble("limite_descoberto");
                    conta = new ContaCorrente(idConta, id_cliente, saldo, limiteDescoberto);
                }
                contas.add(conta);
            }

        } finally {
            GestorBD.closeConnection(conn);
        }

        return contas;
    }

    
    /**
	 * Obtém todas as contas registadas na base de dados.
	 *
	 * @return Uma List de todos os objetos Conta (e suas subclasses).
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public List<Conta> getAllContas() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Conta> contas = new ArrayList<>();

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_READ_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
               
                int idConta = rs.getInt("id_conta");
                int idCliente = rs.getInt("id_cliente");
                double saldo = rs.getDouble("saldo");
                String tipo = rs.getString("tipo");

                Conta conta;

                if (tipo.equals("POUPANCA")) {
                    double taxaJuro = rs.getDouble("taxa_juro");
                    conta = new ContaPoupanca(idConta, idCliente, saldo, taxaJuro);
                } else {
                    double limiteDescoberto = rs.getDouble("limite_descoberto");
                    conta = new ContaCorrente(idConta, idCliente, saldo, limiteDescoberto);
                }
                contas.add(conta);
            }

        } finally {
            GestorBD.closeConnection(conn);
        }

        return contas;
    }

    
    /**
	 * Método utilitário privado para executar a atualização da conta
	 * (apenas saldo e atributos específicos) na base de dados,
	 * utilizando uma conexão existente (internamente a uma transação).
	 *
	 * @param conn - A Connection ativa a ser utilizada para a operação.
	 * @param conta - O objeto Conta com os dados a serem atualizados.
	 * @throws SQLException Se ocorrer um erro no acesso à base de dados.
	 */
    private void updateInternal(Connection conn, Conta conta) throws SQLException {
        PreparedStatement stmtUpdate = null;
        try {
            stmtUpdate = conn.prepareStatement(SQL_UPDATE);

           
            stmtUpdate.setDouble(1, conta.getSaldo());

            
            if (conta instanceof ContaCorrente cc) {
                stmtUpdate.setDouble(2, cc.getLimiteDescoberto());
                stmtUpdate.setNull(3, Types.REAL);
            } else if (conta instanceof ContaPoupanca cp) {
                stmtUpdate.setNull(2, Types.REAL);
                stmtUpdate.setDouble(3, cp.getTaxaJuro());
            } else {
                stmtUpdate.setNull(2, Types.REAL);
                stmtUpdate.setNull(3, Types.REAL);
            }

            
            stmtUpdate.setInt(4, conta.getId_conta());
            stmtUpdate.executeUpdate();
        } finally {
            if (stmtUpdate != null) stmtUpdate.close();
        }
    }
    
    
    /**
	 * Atualiza o registo de uma conta existente.
	 * Nota: Este método obtém uma nova conexão e faz a atualização e fecho
	 * da conexão. Para operações transacionais (depósito/levantamento/transferência),
	 * é preferível usar updateInternal(Connection, Conta) com uma conexão gerida externamente.
	 *
	 * @param conta - O objeto Conta com os dados a serem atualizados.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public void update(Conta conta) throws SQLException {
        Connection conn = null;
        try {
            
            conn = GestorBD.getConnection(); 
            
            
            updateInternal(conn, conta); 
            
        } finally {
            
            if (conn != null) {
                conn.close();
            }
        }
    }

    
    /**
	 * Executa uma operação de depósito:
	 * 1. Valida o valor.
	 * 2. Lê a conta, atualiza o saldo (chama conta.depositar(valor)).
	 * 3. Atualiza o registo da conta na base de dados.
	 * 4. Insere um novo registo de transação.
	 * A operação é atómica (tudo ou nada) através da gestão de transações do GestorBD.
	 *
	 * @param idConta -  O ID da conta de destino.
	 * @param valor - O valor a ser depositado (deve ser positivo).
	 * @throws SQLException Se a conta não for encontrada ou ocorrer um erro de base de dados.
	 * @throws IllegalArgumentException Se o valor for menor ou igual a zero.
	 */
    public void depositar(int idConta, double valor) throws SQLException {
    if (valor <= 0) {
        throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
    }
    Connection conn = null;

    try {
        conn = GestorBD.getConnection();

        
        Conta conta = read(idConta);

        if (conta == null) {
            throw new SQLException("Conta de destino não encontrada: ID " + idConta);
        }

       
        conta.depositar(valor);

        

        
        updateInternal(conn, conta);

       
        Transacao deposito = new Transacao(
            -1,
            "DEPOSITO",
            valor,
            0, // ID de Origem: 0 (externo)
            idConta
        );

        
        transacaoDAO.insertInternal(conn, deposito); 

        
        GestorBD.commit(conn);

    } catch (SQLException | IllegalArgumentException e) {
        
        GestorBD.rollback(conn);
        throw e;
    } finally {
        
        GestorBD.closeConnection(conn);
    }
}

    
    /**
	 * Executa uma operação de levantamento:
	 * 1. Valida o valor.
	 * 2. Lê a conta, atualiza o saldo (chama conta.levantar(valor)).
	 * 3. Atualiza o registo da conta na base de dados.
	 * 4. Insere um novo registo de transação.
	 * A operação é atómica (tudo ou nada) através da gestão de transações do GestorBD.
	 *
	 * @param idConta - O ID da conta de origem.
	 * @param valor - O valor a ser levantado (deve ser positivo).
	 * @throws SQLException Se a conta não for encontrada ou ocorrer um erro de base de dados.
	 * @throws IllegalArgumentException Se o valor for menor ou igual a zero.
	 * @throws models.SaldoInsuficienteException Se a conta não tiver saldo (e/ou limite) suficiente.
	 */
    public void levantar(int idConta, double valor)
            throws SQLException, models.SaldoInsuficienteException {

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do levantamento deve ser positivo.");
        }

        Connection conn = null;

        try {
            conn = GestorBD.getConnection();

            // 1. LER CONTA
            Conta conta = read(idConta);

            if (conta == null) {
                throw new SQLException("Conta não encontrada: ID " + idConta);
            }

            
            conta.levantar(valor);
           

           

            
            updateInternal(conn, conta); 

            
            Transacao levantamento = new Transacao(
                -1,
                "LEVANTAMENTO",
                valor,
                idConta, 
                0 
            );

            
            transacaoDAO.insertInternal(conn, levantamento);

            
            GestorBD.commit(conn);

        } catch (SQLException | IllegalArgumentException | models.SaldoInsuficienteException e) {
            
            GestorBD.rollback(conn);
            throw e;
        } finally {
            
            GestorBD.closeConnection(conn);
        }
    }

    
    /**
	 * Executa uma operação de transferência de fundos entre duas contas.
	 * Esta é uma operação crítica que envolve múltiplos passos:
	 * 1. Valida valores e IDs.
	 * 2. Lê ambas as contas.
	 * 3. Chama origem.levantar(valor) e destino.depositar(valor).
	 * 4. Atualiza ambos os registos de conta na base de dados.
	 * 5. Insere um novo registo de transação.
	 * A operação é garantida como atómica (tudo ou nada) através da gestão de transações do GestorBD.
	 *
	 * @param idContaOrigem - O ID da conta de onde o valor será retirado.
	 * @param idContaDestino - O ID da conta onde o valor será depositado.
	 * @param valor - O valor a ser transferido (deve ser positivo).
	 * @throws SQLException Se alguma das contas não for encontrada ou ocorrer um erro de base de dados.
	 * @throws IllegalArgumentException Se o valor for <= 0 ou se as contas de origem e destino forem as mesmas.
	 * @throws models.SaldoInsuficienteException Se a conta de origem não tiver saldo (e/ou limite) suficiente.
	 */
    public void transferir(int idContaOrigem, int idContaDestino, double valor)
            throws SQLException, models.SaldoInsuficienteException {

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }
        if (idContaOrigem == idContaDestino) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta.");
        }

        Connection conn = null;

        try {
            conn = GestorBD.getConnection();

           
            Conta origem = read(idContaOrigem);
            Conta destino = read(idContaDestino);

            if (origem == null || destino == null) {
                throw new SQLException("Conta de origem ou destino não encontrada.");
            }

            origem.levantar(valor);  
            destino.depositar(valor);


            updateInternal(conn, origem);

            updateInternal(conn, destino);

            Transacao transferencia = new Transacao(
                -1,
                "TRANSFERENCIA",
                valor,
                idContaOrigem,
                idContaDestino
            );

            transacaoDAO.insertInternal(conn, transferencia);

            GestorBD.commit(conn);

        } catch (SQLException | IllegalArgumentException | models.SaldoInsuficienteException e) {
            GestorBD.rollback(conn);
            throw e;
        } finally {
            GestorBD.closeConnection(conn);
        }
    }

}
