package org.testng.reporters;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.collections.Lists;
import org.testng.collections.Maps;
import org.testng.internal.Utils;
import org.testng.internal.annotations.Sets;
import org.testng.xml.XmlSuite;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class JUnitReportReporter implements IReporter {

  @Override
  public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
      String outputDirectory) {

    Map<Class<?>, Set<ITestResult>> results = Maps.newHashMap();
    for (ISuite suite : suites) {
      Map<String, ISuiteResult> suiteResults = suite.getResults();
      for (ISuiteResult sr : suiteResults.values()) {
        ITestContext tc = sr.getTestContext();
        addResults(tc.getPassedTests().getAllResults(), results);
        addResults(tc.getFailedTests().getAllResults(), results);
        addResults(tc.getSkippedTests().getAllResults(), results);
      }
    }

    XMLStringBuffer xsb = new XMLStringBuffer("");
    xsb.setXmlDetails("1.0", "UTF-8");
    xsb.push("testsuites");
    for (Map.Entry<Class<?>, Set<ITestResult>> entry : results.entrySet()) {
      Class<?> cls = entry.getKey();
      Properties p1 = new Properties();
      p1.setProperty("name", cls.getName());
      Date timeStamp = Calendar.getInstance().getTime();
      p1.setProperty(XMLConstants.ATTR_TIMESTAMP, timeStamp.toGMTString());

      List<Properties> testCases = Lists.newArrayList();
      int failures = 0;

      for (ITestResult tr: entry.getValue()) {
        if (tr.getStatus() != ITestResult.SUCCESS) failures++;
        Properties p2 = new Properties();
        p2.setProperty("classname", tr.getMethod().getMethod().getDeclaringClass().getName());
        p2.setProperty("name", tr.getMethod().getMethodName());
        p2.setProperty("time", "" + (tr.getEndMillis() - tr.getStartMillis()));
        testCases.add(p2);
      }

      p1.setProperty("failures", "" + failures);
      p1.setProperty("errors", "" + 0);
      p1.setProperty("name", cls.getName());
      try {
        p1.setProperty(XMLConstants.ATTR_HOSTNAME, InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException e) {
        // ignore
      }

      //
      // Now that we have all the information we need, generate the file
      //
      xsb.push("testsuite", p1);
      for (Properties p : testCases) {
        xsb.addEmptyElement("testcase", p);
      }
      xsb.pop("testsuite");
    }
    xsb.pop("testsuites");

//    System.out.println(xsb.toXML());
//    System.out.println("");

    String fileName = "TEST-" + suites.get(0).getName() + ".xml";
    Utils.writeFile(outputDirectory, fileName, xsb.toXML());
  }

  private void addResults(Set<ITestResult> allResults, Map<Class<?>, Set<ITestResult>> out) {
    for (ITestResult tr : allResults) {
      Class<?> cls = tr.getMethod().getMethod().getDeclaringClass();
      Set<ITestResult> l = out.get(cls);
      if (l == null) {
        l = Sets.newHashSet();
        out.put(cls, l);
      }
      l.add(tr);
    }
  }

}
