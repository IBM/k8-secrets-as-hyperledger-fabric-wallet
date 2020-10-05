package application.secret.wallet.model;

public class InvokeRequest {
    private String channelName;
    private String chaincodeName;
    private String chaincodeMethod;
    private String[] data;

    public InvokeRequest(){}

    public InvokeRequest(String channelName, String chaincodeName, String chaincodeMethod,  String[] data) {
        this.channelName = channelName;
        this.chaincodeName = chaincodeName;
        this.chaincodeMethod = chaincodeMethod;
        this.data = data;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getChaincodeMethod() {
        return chaincodeMethod;
    }

    public void setChaincodeMethod(String chaincodeMethod) {
        this.chaincodeMethod = chaincodeMethod;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "InvokeRequest{" +
                "channelName='" + channelName + '\'' +
                ", chaincodeName='" + chaincodeName + '\'' +
                ", chaincodeMethod='" + chaincodeMethod + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

}
