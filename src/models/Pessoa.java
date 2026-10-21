package models;


/**
 * Classe abstrata base que representa uma Pessoa genérica.
 * Contém atributos comuns a todas as pessoas no sistema, como nome, NIF, morada e idade.
 * 
 * Classes específicas (ex: Cliente, Funcionario) devem herdar desta classe.
 * 
 */
public abstract class Pessoa {
	/** O nome completo da pessoa. */
	protected String nome;
	/** O Número de Identificação Fiscal (NIF) da pessoa. */
	protected String NIF;
	/** A morada completa da pessoa. */
	protected String morada;
	/** A idade da pessoa. */
	protected int idade;


	
	/**
	 * Construtor para inicializar uma nova Pessoa.
	 *
	 * @param nome - O nome completo da pessoa.
	 * @param NIF - O Número de Identificação Fiscal (NIF) da pessoa.
	 * @param morada - A morada completa da pessoa.
	 * @param idade - A idade da pessoa.
	 */
	public Pessoa(String nome, String NIF, String morada, int idade) {
		this.nome = nome;
		this.NIF = NIF;
		this.morada = morada;
		this.idade = idade;
	}

	// --- Getters ---

	/**
	 * Retorna o nome completo da pessoa.
	 * @return O nome da pessoa.
	 */
	public String getNome() {
		return nome;
	}
	
	/**
	 * Retorna o Número de Identificação Fiscal (NIF) da pessoa.
	 * @return O NIF da pessoa.
	 */
	public String getNIF() {
		return NIF;
	}
	
	/**
	 * Retorna a morada da pessoa.
	 * @return A morada da pessoa.
	 */
	public String getMorada() {
		return morada;
	}
	
	/**
	 * Retorna a idade da pessoa.
	 * @return A idade da pessoa.
	 */
	public int getIdade() {
		return idade;
	}

	
	/**
	 * Define o nome completo da pessoa.
	 * @param nome - O novo nome completo.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/**
	 * Define o Número de Identificação Fiscal (NIF) da pessoa.
	 * @param NIF - O novo NIF.
	 */
	public void setNIF(String NIF) {
		this.NIF = NIF;
	}
	
	/**
	 * Define a morada da pessoa.
	 * @param morada - A nova morada.
	 */
	public void setMorada(String morada) {
		this.morada = morada;
	}
	
	/**
	 * Define a idade da pessoa.
	 * @param idade - A nova idade.
	 */
	public void setIdade(int idade) {
		this.idade = idade;
	}

}
