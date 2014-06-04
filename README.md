# Metrics Datadog Reporter
Simple Metrics reporter backend that sends metrics to DataDog.

## Usage

~~~java
import com.codahale.metrics.datadog.DatadogReporter

final DatadogReporter reporter = DatadogReporter.forRegistry(registry)
                                                .withApiKey(apiKey)
                                                .withApplicationKey(applicationKey)
                                                .filter(MetricFilter.ALL)
                                                .build();

reporter.start(1, TimeUnit.MINUTES);
~~~

The hostname provided with each publish needs to match the hostname defined in the /etd/dd-agent/datadog.conf file
that configures the DataDog agent.  This is so that the metrics being pushed directly to datadog via this library
can be associated with other (box-level) metrics being pushed by the agent.
