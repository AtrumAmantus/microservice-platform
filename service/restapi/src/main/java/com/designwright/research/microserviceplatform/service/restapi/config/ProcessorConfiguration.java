package com.designwright.research.microserviceplatform.service.restapi.config;

import com.designwright.research.microserviceplatform.service.restapi.annotation.PostProcessor;
import com.designwright.research.microserviceplatform.service.restapi.annotation.PreProcessor;
import com.designwright.research.microserviceplatform.service.restapi.processing.DefaultProcessor;
import com.designwright.research.microserviceplatform.service.restapi.processing.Processor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ProcessorConfiguration {

    private final ApplicationContext context;

    private final Map<String, Processor> postProcessors = new HashMap<>();
    private final Map<String, Processor> preProcessors = new HashMap<>();

    @PostConstruct
    public void init() {
        buildProcessorMaps();
    }

    private void buildProcessorMaps() {
        Map<String, Object> postProcessorBeans = context.getBeansWithAnnotation(PostProcessor.class);
        Map<String, Object> preProcessorBeans = context.getBeansWithAnnotation(PreProcessor.class);

        for (Map.Entry<String, Object> bean : postProcessorBeans.entrySet()) {
            if (bean.getValue() instanceof Processor) {
                postProcessors.put(bean.getKey(), (Processor) bean.getValue());
            }
        }

        for (Map.Entry<String, Object> bean : preProcessorBeans.entrySet()) {
            if (bean.getValue() instanceof Processor) {
                preProcessors.put(bean.getKey(), (Processor) bean.getValue());
            }
        }
    }

    public Processor getPostProcessor(String namePrefix) {
        Processor processor;
        String processorName = namePrefix + "PostProcessor";

        if (postProcessors.containsKey(processorName)) {
            processor = postProcessors.get(processorName);
        } else {
            processor = new DefaultProcessor();
        }

        return processor;
    }

    public Processor getPreProcessor(String namePrefix) {
        Processor processor;
        String processorName = namePrefix + "PreProcessor";

        if (preProcessors.containsKey(processorName)) {
            processor = preProcessors.get(processorName);
        } else {
            processor = new DefaultProcessor();
        }

        return processor;
    }
}
