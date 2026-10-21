package models;

import java.time.LocalDateTime;


/**
 * Representa uma transação bancária, como depósito, levantamento ou transferência.
 * Esta classe armazena informações essenciais sobre a transação,
 * incluindo ID, tipo, valor, data/hora, e as contas de origem e destino.
 */
public class Transacao {
	/** O identificador único da transação (Chave Primária). */
    private int id_transacao; //PK
    /** O tipo de transação (ex: "DEPOSITO", "LEVANTAMENTO", "TRANSFERENCIA"). */
    private String tipo;
    /** O valor monetário envolvido na transação. */
    private double valor;
    /** A data e hora em que a transação ocorreu. */
    private LocalDateTime data;
    /** O ID da conta de origem envolvida na transação. Pode ser 0 para depósitos/levantamentos. */
    private int id_origem;
    /** O ID da conta de destino envolvida na transação. Pode ser 0 para levantamentos. */
    private int id_destino;

    /**
     * Construtor para criar uma **nova** transação.
     * A data da transação é definida automaticamente para a data e hora atuais.
     * Este construtor é tipicamente usado pelo serviço/DAO antes de inserir no banco de dados.
     *
     * @param id_transacao - O ID da transação (pode ser temporário, ou 0 se gerado pela BD).
     * @param tipo - O tipo da transação (ex: "DEPOSITO", "TRANSFERENCIA").
     * @param valor - O valor da transação.
     * @param id_origem - O ID da conta de origem.
     * @param id_destino - O ID da conta de destino.
     */
    public Transacao(int id_transacao, String tipo, double valor, int id_origem, int id_destino) {
        this.id_transacao = id_transacao; 
        this.tipo = tipo;
        this.valor = valor;
        this.data = LocalDateTime.now(); 
        this.id_origem = id_origem;
        this.id_destino = id_destino;
    }

    /**
     * Construtor para **ler** uma transação existente do banco de dados (BD).
     * A data da transação é fornecida como um parâmetro, sendo o valor lido da BD.
     * Este construtor é tipicamente usado pelo TransacaoDAO.
     *
     * @param id_transacao - O ID único da transação lido da BD.
     * @param tipo - O tipo da transação lido da BD.
     * @param valor - O valor da transação lido da BD.
     * @param data - A data e hora da transação lida da BD.
     * @param id_origem - O ID da conta de origem lido da BD.
     * @param id_destino - O ID da conta de destino lido da BD.
     */
    public Transacao(int id_transacao, String tipo, double valor,
                     LocalDateTime data, int id_origem, int id_destino) {
        this.id_transacao = id_transacao;
        this.tipo = tipo;
        this.valor = valor;
        this.data = data; // Data LIDA da BD
        this.id_origem = id_origem;
        this.id_destino = id_destino;
    }


    // --- Getters e Setters ---

    
    /**
     * Retorna o ID único da transação.
     * @return O ID da transação.
     */
    public int getId_transacao() {
        return id_transacao;
    }

    /**
     * Define o ID da transação.
     * Este método é útil para o DAO definir o ID gerado automaticamente pelo banco de dados
     * após a inserção de uma nova transação.
     * @param id_transacao - O novo ID da transação.
     */    public void setId_transacao(int id_transacao) {
        this.id_transacao = id_transacao;
    }

     
    /**
     * Retorna o tipo de transação.
     * @return O tipo da transação (ex: "DEPOSITO", "TRANSFERENCIA").
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Retorna o valor monetário da transação.
     * @return O valor da transação.
     */
    public double getValor() {
        return valor;
    }

    /**
     * Retorna a data e hora em que a transação ocorreu.
     * @return A data e hora da transação.
     */
    public LocalDateTime getData() {
        return data;
    }

    /**
     * Retorna o ID da conta de origem.
     * @return O ID da conta de origem.
     */
    public int getId_origem() {
        return id_origem;
    }

    /**
     * Retorna o ID da conta de destino.
     * @return O ID da conta de destino.
     */
    public int getId_destino() {
        return id_destino;
    }
}
