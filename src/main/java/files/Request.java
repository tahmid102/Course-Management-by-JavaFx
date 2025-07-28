package files;

import java.io.Serializable;

public class Request implements Serializable {
    public enum RequestType{GET_ALL_COORDINATED_DATA}
    private RequestType requestType;;

    public Request(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }


}
