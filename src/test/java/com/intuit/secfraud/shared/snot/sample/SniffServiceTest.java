package com.intuit.secfraud.shared.snot.sample;

import static org.junit.jupiter.api.Assertions.fail;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SniffServiceTest {

    @Resource
    private SniffService service;

    @Test
    void test() {
        service.sniff("Rotten fruit");
    }
    
    @Test
    void blowTest() {
        try {
            service.blow();
            fail("Should throw exception");
        } catch (Exception e) {
            
        }
    }
    
    @Test
    void blowNotTest() {
        try {
            service.blowNot();
            fail("Should throw exception");
        } catch (Exception e) {
            
        }
    }
    
    @Test
    void gotZeroTest() {
        service.got(0);
    }
    
    @Test
    void gotOneTest() {
        service.got(1);
    }
    
    @Test
    void gotHundredTest() {
        service.not(100);
    }
    
    @Test
    void notGotHundredTest() {
        service.not(200);
    }
    
    @Test
    void notButDidGetHundredTest() {
        service.not(100);
    }
    
    @Test
    void noFiltersTest() {
        service.noFilters();
    }
    
    @Test
    void gotResponseTest() {
        service.gotResponse();
    }
    
    @Test
    void notResponseTest() {
        service.notResponse();
    }
    
    @Test
    void notGoldTest() {
        service.notGold(1);
    }
    
    @Test
    void notMyProblemTest() {
        service.notMyProblem(null);
    }
    
    @Test
    void peachesTest() {
        service.peachesSmellGood();
    }
    
    @Test
    void smellUnderWaterTest() {
        service.nothingSmellsUnderWater();
    }

    @Test
    void smellBeachesTest() {
        service.beachesSmellGood();
    }
    
    @Test
    void smellBeachesTest_NoPropertyExists() {
        service.beachesSmellGoodWithProperty();
    }
    
    @Test
    void smellBeachesTest_PropertyExists() {
        service.beachesSmellGoodWithAnotherProperty();
    }
    
    @Test
    void smellBeachesTest_PropertyExistsNoMatch() {
        service.beachesSmellGoodWithAnotherPropertyNoMatch();
    }
    
    @Test
    void smellyButGoodTest() {
        service.smellyButGood("in");
    }
    
    @Test
    void voidTest() {
        service.voided("in", "innnnnnn");
    }
    
    @Test
    void returnsNullTest() {
        service.returnsNull();
    }
    
    @Test
    void primitivesTest() {
        service.primitives(1, 2);
    }
    
    @Test
    void uhOhTest() {
        try {
            service.uhoh();
            fail("Should not get here");
        } catch (Exception e) {
        }
    }
    
    @Test
    void nullPassedTest() {
        service.nullPassed(null);
    }
    
    @Test
    void testTypeThrowable() {
        service.isOfType(new Exception("hmmmm"));
    }
    
    @Test
    void testNotTypeThrowable() {
        service.isNotOfType(new Exception("hmmmm"));
    }

}
