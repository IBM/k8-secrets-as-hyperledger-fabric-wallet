package application.secret.wallet.util;

import org.hyperledger.fabric.gateway.Wallet.Identity;

import org.hyperledger.fabric.gateway.impl.WalletIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class KubernetesSecretsWallet {
    final Logger log = LoggerFactory.getLogger(KubernetesSecretsWallet.class);
    @Autowired
    private Environment env;


    public Identity get(String user) throws InvalidKeySpecException, NoSuchAlgorithmException {

        try {

            String msp = env.getProperty(user + "_MSP_ID").trim();
            String cert = env.getProperty(user + "_CERTIFICATE").trim();

            String decode = env.getProperty(user + "_PRIVATE_KEY").trim();

            if(msp==null ||cert ==null || decode ==null )
            {
                log.error("Kubernetes Secrets not found for user :"+user);
                return null;
            }

            decode = decode.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

            KeyFactory kf = KeyFactory.getInstance("EC");


            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(decode));

            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);

            return new WalletIdentity(msp, cert, privKey);
        }catch (Exception ex){
            log.error("Error getting identity:"+ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

}
