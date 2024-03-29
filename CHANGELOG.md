# GMetrics Change Log

Version 2.1.0 (Jun 2022)
-------------------------------------------------------------------------------------------
INFRASTRUCTURE
 - #26: Compatibility with Groovy 4.x. Upgrade imports to use groovy.xml.xx and groovy.ant.AntBuilder (Groovy 4).
 - #26: AbcMetric and CyclomaticComplexity rules: AST operation name for safe indexing is no longer just "[" (Groovy 4).
 - #26: Upgrade to CodeNarc 3.0.1.
 - #27: Upgrade to use Gradle maven-publishing plugin. 
 - #27: Build and publish separate GMetrics-Groovy4 jar (Groovy 4.x-compatible).


Version 2.0.0 (Jan 2022)
-------------------------------------------------------------------------------------------
BREAKING CHANGES
 - Requires Java 8 and Groovy 3.x.

FIXES AND ENHANCEMENTS
 - #21: AbstractCoberturaCoverageMetric: Make logger instance field to enable verifying logged warnings.
 - #23: CyclomaticComplexityMetric: Support Groovy 3 syntax: do/while loop, elvis assignment (`?=`), safe index, (e.g. `x?[1, 2]`).
 - #24: AbcMetric: Support Groovy 3 syntax: try-with-resources, `===`, `!==`, `!in`, `!instanceof`, elvis assignment (`?=`), safe index, (e.g. `x?[1, 2]`). Also add support for existing `in` and `instanceof` operators.

INFRASTRUCTURE AND TESTS
 - #20: Requires Java 8 or later.
 - #20: Upgrade to Groovy 3.0.9. Groovy 2.x no longer supported.
 - #20: Upgrade CodeNarc to 2.2.0.
 - #20: Upgrade to SLF4J 1.7.32.
 - #22: Upgrade to JUnit 5.8.2.
 - #20: Remove Cobertura Gradle plugin.
 - #20: Remove unused `pom.xml` and src/assembly.

DOCUMENTATION
 - #25: Update project website home page, links and navigation. 


Version 1.1 (May 2020)
-------------------------------------------------------------------------------------------
FIXES AND ENHANCEMENTS
 - #8: Fix Method metrics with wrong line number for annotated method.
 - #10: Refactorings: Remove deprecated org.codehaus.groovy.ast.expr.RegexExpression; remove duplicated defs of groovy's transform.powerassert.Value; add generics to MetricSet.getMetrics(). (Dominik Broj)
 - #16: Support Groovy 3.0. Fixed AbcMetric. Added explicit `groovy-templates` dependency.

INFRASTRUCTURE AND TESTS
 - #9: Add Gradle wrapper & remove IDE specific files.  (Dominik Broj)
 - #14: Switch to individual groovy jar dependencies rather than groovy-all.
 - #12: Upgrade to CodeNarc 1.0; remove Log4J (test) dependency.
 - #15: Upgrade to CodeNarc 1.5.
 - #17: Fix Travis CI build.
 - #18: Remove legacy site files.


Version 1.0 (Aug 2017)
-------------------------------------------------------------------------------------------
BREAKING CHANGES
- #4: Switch from Log4J 1.x to SLF4J.

FIXES AND ENHANCEMENTS
- #3: Create missing directories for report output files
- #6: Fix Method metrics with wrong line number for annotated method. 

INFRASTRUCTURE AND TESTS
- #1: Migrate (import) GMetrics project from Sourceforge.
- #2: Convert GMetrics project from Maven to Gradle.
- #5: Migrate GMetrics website from Sourceforge to GitHub Pages. 


Version 0.7 (Jan 2015)
-------------------------------------------------------------------------------------------
BREAKING CHANGES
- 31: Upgrade to Groovy 2.x (>=2.1); support Groovy 2.x.
- 32: Depend on individual Groovy modules rather than groovy-all.


Version 0.6 (Sep 2012)
-------------------------------------------------------------------------------------------
NEW METRICS
- 3341849: New EfferentCouplingMetric. Package-level metric that counts the number of other packages that the classes in a package depend upon, and is an indicator of the package's independence.
- 3341855: New AfferentCouplingMetric. Package-level metric that counts the number of other packages that depend on the classes within a package. It is an indicator of the package's responsibility.

NEW FEATURES
- 3560963: Add fileName and filePath attributes to <Class> elements on XML report.
- 3563055: Include a new name attribute in the <Package> elements in the XML report, to contain the actual package name. Add packageName property to PackageResultsNode to hold the actual package name, independent of the source folder. NOTE: Not using packageName instead of path in HTML reports for now.

FIXES AND ENHANCEMENTS
- 3508192: Make CoberturaCoverageFile threadsafe.
- 3520789: Fix CyclomaticComplexity metric does not count null-check operator if it is applied to a method call.
- 3564284: Use actual package name in CoberturaBranchCoverage and CoberturaLineCoverage metrics. Remove packageNamePrefixes property.
- 3523819: Omit attributes that have null value from the XML report.
- 3523820: Reports: Support for metric values that are lists or maps.
- 3523828: BasicHtmlReportWriter: Display ‘N/A’ if the metric value is null.

FRAMEWORK/API CHANGES
- 3564286: Changed Metric interface to include actual package name in applyToPackage:
    - Modified interface method: applyToPackage(String path, String packageName, Collection<MetricResult> childMetricResults)
    - AbstractMetric: Changed to calculateForPackage(String path, String packageName, Collection<MetricResult> childMetricResults)
- Remove FilesystemSourceAnalyzer (not used).

BREAKING CHANGES
- 3564286: Change to Metric interface and AbstractMetric to include actual package name in applyToPackage() and calculateForPackage().
- 3564284: Remove packageNamePrefixes property from CoberturaBranchCoverage and CoberturaLineCoverage metrics.

THANKS
- Thanks to Akila Perera for the patch for #3560963.


Version 0.5 (Jan 2012)
-------------------------------------------------------------------------------------------
NEW METRICS
- New CoberturaBranchCoverageMetric. Branch coverage at method/class/package level. Requires a Cobertura "coverage.xml" file. (#3474462)
- New CoberturaLineCoverageMetric. Line coverage at method/class/package level. Requires a Cobertura "coverage.xml" file. (#3474462)
- New CrapMetric. Calculated the CRAP score for a method, based on its complexity and code coverage. Requires a Cobertura "coverage.xml" file. (#3192158.)

NEW FEATURES
- MetricSet DSL: Return value from each metric in the metric set; Allow storing metric in variable. #3465486.
  This is useful for composite Metrics that require other metric values to calculate results (e.g. CrapMetric). If a metric is
  defined within another metric definition (assigned to a field), do not include as a standalone metric in the metric set.

INTEGRATION WITH OTHER TOOLS
- If you use GMetric with CodeNarc, this release requires CodeNarc 0.16.

FIXES AND ENHANCEMENTS
- Method-level Metrics: Handle multiple overloaded methods. (#3439103). Use MethodKey class.
- Fix: For SingleSeries package-level reports, show the full package name, not just the rightmost part of the package name. (#3439104).
- Add includeClosureFields property to AbstractMethodMetric. Defaults to true.
- Add String getSignature() method to MethodResultsNode and MethodKey. Add signature attribute to Method elements in XmlReportWriter. #3444707.

FRAMEWORK/API CHANGES  (Potential breaking changes if you have implemented your own Metrics)
- Change Metric and AbstractMetric to add packageName to applyToPackage(): applyToPackage(String packageName, Collection<MetricResult> childMetricResults). (Breaking Change). (#3465484)
- Introduce new MethodMetric (extends Metric) interface: applyToMethod() and applyToClosure().
- Create MethodKey class, with MethodKey(MethodNode) and MethodKey(String) constructors and String getMethodName() method, along with equals() and hashCode().
- Introduce Formatter framework. Enable configuring Formatter for Metric within "gmetrics-base-messages.properties".
- Reorganize MetricResult implementation classes; rename NumberMetricResult to SingleNumberMetricResult; Create new
  NumberMetricResult: allows setting values for total, average, min, max.
- AggregateNumberMetricResult: Add support for predefinedValues.
- Change writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext) to protected


Version 0.4 (Oct 2011)
-------------------------------------------------------------------------------------------
NEW FEATURES
- Upgrade to Groovy 1.7. NOTE: GMetrics now requires Groovy 1.7. (#3421010)
- Support specifying metrics within the MetricSet by specifying the metric name, with optional properties Map or optional closure. (#3421008)

NEW METRICS
- New FieldCountMetric. Counts the number of fields within each class. (#3341811)
- New MethodCountMetric. Counts the number of methods (and closure fields) within each class. (#3341804)
- New ClassCountMetric. Counts the number of classes within each package. (#3341812)

FIXES AND ENHANCEMENTS
- Fix #3305753: Make GMetrics runnable (compatible) with Groovy 1.8.
- Fix #3413604: Reimplement ASTUtil.getVariableExpressions() to optimize performance. Thanks to Hamlet D’Arcy.
- Fix #3186167: GMetrics can't be built with Groovy >= 1.7.7. Fix illegal writes to final properties.
- Fix #3418843: Reports: Exclude results for level "less" than metric base level
- ReportWriters: Include output file name in “Report created” message

FRAMEWORK/API CHANGES  (Potential breaking changes if you have implemented your own Metrics)
- 3418135: Add MetricLevel getMetricLevel() to MetricResult interface and impl classes.
  Also  AbstractMetric: Change protected createAggregateMetricResult() to add MetricLevel parameter.
- Specify collection types (generics) for method signatures within Metric, MetricResult , ResultsNode and ClassMetricResult classes.
- Add isValid() method to the SourceCode interface and implementations.

INFRASTRUCTURE AND TESTS
- Create GMetricsVersion class with String getVersion(). Remove "GMetrics" from version file.
- Upgrade to use CodeNarc 0.15 for tests/analysis.


Version 0.3 (23 Jul 2010)
-------------------------------------------------------------------------------------------
NEW FEATURES
- New SingleSeriesHtmlReportWriter. Creates HTML report for single series (univariate) of metric values.
    This single series is specified by a single Metric, a level (package, class or method) and a single function (total, average, minimum, maximum).
    e.g., metric:"ABC", function:"average", level:"method".
    Also supports optional: greaterThan:"50", lessThan:"100", sort:"descending", maxResults:"20"
- For BasicHtmlReportWriter and XmlReportWriter:
    Add support for filtering metric values included within the reports by metric name, level ("package", "class" and "method") or function ("total", "average", "minimum" and "maximum").
    e.g., 
        metrics="CyclomaticComplexity, ABC"
        levels="CyclomaticComplexity=class,method; ABC=method"
        functions="ABC=average; CyclomaticComplexity=total,maximum"
- Add support for "maximum" and "minimum" function for metrics.

FIXES AND ENHANCEMENTS
- Fix: All method-level Metrics (e.g. MethodLineCountMetric): Apply to constructors as well as regular methods.

FRAMEWORK/API CHANGES
- MetricResult: Replace hard-coded getTotal() and getAverage() methods with getAt(String) to support the metricResult['total'] syntax.
- MetricResult. Add getLineNumber() method to interface. Change AbstractMetric and subclasses to populate the results accordingly.
- AbstractMetric: Change calculateForClass() to be protected. AbstractMethodMetric: Specify MetricResult return type for calculate() methods.
- MetricLevel: Change to be an enum.
- GroovyDslMetricSet: Throw MissingPropertyException if a non-existent property is set within a Groovy MetricSet DSL. Set ruleset Closure resolveStrategy to DELEGATE_ONLY. (See CodeNarc GroovyDslRuleSet).
- Upgrade to GMaven 1.2.


Version 0.2 (10 Mar 2010)
-------------------------------------------------------------------------------------------
NEW FEATURES
- New CyclomaticComplexityMetric
- New MetricSet DSL: Implement GroovyDslMetricSet and MetricSetBuilder.
- New XmlReportWriter

BREAKING CHANGES
- Modify ReportWriter interface (and implementations) to pass AnalysisContext: void writeReport(ResultsNode resultsNode, AnalysisContext analysisContext).
- Change DefaultMetricSet to include CyclomaticComplexityMetric instead of AbcMetric.

FIXES AND ENHANCEMENTS
- Add metricSetFile property to GMetricsTask Ant Task.
- AbcMetric (AbcVector): Fix "NumberFormatException: Infinite or NaN" error with large numbers.
- AbcMetric: Calculate metric for run() synthetic method (special case).
- GMetricsTask: Fix: Replace call to Class.forName() with getClass().classLoader.loadClass().
- WildcardPattern: Escape plus sign ('+') within patterns. Add plus sign ('+') to convertStringWithWildcardsToRegex().

REPORTS
- BasicHtmlReportWriter: Don't nest packages? Put all packages at the same level (no indent).
- BasicHtmlReportWriter: Display full (relative) package names, e.g., "org/gmetrics/source".
- Enable configuring all reports (AbstractReportWriter) to write to the stdout (console).
- Add support for 'enabled' property to BasicHtmlReportWriter.

METRICS
- New CyclomaticComplexity metric
- Introduce AbstractMetric.
- Added boolean isEnabled() to the Metric interface and enabled property to AbstractMetric.
- Add Guidelines for Interpreting for ABC Metric Values to web site.
- Add web site page for the DefaultMetricSet.

INFRASTRUCTURE AND TESTS
- Introduce CompositeMetricSet.
- Introduce AnalysisContext class with sourceDirectories and metricSet.
- Add List getSourceDirectories() to SourceAnalyzer interface and implementation classes. For AntFileSetSourceAnalyzer: Calculate sourceDirectories relative to project base directory.
- Rename AbstractMetricTest to AbstractMetricTestCase.
- Add AbstractCommonMetricTestCase. Common tests: enabled returns null for package,class; metric implements Metric.


Version 0.1.1 (7 Feb 2010)
-------------------------------------------------------------------------------------------
- Fix Bug #2919722: "Method metrics only process first closure field". https://sourceforge.net/tracker/?func=detail&aid=2919722&group_id=288180&atid=1220655.
- Enable syncing with Maven Central Repository.


Version 0.1 (16 Dec 2009)
-------------------------------------------------------------------------------------------
- Initial release. Includes AbcMetric, ClassLineCountMetric, MethodLineCountMetric.

<http://www.gmetrics.org>