package servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Producto;
import service.ProductoService;
import service.ProductoServiceImplement;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet que ofrece un listado de productos en tres formatos:
 * - HTML navegable con enlaces de exportación
 * - Fichero Excel (.xls) descargable
 * - Fichero JSON descargable
 *
 * Mapea a las URLs: /productos.html, /productos.xls, /productos.json
 */
@WebServlet({"/productos.html", "/productos.xls", "/productos.json"})
public class ProductoXlsServlet extends HttpServlet {

    /**
     * Procesa peticiones GET y delega la salida en función de la extensión solicitada.
     * @param req  Objeto que representa la petición HTTP
     * @param resp Objeto que representa la respuesta HTTP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1) Obtenemos la lista de productos desde la capa de servicio
        ProductoService service = new ProductoServiceImplement();
        List<Producto> productos = service.listar();

        // 2) Determinamos qué formato solicita el cliente
        //    Se basa en la extensión del path: .xls o .json (el HTML es la opción por defecto)
        String servletPath = req.getServletPath();
        boolean esXls  = servletPath.endsWith(".xls");  // True para descargar Excel
        boolean esJson = servletPath.endsWith(".json"); // True para descargar JSON

        // 3) Si es petición JSON, configuramos cabeceras y devolvemos JSON
        if (esJson) {
            // Indica al cliente que el contenido es JSON con codificación UTF-8
            resp.setContentType("application/json;charset=UTF-8");
            // descarga el archivo json
            resp.setHeader("Content-Disposition", "attachment; filename=productos.json");

            // Serializamos lista de productos a JSON usando Gson y la escribimos en la respuesta
            try (PrintWriter out = resp.getWriter()) {
                String json = new Gson().toJson(productos);
                out.print(json);
            }
            return; // Terminamos aquí para no ejecutar la generación HTML/XLS
        }

        // 4) Si es petición XLS, configuramos cabeceras para Excel
        if (esXls) {
            // MIME type para que Excel abra correctamente el contenido
            resp.setContentType("application/vnd.ms-excel");
            // descarga el archivo excel
            resp.setHeader("Content-Disposition", "attachment; filename=productos.xls");
        } else {
            // 5) Por defecto, generamos HTML para navegadores
            resp.setContentType("text/html;charset=UTF-8");
        }

        // 6) Generación de contenido: HTML o Excel (que es HTML con otro MIME)
        try (PrintWriter out = resp.getWriter()) {
            if (!esXls) {
                // Solo para HTML: cabeceras de documento y enlaces de exportación
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("  <meta charset=\"utf-8\">");
                out.println("  <title>Listado de Productos</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("  <h1>Listado de productos</h1>");
                // Enlace para descargar en formato Excel (.xls)
                out.printf("<p><a href='%s/productos.xls'>Exportar a Excel</a></p>",
                        req.getContextPath());
                // Enlace para descargar en formato JSON (.json)
                out.printf("<p><a href='%s/productos.json'>Mostrar JSON</a></p>",
                        req.getContextPath());
            }

            // 7) Común a ambos formatos: tabla con los datos de productos
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>id</th>");
            out.println("<th>nombre</th>");
            out.println("<th>tipo</th>");
            out.println("<th>precio</th>");
            out.println("</tr>");

            for (Producto p : productos) {
                //Abre una nueva fila de la tabla
                out.println("<tr>");
                //Imprime la celda con el ID del producto
                out.println("<td>" + p.getId() + "</td>");
                //Imprime la celda con el nombre del producto
                out.println("<td>" + p.getNombre() + "</td>");
                //Imprime la celda con el tipo de producto
                out.println("<td>" + p.getTipo() + "</td>");
                //Imprime la celda con el precio del producto,
                //formateado con dos decimales
                out.println("<td>" + String.format("%.2f", p.getPrecio()) + "</td>");
                //Cierra la fila de la tabla
                out.println("</tr>");
            }
            out.println("</table>");

            if (!esXls) {
                //Cierre de etiquetas para HTML solo
                out.println("</body>");
                out.println("</html>");
            }
        }
    }
}