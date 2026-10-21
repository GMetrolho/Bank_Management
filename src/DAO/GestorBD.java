package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * GestorBD é uma classe de utilidade estática responsável por gerir
 * a conexão com a base de dados (SQLite) e o controlo de transações (ACID).
 * Garante que cada Connection é criada com setAutoCommit(false,
 * permitindo que as operações de CRUD e de negócio (em classes DAO) sejam
 * executadas atomicamente usando commit(Connection) e rollback(Connection).
 *
 */
public class GestorBD {
	/** Constante que define o URL de conexão para a base de dados SQLite. */
    private static final String bd = "jdbc:sqlite:banco.db";

    
    /**
   	 * Estabelece e retorna uma nova conexão com a base de dados.
   	 *
   	 * Importante: A conexão é configurada com setAutoCommit(false),
   	 * o que significa que todas as operações devem ser explicitamente
   	 * confirmadas (via commit(Connection)) ou revertidas (via rollback(Connection)).
   	 *
   	 * @return Uma nova instância de Connection configurada para transações manuais.
   	 * @throws SQLException Se ocorrer um erro durante a tentativa de conexão.
   	 */
    public static Connection getConnection() throws SQLException {
            Connection conn = DriverManager.getConnection(bd);

        conn.setAutoCommit(false);
        //

         return conn;
    }

    
    /**
	 * Estabelece a conexão com a base de dados e executa a criação das tabelas
	 * cliente, conta e transacao, caso ainda não existam.
	 * Também verifica e adiciona colunas de migração se necessário (limite_descoberto e taxa_juro).
	 */
    public static void getConnectionAndCreateTables() {

        try (Connection conn = DriverManager.getConnection(bd);
             Statement stmt = conn.createStatement()) {

            System.out.println("Ligação estabelecida com sucesso!");

                String sqlCliente = "CREATE TABLE IF NOT EXISTS cliente (" +
                    "id_cliente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "nif TEXT UNIQUE NOT NULL, " +
                    "morada TEXT, " +
                    "idade INTEGER)";

                stmt.execute(sqlCliente);
                System.out.println("Tabela 'cliente' criada/verificada com sucesso.");

                String sqlConta = "CREATE TABLE IF NOT EXISTS conta (" +
                    "id_conta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_cliente INTEGER NOT NULL, " +
                    "tipo TEXT, " +
                    "saldo REAL DEFAULT 0, " +
                    "limite_descoberto REAL DEFAULT 0, " +
                    "taxa_juro REAL DEFAULT 0, " +
                    "FOREIGN KEY(id_cliente) REFERENCES cliente(id_cliente))";

                stmt.execute(sqlConta);
                System.out.println("Tabela 'conta' criada/verificada com sucesso.");

                String sqlTransacao = "CREATE TABLE IF NOT EXISTS transacao (" +
                    "id_transacao INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipo TEXT NOT NULL, " +
                    "valor REAL NOT NULL, " +
                    "data TEXT NOT NULL, " +
                    "id_origem INTEGER NOT NULL, " +
                    "id_destino INTEGER NOT NULL, " +
                    "FOREIGN KEY(id_origem) REFERENCES conta(id_conta), " +
                    "FOREIGN KEY(id_destino) REFERENCES conta(id_conta))";
                stmt.execute(sqlTransacao);
                System.out.println("Tabela 'transacao' criada/verificada com sucesso.");

                try {
                    stmt.execute("ALTER TABLE conta ADD COLUMN limite_descoberto REAL DEFAULT 0");
                } catch (SQLException ignore) {
                }

                try {
                    stmt.execute("ALTER TABLE conta ADD COLUMN taxa_juro REAL DEFAULT 0");
                } catch (SQLException ignore) {
                }

        } catch (SQLException e) {
            System.out.println("Erro ao ligar ou criar tabela: " + e.getMessage());
        }
    }

    
    /**
	 * Fecha a conexão de base de dados, prevenindo a saída de recursos.
	 *
	 * @param conn - A Connection a ser fechada. Se for null, não faz nada.
	 */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

    
    /**
	 * Confirma (commit) a transação atual na conexão fornecida, tornando permanentes
	 * todas as alterações feitas desde o último commit/rollback.
	 *
	 * @param conn - A Connection que deve ter o commit executado.
	 * @throws SQLException Se ocorrer um erro durante a confirmação da transação.
	 */
    public static void commit(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    
    /**
	 * Reverte (rollback) a transação atual na conexão fornecida, desfazendo
	 * todas as alterações feitas desde o último commit/rollback e restaurando
	 * o estado anterior da base de dados.
	 *
	 * @param conn - A Connection que deve ter o rollback executado.
	 */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("Erro ao fazer rollback: " + e.getMessage());
            }
        }
    }
}
