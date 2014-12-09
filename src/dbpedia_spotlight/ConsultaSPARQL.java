/* 
 * @author Jairo Andrés
 * Ultima modificacion: Abril 29 de 2013
 */

package dbpedia_spotlight;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.dbpedia.spotlight.exceptions.OutputException;
import org.dbpedia.spotlight.exceptions.SparqlExecutionException;

public class ConsultaSPARQL {

    private final static Logger LOG = Logger.getLogger(ConsultaSPARQL.class);
    private static HttpClient client = new HttpClient();
    private String respuestaConsulta;

    String mainGraph;
    String sparqlUrl;

    public ConsultaSPARQL(String mainGraph, String sparqlUrl) {
        this.mainGraph = mainGraph;
        this.sparqlUrl = sparqlUrl;
    }
    
    public ConsultaSPARQL() {
        this.mainGraph = "http://dbpedia.org";
        this.sparqlUrl = "http://dbpedia.org/sparql";
    }

    protected URL getUrl(String query) throws UnsupportedEncodingException, MalformedURLException {
        String graphEncoded = URLEncoder.encode(mainGraph, "UTF-8");
        String formatEncoded = URLEncoder.encode("application/sparql-results+json", "UTF-8");
        String queryEncoded = URLEncoder.encode(query, "UTF-8");
        String url = sparqlUrl+"?"+"default-graph-uri="+graphEncoded+"&query="+queryEncoded+"&format="+formatEncoded+"&debug=on&timeout=";
        return new URL(url);
    }

    private String query(String query) throws IOException, OutputException, SparqlExecutionException {
        if (query==null){
            System.err.println("Query vacia");
        }

        URL url = getUrl(query);

        String response = null;
        try {
            response = request(url);
            return response;
            
        } catch (Exception e) {
            throw new OutputException(e+response);
        }
    }

    private String request(URL url) throws SparqlExecutionException {
        GetMethod method = new GetMethod(url.toString());
        String response = null;

        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        try{
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                LOG.error("SparqlQuery failed: " + method.getStatusLine());
                throw new SparqlExecutionException(String.format("%s (%s). %s",
                                                                 method.getStatusLine(),
                                                                 method.getURI(),
                                                                 method.getResponseBodyAsString()));
            }

            byte[] responseBody = method.getResponseBody();

            response = new String(responseBody);

        }catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            throw new SparqlExecutionException(e);
        }catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            throw new SparqlExecutionException(e);
        }finally {
            method.releaseConnection();
        }
        return response;
    }
    
    private String extraerTextoUtilDeResultadoConsulta(String resultadoConsulta){
        int posFinalDosPuntos = resultadoConsulta.lastIndexOf(":");
        String textoUtil = resultadoConsulta.substring(posFinalDosPuntos+1, resultadoConsulta.length());
        return textoUtil;
    }

    public ArrayList<String> obtenerResultadoDeConsulta(){
        String[] tokensRespuesta = respuestaConsulta.split("\"");
        ArrayList<String> valoresObtenidos = new ArrayList<String>();
        
        for(int i=0; i<tokensRespuesta.length; i++){
            if(tokensRespuesta[i].equals("value")){
                valoresObtenidos.add("#"+extraerTextoUtilDeResultadoConsulta(tokensRespuesta[i+2]));
            }
        }
//        System.out.println(valoresObtenidos);
        return valoresObtenidos;
    }

    public void ejecutarConsulta(String linkDelConcepto){
        String consulta = "SELECT ?valorPropiedad\n" +
                "WHERE {\n" +
                "{ <"+linkDelConcepto+"> dcterms:subject ?valorPropiedad}\n" +
                "}";
        try{
            respuestaConsulta = query(consulta);
        }
        catch(Exception e){
            LOG.error("No fue posible realizar consulta SPARQL. "+e.getMessage());
        }
    }


    public static void main(String[] args) throws Exception {
        ConsultaSPARQL c = new ConsultaSPARQL();
        c.ejecutarConsulta("http://dbpedia.org/resource/Roger_Federer");
        c.obtenerResultadoDeConsulta();
//        for(int i=0; i<5;i++){
//            c.ejecutarConsulta("http://dbpedia.org/resource/Roger_Federer");
//            c.obtenerResultadoDeConsulta();
//            System.out.print(i+", ");
//        }        
    }
}