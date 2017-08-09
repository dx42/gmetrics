# Efferent Coupling Metric

  Calculates the **Efferent Coupling** for a package. This is a count of the number of other packages
  that the classes in a package depend upon, and is an indicator of the package's independence ([1]).
  This is a **package**-level metric.

 Implemented by the `org.gmetrics.metric.coupling.EfferentCouplingMetric` class.


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.

| **Property**         | **Description**                                                    | **Default Value**      |
|----------------------|--------------------------------------------------------------------|------------------------|
| *enabled*            | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports. | `true`                
| *functions*          | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "value" - the value for the current package; "total" - the total value for the current package and its descendant packages; "average" - the average value for the current package and its descendant packages; "referencedFromPackages" - the list of packages that reference classes within the current package. | `["value","average"]`  
| *ignorePackageNames* | The names of packages to ignore when calculating afferent coupling. This pattern string may contain wildcard characters ('*' or '?'); it may also contain more than one pattern, separated by commas. | *null*


## References

 * **[1]** The [The *Wikipedia* page for *Software package metrics*](http://en.wikipedia.org/wiki/Software_package_metrics).

 * **[2]** ["CodeQuality for Software Architects: Use coupling metrics to support your system architecture"](http://www.ibm.com/developerworks/java/library/j-cq04256/) -
   Andrew Glover, part of the "In pursuit of code quality" series in *developerWorks*.
   This article includes a discussion of **Efferent Coupling**, among other metrics.

 * **[3]** [Code Quality: The Open Source Perspective](http://www.spinellis.gr/codequality/) - Diomidis Spinellis. Addison Wesley, 2006.

 * **[4]** [Agile Software Development, Principles, Patterns, and Practices](http://www.amazon.com/exec/obidos/ASIN/0135974445/objectmentorinc) -
    Robert C. Martin. Prentice Hall, 2002.

