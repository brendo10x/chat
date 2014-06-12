package criptografia;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/*

 Classe serve para realizar operações de segurança da informação
 *GERAÇÃO DE PAR DE CHAVES, PÚBLICA E PRIVADA - RSA - 1024
 *GERAÇÃO DA CHAVE SIMÉTRICA - AES - 128

 */

public class Criptografia {

	// me retorna uma lista de chaves uma pública e um privada
	public static List<Object> geraParChaves() {

		List<Object> listaObjeto = null;// lista
		final int RSAKEYSIZE = 1024;// tamanho da chave RSA

		try {

			// 1º gerador de par de chaves do tipo RSA
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(new RSAKeyGenParameterSpec(RSAKEYSIZE,
					RSAKeyGenParameterSpec.F4));

			// 2º gera o par de chaves
			KeyPair kpr = kpg.generateKeyPair();

			// 3º atribui as chaves
			PrivateKey priv = kpr.getPrivate();
			PublicKey pub = kpr.getPublic();

			// 4º crio lista de chaves
			listaObjeto = new ArrayList<>();

			// 5º add lista de chaves
			listaObjeto.add(pub);
			listaObjeto.add(priv);

			// trata erro
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}// fim catch e try

		return listaObjeto;// retorna lista

	}// fim geraParChaves()

	// auxilia na geração de chave simétrica normal
	public static byte[] geraChaveSimetrica() {

		byte[] chave = null;// chave simétrica

		try {
			// 1º Gerando uma chave simétrica de 128 bits
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			SecretKey sk = kg.generateKey();
			chave = sk.getEncoded();

			// trata erro
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}// fim catch e try

		return chave;// retorna chave simétrica

	}// fim geraChaveSimetrica()

	// me retorna um texto cifrado e a chave simétrica cifrada
	public static byte[][] cifra(PublicKey pub, String textoClaro,
			byte[] ChaveSimetrica) {

		byte[] textoCifrado = null;// texto cifrado
		byte[] chaveCifrada = null;// chave cifrada

		try {
			// 1º recupera chave simétrica
			byte[] chave = ChaveSimetrica;

			// 2º Cifrando o texto com a chave simétrica
			Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
			aescf.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(chave, "AES"),
					ivspec);
			textoCifrado = aescf.doFinal(textoClaro.getBytes("ISO-8859-1"));

			// 3º Cifrando a chave simétrica com a chave pública
			Cipher rsacf = Cipher.getInstance("RSA");
			rsacf.init(Cipher.ENCRYPT_MODE, pub);
			chaveCifrada = rsacf.doFinal(chave);

			// trata erro
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException
				| UnsupportedEncodingException e) {
			e.printStackTrace();
		}// fim catch e try

		// retorna o texto cifrado e chave simétrica cifrada
		return new byte[][] { textoCifrado, chaveCifrada };

	}// fim cifra()

	// me retorna uma lista com a msg e a chv simétrica cifradas
	public static List<String> decifra(PrivateKey pvk, byte[] textoCifrado,
			byte[] chaveCifrada) {

		// crio lista
		List<String> listaDecifrada = new ArrayList<>();

		try {

			// 1º Decifrando a chave simétrica com a chave privada
			Cipher rsacf = Cipher.getInstance("RSA");
			rsacf.init(Cipher.DECRYPT_MODE, pvk);
			byte[] chaveDecifrada = rsacf.doFinal(chaveCifrada);

			// 2º Decifrando o texto com a chave simétrica decifrada
			Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
			aescf.init(Cipher.DECRYPT_MODE, new SecretKeySpec(chaveDecifrada,
					"AES"), ivspec);
			byte[] textoDecifrado = aescf.doFinal(textoCifrado);

			// 3º defino charset para montagem do texto decifrado
			Charset iso88591charset = Charset.forName("ISO-8859-1");
			String textoDec = new String(textoDecifrado, iso88591charset);
			String chvDec = new String(chaveDecifrada, iso88591charset);

			// 4º add a lista
			listaDecifrada.add(textoDec);
			listaDecifrada.add(chvDec);

			// trata erro
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}// fim catch e try

		return listaDecifrada;// retorna lista

	}// fim decifra()

	// me retorna uma palavra montada
	public static String montaPalavra(byte[] letras) {

		// 1º defino charset
		Charset iso88591charset = Charset.forName("ISO-8859-1");
		// 2º montagem da palavra
		String palavra = new String(letras, iso88591charset);

		return palavra;// retorna palavra montada

	}// fim montaPalavra()

	// exemplo de seguência de uso em 7 passos
	public static void main(String[] args) {

		// 1º gerar as chaves uma publica e uma privada
		List<Object> listaDeChaves = Criptografia.geraParChaves();

		// 2º atribuir as chaves
		PublicKey pub = (PublicKey) listaDeChaves.get(0);
		PrivateKey priv = (PrivateKey) listaDeChaves.get(1);

		// 3º atribuir texto para a cifrar
		String textoClaro = "Brendo é o cão mermo ção";

		// 4º gera chave simétrica
		byte[] ChaveSimetrica = Criptografia.geraChaveSimetrica();

		// 5º cifrar e retorna a mensagem e a chave simétrica cifrada
		byte[][] cifrado = Criptografia.cifra(pub, textoClaro, ChaveSimetrica);

		// mostra Chave simétrica normal
		System.out.println("Chave simétrica normal"
				+ Criptografia.montaPalavra(ChaveSimetrica));

		// mostra mensagem cifrada
		System.out
				.println("mensagem: " + Criptografia.montaPalavra(cifrado[0]));

		// mostra chave simétrica cifrada
		System.out.println("chave simétrica: "
				+ Criptografia.montaPalavra(cifrado[1]));

		// 6º decifrar
		List<String> listaDecifrado = Criptografia.decifra(priv, cifrado[0],
				cifrado[1]);

		// 7º mostrar informações
		// mensagem decifrada
		System.out.println("Texto decifrado: " + listaDecifrado.get(0));
		// chave simétrica decifrada
		System.out.println("Chave simétrica decifrado: "
				+ listaDecifrado.get(1));

	}

}
