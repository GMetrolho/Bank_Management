package models;


/**
 * Representa uma Conta Corrente, que é um tipo de Conta
 * que permite ao titular ficar a descoberto até um certo limite pré-definido.
 * Esta classe estende as funcionalidades base da Conta,
 * nomeadamente a lógica de levantamento.
 */
public class ContaCorrente extends Conta{
	
	/** O limite máximo que o titular da conta pode ficar a descoberto (saldo negativo). */
	private double limiteDescoberto;

	
	/**
	 * Construtor para inicializar uma nova Conta Corrente.
	 *
	 * @param id_conta - O ID único da conta.
	 * @param id_cliente - O ID do cliente proprietário da conta.
	 * @param saldo - O saldo inicial da conta.
	 * @param limiteDescoberto - O valor máximo do limite de descoberto permitido.
	 */
	public ContaCorrente (int id_conta, int id_cliente, double saldo, double limiteDescoberto) {
		super(id_conta, id_cliente, saldo);
		this.limiteDescoberto = limiteDescoberto;
	}

	
	/**
	 * Realiza um levantamento da conta.
	 * O levantamento é permitido se o valor for menor ou igual à soma do saldo atual
	 * com o limiteDescoberto.
	 *
	 * @param valor - O montante a ser levantado. Deve ser positivo.
	 * @throws SaldoInsuficienteException Se o valor for superior ao saldo disponível
	 * mais o limite de descoberto.
	 * @throws IllegalArgumentException Se o valor for menor ou igual a zero.
	 */
	@Override
	public void levantar (double valor) throws SaldoInsuficienteException{
    if (valor <= 0) {
        throw new IllegalArgumentException("O valor de levantamento tem que ser positivo.");
    }

    double saldoComLimite = saldo + limiteDescoberto;

    if (saldoComLimite >= valor) {
        saldo -= valor;
        System.out.println("O levantamento de " + valor + "€ feito na Conta Corrente " + id_conta + " foi efetuado com sucesso.");

        if (saldo < 0) {
            System.out.println("A conta encontra-se a descoberto. Saldo atual: " + saldo +"€");
        }

    } else {
        throw new SaldoInsuficienteException ("Saldo insuficiente (incluindo descoberto de " + limiteDescoberto + "€) para levantar " + valor +"€ na conta " + id_conta );
    }
}


	//--- Getters e Setters ---

	
	/**
	 * Retorna o limite máximo que a conta pode ficar a descoberto.
	 * @return O limite de descoberto.
	 */
	public double getLimiteDescoberto() {
		return limiteDescoberto;
	}

	
	/**
	 * Define um novo valor para o limite de descoberto da conta.
	 * @param limiteDescoberto - O novo limite de descoberto.
	 */
	public void setLimiteDescoberto(double limiteDescoberto) {
		this.limiteDescoberto = limiteDescoberto;
	}
}
