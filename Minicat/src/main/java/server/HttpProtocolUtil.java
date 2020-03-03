package server;

/**
 * http协议工具类，主要是提供响应头信息
 * 只提供200和404的情况
 */
public class HttpProtocolUtil {
    /**
     * 为响应码200提供响应头信息
     * @param contentLength
     * @return
     */
    public static String getHttpHeader200(long contentLength){
        return "Http/1.1 200 OK \n"+
                "Content-Type: text/html \n" +
                "Content-Length: " + contentLength +" \n"+
                "\r\n";
    }

    /**
     * 为响应码404提供响应头信息
     * @param
     * @return
     */
    public static String getHttpHeader404(){
        String str404 ="<h1>404 NOT Found</h1>";
        return "Http/1.1 404 NOT Found \n"+
                "Content-Type: text/html \n" +
                "Content-Length: " + str404.getBytes().length +" \n"+
                "\r\n" + str404;
    }
}
