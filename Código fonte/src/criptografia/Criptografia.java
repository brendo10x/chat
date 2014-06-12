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

 Classe serve para realizar opera��es de seguran�a da informa��o
 *GERA��O DE PAR DE CHAVES, P�BLICA E PRIVADA - RSA - 1024
 *GERA��O DA CHAVE SIM�TRICA - AES - 128

 */

public class Criptografia {

	// me retorna uma lista de chaves uma p�blica e um privada
	public static List<Object> geraParChaves() {

		List<Object> listaObjeto = null;// lista
		final int RSAKEYSIZE = 1024;// tamanho da chave RSA

		try {

			// 1� gerador de par de chaves do tipo RSA
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(new RSAKeyGenParameterSpec(RSAKEYSIZE,
					RSAKeyGenParameterSpec.F4));

			// 2� gera o par de chaves
			KeyPair kpr = kpg.generateKeyPair();

			// 3� atribui as chaves
			PrivateKey priv = kpr.getPrivate();
			PublicKey pub = kpr.getPublic();

			// 4� crio lista de chaves
			listaObjeto = new ArrayList<>();

			// 5� add lista de chaves
			listaObjeto.add(pub);
			listaObjeto.add(priv);

			// trata erro
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}// fim catch e try

		return listaObjeto;// retorna lista

	}// fim geraParChaves()

	// auxilia na gera��o de chave sim�trica normal
	public static byte[] geraChaveSimetrica() {

		byte[] chave = null;// chave sim�trica

		try {
			// 1� Gerando uma chave sim�trica de 128 bits
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			SecretKey sk = kg.generateKey();
			chave = sk.getEncoded();

			// trata erro
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}// fim catch e try

		return chave;// retorna chave sim�trica

	}// fim geraChaveSimetrica()

	// me retorna um texto cifrado e a chave sim�trica cifrada
	public static byte[][] cifra(PublicKey pub, String textoClaro,
			byte[] ChaveSimetrica) {

		byte[] textoCifrado = null;// texto cifrado
		byte[] chaveCifrada = null;// chave cifrada

		try {
			// 1� recupera chave sim�trica
			byte[] chave = ChaveSimetrica;

			// 2� Cifrando o texto com a chave sim�trica
			Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
			aescf.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(chave, "AES"),
					ivspec);
			textoCifrado = aescf.doFinal(textoClaro.getBytes("ISO-8859-1"));

			// 3� Cifrando a chave sim�trica com a chave p�blica
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

		// retorna o texto cifrado e chave sim�trica cifrada
		return new byte[][] { textoCifrado, chaveCifrada };

	}// fim cifra()

	// me retorna uma lista com a msg e a chv sim�trica cifradas
	public static List<String> decifra(PrivateKey pvk, byte[] textoCifrado,
			byte[] chaveCifrada) {

		// crio lista
		List<String> listaDecifrada = new ArrayList<>();

		try {

			// 1� Decifrando a chave sim�trica com a chave privada
			Cipher rsacf = Cipher.getInstance("RSA");
			rsacf.init(Cipher.DECRYPT_MODE, pvk);
			byte[] chaveDecifrada = rsacf.doFinal(chaveCifrada);

			// 2� Decifrando o texto com a chave sim�trica decifrada
			Cipher aescf = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(new byte[16]);
			aescf.init(Cipher.DECRYPT_MODE, new SecretKeySpec(chaveDecifrada,
					"AES"), ivspec);
			byte[] textoDecifrado = aescf.doFinal(textoCifrado);

			// 3� defino charset para montagem do texto decifrado
			Charset iso88591charset = Charset.forName("ISO-8859-1");
			String textoDec = new String(textoDecifrado, iso88591charset);
			String chvDec = new String(chaveDecifrada, iso88591charset);

			// 4� add a lista
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

		// 1� defino charset
		Charset iso88591charset = Charset.forName("ISO-8859-1");
		// 2� montagem da palavra
		String palavra = new String(letras, iso88591charset);

		return palavra;// retorna palavra montada

	}// fim montaPalavra()

	// exemplo de segu�ncia de uso em 7 passos
	public static void main(String[] args) {

		// 1� gerar as chaves uma publica e uma privada
		List<Object> listaDeChaves = Criptografia.geraParChaves();

		// 2� atribuir as chaves
		PublicKey pub = (PublicKey) listaDeChaves.get(0);
		PrivateKey priv = (PrivateKey) listaDeChaves.get(1);

		// 3� atribuir texto para a cifrar
		String textoClaro = "Brendo � o c�o mermo ��o";

		// 4� gera chave sim�trica
		byte[] ChaveSimetrica = Criptografia.geraChaveSimetrica();

		// 5� cifrar e retorna a mensagem e a chave sim�trica cifrada
		byte[][] cifrado = Criptografia.cifra(pub, textoClaro, ChaveSimetrica);

		// mostra Chave sim�trica normal
		System.out.println("Chave sim�trica normal"
				+ Criptografia.montaPalavra(ChaveSimetrica));

		// mostra mensagem cifrada
		System.out
				.println("mensagem: " + Criptografia.montaPalavra(cifrado[0]));

		// mostra chave sim�trica cifrada
		System.out.println("chave sim�trica: "
				+ Criptografia.montaPalavra(cifrado[1]));

		// 6� decifrar
		List<String> listaDecifrado = Criptografia.decifra(priv, cifrado[0],
				cifrado[1]);

		// 7� mostrar informa��es
		// mensagem decifrada
		System.out.println("Texto decifrado: " + listaDecifrado.get(0));
		// chave sim�trica decifrada
		System.out.println("Chave sim�trica decifrado: "
				+ listaDecifrado.get(1));

	}

}
