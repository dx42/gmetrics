# GMetrics

## Running and Configuring GMetrics

  You can run **GMetrics** using the supplied [Ant Task](./AntTask). Define the metrics to calculate by [Creating a MetricSet](./CreatingMetricSet).

## Metrics

  Total and average values for the following metrics are provided:
  * [Cyclomatic Complexity](./metrics/CyclomaticComplexityMetric).
  * [ABC](./metrics/AbcMetric) - Size/Complexity. Also see the [C2 Wiki page](http://c2.com/cgi/wiki?AbcMetric).
  * [Cobertura line coverage](./metrics/CoberturaLineCoverageMetric) and [Cobertura branch coverage](./metrics/CoberturaBranchCoverageMetric)
  * [CRAP](./metrics/CrapMetric) - (Change Risk Anti-Patterns) score
  * [Afferent Coupling](./metrics/AfferentCouplingMetric)
  * [Efferent Coupling](./metrics/EfferentCouplingMetric)
  * [Method Line Count](./metrics/MethodLineCountMetric) - Lines per method
  * [Class Line Count](./metrics/ClassLineCountMetric) - Lines per class
  * [Class Count](./metrics/ClassCountMetric) - Number of classes per package
  * [Field Count](./metrics/FieldCountMetric) - Number of field per class


## Reports
  **GMetrics** provides the following reports:
  * [BasicHtmlReportWriter](./reports/BasicHtmlReportWriter) ([Sample HTML Report](./reports/SampleGMetricsReport.html))
  * [SingleSeriesHtmlReportWriter](./reports/SingleSeriesHtmlReportWriter) ([Sample Single Series HTML Report](./reports/SampleGMetricsSingleSeriesReport.html))
  * [XmlReportWriter](./reports/XmlReportWriter) ([Sample XML Report](./reports/SampleGMetricsXmlReport.html))
  
  Or you can use XSLT to generate a custom HTML report. [MrHaki](http://www.mrhaki.com/) has written a detailed blog post describing [How to Create a GMetrics Report with XSLT](http://mrhaki.blogspot.com/2011/01/groovy-goodness-create-gmetrics-report.html).


## Requirements

**GMetrics** requires:

 * Groovy version 2.4 or later
 * Java 1.6 or later
 * The SLF4J API jar and an SLF4J binding jar. See [The SLF4J Manual](https://www.slf4j.org/manual.html). 


## Getting GMetrics from the Maven2 Central Repository

  For projects built using [Maven](http://maven.apache.org/), **GMetrics** is now available from the *Maven Central Repository*.

  * groupId = org.gmetrics
  * artifactId = GMetrics


