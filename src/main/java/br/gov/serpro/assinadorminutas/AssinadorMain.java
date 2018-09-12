package br.gov.serpro.assinadorminutas;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import br.gov.frameworkdemoiselle.certificate.signer.SignerException;
import br.gov.frameworkdemoiselle.certificate.ui.action.AbstractFrameExecute;
import br.gov.frameworkdemoiselle.certificate.ui.util.AuthorizationException;
import br.gov.frameworkdemoiselle.certificate.ui.util.ConectionException;
import br.gov.frameworkdemoiselle.certificate.ui.view.MainFrame;
import br.gov.serpro.assinadorminutas.util.ConnectionUtil;

public class AssinadorMain extends AbstractFrameExecute {

	private static final Logger LOGGER = Logger.getLogger(AssinadorMain.class.getName());

    String jnlpService = "http://localhost:4200";
    String jnlpToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJjcGZcIjoyNTc4MjMwNixcIm5vbWVDb21wbGV0b1wiOlwiQUxFWEFORFJFIFRPUlJFUyBERSBBUkFVSk9cIixcInVuaWRhZGVBZG1pbmlzdHJhdGl2YVwiOntcImNvZGlnb1wiOjEwMTAwLFwibm9tZVwiOlwiR0FCSU4gLSBHQUJJTkVURSBSRkJcIn0sXCJlbWFpbFwiOlwiYWxleGFuZHJlLnQuYXJhdWpvQHJlY2VpdGEuZmF6ZW5kYS5nb3YuYnJcIixcImNwZkNvbXBsZXRvXCI6XCIwMjU3ODIzMDY5MlwiLFwicGVyZmlzXCI6W1wiQ1RNTl9BRE1JTklTVFJBRE9SXCJdLFwidHJhbnNhY29lc1wiOltcIkNUTU5VMDAxXCJdLFwidG9rZW5cIjpudWxsLFwiYXR1YXJDb21vXCI6bnVsbH0iLCJleHAiOjE1MzY4NDIzODZ9.L1IID8uTnewASLtg423je6VR9gaeSSLkd5vFsURskxniG0-50qC1N1bTU9o7WYLn1cmf_RkeGsJiEQMLIKIIzA";
    String jnlpSessionID = "g8JJ0r0n-fWBnH59P3zpWL1W1T0sIN1dA7Or2fmj";

	public AssinadorMain() throws IOException {

    	//Propriedades do JNLP
        jnlpService = System.getProperty("jnlp.service");
        jnlpToken = System.getProperty("jnlp.token");
        jnlpSessionID = System.getProperty("jnlp.sessionID");

        LOGGER.log(Level.INFO, "jnlp.service.....: " + jnlpService);
        LOGGER.log(Level.INFO, "jnlp.token.....: " + jnlpToken);
        LOGGER.log(Level.INFO, "jnlp.sessionID.....: " + jnlpSessionID);

        if (jnlpService == null || jnlpService.isEmpty()) {
        	LOGGER.log(Level.SEVERE, "A variavel \\\"jnlp.service\\\" não está configurada.");
            JOptionPane.showMessageDialog(null, "A variavel \"jnlp.service\" não está configurada.", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        if (jnlpToken == null || jnlpToken.isEmpty()) {
        	LOGGER.log(Level.SEVERE, "A variavel \\\"jnlp.token\\\" não está configurada.");
            JOptionPane.showMessageDialog(null, "A variavel \"jnlp.token\" não está configurada.", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if (jnlpSessionID == null || jnlpSessionID.isEmpty()) {
        	LOGGER.log(Level.SEVERE, "A variavel \\\"jnlp.sessionID\\\" não está configurada.");
            JOptionPane.showMessageDialog(null, "A variavel \"jnlp.sessionID\" não está configurada.", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    @Override
    public void execute(KeyStore ks, String alias, MainFrame principal) {
    	int respostaServidor;
        try {
        	respostaServidor = ConnectionUtil.enviaAssinatura(this.jnlpService,
                                                              this.jnlpToken,
                                                              ((X509Certificate) ks.getCertificateChain(alias)[0]).getSignature(),
                                                              this.jnlpSessionID);
			if(respostaServidor == 200) {
				LOGGER.log(Level.INFO, "Assinatura enviada com sucesso.");
				JOptionPane.showMessageDialog(principal, "Sucesso: Assinatura enviada com sucesso.", "OK", JOptionPane.OK_OPTION);
				System.exit(0);
			}else {
				LOGGER.log(Level.SEVERE, "Código do erro http: " + respostaServidor);
				JOptionPane.showMessageDialog(principal, "Código do erro http: " + respostaServidor, "OK", JOptionPane.OK_OPTION);
				System.exit(0);
			}
        }catch(AuthorizationException ex){
        	LOGGER.log(Level.SEVERE, ex.getMessage());
        	ex.printStackTrace();
        	JOptionPane.showMessageDialog(principal, "Token Inválido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        	System.exit(0);
    	}catch(ConectionException ex){
    		LOGGER.log(Level.SEVERE, ex.getMessage());
    		ex.printStackTrace();
    		JOptionPane.showMessageDialog(principal, "Erro de Conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	} 
        catch (KeyStoreException ex) {
        	LOGGER.log(Level.SEVERE, ex.getMessage());
        	ex.printStackTrace();
            JOptionPane.showMessageDialog(principal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        catch(SignerException ex){
        	LOGGER.log(Level.SEVERE, ex.getMessage());
        	ex.printStackTrace();
            JOptionPane.showMessageDialog(principal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);        	
        } catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
        	ex.printStackTrace();
            JOptionPane.showMessageDialog(principal, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0); 
		}
    }

    @Override
    public void cancel(KeyStore ks, String alias, MainFrame principal) {
    	System.exit(0);
    }

    @Override
    public void close(MainFrame principal) {
    	System.exit(0);
    }
}
