package com.github.banjowaza.snot;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage Tissue resources.  This class loads the associated json template resource for each defined Tissue
 * 
 * @see Tissue 
 *
 */
@Component
public class SnotTemplates {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnotTemplates.class);

    private final Map<Tissue, String> templates;

    public SnotTemplates() {

        templates = new HashMap<>();

        Arrays.asList(Tissue.values()).forEach(tissueType -> {
            try {
                final Resource resource = new ClassPathResource("slack/" + tissueType.getTemplate(), this.getClass().getClassLoader());
                templates.put(tissueType, IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8));
            } catch (Exception e) {
                // Catch all for now. Don't want the app to not startup if it cannot load a template.
                LOGGER.error("Unable to load template " + tissueType.getTemplate());
            }
        });
    }

    public String getTemplate(Tissue tissue) {
        return templates.get(tissue);
    }
}
