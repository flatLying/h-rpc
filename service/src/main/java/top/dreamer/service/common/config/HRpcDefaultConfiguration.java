package top.dreamer.service.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import top.dreamer.service.module.balancer.HBalancer;
import top.dreamer.service.module.balancer.impl.ConsistentHashBalancer;
import top.dreamer.service.module.balancer.impl.MinimumResponseTimeBalancer;
import top.dreamer.service.module.balancer.impl.RoundRobinBalancer;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HSerializeType;

import java.io.InputStream;
import java.io.IOException;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-14 10:42
 * @description: 默认配置类
 */
@Setter
@Getter
@Slf4j
public class HRpcDefaultConfiguration {

    private String host;
    private int port;
    private String registry;
    private HSerializeType serializer;
    private HCompressType compressor;
    private HBalancer balancer;

    public HRpcDefaultConfiguration() {
        loadConfig("hrpc.xml");
    }

    public void loadConfig(String fileName) {
        try (InputStream xmlFile = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (xmlFile == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(true); // 启用 DTD 验证

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // 自定义 EntityResolver，允许从 classpath 加载 DTD 文件
            dBuilder.setEntityResolver((publicId, systemId) -> {
                if (systemId.contains("hrpc-config.dtd")) {
                    InputStream dtdStream = getClass().getClassLoader().getResourceAsStream("hrpc-config.dtd");
                    if (dtdStream != null) {
                        return new InputSource(dtdStream);
                    } else {
                        throw new IllegalArgumentException("DTD file not found in classpath");
                    }
                }
                return null;
            });

            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // 读取 server 配置
            Element serverElement = (Element) doc.getElementsByTagName("server").item(0);
            this.host = serverElement.getElementsByTagName("host").item(0).getTextContent();
            this.port = Integer.parseInt(serverElement.getElementsByTagName("port").item(0).getTextContent());

            // 读取 registry 配置
            this.registry = doc.getElementsByTagName("registry").item(0).getTextContent();

            // 读取 pipeline 配置
            Element pipelineElement = (Element) doc.getElementsByTagName("pipeline").item(0);

            switch (pipelineElement.getElementsByTagName("serializer").item(0).getTextContent()){
                case "JAVA":
                    this.serializer = HSerializeType.JAVA;
                    break;
                default:
                    this.serializer = HSerializeType.FAST_JSON;
            }
            switch (pipelineElement.getElementsByTagName("compressor").item(0).getTextContent()) {
                case "GZIP":
                    this.compressor = HCompressType.GZIP;
                    break;
                default:
                    this.compressor = HCompressType.GZIP;
            }
            switch (pipelineElement.getElementsByTagName("balancer").item(0).getTextContent()) {
                case "RoundRobinBalancer":
                    this.balancer = new RoundRobinBalancer(null);
                    break;
                case "ConsistentHashBalancer":
                    this.balancer = new ConsistentHashBalancer(null);
                    break;
                default:
                    this.balancer = new MinimumResponseTimeBalancer(null);
            }
        } catch (Exception e) {
            log.error("HRpc配置信息XML出错: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        HRpcDefaultConfiguration config = new HRpcDefaultConfiguration();
        System.out.println("Host: " + config.getHost());
        System.out.println("Port: " + config.getPort());
        System.out.println("Registry: " + config.getRegistry());
    }
}
