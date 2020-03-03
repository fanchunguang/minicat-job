package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class StaticResourceUtil {

    /**
     * 获取静态资源的绝对路径
     * @param path
     * @return
     */
    public static String getAbsolutePath(String path){
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replace("\\\\","/")+path;
    }

    /**
     * 根据静态文件输入流，通过输出流输出
     */
    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {
        int count =0;
        while(count ==0){
            count=inputStream.available();
        }
        int resourceSize = count;
        //输出http请求头
        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());

        //读取内容输出
        long written =0;//已经读取内容长度
        int byteSize=1024; //计划每次缓冲的长度
        byte[] bytes = new byte[byteSize];

        while(written < resourceSize){
            if(written + byteSize > resourceSize){//说明剩余未读取大小不足1024
                byteSize = (int)(resourceSize - written); //剩余的内容长度
            }

            inputStream.read(bytes);
            outputStream.write(bytes);

            outputStream.flush();
            written+=byteSize;
        }
    }
}
