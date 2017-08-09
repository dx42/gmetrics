# MethodLineCount Metric

 Metric for counting the number of lines for methods and closure fields. Note that
 this metric measures the number of lines from the first line of the method to the
 last line of the metric. The count includes all program statements, comment lines
 and whitespace.

 Implemented by the `org.gmetrics.metric.linecount.MethodLineCountMetric` class.


## Additional notes

 * If a class field is initialized to a Closure (ClosureExpression), then that Closure is
   analyzed just like a method.


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.

| **Property**           | **Description**                                                    | **Default Value**      |
|------------------------|--------------------------------------------------------------------|------------------------|
| *enabled*              | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports. | `true`                
| *functions*            | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "total", "average", "minimum", "maximum" | `["total","average"]`  |
| *includeClosureFields* | This `boolean` property controls whether metric values are calculated for *Closure Fields* and treated as *methods*. A *Closure Field* is a field that is initialized to a *Closure Expression*, e.g., `def myField = { println 12 }`. | `true`


## References

 * The [The C2 Wiki page](http://c2.com/cgi/wiki?LinesOfCode) for **Lines of Code**
   has a good discussion of the perils of using (abusing) lines of code as a measure
   of just about anything. Note: That page specifically refers to measuring non-comment
   lines. This metric only measures total lines, including statements, comments and
   whitespace.
