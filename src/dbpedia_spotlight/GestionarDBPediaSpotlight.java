/* 
 * @author Jairo Andrés
 * Ultima modificacion: Abril 30 de 2013
 */

package dbpedia_spotlight;

import entrada_salida.GestionarArchivos;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.log4j.Logger;

public class GestionarDBPediaSpotlight {
    
    private final static Logger LOG = Logger.getLogger(GestionarDBPediaSpotlight.class);
    
    private File directorioDeEjecucion;
    private String comandoDeEjecucion;    
    private Process procesoEjecucion;
    private BufferedReader buferLector;
    private Hashtable<String,String> tablaPalabras_LinksConceptosDBPedia;
    
    private GestionarArchivos gestionArchivos;

    public GestionarDBPediaSpotlight(){
        directorioDeEjecucion = new File("C:/Users/JairoAndrés/Documents/NetBeansProjects/ClasificadorComentarios/lib/DBPedia_Spotlight v2.0/eval");
        comandoDeEjecucion = "mvn scala:run -DmainClass=org.dbpedia.spotlight.evaluation.external.DBpediaSpotlightClient";
        gestionArchivos = new GestionarArchivos();
    }
    
    //Método que ejecuta DBPedia-Spotlight para encontrar conceptos (ontologias) en un texto. Hace un llamado por comandos
    //de consola a la herramienta, y retorna una lista con los links de los conceptos encontrados.
    public void identificarConceptosEnTexto(ArrayList<String> listaPalabras){
        LOG.info("Identificando conceptos con DBPediaSpotlight...");
        generarArchivoTextoParaProcesar(listaPalabras);
        tablaPalabras_LinksConceptosDBPedia = new Hashtable<String,String>();        
        try{
            procesoEjecucion = Runtime.getRuntime().exec("cmd /c "+comandoDeEjecucion, null, directorioDeEjecucion);
            buferLector = new BufferedReader(new InputStreamReader(procesoEjecucion.getInputStream()));    
            //Como el número de lineas que se imprimen en consola no es definifo, se establece 10000 como número límite,
            //pero nunca llega hasta ese iteración. Sólo hasta que encuentre conceptos, o la señal de que no se hallaron.
            for(int i=0; i<100000000; i++){
                String linea = buferLector.readLine();
                //Imprimir texto salida de la consola.
//                System.out.println(linea);
                if(linea.equals("Cantidad_Conceptos")){
                    int cantConceptos = Integer.parseInt(buferLector.readLine());
                    for(int j=0; j<cantConceptos; j++){
                        String palabraOriginal = buferLector.readLine();
                        String linkConcepto = buferLector.readLine();
                        tablaPalabras_LinksConceptosDBPedia.put(palabraOriginal, linkConcepto);
                    }
                    break;                   
                }
                if(linea.equals("No_se_hallaron_conceptos")){
                    LOG.warn("No se identificaron conceptos");
                    break;
                }
            }            
            procesoEjecucion.destroy();
            LOG.info("Ejecucion DBPediaSpotlight terminada. ("+tablaPalabras_LinksConceptosDBPedia.size()+" conceptos identificados)");
//            System.out.println("Lista["+tablaPalabras_LinksConceptosDBPedia.size()+"]: "+tablaPalabras_LinksConceptosDBPedia);
        }
        catch(Exception e){
            LOG.error("No fue posible identificar conceptos. "+e.getMessage());
        }
    }
    
    //Crea un archivo txt con las palabras ingresadas como parámetro. Este archivo será la entrada de DBPedia-Spotlight.
    private void generarArchivoTextoParaProcesar(ArrayList<String> listaPalabras){
        StringBuilder texto = new StringBuilder();
        for(int i=0; i<listaPalabras.size(); i++){
            if(i != (listaPalabras.size()-1)){
                texto.append(listaPalabras.get(i)).append(" ");
            }
            else{
                texto.append(listaPalabras.get(i));
            }
        }
        gestionArchivos.crearArchivoTexto("TextoDBPedia", 0);
        gestionArchivos.escribirLineaEnArchivoTexto(texto.toString());
        gestionArchivos.cerrarArchivoTexto();
    }
    
    public String obtenerLinkConceptoPalabra(String palabra){        
        String linkConcepto = "NO_CONCEPTO";
        if(tablaPalabras_LinksConceptosDBPedia.containsKey(palabra)){
            linkConcepto = tablaPalabras_LinksConceptosDBPedia.get(palabra);
            return linkConcepto;
        }
        else{
            return linkConcepto;
        }
    }
    
    public static void main(String[] args){
        GestionarDBPediaSpotlight g = new GestionarDBPediaSpotlight();
        ArrayList<String> a = new ArrayList<String>();
        a.add("Sony,");
//        a.add("aroma");
        a.add("medical");
        a.add("Roger");
        a.add("Federer,");
        a.add("monday");
        g.identificarConceptosEnTexto(a);
//        System.out.println(g.obtenerLinkConceptoPalabra("sony"));
//        System.out.println(g.obtenerLinkConceptoPalabra("son"));
//        System.out.println(g.obtenerLinkConceptoPalabra("medical"));
//        System.out.println(g.obtenerLinkConceptoPalabra("roger federer"));
    }
}