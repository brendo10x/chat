package chat;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import criptografia.Criptografia;

/*

 Classe usada pelo servidor como ponto conex�o de 
 clientes para recibo e distribui��o de informa��o
 pela rede

 */

public class ChatServidor extends JFrame {

	private static final long serialVersionUID = 1L;
	// vari�veis de escopo global
	String texto;
	JTextArea txtFluxoDeInformacoes;
	ServerSocket servidor;
	List<ConectadoCliente> conectadoCliente = new ArrayList<>();

	// inicializando o servidor, e monitorando novos sockets
	// ou conex�es de novos clientes
	@SuppressWarnings("static-access")
	public ChatServidor() {

		// criando componentes
		Container painelPrincipal = new JPanel();
		painelPrincipal.setLayout(new GridLayout(1, 1));

		txtFluxoDeInformacoes = new JTextArea();
		JScrollPane scrollFluxoDeInformacoes = new JScrollPane(
				txtFluxoDeInformacoes);
		((JComponent) scrollFluxoDeInformacoes).setBorder(BorderFactory
				.createTitledBorder("Servidor - Fluxo de informa��o"));
		painelPrincipal.add(scrollFluxoDeInformacoes);

		getContentPane().add(painelPrincipal);

		// configura��es de janela
		setTitle("Servidor");
		setBounds(800, 30, 500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			// cria o servidor
			servidor = new ServerSocket(5100);
			// mostra informa��o
			txtFluxoDeInformacoes.append("Servidor conectado... \n");

			while (true) {

				// monitora se a novas conex�es de clientes neste servidor
				Socket socket = servidor.accept();

				// leitor inicial para captura da chave p�blica do novo
				// cliente
				ObjectInputStream leitor = new ObjectInputStream(
						socket.getInputStream());

				// recupera chave p�blica do cliente conectado
				PublicKey chvPublica = (PublicKey) leitor.readObject();

				// escritor referente ao cliente conectado para que o servidor
				// possa lhe enviar informa��es
				ObjectOutputStream output = new ObjectOutputStream(
						socket.getOutputStream());

				// monta o objeto cliente conectado, serve de aux�lio para o
				// servidor ter controle de quem est� conectado
				ConectadoCliente conctCliente = new ConectadoCliente();

				// define
				conctCliente.setEscritor(output);
				conctCliente.setChavePublica(chvPublica);

				// verificando se o socket do novo clente esta na mesma m�quina
				// tipo se o o cliente est� na mesma m�quina do servidor
				if (socket.getInetAddress().getHostAddress()
						.equalsIgnoreCase("127.0.0.1")) {
					// define
					conctCliente.setEnderecoIP(socket.getLocalAddress()
							.getLocalHost().getHostAddress());
				} else {
					// define
					conctCliente.setEnderecoIP(socket.getInetAddress()
							.getHostAddress());
				}// fim if e else

				// adiciona a lista de conectados
				conectadoCliente.add(conctCliente);

				// mostra informa��es
				txtFluxoDeInformacoes.append(("Cliente "
						+ socket.getInetAddress().getHostName()
						+ " com Ip conectado: " + conctCliente.getEnderecoIP()
						+ " no servidor com chave p�blica : "
						+ chvPublica.toString() + "\n"));

				// monitora cada cliente conectado com seu leitor referente
				new Thread(new EscutaCliente(leitor)).start();

			}// fim while

			// trata erro
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			txtFluxoDeInformacoes
					.append("Erro ao adicionar novo cliente de conex�o neste servidor - msg: "
							+ e.getMessage() + "\n causado por " + e.getCause());
		}// fim catch e try

	}// fim do contrutor padr�o ChatServidor

	// monitora o que o cliente est� lhe enviando
	private class EscutaCliente implements Runnable {

		ObjectInputStream leitor;

		public EscutaCliente(ObjectInputStream leitor) {

			this.leitor = leitor;

		}// fim construtor padr�o EscutaCliente

		// aqui ele vai ficar monitorando oque est� sento esquito pelo cliente
		@SuppressWarnings("unchecked")
		@Override
		public void run() {

			try {

				while (true) {

					Object tipoObjeto = leitor.readObject();

					// cliente est� solicitando conex�o com amigo
					if (tipoObjeto instanceof List) {

						List<String> listaIps = (List<String>) tipoObjeto;
						String ipDoSolicitador = listaIps.get(0);
						String ipDoAmigoDoSolicitador = listaIps.get(1);

						// enviando para ambos a chave p�blica
						encaminharChavePublicaAmigo(ipDoSolicitador,
								ipDoAmigoDoSolicitador);

					}// fim if

					// est� enviando mensagem para seu amigo
					if (tipoObjeto instanceof InfoDestinoOuRecibo) {
						InfoDestinoOuRecibo info = (InfoDestinoOuRecibo) tipoObjeto;

						encaminharInformacao(info);

					}// fim if

				}// fim while

				// trata erro
			} catch (ClassNotFoundException | IOException e) {

				e.printStackTrace();

			}// fim catch e try

		}// fim run()

	}// fim da classe EscutaCliente

	// solicita��o e troca de chaves p�blicas
	private void encaminharChavePublicaAmigo(String ipDoSolicitador,
			String ipDoAmigoDoSolicitador) {

		// envia chve p�blica do solicitador para o amigo do solicitador
		for (ConectadoCliente cliente : conectadoCliente) {
			if (cliente.getEnderecoIP()
					.equalsIgnoreCase(ipDoAmigoDoSolicitador)) {

				// achou
				PublicKey chvPublica = recuperarChavePublicaPorIp(ipDoSolicitador);

				try {

					// prepara informa��o
					cliente.getEscritor().writeObject((chvPublica));
					// envia informa��o
					cliente.getEscritor().flush();

					break;// para loop

					// trata erro
				} catch (IOException e) {

					e.printStackTrace();

				}// fim catch e try

			}// fim if

		}// fim for

		// envia chve p�blica do amigo para solicitador
		for (ConectadoCliente cliente : conectadoCliente) {
			if (cliente.getEnderecoIP().equalsIgnoreCase(ipDoSolicitador)) {

				// achou
				PublicKey chvPublica = recuperarChavePublicaPorIp(ipDoAmigoDoSolicitador);

				try {
					// preparando informa��o
					cliente.getEscritor().writeObject((chvPublica));
					// envia informa��es
					cliente.getEscritor().flush();

					break;// para loop

					// trata erro
				} catch (IOException e) {

					e.printStackTrace();

				}// fim catch e try

			}// fim if

		}// fim for

	}// fim encaminharChavePublicaAmigo()

	// recuperar a chave p�blica por ip
	private PublicKey recuperarChavePublicaPorIp(String ip) {
		PublicKey chvPublica = null;
		// procurar por ip a chave publica
		for (ConectadoCliente cliente : conectadoCliente) {
			if (cliente.getEnderecoIP().equalsIgnoreCase(ip)) {

				chvPublica = cliente.getChavePublica();

			}// fim if

		}// fim for

		return chvPublica;

	}// fim recuperarChavePublicaPorIp()

	// fluxo de informa��o destino
	private void encaminharInformacao(InfoDestinoOuRecibo info) {

		// procurar o ip do meu amigo, para envio de informa��o
		for (ConectadoCliente cliente : conectadoCliente) {
			if (cliente.getEnderecoIP().equalsIgnoreCase(info.getEnderecoIP())) {

				try {

					// prepara informa��o
					cliente.getEscritor().writeObject((info));
					// envia informa��o
					cliente.getEscritor().flush();

					// mostra informa��es no servidor
					txtFluxoDeInformacoes.append(info.getNome()
							+ " envou para o  ip: "
							+ info.getEnderecoIP()
							+ " sua msg cifrada: "
							+ Criptografia.montaPalavra(info.getMensagem())
							+ " "
							+ " com chave sim�trica cifrada: "
							+ Criptografia.montaPalavra(info
									.getChaveSimetrica()) + "\n\n");

					break;// para loop

					// trata erro
				} catch (IOException e) {

					e.printStackTrace();

				}// fim catch e try

			}// fim if

		}// fim for

	}// fim encaminharInformacao()

	// executa
	public static void main(String[] args) {
		new ChatServidor();
	}

}// fim class ChatServidor
