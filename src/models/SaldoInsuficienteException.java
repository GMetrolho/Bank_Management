package models;


/**
 * Exceção lançada para indicar que uma operação (ex: levantamento, transferência)
 * não pôde ser concluída devido a um saldo insuficiente na conta de origem.
 * 
 * Esta é uma exceção verificada (checked exception), forçando o código cliente
 * a tratá-la ou declará-la.
 * 
 */
public class SaldoInsuficienteException extends Exception{

	
	/**
	 * Construtor que cria uma nova exceção com a mensagem de detalhe especificada.
	 *
	 * @param mensagem - A mensagem de detalhe que descreve a causa específica do erro
	 * (ex: "Saldo insuficiente para efetuar o levantamento de X.").
	 */
	public SaldoInsuficienteException(String mensagem) {
		super(mensagem);
	}

}
