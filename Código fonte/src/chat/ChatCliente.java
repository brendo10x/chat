package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import criptografia.Criptografia;

/*

 Classe utilizada pelo cliente, para se conectar
 a outro cliente ou "amigo" e ao servidor

 */

public class ChatCliente extends JFrame {

	private static final long serialVersionUID = 1L;

	// vair�veis de escopo global

	// vari�veis GUI
	JTextField textoParaEnviar, txtIPservidor, txtIPamigo;
	JTextArea txtMinhaChavePublica, txtAmigoChavePublica,
			txtMinhaChaveSimetrica, txtMinhaChavePrivada,
			txtAmigoChaveSimetricaCrifrada;
	JLabel lblNomeAmigo, lblNomeServidor;
	String nome;
	JTextArea textoRecebido;
	JButton botaoEnviarMensagem, botaoConectaServidor, botaoConectaAmigo;

	// vari�veis de rede
	Socket socket;
	ObjectOutputStream escritor;
	ObjectInputStream leitor;

	// obj de informa��es
	InfoDestinoOuRecibo infoDestinoOuRecibo;

	// vari�veis de criptografia
	PrivateKey chavePrivada;
	PublicKey chavePublica;
	// quando recebe do servidor
	PublicKey chvPublicaDoMeuAmigo;

	// inicializa o chat
	public ChatCliente(String nome) {
		// inicia nome
		this.nome = nome;

		// define t�tulo
		setTitle("Chat: " + nome);

		// cria fonte
		Font fonte = new Font("Serif", Font.PLAIN, 12);

		// criando componentes
		lblNomeAmigo = new JLabel("IP do seu amigo:");
		lblNomeServidor = new JLabel("IP do servidor");

		textoParaEnviar = new JTextField();
		textoParaEnviar.setFont(fonte);

		txtIPservidor = new JTextField("127.0.0.1");
		txtIPamigo = new JTextField();

		txtMinhaChavePublica = new JTextArea();
		txtMinhaChavePublica.setBackground(Color.green);

		txtAmigoChavePublica = new JTextArea();
		txtAmigoChavePublica.setBackground(Color.CYAN);

		txtMinhaChaveSimetrica = new JTextArea();
		txtMinhaChaveSimetrica.setBackground(Color.orange);

		txtAmigoChaveSimetricaCrifrada = new JTextArea();
		txtMinhaChavePrivada = new JTextArea();
		txtMinhaChavePrivada.setBackground(Color.YELLOW);

		textoRecebido = new JTextArea();
		textoRecebido.setMargin(new Insets(10, 10, 10, 10));
		textoRecebido.setFont(fonte);
		textoRecebido.setAutoscrolls(true);

		botaoEnviarMensagem = new JButton("Enviar mensagem");
		botaoEnviarMensagem.setFont(fonte);
		botaoEnviarMensagem.addActionListener(new MonitoraBotao());

		botaoConectaServidor = new JButton("Conecta Servidor");
		botaoConectaServidor.addActionListener(new MonitoraBotao());

		botaoConectaAmigo = new JButton("Conecta amigo");
		botaoConectaAmigo.addActionListener(new MonitoraBotao());

		// criando containers princip�is
		Container painelPrincipal = new JPanel();
		painelPrincipal.setLayout(new GridLayout(4, 1));
		painelPrincipal.setPreferredSize(new Dimension(100, 100));

		Container painelEsquerdo = new JPanel();
		painelEsquerdo.setLayout(new GridLayout(2, 1));

		Container painelDireito = new JPanel();
		painelDireito.setLayout(new GridLayout(3, 1));
		painelDireito.setPreferredSize(new Dimension(240, 100));

		// criando containers secund�rios
		Container painelEsqMinhaChavePublica = new JPanel(new GridLayout(1, 1));
		painelEsqMinhaChavePublica.setPreferredSize(new Dimension(200, 500));
		((JComponent) painelEsqMinhaChavePublica).setBorder(BorderFactory
				.createTitledBorder("Minha chave p�blica:"));

		Container painelEsqAmigoChavePublica = new JPanel(new GridLayout(1, 1));
		((JComponent) painelEsqAmigoChavePublica).setBorder(BorderFactory
				.createTitledBorder("Chave p�blica do meu amigo:"));

		Container painelDirMinhaChavePrivada = new JPanel(new GridLayout(1, 1));
		((JComponent) painelDirMinhaChavePrivada).setBorder(BorderFactory
				.createTitledBorder("Minha chave privada:"));

		Container painelDirMinhaChaveSimetrica = new JPanel(
				new GridLayout(1, 1));
		((JComponent) painelDirMinhaChaveSimetrica).setBorder(BorderFactory
				.createTitledBorder("Minha chave sim�trica:"));

		Container painelDirMinhaChaveSimetricaCifrada = new JPanel(
				new GridLayout(1, 1));
		((JComponent) painelDirMinhaChaveSimetricaCifrada)
				.setBorder(BorderFactory
						.createTitledBorder("Chave sim�trica crifada do meu amigo: "));

		// criando barra de rolagem
		JScrollPane scrolltextoRecebido = new JScrollPane(textoRecebido);

		JScrollPane scrollMinhaChavePublica = new JScrollPane(
				txtMinhaChavePublica);

		JScrollPane scrollAmigoChavePublica = new JScrollPane(
				txtAmigoChavePublica);

		JScrollPane scrollMinhaChavePrivada = new JScrollPane(
				txtMinhaChavePrivada);
		JScrollPane scrollMinhaChaveSimetrica = new JScrollPane(
				txtMinhaChaveSimetrica);

		JScrollPane scrollMinhaChaveSimetricaCrifrada = new JScrollPane(
				txtAmigoChaveSimetricaCrifrada);

		// add ao painel principal
		painelPrincipal.add(textoParaEnviar);
		painelPrincipal.add(botaoEnviarMensagem);
		painelPrincipal.add(botaoConectaServidor);
		painelPrincipal.add(botaoConectaAmigo);
		painelPrincipal.add(lblNomeServidor);
		painelPrincipal.add(lblNomeAmigo);
		painelPrincipal.add(txtIPservidor);
		painelPrincipal.add(txtIPamigo);

		// add rolagem aos pain�is
		painelEsqMinhaChavePublica.add(scrollMinhaChavePublica);
		painelEsqAmigoChavePublica.add(scrollAmigoChavePublica);
		painelDirMinhaChavePrivada.add(scrollMinhaChavePrivada);
		painelDirMinhaChaveSimetrica.add(scrollMinhaChaveSimetrica);
		painelDirMinhaChaveSimetricaCifrada
				.add(scrollMinhaChaveSimetricaCrifrada);

		// add os pain�is segund�rios dentro de pain�is princip�is
		painelEsquerdo.add(painelEsqMinhaChavePublica);
		painelEsquerdo.add(painelEsqAmigoChavePublica);
		painelDireito.add(painelDirMinhaChavePrivada);
		painelDireito.add(painelDirMinhaChaveSimetrica);
		painelDireito.add(painelDirMinhaChaveSimetricaCifrada);

		// add ao conte�do principal
		getContentPane().add(scrolltextoRecebido);
		getContentPane().add(BorderLayout.SOUTH, painelPrincipal);
		getContentPane().add(BorderLayout.WEST, painelEsquerdo); // esquerda
		getContentPane().add(BorderLayout.EAST, painelDireito); // direta

		// Inicializando criptografia
		inicializaCriptografia();

		// x,y,largura,altura
		setBounds(10, 10, 950, 700);
		// fechamento padr�o
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// torna vis�vel
		setVisible(true);

	}// fim do contrutor padr�o ChatCliente

	// escuta servidor - recebe informa��es
	private class EscutaServidor implements Runnable {

		// receber informa��es do servidor
		@Override
		public void run() {

			try {
				while (true) {

					Object tipoObjeto = leitor.readObject();

					// se for do tipo PublicKey
					// est� recebendo a chave p�blica do amigo
					if (tipoObjeto instanceof PublicKey) {

						chvPublicaDoMeuAmigo = (PublicKey) tipoObjeto;

						txtAmigoChavePublica.append(chvPublicaDoMeuAmigo
								.toString() + "\n");

					}// fim if

					// se for do tipo InfoDestinoOuRecibo
					// est� recebendo informa��es do amigo
					if (tipoObjeto instanceof InfoDestinoOuRecibo) {

						// recupera o objeto desejado
						InfoDestinoOuRecibo info = (InfoDestinoOuRecibo) tipoObjeto;

						// decrifra a mensagem
						List<String> listaDecifrado = Criptografia.decifra(
								chavePrivada, info.getMensagem(),
								info.getChaveSimetrica());

						// recupera a mensagem e a chv sim�trica decifrada
						String mensagemDecifrada = listaDecifrado.get(0);
						String chvSimetricaDecifrada = listaDecifrado.get(1);

						// mostrar informa��es
						txtAmigoChaveSimetricaCrifrada.setText(Criptografia
								.montaPalavra(info.getChaveSimetrica()));

						textoRecebido.append(info.getNome()
								+ " disse:\n msg decifrada: "
								+ mensagemDecifrada + "\n");

						textoRecebido.append(" msg cifrada: "
								+ Criptografia.montaPalavra(info.getMensagem())
								+ "\n");
						textoRecebido
								.append(" chave sim�trica recebida decifrada: "
										+ chvSimetricaDecifrada + "\n\n");

					}// fim if

				}// fim while

				// trata erro
			} catch (ClassNotFoundException | IOException e) {

				textoRecebido.append("Erro " + nome + " " + e.getMessage());

				e.printStackTrace();

			}// fim catch

		}// fim run()

	}// fim class EscutaServidor

	// escuta bot�o - envia informa��es
	private class MonitoraBotao implements ActionListener {

		@SuppressWarnings("static-access")
		@Override
		public void actionPerformed(ActionEvent botao) {

			// click no bot�o "Enviar mensagem"
			if (botao.getSource() == botaoEnviarMensagem) {
				try {

					// monta criptografia

					// 1� gera a chave sim�trica
					byte[] chaveSimetrica = Criptografia.geraChaveSimetrica();
					txtMinhaChaveSimetrica.setText(Criptografia
							.montaPalavra(chaveSimetrica));

					// 2� cifrar e retorna a mensagem e a chave sim�trica
					// cifrada
					byte[][] cifrado = Criptografia.cifra(chvPublicaDoMeuAmigo,
							textoParaEnviar.getText(), chaveSimetrica);

					infoDestinoOuRecibo = new InfoDestinoOuRecibo();
					infoDestinoOuRecibo.setNome(nome);
					infoDestinoOuRecibo.setMensagem(cifrado[0]);
					infoDestinoOuRecibo.setChaveSimetrica(cifrado[1]);
					infoDestinoOuRecibo.setEnderecoIP(txtIPamigo.getText());

					// prepara o objeto para enviar
					escritor.writeObject(infoDestinoOuRecibo);
					// envia este informa��o para o servidor
					escritor.flush();

					// mostra para o cliente
					textoRecebido.append(nome + " disse:"
							+ textoParaEnviar.getText() + "\n\n");

					// limpa texto
					textoParaEnviar.setText("");
					// foco no campo
					textoParaEnviar.requestFocus();

					// trata erro
				} catch (IOException e) {

					textoRecebido.append("Erro " + nome
							+ " N�o pode enviar sua mensagem para seu amigo!"
							+ "\n" + e.getMessage());
					e.printStackTrace();

				}// fim catch e try

			}// fim if

			// click no bot�o "Conecta amigo"
			if (botao.getSource() == botaoConectaAmigo) {

				try {
					// monta lista com ips para envio
					List<String> listaInfoSolicitacao = new ArrayList<>();

					// monta lista de ips o (meu) e do (meu amigo)
					listaInfoSolicitacao.add(socket.getLocalAddress()
							.getLocalHost().getHostAddress());
					listaInfoSolicitacao.add(txtIPamigo.getText());

					escritor.writeObject(listaInfoSolicitacao);
					escritor.flush(); // enviar informa��es

					// trata erro
				} catch (IOException e) {
					textoRecebido.append("Erro " + nome
							+ " ao conectar amigo - " + e.getMessage());
					e.printStackTrace();
				}// fim catch e try

			}

			// click no bot�o "Conecta Servidor"
			if (botao.getSource() == botaoConectaServidor) {

				configuraRede();

			}// fim if

		}// fim actionPerformed()

	}// fim class MonitoraBotao

	// configura a rede - estabelece conex�o com o servidor
	@SuppressWarnings("static-access")
	private void configuraRede() {
		try {

			// crio o socket do cliente com referencia a porta do servidor
			socket = new Socket(txtIPservidor.getText(), 5100);
			escritor = new ObjectOutputStream(socket.getOutputStream());
			// prepara informa��o
			escritor.writeObject(chavePublica);
			escritor.flush(); // envia informa��o
			leitor = new ObjectInputStream(socket.getInputStream());
			// mensagem para do meu ip de m�quina
			textoRecebido.append("Conectado ao servidor... seu ip: "
					+ socket.getLocalAddress().getLocalHost().getHostAddress()
					+ " na porta: " + socket.getPort() + "\n\n");
			// escutando o servidor
			new Thread(new EscutaServidor()).start();

			// trata erro
		} catch (Exception ex) {
			textoRecebido.append("Erro ao se conectar com o servidor:" + "\n"
					+ ex.getMessage() + " causa " + ex.getCause());
		}// fim catch e try

	}// fim configuraRede()

	// inicializa a criptografia
	private void inicializaCriptografia() {
		// 1� gera as chaves uma p�blica e uma privada
		List<Object> listaDeChaves = Criptografia.geraParChaves();

		// 2� atribuir as chaves
		chavePublica = (PublicKey) listaDeChaves.get(0);
		chavePrivada = (PrivateKey) listaDeChaves.get(1);

		// mostra informa��es
		txtMinhaChavePublica.setText(chavePublica.toString());
		txtMinhaChavePrivada.setText(chavePrivada.toString());

	}// fim inicializaCriptografia()

	// m�todo principal
	public static void main(String[] args) {

		String opcao = JOptionPane.showInputDialog("Digite seu nome:");
		
		if (opcao != null) {
			new ChatCliente(opcao);
		}
		

	}

}// fim class ChatCliente
