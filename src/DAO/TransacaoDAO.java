package DAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import models.Transacao;


/**
 * TransacaoDAO é a classe de Objeto de Acesso a Dados (DAO)
 * responsável por todas as operações de persistência relacionadas à entidade
 * models.Transacao.
 * Inclui métodos para inserir novos registos de transação e consultar
 * o histórico de transações de uma conta.
 *
 */
public class TransacaoDAO {

  // SQL QUERIES
	
	/** Query SQL para inserir um novo registo de transação. */
  private static final String SQL_INSERT = "INSERT INTO transacao (tipo, valor, data, id_origem, id_destino) VALUES (?, ?, ?, ?, ?)";
  /** Query SQL para ler todas as transações associadas a um ID de conta, seja como origem ou destino. */
  private static final String SQL_READ_BY_ACCOUNT = "SELECT * FROM transacao WHERE id_origem = ? OR id_destino = ? ORDER BY data";

  
  
  /**
	 * Persiste um novo objeto Transacao na base de dados.
	 *
	 * Este método obtém uma nova conexão, executa a inserção e fecha a conexão.
	 * Não é utilizado em operações transacionais complexas (como transferência/depósito/levantamento)
	 * que precisam de atomicidade com a atualização do saldo, onde se deve usar
	 * insertInternal(Connection, Transacao)}.
	 *
	 * @param transacao - O objeto Transacao a ser guardado.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
  public void save(Transacao transacao) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, transacao.getTipo());
            stmt.setDouble(2, transacao.getValor());
            stmt.setString(3, transacao.getData().toString());
            stmt.setInt(4, transacao.getId_origem());
            stmt.setInt(5, transacao.getId_destino());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGerado = rs.getInt(1);
                System.out.println("Transação inserida com sucesso! Novo ID: " + idGerado);
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            GestorBD.closeConnection(conn);
        }
    }


    
    
  
  	/**
	 * Obtém a lista de todas as transações (histórico) associadas a um ID de conta.
	 * A conta é considerada envolvida se for a conta de origem ou a conta de destino.
	 *
	 * @param idConta - O ID da conta para buscar o histórico de transações.
	 * @return Uma List de objetos Transacao ordenadas por data.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public List<Transacao> getTransacoesByConta(int idConta) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transacao> transacoes = new ArrayList<>();

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_READ_BY_ACCOUNT);

            stmt.setInt(1, idConta);
            stmt.setInt(2, idConta);

            rs = stmt.executeQuery();

            while (rs.next()) {
                int idTransacao = rs.getInt("id_transacao");
                String tipo = rs.getString("tipo");
                double valor = rs.getDouble("valor");

                String dataString = rs.getString("data");
                LocalDateTime data = LocalDateTime.parse(dataString);

                int idOrigem = rs.getInt("id_origem");
                int idDestino = rs.getInt("id_destino");

                Transacao transacao = new Transacao(idTransacao, tipo, valor, data, idOrigem, idDestino);

                transacoes.add(transacao);

            }

        } catch (SQLException e) {
          GestorBD.rollback(conn);
          throw e;

        } finally {
          if (rs != null) rs.close();
          if (stmt != null) stmt.close();
          GestorBD.closeConnection(conn);
        }

        return transacoes;
    }

    
    
    /**
	 * Insere um novo objeto Transacao utilizando uma Connection
	 * fornecida externamente.
	 * Este método é essencial para operações transacionais (como em  ContaDAO)
	 * onde a inserção do registo de transação deve fazer parte da mesma transação
	 * de base de dados que a atualização do saldo da conta, garantindo a atomicidade.
	 *
	 * @param conn - A Connection ativa que está a gerir a transação atual.
	 * @param transacao - O objeto Transacao a ser guardado.
	 * @throws SQLException Se ocorrer um erro no acesso à base de dados.
	 */
    public void insertInternal(Connection conn, Transacao transacao) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, transacao.getTipo());
            stmt.setDouble(2, transacao.getValor());
            stmt.setString(3, transacao.getData().toString());
            stmt.setInt(4, transacao.getId_origem());
            stmt.setInt(5, transacao.getId_destino());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGerado = rs.getInt(1);
                transacao.setId_transacao(idGerado);
                System.out.println("Transação inserida internamente! Novo ID: " + idGerado);
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

}
