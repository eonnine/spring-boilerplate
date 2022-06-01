package com.lims.api.common;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class YamlFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        return new PropertiesPropertySource(getName(name, resource), loadYaml(resource));
    }

    private Properties loadYaml(EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    private String getName(String name, EncodedResource resource) {
        return Strings.isNotEmpty(name) ? name : resource.getResource().getFilename();
    }
}