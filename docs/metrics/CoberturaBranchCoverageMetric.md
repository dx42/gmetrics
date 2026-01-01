# CoberturaBranchCoverage Metric

 Metric that measures the code coverage of branches (conditionals) based on a
 [Cobertura](http://cobertura.sourceforge.net/) coverage XML file.

 Implemented by the `org.gmetrics.metric.coverage.CoberturaBranchCoverageMetric` class.


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.

| **Property**      | **Description**                                                    | **Default Value**      |
|-------------------|--------------------------------------------------------------------|------------------------|
| *enabled*         | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports. | `true`                
| *functions*       | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "total", "average", "minimum", "maximum" | `["total","average"]`  |
| *coberturaFile*   | The path to the **Cobertura** XML file. By default, the path is relative to the classpath. But the path may be optionally prefixed by any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute path on the filesystem), or "http:". This property is REQUIRED. | *N/A*


## Known Limitations

  This metric does not calculate coverage for *Closure Fields* (fields initialized to a *Closure Expression*), unlike some other *method*-level metrics.


## References

 * [Cobertura](https://github.com/cobertura/cobertura) -- Cobertura is a free Java tool that calculates the percentage of code accessed by tests. It can be used to identify which parts of your Java program are lacking test coverage.

 * [Cobertura Ant Task Reference](https://github.com/cobertura/cobertura/wiki/Ant-Task-Reference/)

 * [Cobertura Maven Plugin](https://www.mojohaus.org/cobertura-maven-plugin/)
