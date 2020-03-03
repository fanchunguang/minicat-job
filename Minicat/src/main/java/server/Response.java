package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 封装Response对象，需要依赖于OutputStrean
 *
 * 该对象需要提供核心方法，输出html
 */
public class Response {

    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    //输出指定字符串
    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }
    /**
     * 根据url获取静态资源的绝对路径，读取静态资源文件，最终通过输出流封装
     * @param path
     */
    public void outputHtml(String path) throws IOException {
        //获取静态资源绝对路径
        String absoluteResourcePath=StaticResourceUtil.getAbsolutePath(path);

        //输入静态资源文件
        File file = new File(absoluteResourcePath);
        if(file.exists() && file.isFile()){
            //输出静态资源
            StaticResourceUtil.outputStaticResource(new FileInputStream(file),outputStream);
        }else{
            //输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }
}
