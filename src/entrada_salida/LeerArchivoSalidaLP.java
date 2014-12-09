/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 6 de 2013
 */

package entrada_salida;

import java.io.*;
import java.util.*;
import main.Main;
import org.apache.log4j.Logger;
import verificaciones.GestionarVerificaciones;

public class LeerArchivoSalidaLP {
    
    private final static Logger LOG = Logger.getLogger(LeerArchivoSalidaLP.class);

    private Vector<String> listaEtiquetasCorrespondientes;
    private Vector<String> listaEtiquetasPropagadasLP;
    private Hashtable<String,String> tabla_Nodo_Etiqueta;

    public LeerArchivoSalidaLP(boolean elegirRuta) {
        listaEtiquetasCorrespondientes = new Vector();
        listaEtiquetasPropagadasLP = new Vector();
        tabla_Nodo_Etiqueta = new Hashtable<String, String>();
        leerLineasArchivo(elegirRuta);
    }

    private void leerLineasArchivo(boolean elegirRuta){
        try {
            FileInputStream fis;
            if(elegirRuta){
//                fis = new FileInputStream(new GestionarArchivos().obtenerRutaArchivo("C:/Label_Propagation/Datos_Prueba"));
                fis = new FileInputStream(new GestionarArchivos().obtenerRutaArchivo("F:/Archivos/Archivos de la Universidad/Semestre 10/"
                        + "Trabajo de Grado II/Histórico de Pruebas/Sistema v2.4/Datos_Prueba v2.4"));
            }
            else{
                fis = new FileInputStream("C:/Label_Propagation/Datos_Prueba/Datos_"+
                    GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios()+"/label_prop_"+
                    GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios()+"_"+Main.listaComentariosNormalizados.size());
            }
            
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferlector = new BufferedReader(isr);
            String lineaLeida;
            while ((lineaLeida = bufferlector.readLine()) != null){
                construirListaEtiquetasCorrespondientes(lineaLeida);
                construirListaEtiquetasPropagadasLP(lineaLeida);
                construirTabla_Nodo_Etiqueta(lineaLeida);
//                GestionarVerificaciones.verificarSalidaLP(lineaLeida);
            }
            // Verificación de proceso
//            LOG.debug("Etiquetas correctas salida LP: "+GestionarVerificaciones.etiquetasSalidaLPCorrectas);
        }
        catch (Exception e){
            LOG.error("Error en Leer archivo salida LP: "+e.getMessage());
        }
    }

    //Método que se encarga de cosntruir una lista con las etiquetas solución o correspondientes de una lista de comentarios.
    // La lista es leída de un archivo generado por Label Propagation.
    private void construirListaEtiquetasCorrespondientes(String lineaLeida){
        StringTokenizer stLineaLeida = new StringTokenizer(lineaLeida);
        if(!esEtiquetaSemilla(lineaLeida)){
            StringBuilder sbEtiquetaLeida = new StringBuilder();
            stLineaLeida.nextToken(); //Salto de un token que corresponde al identificador del nodo (Ni).
            while(true){
                String parte = stLineaLeida.nextToken();
                if(parte.startsWith("1")) {
                    break;
                }
                sbEtiquetaLeida.append(parte).append(" ");
            }
            listaEtiquetasCorrespondientes.addElement(sbEtiquetaLeida.toString().trim());
        }
    }

    //Método que se encarga de cosntruir una lista con las etiquetas que fueran puestas por el Label Propagation,
    // La lista es leída de un archivo generado por Label Propagation.
    private void construirListaEtiquetasPropagadasLP(String lineaLeida){
        StringTokenizer stLineaLeida = new StringTokenizer(lineaLeida);
        StringBuilder sbEtiquetaLeida = new StringBuilder();
        if(!esEtiquetaSemilla(lineaLeida)){
            while(!stLineaLeida.nextToken().startsWith("1")){}
            while(true){
                String parte = stLineaLeida.nextToken();
                if(parte.startsWith("0")) {
                    break;
                }
                sbEtiquetaLeida.append(parte).append(" ");
            }
            listaEtiquetasPropagadasLP.addElement(sbEtiquetaLeida.toString().trim());
        }
    }
    
    private void construirTabla_Nodo_Etiqueta(String lineaLeida){
        StringTokenizer stLineaLeida = new StringTokenizer(lineaLeida);
        StringBuilder sbEtiquetaLeida = new StringBuilder();
        String nodo = stLineaLeida.nextToken(); // Obtiene Nodo: "N#"
        if(!esEtiquetaSemilla(lineaLeida)){
            while(!stLineaLeida.nextToken().startsWith("1"))  {}
            while(true){
                String parte = stLineaLeida.nextToken();
                if(parte.startsWith("0")) {
                    break;
                }
                sbEtiquetaLeida.append(parte).append(" ");
            }
        }
        else{
            while(true){
                String parte = stLineaLeida.nextToken();
                if(parte.startsWith("1")) {
                    break;
                }
                sbEtiquetaLeida.append(parte).append(" ");
            }
        }
        String etiqueta = sbEtiquetaLeida.toString().trim();
        tabla_Nodo_Etiqueta.put(nodo, etiqueta);
    }

    //Método que identifica si la etiqueta leida, corresponde a una de las etiquetas que fue pasada como semilla de entrenamiento a LP.
    private boolean esEtiquetaSemilla(String linea){
        StringTokenizer stLinea = new StringTokenizer(linea);
        int cantTokens = stLinea.countTokens();
        for(int i=0; i<cantTokens-2; i++) {
            stLinea.nextToken();
        }
        String indicador = stLinea.nextToken();
        if(indicador.equals("false")){
            return true;
        }
        else {
            return false;
        }
    }

    public Vector<String> obtenerListaEtiquetasCorrespondientes(){
        return listaEtiquetasCorrespondientes;
    }

    public Vector<String> obtenerListaEtiquetasPropagadasLP(){
        return listaEtiquetasPropagadasLP;
    }
    
    public Hashtable<String,String> obtenerTabla_Nodo_Etiqueta(){
        return tabla_Nodo_Etiqueta;
    }
}