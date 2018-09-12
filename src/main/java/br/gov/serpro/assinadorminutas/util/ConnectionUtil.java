package br.gov.serpro.assinadorminutas.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class ConnectionUtil {

	public static int enviaAssinatura(String urlString, String token, byte[] assinatura, String sessionID) throws IOException {
    	String assinaturaBase64 = new String(Base64.getEncoder().encode(assinatura));

		URL myURL = new URL(urlString);
		HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();

		myURLConnection.setUseCaches(false);
		myURLConnection.setDoOutput(true);
		myURLConnection.setRequestMethod("POST");

		myURLConnection.setRequestProperty ("Authorization", token);
		myURLConnection.setRequestProperty("Cookie","JSESSIONID=" + sessionID);
		
		StringBuilder parametro = new StringBuilder();
		parametro.append(URLEncoder.encode("assinaturaBase64", "UTF-8"));
		parametro.append("=");
		parametro.append(URLEncoder.encode(assinaturaBase64, "UTF-8"));
		
		OutputStream saidEndPoint = myURLConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(saidEndPoint, "UTF-8"));
		writer.write(parametro.toString());
		writer.flush();
		writer.close();
		saidEndPoint.close();

		int responseCode = myURLConnection.getResponseCode();
        myURLConnection.disconnect();
        return responseCode;
	}
}
