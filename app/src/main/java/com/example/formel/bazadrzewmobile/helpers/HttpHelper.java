package com.example.formel.bazadrzewmobile.helpers;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by formel on 17.12.15.
 */
public class HttpHelper {

    public static final String EMPTY_RESPONSE="";

    public static class ProgressiveEntity implements HttpEntity {

        HttpEntity entity;
        public ProgressiveEntity(HttpEntity entity){
            this.entity = entity;
        }
        @Override
        public void consumeContent() throws IOException {
            entity.consumeContent();
        }
        @Override
        public InputStream getContent() throws IOException,
                IllegalStateException {
            return entity.getContent();
        }
        @Override
        public Header getContentEncoding() {
            return entity.getContentEncoding();
        }
        @Override
        public long getContentLength() {
            return entity.getContentLength();
        }
        @Override
        public Header getContentType() {
            return entity.getContentType();
        }
        @Override
        public boolean isChunked() {
            return entity.isChunked();
        }
        @Override
        public boolean isRepeatable() {
            return entity.isRepeatable();
        }
        @Override
        public boolean isStreaming() {
            return entity.isStreaming();
        }

        @Override
        public void writeTo(OutputStream outstream) throws IOException {

            class ProxyOutputStream extends FilterOutputStream {

                public ProxyOutputStream(OutputStream proxy) {
                    super(proxy);
                }
                public void write(int idx) throws IOException {
                    out.write(idx);
                }
                public void write(byte[] bts) throws IOException {
                    out.write(bts);
                }
                public void write(byte[] bts, int st, int end) throws IOException {
                    out.write(bts, st, end);
                }
                public void flush() throws IOException {
                    out.flush();
                }
                public void close() throws IOException {
                    out.close();
                }
            }

            class ProgressiveOutputStream extends ProxyOutputStream {
                public ProgressiveOutputStream(OutputStream proxy) {
                    super(proxy);
                }
                public void write(byte[] bts, int st, int end) throws IOException {

                    out.write(bts, st, end);
                }
            }

            entity.writeTo(new ProgressiveOutputStream(outstream));
        }


    };

    public static String getResponse(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String body = "";
        String content = "";

        while ((body = rd.readLine()) != null)
        {
            content += body + "\n";
        }
        return content.trim();
    }

    public static String getResponse(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

}



