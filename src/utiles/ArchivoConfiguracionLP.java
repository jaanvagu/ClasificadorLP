/* 
 * @author Jairo Andrés
 * Ultima modificacion: Abril 25 de 2013
 */

package utiles;

import entrada_salida.GestionarArchivos;
import main.Main;
import org.apache.log4j.Logger;

public class ArchivoConfiguracionLP {
    
    private final static Logger LOG = Logger.getLogger(ArchivoConfiguracionLP.class);
    
    private static GestionarArchivos gestionArchivosConfigLP = new GestionarArchivos();
    
    //Método que crea un archivo de configuración para la ejecución de Label Propagation. 
    public static void generarArchivo(int cantDatos){
        
        gestionArchivosConfigLP.crearArchivoTexto("configuracion", Main.listaComentariosNormalizados.size());
        
        String nombreArchivo = GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios();
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("graph_file = Datos_Prueba/Datos_"+nombreArchivo+"/input_graph_"+nombreArchivo+"_"+cantDatos);
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("data_format = edge_factored");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("seed_file = Datos_Prueba/Datos_"+nombreArchivo+"/seeds_"+nombreArchivo+"_"+cantDatos);
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("gold_labels_file = Datos_Prueba/Datos_"+nombreArchivo+"/gold_labels_"+nombreArchivo+"_"+cantDatos);
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("test_file = Datos_Prueba/Datos_"+nombreArchivo+"/gold_labels_"+nombreArchivo+"_"+cantDatos);
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("iters = 1");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("verbose = false");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("prune_threshold = 0");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("algo = lp_zgl");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("mu1 = 1");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("mu2 = 1e-2");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("mu3 = 1e-2");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("beta = 2");
        gestionArchivosConfigLP.escribirLineaEnArchivoTexto("output_file = Datos_Prueba/Datos_"+nombreArchivo+"/label_prop_"+nombreArchivo+"_"+cantDatos);
        
        gestionArchivosConfigLP.cerrarArchivoTexto();
        LOG.info("Archivo configuracion generado");
    }
            
}