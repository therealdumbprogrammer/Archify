package com.archify.generator.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class TemplateEngine {
    private static final String TEMPLATE_BASE_PATH = "/templates/spring";

    private final Configuration configuration;

    public TemplateEngine() {
        this.configuration = new Configuration(Configuration.VERSION_2_3_33);
        this.configuration.setDefaultEncoding("UTF-8");
        this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.configuration.setClassForTemplateLoading(TemplateEngine.class, TEMPLATE_BASE_PATH);
    }

    public String render(String templateName, Map<String, Object> model) {
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            return stringWriter.toString();
        } catch (IOException | TemplateException exception) {
            throw new IllegalStateException("Failed to render template: " + templateName, exception);
        }
    }
}
