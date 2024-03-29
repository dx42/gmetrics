# Cyclomatic Complexity (McCabe) Metric

 Calculates the **Cyclomatic Complexity** Metric for a class or method. **Cyclomatic Complexity** is a metric of complexity that counts the number of independent paths through the source code, and assigns a single numerical score for each method. The **Cyclomatic Complexity** score can also serve as an upper bound for the number of test cases that are necessary to achieve a complete branch coverage for a particular method ([1]).

 Implemented by the `org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric` class.


## Metric Calculation Rules

 The **Cyclomatic Complexity** Metric calculates a value for each method. Furthermore, if a class
 field is initialized to a *Closure* (`ClosureExpression`), then that *Closure* is analyzed
 just like a method.

 * Start with an initial (default) value of one (1). Add one (1) for each occurrence of each of the following:
   * `if` statement
   * `while` and `do-while` statement
   * `for` statement
   * `case` statement
   * `catch` statement
   * `&&` and `||` boolean operations
   * `?:` ternary operator and `?:` *Elvis* operator
   * `?=` *Elvis* assignment
   * `?.` null-check operator
   * `x?[]` safe indexing


## Interpreting Cyclomatic Complexity Values

  The value of **10** is often considered as the threshold between *acceptable* (low risk) code
  and *too complex* (higher risk). See **[1]** and **[2]**, for instance. As McCabe ([1]) says,
  > The particular upper bound that has been used for cyclomatic complexity is 10 which seems like 
  > a reasonable, but not magical, upper limit.

  Other sources cite **15** as a useful threshold, and/or draw further delineations between
  low/medium/high/unacceptable complexity values. **NDepend** ([4]), for instance, recommends that
  methods with a score of 
  > "15 are hard to understand and maintain. Methods where CC is higher than
  > 30 are extremely complex and should be split."
  
  On the other hand, [5] categorizes cyclomatic complexity scores into the following levels: 
  * 1-10 = Low risk program
  * 11-20 = Moderate risk
  * 21-50 = High risk
  * \>50 = Most complex and highly unstable method


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.


| **Property**           | **Description**                                                                                                                                                                                                                             | **Default Value**     |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| *enabled*              | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports.                                                                          | `true`                |
| *functions*            | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "total", "average", "minimum", "maximum" | `["total","average"]` |
| *includeClosureFields* | This `boolean` property controls whether metric values are calculated for *Closure Fields* and treated as *methods*. A *Closure Field* is a field that is initialized to a *Closure Expression*, e.g., `def myField = { println 12 }`.      | `true`                |


## References

 * **[1]** The [The *Wikipedia* page for *Cyclomatic Complexity*](http://en.wikipedia.org/wiki/Cyclomatic_complexity).

 * **[2]** The [original paper from Thomas J. McCabe](http://www.literateprogramming.com/mccabe.pdf) describing
   *Cyclomatic Complexity* in *IEEE Transactions on Software Engineering* Vol. 2, No. 4, p. 308 (1976).  

 * **[3]** [Groovy Code Metrics: Cyclomatic Complexity](https://tenpercentnotcrap.wordpress.com/2012/07/08/groovy-code-metrics-cyclomatic-complexity/) - Discusses 
   using **GMetrics** and **CodeNarc** for measuring and enforcing *Cyclomatic Complexity* on Groovy code.

 * **[4]** [NDepend](http://www.ndepend.com/Metrics.aspx) - an impressive source code metrics tool for Java.


