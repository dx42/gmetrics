# GMetrics

**GMetrics** provides calculation and reporting of size and complexity metrics for Groovy source code.

**GMetrics** scans Groovy source code and applies a set of metrics. Available metrics include *Cyclomatic Complexity*,
*ABC*, *Line counts*, *Field/Method/Class counts*, and *Cobertura Code Coverage*. See [Metrics](./Metrics).

The results are reported in HTML or XML. See [Reports](./Reports).

## Running and Configuring GMetrics

  You can run **GMetrics** using the supplied [Ant Task](./AntTask). 
  
  Define the metrics to calculate by [Creating a MetricSet](./CreatingMetricSet).
  
  The [Sonar Groovy Plugin](https://github.com/Inform-Software/sonar-groovy) uses **GMetrics** for its calculation of *Cyclomatic Complexity* for Groovy source code.
  

## Requirements

**GMetrics** requires:

 * Java 1.8 or later
 * The SLF4J API jar and an SLF4J binding jar. See [The SLF4J Manual](https://www.slf4j.org/manual.html).
 * Either the **GMetrics** or **GMetrics-Groovy4** jar. 

**GMetrics** (Groovy 3)
* The "main" **GMetrics** artifact requires Groovy version 3.0 or later

**GMetrics-Groovy4** (Groovy 4)
* The **GMetrics-Groovy4** artifact requires Groovy version 4.0 or later


## Getting GMetrics from the Maven2 Central Repository

  For projects built using [Maven](http://maven.apache.org/), **GMetrics** is now available from the *Maven Central Repository*.

  * groupId = **org.gmetrics**
  * artifactId = **GMetrics** or **GMetrics-Groovy4**


