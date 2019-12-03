package com.intuit.secfraud.shared.snot.sample;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.intuit.secfraud.shared.snot.aop.Snot;
import com.intuit.secfraud.shared.snot.aop.SnotGot;
import com.intuit.secfraud.shared.snot.aop.SnotNot;
import com.intuit.secfraud.shared.snot.aop.SnotType;

/**
 * Sample service that tests all variations of SNOT
 * behavior.
 * 
 * @author jingram1
 *
 */
@Service
public class SniffService {
    
    @Snot
    public String smellyButGood(final String in) {
        return "out";
    }
    
    @Snot
    public void voided(final String in, final String in2) {
    }
    
    @Snot 
    public String returnsNull() {
        return null;
    }
    
    @Snot(message = "Yea for primitive!")
    public boolean primitives(int val1, int val2) {
        return false;
    }
    
    @Snot
    public void uhoh() throws Exception {
        throw new Exception("Badness");
    }
    
    @Snot
    public void nullPassed(final Object obj) {
    }

    @Snot(message = "Can't smell, need to clear my nose", targets = { "oifp-dev-snot" })
    public void sniff(final String smell) {
        System.out.println(smell + ", doesn't smell good.");
    }
    
    @Snot(message = "Thar she blows!")
    public void blow() throws Exception {
        throw new Exception("whoops! blow");
    }
    
    @Snot(message = "Thar she won't blow?")
    public void blowNot() throws Exception {
        throw new Exception("whoops! blowNot");
    }
    
    @Snot(message = "Yea, I got a zero!")
    public void got(@SnotGot("0") final int value) {
    }
    
    @Snot(message = "Yea, I didn't get 100!")
    public void not(@SnotNot("100") final int value) {
    }
    
    @Snot(message="Nothing to see here")
    public void noFilters() {     
    }
    
    @Snot(message="What's that on the floor, one booger!")
    public @SnotGot("1") int gotResponse() {
        return 1;
    }
    
    @Snot(message="What's that on the floor, the booger is missing!")
    public @SnotGot("1") int notResponse() {
        return 0;
    }
    
    @Snot(message="Not mine, don't touch!")
    public void notGold(@Validated final int param) {
    }
    
    @Snot(message="I don't know where it went, but someone else cares?")
    public @Validated Object notMyProblem(@SnotGot("fresh air") final Object somethingElse) {
        return null;
    }
    
    @Snot(message="I hope I don't get a whiff of peaches")
    public @SnotNot("peaches") String peachesSmellGood() {
        return "peaches";
    }
    
    @Snot(message="I hope I get a whiff of beaches!")
    public @SnotNot("peaches") String beachesSmellGood() {
        return "beaches";
    }
    
    @Snot(message="I hope I get a whiff of beaches! property does not exist")
    public @SnotNot(value = "peaches", property="non-existant") String beachesSmellGoodWithProperty() {
        return "beaches";
    }
    
    @Snot(message="I hope I get a whiff of beaches! and another property")
    public @SnotNot(value = "peaches", property="snot.app-name") String beachesSmellGoodWithAnotherProperty() {
        return "beaches";
    }
    
    @Snot(message="I hope I get a whiff of beaches! and another property and no match")
    public @SnotNot(value = "peaches", property="snot.app-name") String beachesSmellGoodWithAnotherPropertyNoMatch() {
        return "NARC";
    }
    
    @Snot(message="We shouldn't smell anything underwater, what does water smell like anyway?")
    public @Validated int nothingSmellsUnderWater() {
        return 0;
    }
    
    @Snot(message = "Ohhhhhhhh yea isOfType")
    public void isOfType(@SnotType(value = { "java.lang.Throwable" }) final Exception ex) {
    }
    
    @Snot(message = "Ohhhhhhhh yea isNotOfType")
    public void isNotOfType(@SnotType(value = { "java.lang.String" }) final Exception ex) {
    }

}
