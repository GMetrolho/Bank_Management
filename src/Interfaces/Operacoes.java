package Interfaces;


import models.SaldoInsuficienteException;


/**
 * A interface Operacoes define o contrato para as operações financeiras
 * básicas que qualquer entidade de conta (como ContaCorrente ou ContaPoupanca})
 * deve implementar.
 * O foco está em garantir a existência dos métodos de depósito, levantamento 
 * e transferência, que são essenciais para a movimentação de fundos.
 *
 */
public interface Operacoes {

	
	/**
	 * Adiciona um valor especificado ao saldo da conta.
	 *
	 * @param valor - O montante a ser depositado. Deve ser positivo.
	 */
	void depositar(double valor);

	
	/**
	 * Retira um valor especificado do saldo da conta.
	 *
	 * @param valor - O montante a ser levantado. Deve ser positivo.
	 * @throws SaldoInsuficienteException Se o saldo atual (e, se aplicável, o limite)
	 * for insuficiente para cobrir o levantamento.
	 */
	void levantar(double valor) throws SaldoInsuficienteException;

	
	
	/**
	 * Transfere um valor especificado desta conta para uma conta de destino.
	 *
	 * @param destino - O objeto models.Conta que receberá o valor.
	 * @param valor - O montante a ser transferido. Deve ser positivo.
	 * @throws SaldoInsuficienteException Se o saldo atual (e, se aplicável, o limite)
	 * desta conta for insuficiente para cobrir a transferência.
	 */
	void transferir(models.Conta destino ,double valor) throws SaldoInsuficienteException;



}
