package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装为Request(根据inputstream封装)
 */
public class Request {

    private String method;// 请求方式
    private String url; //例如/,/index.html

    private InputStream inputStream; //输入流，其他属性从输入流解析

    public Request() {
    }

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        int count =0;
        while(count ==0){
            count=inputStream.available();
        }

        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        System.out.println("=========>请求信息: " + new String(bytes));
        String inputStr = new String(bytes);
        //获取第一行请求头信息
        String firstLine = inputStr.split("\\n")[0];

        String[] strs = firstLine.split(" ");

        this.method= strs[0];
        this.url =strs[1];
        System.out.println("==========> method: " + method);
        System.out.println("==========> url: " + url);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
