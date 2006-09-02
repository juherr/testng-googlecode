package test;

import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.Configuration;
import org.testng.annotations.Test;
import org.testng.junit.JUnitClassFinder;

import test.junit.SetNameTest;
import test.junit.Suite1;
import test.junit.TestAa;
import test.junit.TestAb;
import test.junit.TestAc;
import test.junit.TestAd;
import test.junit.TestAe;
import test.junit.TestAf;
import test.sample.JUnitSample1;
import test.sample.JUnitSample2;


/**
 * This class
 *
 * @author Cedric Beust, May 5, 2004
 * 
 */
public class JUnitTest1 extends BaseTest {
  @Configuration(beforeTestMethod = true, dependsOnGroups = { "initTest"} )
  public void initJUnitFlag() {
    getTest().setJUnit(true);
  }
  
  @Test
  public void methodsThatStartWithTest() {
    addClass("test.sample.JUnitSample1");
    assert getTest().isJUnit();
    
    run();
    String[] passed = {
        JUnitSample1.EXPECTED1, JUnitSample1.EXPECTED2
    };
    String[] failed = {
    };
    
    verifyTests("Passed", passed, getPassedTests());
    verifyTests("Failed", failed, getFailedTests());  
  }
  
  @Test
  public void methodsWithSetup() {
    addClass("test.sample.JUnitSample2");
    run();
    String[] passed = {
      "testSample2ThatSetUpWasRun",
    };
    String[] failed = {
    };
    
    verifyTests("Passed", passed, getPassedTests());
    verifyTests("Failed", failed, getFailedTests());  
  }
  
  @Test
  public void testSuite() {
    addClass("test.sample.AllJUnitTests");
    run();
    String[] passed = {
        JUnitSample1.EXPECTED1, JUnitSample2.EXPECTED
    };
    String[] failed = {
    };
    
    verifyTests("Passed", passed, getPassedTests());
    verifyTests("Failed", failed, getFailedTests());  
  }  
  
  @Test
  public void testNewInstance() {
    addClass("test.sample.JUnitSample3");
    run();
    String[] passed = {
      "test1", "test2"
    };
    String[] failed = {
    };
    
    verifyTests("Passed", passed, getPassedTests());
    verifyTests("Failed", failed, getFailedTests());  
  }  
  
  @Test
  public void suitesOfSuites() {
    Class[] expected = {
      TestAa.class, TestAb.class,
      TestAc.class, TestAd.class,
      TestAe.class, TestAf.class,
    };
    Collection<Class> result = JUnitClassFinder.invokeSuite(
        new Class[] { test.junit.MainSuite.class});

    for (Class c : expected) {
      boolean success = result.remove(c);
      Assert.assertTrue(success, "Expected to find class " + c);
    }
    Assert.assertEquals(result.size(), 0, "Returned result contains extra classes");
  }
  
  @Test
  public void suitesOfSuites2() {
    Class[] expected = { 
      TestAa.class, TestAb.class 
    };
    Collection<Class> result = JUnitClassFinder
        .invokeSuite(new Class[] { Suite1.class });

    for (Class c : expected) {
      boolean success = result.remove(c);
      Assert.assertTrue(success, "Expected to find class " + c);
    }
    Assert.assertEquals(result.size(), 0,
        "Returned result contains extra classes");
  }
  
  @Test
  public void setUpFailingShouldCauseMethodsToBeSkipped() {
    addClass("test.junit.SetUpExceptionSampleTest");
    run();
    String[] passed = {
    };
    String[] failed = {
        "testM1"
    };
    String[] skipped = {
    };
    verifyTests("Passed", passed, getPassedTests());
    verifyTests("Skipped", skipped, getSkippedTests());    
    verifyTests("Failed", failed, getFailedTests());    
  }

  @Test
  public void setNameShouldBeInvoked() {
    addClass("test.junit.SetNameTest");
    SetNameTest.m_ctorCount = 0;
    run();
    String[] passed = {
      "testFoo", "testBar",
    };
    String[] failed = {
    };
    String[] skipped = {
    };
    verifyTests("Passed", passed, getPassedTests());
    verifyTests("Skipped", skipped, getSkippedTests());    
    verifyTests("Failed", failed, getFailedTests());    
    
    Assert.assertEquals(SetNameTest.m_ctorCount, 2,
        "Expected 2 instances to be created, found " + SetNameTest.m_ctorCount);
  }

  public static void ppp(String s) {
    System.out.println("[JUnitTest1] " + s);
  }


}
