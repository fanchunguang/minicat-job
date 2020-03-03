package server;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Minicat的主类
 */
public class Bootstrap {

    /**定义socket监听的端口号**/
    private int port =8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * minicat启动需要初始化展开的一些操作
     */
    public void start() throws IOException {

        // 加载解析相关的配置，web.xml
        //loadServlet();
        loadMapperServlet();//加载server.xml

        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize =50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        //完成minicat 1.0版本（浏览器请求http://localhost:8080，
        // 返回一个固定的字符串到页面"Hello Minicat!"）
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("=============>minicat start on port:"+port);

        /*while(true){
            Socket socket = serverSocket.accept();
            // 接收到请求
            OutputStream outputStream = socket.getOutputStream();
            String responseText = HttpProtocolUtil.getHttpHeader200("Hello Minicat ".getBytes().length)+"Hello Minicat";
            outputStream.write(responseText.getBytes());
            socket.close();
        }*/

        /**
         * minicat2.0版本
         * 需求：封装Request和Response对象，返回html静态资源文件
         */
//        while (true){
//            Socket socket = serverSocket.accept();
//            InputStream inputStream = socket.getInputStream();
//            //从输入流中获取请求信息
//
//            socket.close();
//        }

        /**
         * 完成Minicat 3.0版本
         * 需求：可以请求动态资源（Servlet）
         */
        /*while(true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 静态资源处理
            if(servletMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            }else{
                // 动态资源servlet请求
                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request,response);
            }

            socket.close();

        }
*/

        /*
            多线程改造（不使用线程池）
         */
        /*while(true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,servletMap);
            requestProcessor.start();
        }*/



        System.out.println("=========>>>>>>使用线程池进行多线程改造");
        /*
            多线程改造（使用线程池）
         */
        /*while(true) {

            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,servletMap);
            //requestProcessor.start();
            threadPoolExecutor.execute(requestProcessor);
        }*/

        /**
         * Minicat4.0 模拟出webapps部署效果
         */


        while(true){
            Socket socket = serverSocket.accept();

            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMapper);

            threadPoolExecutor.execute(requestProcessor);
        }
    }

    private Map<String,HttpServlet> servletMap = new HashMap<String,HttpServlet>();
    //存储url,servlet映射关系
    private Map<String,Mapper> servletMapper = new HashMap<>();
    private static final String SERVERXML = "server.xml";
    private static String PORT = "";

    private void loadMapperServlet(){
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(SERVERXML);
        SAXReader saxReader=new SAXReader();
        try{
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("Service");
            //封装Mapper
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element =  selectNodes.get(i);
                // connector
                Element connector = (Element) element.selectSingleNode("Connector");
                PORT = connector.attribute("port").getValue();
                //
                Element engineElement = (Element) element.selectSingleNode("Engine");
                //找到HOST  localhost appBase=D:\workspace-yonyou\my-work\out\artifacts
                Element host =(Element)engineElement.selectSingleNode("Host");
                String name = host.attribute("name").getValue();
                String appBase = host.attribute("appBase").getValue();

                Mapper mapper = new Mapper();
                Mapper.MapperHost mapperHost = new Mapper.MapperHost();
                mapperHost.setHost(name);
                List<Mapper.MapperContent> mapperContentList = new ArrayList<>();

                // 根据appBase找到content
                File file = new File(appBase);
                for(File file1:file.listFiles()){
                    if(file1.isDirectory()){
                        Mapper.MapperContent mapperContent = new Mapper.MapperContent();
                        String path = file1.getPath();
                        mapperContent.setContent(path.substring(path.lastIndexOf("\\")+1,path.length()));

                        //封装wrapper 解析xml servlet位置 进行初始化
                        Mapper.MapperWrapper mapperWrapper = new Mapper.MapperWrapper();
                        String xmlPath="";

                        String url = path+"\\WEB-INF\\classes";
                        MyClassLoader classLoader = new MyClassLoader(url+"\\server\\LagouServlet.class");

                        Class<?> clazz = Class.forName("server.LagouServlet", true, classLoader);

                        /*URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + url)});
                        Class<?> lagouServlet = classLoader.loadClass(url+"\\server\\LagouServlet.class");*/
                        Object o = clazz.newInstance();
                        mapperWrapper.setObject(o);
                        mapperContent.setMapperWrappers(new Mapper.MapperWrapper[]{mapperWrapper});
                        mapperContentList.add(mapperContent);
                        mapper.setMapperHost(mapperHost);
                        servletMapper.put("/" +path.substring(path.lastIndexOf("\\")+1,path.length()),mapper);
                    }
                }
                mapperHost.setContentList(mapperContentList);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private void loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element =  selectNodes.get(i);
                // <servlet-name>lagou</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // <servlet-class>server.LagouServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();


                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /lagou
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());

            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Minicat 的程序启动入口
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
