package com.yammer.metrics.reporting;

import com.google.common.base.Throwables;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AwsHelper {

    public static final String url = "http://169.254.169.254/latest/meta-data/instance-id";

    public static String getEc2InstanceId() {
        final HttpClient client = new DefaultHttpClient();
        try {
            return EntityUtils.toString(client.execute(new HttpGet(url)).getEntity(), "UTF-8");
        } catch (ClientProtocolException e) {
            Throwables.propagate(e);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return null;
    }
}