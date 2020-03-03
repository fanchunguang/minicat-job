package server;

public abstract class HttpServlet implements Servlet {

    public abstract void doGet(Request request,Response response);

    public abstract void doPost(Request request,Response response);

    @Override
    public void service(Request request, Response response) throws Exception {
        if("doPost".equalsIgnoreCase(request.getMethod())){
            doPost(request,response);
        }else{
            doGet(request,response);
        }
    }
}
