# Metrics Datadog Reporter
Simple Metrics reporter backend that sends metrics to DataDog.

## Usage

~~~java
import com.codahale.metrics.datadog.DatadogReporter

...

DatadogReporter.enable(15, TimeUnit.SECONDS, myDatadogKey, dataDogHostname)
~~~

The hostname provided with each publish needs to match the hostname defined in the /etd/dd-agent/datadog.conf file
that configures the DataDog agent.  This is so that the metrics being pushed directly to datadog via this library
can be associated with other (box-level) metrics being pushed by the agent.
