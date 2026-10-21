package DAO;


import java.sql.*;

import models.*;


/**
 * ClienteDAO é a classe de Objeto de Acesso a Dados (DAO)
 * responsável por todas as operações de persistência de dados
 * (CRUD - Criar, Ler, Atualizar, Apagar) para a entidade Cliente.
 * Esta classe interage diretamente com a tabela 'cliente' na base de dados,
 * utilizando o GestorBD para obter e gerir conexões.
 *
 */
public class ClienteDAO {

	//--- SQL QUERIES ---
	
	/** Query SQL para a inserção de um novo cliente na tabela 'cliente'. */
	private static final String CREATE_SQL = "INSERT INTO cliente (nome, NIF, morada, idade) VALUES (?, ?, ?, ?)";
	/**
	 * Query SQL para ler um cliente pelo seu ID, juntamente com todas as suas contas
	 * associadas através de um LEFT JOIN com a tabela 'conta'.
	 */
	private static final String SQL_READ_WITH_ACCOUNTS = "SELECT C.id_cliente, C.nome, C.NIF, C.morada, C.idade, A.id_conta, A.saldo, A.tipo, A.limite_descoberto, A.taxa_juro " +
		    											 "FROM cliente AS C LEFT JOIN conta AS A ON C.id_cliente = A.id_cliente WHERE C.id_cliente = ?";
	/** Query SQL para buscar um cliente utilizando o seu NIF como critério. */
    private static final String SQL_REACH_BY_NIF = "SELECT id_cliente, nome, nif, morada, idade FROM cliente WHERE nif = ?";
    /** Query SQL para atualizar os dados (nome, NIF, morada, idade) de um cliente existente. */
    private static final String SQL_UPDATE = "UPDATE cliente SET nome = ?, nif = ?, morada = ?, idade = ? WHERE id_cliente = ?";
    /** Query SQL para apagar um cliente através do seu ID. */
    private static final String SQL_DELETE = "DELETE FROM cliente WHERE id_cliente = ?";
    /** Query SQL para contar o número total de clientes existentes na tabela. */
    private static final String SQL_COUNT_CLIENTES = "SELECT COUNT(*) FROM cliente";



    //CRUD

    
    /**
	 * Cria um novo registo de cliente na base de dados.
	 * O ID gerado pela base de dados é definido no objeto Cliente
	 * passado após a execução bem-sucedida.
	 *
	 * @param cliente - O objeto Cliente a ser persistido. O ID deve ser nulo/0 antes da chamada.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
	public void criar(Cliente cliente) throws SQLException{

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = GestorBD.getConnection();

           
            stmt = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS);

            // 1. nome, 2. NIF, 3. morada, 4. idade
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getNIF());
            stmt.setString(3, cliente.getMorada());
            stmt.setInt(4, cliente.getIdade());

            stmt.executeUpdate();

           
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGerado = rs.getInt(1);
                cliente.setId_cliente(idGerado);
            }

            GestorBD.commit(conn); 

        } catch (SQLException e) {
            GestorBD.rollback(conn); 
            throw e;
        } finally {
            if (rs != null) rs.close();
            GestorBD.closeConnection(conn);
        }
    }


	
	/**
	 * Busca um cliente na base de dados pelo seu ID e carrega todas as suas contas associadas.
	 * O carregamento é feito através de um único LEFT JOIN na query SQL.
	 *
	 * @param id - O ID único do cliente a ser lido.
	 * @return O objeto Cliente completo, incluindo a sua lista de contas, ou null se o cliente não for encontrado.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
	public Cliente ler(int id) throws SQLException {
	    Cliente cliente = null;

	    try (Connection conn = GestorBD.getConnection();
	        
	         PreparedStatement stmt = conn.prepareStatement(SQL_READ_WITH_ACCOUNTS)) {
	        
	        stmt.setInt(1, id);

	        try (ResultSet rs = stmt.executeQuery()) {
	            
	            while (rs.next()) { 
	                
	                if (cliente == null) {
	                    
	                    String nome = rs.getString("nome");
	                    String nif = rs.getString("nif");
	                    String morada = rs.getString("morada");
	                    int idade = rs.getInt("idade");
	                    cliente = new Cliente(id, nome, nif, morada, idade); 
	                }

	                int idConta = rs.getInt("id_conta");
	                
	                
	                if (idConta != 0) { 
	                    double saldo = rs.getDouble("saldo");
	                    String tipo = rs.getString("tipo"); 

	                    Conta conta;
	                   
	                    if (tipo.equals("POUPANCA")) {
	                        double taxaJuro = rs.getDouble("taxa_juro");
	                        conta = new ContaPoupanca(idConta, id, saldo, taxaJuro);
	                    } else {
	                        double limiteDescoberto = rs.getDouble("limite_descoberto");
	                        conta = new ContaCorrente(idConta, id, saldo, limiteDescoberto);
	                    }
	                    
	                    cliente.getListaContas().add(conta);
	                }
	            }
	        }

	    } 

	    return cliente;
	}


    
	
	/**
	 * Procura um cliente na base de dados pelo NIF.
	 * Depois de encontrar o cliente, carrega as contas associadas usando o {@link ContaDAO}.
	 *
	 * @param nif - O Número de Identificação Fiscal (NIF) do cliente a ser procurado.
	 * @return O objeto Cliente completo, ou null se o cliente não for encontrado.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
    public Cliente lerPorNIF(String nif) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Cliente cliente = null;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_REACH_BY_NIF);
            stmt.setString(1, nif);

            rs = stmt.executeQuery();

            if(rs.next()) {

                int id = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String morada = rs.getString("morada");
                int idade = rs.getInt("idade");

                cliente = new Cliente(id, nome, nif, morada, idade);

                ContaDAO contaDAO = new ContaDAO();
                cliente.getListaContas().addAll(contaDAO.getContasByCliente(id));

            }

        } finally {
            GestorBD.closeConnection(conn);

        }

        return cliente;

    }


    
    /**
	 * Atualiza os dados de um cliente existente na base de dados.
	 *
	 * @param cliente O objeto Cliente com os dados atualizados. O ID é usado para identificar o registo a alterar.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
	public void atualizar(Cliente cliente) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getNIF());
            stmt.setString(3, cliente.getMorada());
            stmt.setInt(4, cliente.getIdade());
            stmt.setInt(5, cliente.getId_cliente());

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
	 * Apaga o registo de um cliente na base de dados pelo ID.
	 *
	 * @param id - O ID do cliente a ser apagado.
	 * @throws SQLException Se ocorrer um erro durante a operação de base de dados.
	 */
	public void apagar(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } finally {
            GestorBD.closeConnection(conn);
        }
    }

	
	/**
	 * Verifica se existe pelo menos um cliente registado na base de dados.
	 *
	 * @return true se existir um ou mais clientes, false caso contrário.
	 * @throws SQLException Se ocorrer um erro durante a contagem na base de dados.
	 */
    public boolean existeAlgumCliente() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            conn = GestorBD.getConnection();
            stmt = conn.prepareStatement(SQL_COUNT_CLIENTES);
            rs = stmt.executeQuery();

            if (rs.next()) {
                
                int count = rs.getInt(1);
                existe = (count > 0);
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            
            GestorBD.closeConnection(conn);
        }
        return existe;
    }

}
