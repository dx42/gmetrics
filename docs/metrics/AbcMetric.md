# ABC Metric

 Calculates the **ABC** Metric for a class or method. **ABC** is a metric of
 size/complexity that counts the number of *Assignments* (A), *Branches* (B)
 and *Conditions* (C) and assigns a single numerical score calculated as:

## |ABC| = sqrt((A\*A)+(B\*B)+(C\*C))

 Implemented by the `org.gmetrics.metric.abc.AbcMetric` class.

 The **ABC** Metric calculation rules for Groovy:

 * Add one to the assignment count for each occurrence of an assignment operator, excluding constant declarations: 
   `= *= /= %= += \<\<= \>\>= &= |= ^= \>\>\>= ?=`

 * Add one to the assignment count for each occurrence of an increment or decrement operator (prefix or postfix): 
   `++ --`

 * Add one to the branch count for each function call or class method call.

 * Add one to the branch count for each occurrence of the new operator.

 * Add one to the branch count for each occurrence of the null-safe dereference (e.g. `x?.y`) or the null-safe indexing (e.g. `x?[1]`).

 * Add one to the condition count for each use of a conditional operator:
   `== != \<= \>= \< \> \<=\> =~ ==~ === !== instanceof !instanceof in !in`

 * Add one to the condition count for each use of the following keywords:
   `else case default try catch ?`

 * Add one to the condition count for each unary conditional expression.
   These are cases where a single variable/field/value is treated as a boolean value.
   Examples include `if (x)` and `return !ready`.


## Additional notes

 * A property access is treated like a method call (and thus increments the branch count).

 * If a class field is initialized to a Closure (ClosureExpression), then that Closure is analyzed just like a method.


## Guidelines for Interpreting ABC Metric Values

  A frequently-referenced [blog post](http://jakescruggs.blogspot.com/2008/08/whats-good-flog-score.html) by Jake Scruggs ([4]) offers the following guidelines for interpreting an **ABC** score. Note that these values refer to the score (magnitude) calculated for a single method:

  * 0-10 = *Awesome*
  * 11-20 = *Good enough*
  * 21-40 = *Might need refactoring*
  * 41-60 = *Possible to justify*
  * 61-100 = *Danger*
  * 100-200 = *Whoop, whoop, whoop*
  * 200+ = *Someone please think of the children*


## Metric Properties

  The following properties can be configured for this metric within a *MetricSet*. See [Creating a MetricSet](../CreatingMetricSet) for information on the syntax of setting a metric property.


| **Property**           | **Description**                                                                                                                                                                                                                             | **Default Value**     |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| *enabled*              | This `boolean` property controls whether the metric is *enabled*. If set to `false`, then the metric is not included as part of the results or the output reports.                                                                          | `true`                |
| *functions*            | This `List<String>` property contains the names of the functions to be calculated at the *method*, *class* and *package* levels and (potentially) included within the report(s). Valid values are: "total", "average", "minimum", "maximum" | `["total","average"]` |
| *includeClosureFields* | This `boolean` property controls whether metric values are calculated for *Closure Fields* and treated as *methods*. A *Closure Field* is a field that is initialized to a *Closure Expression*, e.g., `def myField = { println 12 }`.      | `true`                |


## References

 * **[1]** The [ABC Metric specification](http://www.softwarerenovation.com/ABCMetric.pdf).

 * **[2]** The [The C2 Wiki page](http://c2.com/cgi/wiki?AbcMetric) for the ABC Metric.

 * **[3]** [Groovy Code Metrics: ABC](https://tenpercentnotcrap.wordpress.com/2013/01/14/groovy-code-metrics-abc/) - Discusses
  using **GMetrics** and **CodeNarc** for measuring and enforcing the *ABC* metric on Groovy code.

 * **[4]** [Flog](http://ruby.sadi.st/Flog.html) is the popular Ruby tool that uses ABC.

 * **[5]** [This blog post](http://jakescruggs.blogspot.com/2008/08/whats-good-flog-score.html) describes some guidelines for interpreting the ABC score. The post refers to the **Flog** tool, but the **ABC** score is calculated similarly (though adapted somewhat to account for language specifics) and the guidelines should be transferable.
