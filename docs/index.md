# GMetrics

  The **GMetrics** project provides calculation and reporting of size and complexity metrics for
  Groovy source code. **GMetrics** scans Groovy source code, applying a set of metrics, and
  generates an HTML or XML report of the results.

  You can run **GMetrics** using the supplied [Ant Task](./gmetrics-ant-task.html).

  Total and average values for the following metrics are provided:

  * [Cyclomatic Complexity](./gmetrics-CyclomaticComplexityMetric.html).

  * [ABC](./gmetrics-ABCMetric.html) Size/Complexity. Also see the [C2 Wiki page](http://c2.com/cgi/wiki?AbcMetric).

  * [Cobertura line coverage](./gmetrics-CoberturaLineCoverageMetric.html) and [Cobertura branch coverage](./gmetrics-CoberturaBranchCoverageMetric.html)

  * [CRAP](./gmetrics-CrapMetric.html) - (Change Risk Anti-Patterns) score

  * [Afferent Coupling](./gmetrics-AfferentCouplingMetric.html)

  * [Efferent Coupling](./gmetrics-EfferentCouplingMetric.html)

  * [Lines per method](./gmetrics-MethodLineCountMetric.html)

  * [Lines per class](./gmetrics-ClassLineCountMetric.html)

  * [Number of classes per package](./gmetrics-ClassCountMetric.html)

  * [Number of field per class](./gmetrics-FieldCountMetric.html)

  See the site navigation menu for a list of the metrics and reports provided
  out of the box by **GMetrics**.

  Take a look at a [Sample GMetrics Report](./SampleGMetricsReport.html).

## Requirements

**GMetrics** requires:

 * Groovy version 2.4 or later
 * Java 1.6 or later
 * The SLF4J API jar and an SLF4J binding jar. See [The SLF4J Manual](https://www.slf4j.org/manual.html). 


## Getting GMetrics from the Maven2 Central Repository

  For projects built using [Maven](http://maven.apache.org/), **GMetrics** is now available from the *Maven Central Repository*.

    * groupId = org.gmetrics
    * artifactId = GMetrics


