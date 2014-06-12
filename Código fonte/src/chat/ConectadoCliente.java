package chat;

import java.io.ObjectOutputStream;
import java.security.PublicKey;

/*
 Esta classe serve de controle de clientes conectados no servidor,
 mantendo em memória de execução
 */

public class ConectadoCliente {

	private String EnderecoIP;// ip do cliente
	private PublicKey chavePublica;// chave pública do cliente
	private ObjectOutputStream escritor;// referência do cliente conectado para
										// o servidor poder lhe enviar
										// informações

	public ConectadoCliente() {
		super();
	}

	public ConectadoCliente(String enderecoIP, ObjectOutputStream escritor) {
		super();
		EnderecoIP = enderecoIP;
		this.escritor = escritor;
	}

	public String getEnderecoIP() {
		return EnderecoIP;
	}

	public void setEnderecoIP(String enderecoIP) {
		EnderecoIP = enderecoIP;
	}

	public ObjectOutputStream getEscritor() {
		return escritor;
	}

	public void setEscritor(ObjectOutputStream escritor) {
		this.escritor = escritor;
	}

	public PublicKey getChavePublica() {
		return chavePublica;
	}

	public void setChavePublica(PublicKey chavePublica) {
		this.chavePublica = chavePublica;
	}

}// fim class ConectadoCliente
