package application.secret.wallet.service;

import application.secret.wallet.model.InvokeRequest;

import javax.servlet.http.HttpServletRequest;

public interface FabricService {
    String evaluateTransaction (String channelName, String chaincodeName, String chaincodeMethod, String... params) throws Exception;
    String submitTransaction (InvokeRequest invokeRequest, HttpServletRequest request) throws Exception;
}
