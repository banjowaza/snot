package com.github.banjowaza.snot.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Sample application that can be referenced for other projects using
 * this library and is also used in our unit/integration tests test
 * all variations of SNOT.
 * 
 * @author jingram1
 *
 */
@SpringBootApplication(scanBasePackages = "com.github.banjowaza.snot")
public class SampleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
