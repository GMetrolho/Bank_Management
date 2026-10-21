package models;

import Interfaces.Operacoes;


/**
 * Classe abstrata base que representa uma Conta bancária genérica.
 * Implementa as operações bancárias básicas (depositar, levantar, transferir)
 * definidas na interface Operacoes.
 * 
 * Classes específicas como ContaCorrente e ContaPoupanca devem herdar desta classe.
 * 
 */
public abstract class Conta implements Operacoes {
	
	/** O identificador único da conta (Chave Primária). */
	protected int id_conta;
	/** O ID do cliente proprietário da conta (Chave Estrangeira). */
	protected int id_cliente; //FK
	/** O saldo atual da conta. */
	protected double saldo;

	//--- Construtor ---
	
	/**
	 * Construtor para inicializar uma nova Conta bancária.
	 *
	 * @param id_conta - O ID único da conta.
	 * @param id_cliente - O ID do cliente proprietário da conta.
	 * @param saldo - O saldo inicial da conta.
	 */
	public Conta(int id_conta, int id_cliente, double saldo) {

		this.id_conta = id_conta;
		this.id_cliente = id_cliente;
		this.saldo = saldo;

	}

	// ---- Getters ----
	
	/**
	 * Retorna o ID único da conta.
	 * @return O ID da conta.
	 */
	public int getId_conta() {
		return id_conta;
	}

	
	/**
	 * Retorna o ID do cliente proprietário da conta.
	 * @return O ID do cliente.
	 */
	public int getId_cliente() {
		return id_cliente;
	}

	
	/**
	 * Retorna o saldo atual da conta.
	 * @return O saldo atual.
	 */
	public double getSaldo() {
		return saldo;
	}

	
	/**
	 * Define o ID da conta.
     * Este método é útil para o DAO definir o ID gerado automaticamente pelo banco de dados
     * após a inserção de uma nova conta.
	 * @param id_conta - O novo ID da conta.
	 */
	public void setId_conta(int id_conta) {
    this.id_conta = id_conta;
}



	//--- Implementação dos métodos da Interface operações. ---

	
	/**
	 * Adiciona um valor ao saldo da conta.
	 *
	 * @param valor - O montante a ser depositado. Deve ser positivo.
	 *
	 */
	@Override
	public void depositar (double valor) {

		if(valor > 0) {
			saldo += valor;
			System.out.println("O depósito no valor de " + valor + "€ foi efetuado com sucesso na conta " + id_conta);
		}else {
			System.out.println("O valor do depósito é inválido");
		}

	}

	/**
	 * Retira um valor do saldo da conta, verificando se há fundos suficientes.
	 *
	 * @param valor - O montante a ser levantado. Deve ser positivo.
	 * @throws SaldoInsuficienteException Se o valor for superior ao saldo atual.
	 * @throws IllegalArgumentException Se o valor for menor ou igual a zero.
	 * 
	 */
	@Override
	public void levantar(double valor) throws SaldoInsuficienteException {

		if(valor <= 0) {
			throw new IllegalArgumentException("O valor de levantamento tem que ser positivo.");
		}

		if(saldo >= valor) {
			saldo -= valor;
			System.out.println("O levantamento de " + valor + "€ feito na conta " + id_conta + " foi efetuado com sucesso");
		}else {
			throw new SaldoInsuficienteException("Saldo insuficiente para levantar " + valor + "€ na conta " + id_conta );
		}

	}


	
	/**
	 * Transfere um valor desta conta para uma conta de destino.
	 * Utiliza o método levantar(double) para debitar desta conta
	 * e o método depositar(double) para creditar na conta destino.
	 *
	 * @param destino - A conta para onde o dinheiro será transferido.
	 * @param valor - O montante a ser transferido.
	 * @throws SaldoInsuficienteException Se o saldo for insuficiente para o levantamento.
	 * 
	 */
	@Override
	public void transferir(Conta destino, double valor) throws SaldoInsuficienteException {

		levantar(valor);

		destino.depositar(valor);

		System.out.println("Transferência no valor de " + valor + "€ para a conta " + destino.getId_conta() + " feita com sucesso!");

	}

}
