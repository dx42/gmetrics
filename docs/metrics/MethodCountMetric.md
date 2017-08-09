# MethodCount Metric

 Metric for counting the number methods and closure fields within each class. Note that  this metric counts methods and constructors.

 Implemented by the `org.gmetrics.metric.methodcount.MethodCountMetric` class.


## Additional notes

 * If a class field is initialized to a Closure (ClosureExpression), then that Closure is analyzed just like a method.


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.

| **Property**      | **Description**                                                    | **Default Value**      |
|-------------------|--------------------------------------------------------------------|------------------------|
| *enabled*         | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports. | `true`                
| *functions*       | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: * "total", * "average", * "minimum", * "maximum" | `["total","average"]`
