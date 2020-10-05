package application.secret.wallet.service.impl;

import application.secret.wallet.model.InvokeRequest;
import application.secret.wallet.service.FabricService;
import application.secret.wallet.util.KubernetesSecretsWallet;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;


@Service
public class FabricServiceImpl implements FabricService {
    final Logger log = LoggerFactory.getLogger(FabricServiceImpl.class);
    @Value("${blockchain.connection-profile}")
    private String connectionProfile;

    @Value("${blockchain.user}")
    private String blockchainUser;

    private Contract contract = null;

    @Autowired
    private KubernetesSecretsWallet kubernetesSecretsWallet;

    @Override
    public String evaluateTransaction(String channelName, String chaincodeName, String chaincodeMethod, String... params) throws Exception {
        String queryResult = null;

        log.info("started evaluateTransaction with params channel: {}, chaincodeName: {}, chaincodeMethod: {}, params: {}", channelName, chaincodeName, chaincodeMethod, Arrays.toString(params));


        // get respective contract from network
        Contract contract =null;
        contract = getContract(channelName, chaincodeName, connectionProfile);

        try {
            log.info("executing query in ledger...");
            queryResult = new String(contract.evaluateTransaction(chaincodeMethod, params));
            log.info("query executed successfully");
        } catch (Exception blkException){

        }

        log.info("query response: "+queryResult);
        return queryResult;
    }

    @Override
    public String submitTransaction(InvokeRequest invokeRequest, HttpServletRequest request) throws Exception {

        String invokeResult = null;
        log.info("started submitTransaction: \n Request : {}", invokeRequest);

        String channelName = invokeRequest.getChannelName();
        String chaincodeName = invokeRequest.getChaincodeName();
        String chaincodeMethod = invokeRequest.getChaincodeMethod();
        String[] data = invokeRequest.getData();

        // check for null or empty strings in params

        log.info("invoking ledger:"+data);
        Contract contract =null;
        contract = getContract(channelName, chaincodeName,  connectionProfile);

        try{
            log.info("invoking ledger:"+data);
            invokeResult = new String(contract.submitTransaction(chaincodeMethod, data));
            log.info("invoke successfull ");
        } catch(Exception blkException){

        }

        log.info("invoke response: "+invokeResult);
        return invokeResult;

    }

    private Contract getContract(String channelName, String chaincodeName, String connectionProfile) throws Exception {
        if (contract == null) {
            try {
                Wallet.Identity identity = getIdentity(blockchainUser);
                Wallet wallet = getInMemoryWallet();

                wallet.put(blockchainUser, identity);
                log.debug("connection path :" + "classpath:" + connectionProfile);
                Resource resource = new ClassPathResource(connectionProfile);
                InputStream inputStream = resource.getInputStream();
                Gateway.Builder builder = createBuilder();
                builder.identity(wallet, blockchainUser).networkConfig(inputStream).discovery(true);
                log.info("connecting to the gateway...");
                Gateway gateway = builder.connect();
                log.info("connection successfull");

                log.debug("channelName:" + channelName);
                Network network = gateway.getNetwork(channelName);
                contract = network.getContract(chaincodeName);

            } catch (Exception e) {
                log.error("Error creating contract:" + e.getMessage());
                contract = null;
                throw e;
            }
        }
        return contract;
    }

    private Wallet.Identity getIdentity(String user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Wallet.Identity identity = null;
        log.info("getting identities from wallet....");
        identity = kubernetesSecretsWallet.get(user);

        if(identity==null) {
            log.error("Could not get identity from wallet. User \""+user+"\" does not exist");
        }

        log.info("identity fetched successfully");
        return identity;
    }

    public Gateway.Builder createBuilder(){return Gateway.createBuilder();}
    public Wallet getInMemoryWallet(){ return Wallet.createInMemoryWallet();}

}
