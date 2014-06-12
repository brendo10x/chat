package chat;

import java.io.Serializable;

/*

 Esta classe serve para montar uma conjunto de informa��es para envio 
 e recibo de informa��es entre clientes com distribui��o da mesma pelo servidor

 */

public class InfoDestinoOuRecibo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String nome;// nome de quem enviou
	private String EnderecoIP;// ip do destinat�rio
	private byte[] chaveSimetrica;// chave sim�trica cifrada
	private byte[] mensagem; // mensagem cifrada

	public InfoDestinoOuRecibo() {
		super();
	}

	public InfoDestinoOuRecibo(String enderecoIP, byte[] chaveSimetrica,
			byte[] mensagem) {
		super();
		this.EnderecoIP = enderecoIP;
		this.chaveSimetrica = chaveSimetrica;
		this.mensagem = mensagem;
	}

	public String getEnderecoIP() {
		return EnderecoIP;
	}

	public void setEnderecoIP(String enderecoIP) {
		EnderecoIP = enderecoIP;
	}

	public byte[] getChaveSimetrica() {
		return chaveSimetrica;
	}

	public void setChaveSimetrica(byte[] chaveSimetrica) {
		this.chaveSimetrica = chaveSimetrica;
	}

	public byte[] getMensagem() {
		return mensagem;
	}

	public void setMensagem(byte[] mensagem) {
		this.mensagem = mensagem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}// fim class InfoDestinoOuRecibo
