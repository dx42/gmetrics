# ClassCount Metric

 Metric for counting the number of classes in each package. This metric counts
 the number of classes, interfaces and enums within a package.

 Implemented by the `org.gmetrics.metric.classcount.ClassCountMetric` class.


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](./gmetrics-creating-metricset.html) for information on the syntax of setting a metric property.

| **Property**      | **Description**                                                    | **Default Value**      |
|-------------------|--------------------------------------------------------------------|------------------------|
| *enabled*         | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports. | `true`                
| *functions*       | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "total", "average", "minimum", "maximum" | `["total","average"]`  |
