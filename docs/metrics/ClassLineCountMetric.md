# ClassLineCount Metric

 Metric for counting the number of lines for classes. Note that this metric
 measures the number of lines from the first line of the class to the last line of
 the class. The count includes all program statements, comment lines and whitespace.

 Implemented by the `org.gmetrics.metric.linecount.ClassLineCountMetric` class.


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.

| **Property** | **Description**                                                                                                                                                                                                                             | **Default Value**     |
|--------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| *enabled*    | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports.                                                                          | `true`                |
| *functions*  | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "total", "average", "minimum", "maximum" | `["total","average"]` |


# References

 * The [The C2 Wiki page](http://c2.com/cgi/wiki?LinesOfCode) for **Lines of Code*
   has a good discussion of the perils of using (abusing) lines of code as a measure
   of just about anything. Note: That page specifically refers to measuring non-comment
   lines. This metric only measures total lines, including statements, comments and
   whitespace.
