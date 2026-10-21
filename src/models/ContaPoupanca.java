package models;

public class ContaPoupanca extends Conta{
	
	
	/**
	 * A taxa de juro aplicada ao saldo da conta poupança.
	 * O valor deve ser representado como uma proporção (ex: 0.05 para 5%).
	 */
	private double taxa_juro;

	
	/**
	 * Construtor para criar uma nova ContaPoupanca.
	 *
	 * @param id_conta - O identificador único da conta.
	 * @param id_cliente - O identificador do cliente proprietário da conta.
	 * @param saldo - O saldo inicial da conta.
	 * @param taxa_juro - A taxa de juro a ser aplicada à conta (ex: 0.05).
	 */
	public ContaPoupanca(int id_conta, int id_cliente, double saldo, double taxa_juro) {
		super(id_conta, id_cliente, saldo);
		this.taxa_juro = taxa_juro;
	}

	//--- Lógica da classe ContaPoupanca ---

	
	/**
	 * Calcula os juros com base na taxa_juro e adiciona o valor resultante ao saldo atual da conta.
	 * A fórmula utilizada é: juros = saldo * taxa_juro.
	 */
	public void calculoJuros() {

		double juros = saldo * taxa_juro;
		saldo += juros;
		
	}

	
	/**
	 * Obtém a taxa de juro atual da conta poupança.
	 *
	 * @return A taxa de juro em formato de proporção.
	 */
	public double getTaxaJuro() {
		return taxa_juro;
	}

	
	/**
	 * Define ou atualiza a taxa de juro da conta poupança.
	 *
	 * @param taxa_juro - O novo valor da taxa de juro (em proporção).
	 */
	public void setTaxaJuro(double taxa_juro) {
		this.taxa_juro = taxa_juro;
	}

}
