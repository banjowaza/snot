package com.intuit.secfraud.shared.snot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SnotTemplatesTest {


    private SnotTemplates templates;

    @BeforeEach
    public void init() {
        templates = new SnotTemplates();
    }
    
    @ParameterizedTest
    @EnumSource(value = Tissue.class, names = {"GENERAL", "BLOW"})
    public void getTemplatesTest(Tissue tissue) {
        final String template = templates.getTemplate(tissue);
        assertNotNull(template);
    }
}
