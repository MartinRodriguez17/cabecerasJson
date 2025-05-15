package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Servlet que muestra información de las cabeceras y meta-datos de la petición HTTP recibida.
 * Mapea a la ruta /cabeceras-request.
 */
@WebServlet("/cabeceras-request")
public class CabecerasHttpRequestServlet extends HttpServlet {

    /**
     * Maneja peticiones GET y genera una página HTML que lista:
     * - Método HTTP usado
     * - URI y URL completas de la petición
     * - Context path y servlet path
     * - Dirección IP y puerto locales
     * - Esquema, host y URL construida
     * - Dirección IP del cliente y todas las cabeceras recibidas
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Configurar el content type de la respuesta como HTML con codificación UTF-8
        resp.setContentType("text/html;charset=UTF-8");

        // 1) Información básica de la petición
        String metodoHttp = req.getMethod();// GET, POST, etc.
        String requestUri = req.getRequestURI();// URI sin esquema y dominio
        String requestUrl = req.getRequestURL().toString();// URL completa de la petición
        String contextPath = req.getContextPath();// Contexto de la web app
        String servletPath = req.getServletPath();// Ruta del servlet dentro del contexto

        // 2) Información del servidor local
        String ipLocal = req.getLocalAddr();// IP del servidor
        int portLocal = req.getLocalPort();// Puerto donde escucha el servidor
        String scheme = req.getScheme();// "http" o "https"
        String hostHeader = req.getHeader("host");// Cabecera Host enviada por el cliente

        // 3) Construcción manual de URLs
        // URL utilizando hostHeader (puede incluir puerto)
        String urlConstruida1 = scheme + "://" + hostHeader + contextPath + servletPath;
        // URL utilizando la IP local y puerto obtenidos del servidor
        String urlConstruida2 = scheme + "://" + ipLocal + ":" + portLocal + contextPath + servletPath;

        // 4) Dirección IP del cliente remoto
        String ipCliente = req.getRemoteAddr();// IP desde la que vino la petición

        // 5) Escribir la respuesta HTML
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");// Declaración de tipo de documento
            out.println("<html>");
            out.println("<head>");
            out.println("  <meta charset=\"utf-8\">  ");// Meta para indicar UTF-8
            out.println("  <title>Cabeceras Http Request</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Cabeceras HTTP Request</h1>");
            out.println("<ul>");

            // Listado de propiedades obtenidas
            out.printf("<li>Método HTTP: %s</li>%n", metodoHttp);
            out.printf("<li>Request URI: %s</li>%n", requestUri);
            out.printf("<li>Request URL: %s</li>%n", requestUrl);
            out.printf("<li>Context Path: %s</li>%n", contextPath);
            out.printf("<li>Servlet Path: %s</li>%n", servletPath);
            out.printf("<li>IP local del servidor: %s</li>%n", ipLocal);
            out.printf("<li>Puerto local del servidor: %d</li>%n", portLocal);
            out.printf("<li>Esquema (scheme): %s</li>%n", scheme);
            out.printf("<li>Host (cabecera): %s</li>%n", hostHeader);
            out.printf("<li>URL construida 1: %s</li>%n", urlConstruida1);
            out.printf("<li>URL construida 2: %s</li>%n", urlConstruida2);
            out.printf("<li>IP del cliente remoto: %s</li>%n", ipCliente);

            // 6) Iterar todas las cabeceras enviadas por el cliente
            out.println("<li><strong>CABECERAS:</strong></li>");
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String nombre = headerNames.nextElement();
                String valor = req.getHeader(nombre);
                out.printf("<li>%s: %s</li>%n", nombre, valor);
            }

            out.println("</ul>");
            out.println("</body>");//Cerramos body
            out.println("</html>");//Cerramos html
        }
    }
}