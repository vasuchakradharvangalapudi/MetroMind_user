package metromind;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * MetroMind - Intelligent Public Transport Platform (Review-1: Modules 1-3).
 * Starts a local web server: static UI from ./web and JSON API under /api/.
 * Usage:  java -cp out metromind.Main [port]
 */
public final class Main {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) { }
        }
        File webRoot = new File("web");
        if (!webRoot.isDirectory()) webRoot = new File("../web");
        if (!webRoot.isDirectory()) {
            System.err.println("Cannot find the 'web' folder. Run the program from the project root folder.");
            System.exit(1);
        }
        final File root = webRoot.getCanonicalFile();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/", ex -> {
            try {
                String body = metromind.web.ApiBridge.handle(ex.getRequestURI().getPath(), ex.getRequestURI().getRawQuery());
                send(ex, 200, "application/json; charset=utf-8", body.getBytes(StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
                send(ex, 400, "application/json; charset=utf-8",
                    ("{\"error\":" + quote(e.getMessage()) + "}").getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                send(ex, 500, "application/json; charset=utf-8",
                    ("{\"error\":" + quote("Server error: " + e) + "}").getBytes(StandardCharsets.UTF_8));
            }
        });
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            File f = new File(root, path).getCanonicalFile();
            if (!f.getPath().startsWith(root.getPath()) || !f.isFile()) {
                send(ex, 404, "text/plain; charset=utf-8", "404 Not found".getBytes(StandardCharsets.UTF_8));
                return;
            }
            send(ex, 200, mime(f.getName()), Files.readAllBytes(f.toPath()));
        });
        server.start();
        System.out.println("==============================================");
        System.out.println(" MetroMind is running  ->  http://localhost:" + port);
        System.out.println(" Press Ctrl+C to stop.");
        System.out.println("==============================================");
    }

    private static String quote(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' || c == '\\') b.append('\\').append(c);
            else if (c < 0x20) b.append(' ');
            else b.append(c);
        }
        return b.append('"').toString();
    }

    private static String mime(String n) {
        if (n.endsWith(".html")) return "text/html; charset=utf-8";
        if (n.endsWith(".css")) return "text/css; charset=utf-8";
        if (n.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (n.endsWith(".svg")) return "image/svg+xml";
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".json")) return "application/json; charset=utf-8";
        return "application/octet-stream";
    }

    private static void send(HttpExchange ex, int code, String type, byte[] data) throws IOException {
        ex.getResponseHeaders().add("Content-Type", type);
        ex.getResponseHeaders().add("Cache-Control", "no-store");
        ex.sendResponseHeaders(code, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }
}
