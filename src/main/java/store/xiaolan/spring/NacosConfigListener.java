package store.xiaolan.spring;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@Component
public class NacosConfigListener {

    private Logger log = LoggerFactory.getLogger(NacosConfigListener.class);

    @Value("${nacos.server-addr}")
    private String serverAddr;
    @Value("${spring.application.name}")
    private String dataId;
    @Value("${spring.cloud.nacos.username}")
    private String username;
    @Value("${spring.cloud.nacos.password}")
    private String password;
    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;
    @Value("${com.ali.file.readOnly}")
    private String readOnlyFile;
    @Value("${com.ali.file.writeOnly}")
    private String writeOnlyFile;

    private String group = "DEFAULT_GROUP";
    private String yaml = ".yaml";
    private String mqtt = "com";
    private String ali = "ali";
    private String files= "file";
    private String readOnly = "readOnly";
    private String writeOnly = "writeOnly";

    public void listener() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.put(PropertyKeyConst.NAMESPACE, namespace);
            properties.put(PropertyKeyConst.USERNAME, username);
            properties.put(PropertyKeyConst.PASSWORD, password);
            ConfigService configService = NacosFactory.createConfigService(properties);

            String content = configService.getConfig(dataId + yaml, group, 5000);
            if (StringUtils.isEmpty(content)) {
                log.warn("目标配置内容为空,跳过监听");
                return;
            }
            log.info("初始配置内容读取: {}", content);
            log.info("开始注册修改监听: {}", dataId);
            configService.addListener(dataId + yaml, group, new Listener() {

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("{} 本次修改后的内容: {}", dataId, configInfo);
                    // 解析yaml文件内容，获取对应属性
                    Map<String, Object> config = new Yaml().load(configInfo);
                    Map<String, Object> mqttConfig = (Map<String, Object>) config.get(mqtt);
                    Map<String, Object> aliConfig = (Map<String, Object>) mqttConfig.get(ali);
                    Map<String, Object> filesConfig = (Map<String, Object>) aliConfig.get(files);
                    String readFiles = (String) filesConfig .get(readOnly);
                    String writeFiles = (String) filesConfig .get(writeOnly);
                    // 判断对应属性是否变更
                    if (!StringUtils.equals(readFiles, readOnlyFile) || !StringUtils.equals(writeFiles, writeOnlyFile)) {
                        // action 对应动作处理，TODO

                        // 缓存上次配置
                        readOnlyFile = readFiles;
                        writeFiles = writeOnlyFile;
                    }
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
            log.info("结束注册修改监听: {}", dataId);
        } catch (NacosException e) {
            log.error("Nacos配置相关异常:{}", e.getMessage());
            throw new RuntimeException("Nacos配置读取与修改监听", e);
        }
    }

}


