package server;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread {

    private Socket socket;
    private Map<String,HttpServlet> servletMap;
    private Map<String,Mapper> mapperMap;

    /*public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }*/

    public RequestProcessor(Socket socket, Map<String,Mapper> mapperMap){
        this.socket = socket;
        this.mapperMap = mapperMap;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 静态资源处理
            if(mapperMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            }else{
                // 动态资源servlet请求
                /*HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request,response);*/
                Mapper mapper = mapperMap.get(request.getUrl());
                HttpServlet servlet =(HttpServlet) mapper.getWrapper(request.getUrl());
                if(servlet!=null){
                    servlet.service(request,response);
                }
            }

            socket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
