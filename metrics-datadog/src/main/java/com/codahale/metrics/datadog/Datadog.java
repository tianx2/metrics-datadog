package com.codahale.metrics.datadog;

import com.codahale.metrics.datadog.model.DatadogCounter;
import com.codahale.metrics.datadog.model.DatadogGauge;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Datadog {
    private final String seriesUrl;
    private final ByteArrayOutputStream out;
    private static final Logger LOG = LoggerFactory.getLogger(Datadog.class);
    private static final JsonFactory jsonFactory = new JsonFactory();
    private static final ObjectMapper mapper = new ObjectMapper(jsonFactory);
    private JsonGenerator jsonOut;

    public Datadog(String apiKey, String applicationKey) {
        this.seriesUrl = (applicationKey != null) ?
                String.format("https://app.datadoghq.com/api/v1/series?api_key=%s&application_key=%s",
                        apiKey,
                        applicationKey) :
                String.format("https://app.datadoghq.com/api/v1/series?api_key=%s",
                        apiKey);
        this.out = new ByteArrayOutputStream();
    }

    // @VisibleForTesting
    Datadog(ByteArrayOutputStream out) {
        this.seriesUrl = "https://app.datadoghq.com/api/v1/series";
        this.out = out;
    }

    public void createSeries() {
        try {
            out.reset();
            jsonOut = jsonFactory.createGenerator(out);
            jsonOut.writeStartObject();
            jsonOut.writeArrayFieldStart("series");
        } catch (Exception e) {
            LOG.error("Error creating json", e);
        }
    }

    public void endSeries() {
        try {
            jsonOut.writeEndArray();
            jsonOut.writeEndObject();
            jsonOut.flush();
        } catch (Throwable e) {
            LOG.error("Error ending json", e);
        }
    }

    public void sendSeries() {
        try {
            out.flush();
            out.close();

            org.apache.http.client.fluent.Request.Post(this.seriesUrl)
                .addHeader("Content-Type",
                           ContentType.APPLICATION_JSON.toString())
                .bodyByteArray(this.out.toByteArray())
                .connectTimeout(5000)
                .socketTimeout(30000)
                .execute();
        } catch (Throwable e) {
            LOG.error("Error sending metrics", e);
        }
    }

    private void add(Object value) throws IOException {
        mapper.writeValue(jsonOut, value);
    }

    public void addGauge(String name, Number value, long timestamp, String host)
            throws IOException {
        add(new DatadogGauge(name, value, timestamp, host));
    }

    public void addCounter(String name, Long value, long timestamp, String host)
            throws IOException {
        add(new DatadogCounter(name, value, timestamp, host));
    }
}