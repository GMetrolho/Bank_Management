package models;

import java.util.ArrayList;
import java.util.List;




/**
 * Representa um Cliente do banco, que herda as características básicas de uma Pessoa.
 * Um Cliente possui um ID único e gerencia uma lista das suas Contas bancárias.
 * 
 * O Cliente atua como o agregado principal para as contas bancárias no sistema.
 * 
 * 
 */
public class Cliente extends Pessoa{

	/** O identificador único do cliente (PK). */
	private int id_cliente; 

	/** Lista de contas bancárias (Conta) pertencentes a este cliente. */
	private List<Conta> listaContas;


	
	/**
	 * Construtor para inicializar um novo Cliente.
	 *
	 * @param id_cliente - O ID único do cliente.
	 * @param nome - O nome completo do cliente (herdado de Pessoa).
	 * @param NIF - O NIF do cliente (herdado de Pessoa).
	 * @param morada - A morada do cliente (herdado de Pessoa).
	 * @param idade - A idade do cliente (herdado de Pessoa).
	 */
	public Cliente(int id_cliente, String nome, String NIF, String morada, int idade) {
		super(nome, NIF, morada, idade);
		this.id_cliente = id_cliente;
		this.listaContas =  new ArrayList<>();

	}

	//--- Getters ---

	
	/**
	 * Retorna o ID único do cliente.
	 * @return O ID do cliente.
	 */
	public int getId_cliente() {
		return id_cliente;
	}

	
	/**
	 * Retorna a lista de todas as contas associadas a este cliente.
	 * @return Uma List<Conta> que contem todas as contas do cliente.
	 */
	public List<Conta> getListaContas(){
		return listaContas;
	}

	
	/**
	 * Define o ID do cliente.
	 * Este método é útil para o DAO definir o ID gerado automaticamente pelo banco de dados.
	 * @param id_cliente - O novo ID do cliente.
	 */
	public void setId_cliente(int id_cliente) {
		this.id_cliente = id_cliente;
	}


	
	
	/**
	 * Adiciona uma conta bancária à lista de contas do cliente.
	 * A conta não será adicionada se for nula.
	 *
	 * @param conta - O objeto Conta a ser adicionado ao cliente.
	 */
	public void adicionarConta(Conta conta) {
		if(conta != null) {
			listaContas.add(conta);
		}
	}
}
