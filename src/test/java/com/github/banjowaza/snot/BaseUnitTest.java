package com.github.banjowaza.snot;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ReflectionUtils;

public class BaseUnitTest {

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    protected void injectInto(Object target, String fieldName, String value) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        field.setAccessible(true);
        ReflectionUtils.setField(field, target, value);
    }
}
