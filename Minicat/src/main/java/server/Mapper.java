package server;

import java.sql.Wrapper;
import java.util.List;

/**
 * 处理请求 根据请求地址找到对应的Servlet
 */
public class Mapper {

    private MapperHost mapperHost = new MapperHost();

    public MapperHost getMapperHost() {
        return mapperHost;
    }

    public void setMapperHost(MapperHost mapperHost) {
        this.mapperHost = mapperHost;
    }

    protected static final class MapperHost{
        private String host;
        private List<MapperContent> contentList;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public List<MapperContent> getContentList() {
            return contentList;
        }

        public void setContentList(List<MapperContent> contentList) {
            this.contentList = contentList;
        }
    }

    protected static final class MapperContent{
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private MapperWrapper[] mapperWrappers;

        public MapperWrapper[] getMapperWrappers() {
            return mapperWrappers;
        }

        public void setMapperWrappers(MapperWrapper[] mapperWrappers) {
            this.mapperWrappers = mapperWrappers;
        }
    }

    protected static final class MapperWrapper{
        private String name;
        private Object object;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    public Object getWrapper(String url){
        System.out.println("=============> " + url);
        List<MapperContent> contentList = mapperHost.getContentList();
        for (MapperContent mapperContent : contentList) {
            if(mapperContent.getContent().equals(url.substring(1))){
                MapperWrapper[] mapperWrappers = mapperContent.getMapperWrappers();

                return mapperWrappers[0].getObject();
            }
        }
        return null;
    }
}
