package fr.annway.web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/** Échange HTTP en mémoire, pour tester l'entrée HTTP sans ouvrir de port ([NO-NETWORK]). */
final class FakeExchange extends HttpExchange {

    private final String method;
    private final URI uri;
    private final Headers requestHeaders = new Headers();
    private final Headers responseHeaders = new Headers();
    private final InputStream requestBody;
    private final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    private int responseCode;

    FakeExchange(String method, String uri, String body) {
        this.method = method;
        this.uri = URI.create(uri);
        this.requestBody = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
    }

    String responseText() {
        return responseBody.toString(StandardCharsets.UTF_8);
    }

    @Override
    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    @Override
    public Headers getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public URI getRequestURI() {
        return uri;
    }

    @Override
    public String getRequestMethod() {
        return method;
    }

    @Override
    public HttpContext getHttpContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        // Rien à fermer : tout est en mémoire.
    }

    @Override
    public InputStream getRequestBody() {
        return requestBody;
    }

    @Override
    public OutputStream getResponseBody() {
        return responseBody;
    }

    @Override
    public void sendResponseHeaders(int code, long length) {
        responseCode = code;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress("192.0.2.17", 50_000);
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress("127.0.0.1", WebMain.DEFAULT_PORT);
    }

    @Override
    public String getProtocol() {
        return "HTTP/1.1";
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStreams(InputStream input, OutputStream output) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpPrincipal getPrincipal() {
        throw new UnsupportedOperationException();
    }
}
