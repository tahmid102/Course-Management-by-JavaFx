package files;

import java.io.Serializable;

public class Request implements Serializable {
    public enum RequestType{GET_ALL_COORDINATED_DATA,WRITE_TO_FILE}
    private RequestType requestType;
    String line;
    String path;

    public Request(RequestType requestType, String line, String path) {
        this.requestType = requestType;
        this.line = line;
        this.path = path;
    }

    public Request(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getLine() {
        return line;
    }

    public String getPath() {
        return path;
    }

    public RequestType getRequestType() {
        return requestType;
    }


}
