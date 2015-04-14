/* 
 * @author Jairo Andrés
 * Ultima modificacion: Abril 25 de 2013
 */

package utiles;

import entrada_salida.GestionarArchivos;
import org.openrdf.query.algebra.Str;

public class ArchivoConfiguracionLog4j {
    
    private static GestionarArchivos gestionArchivosConfigLog4j = new GestionarArchivos();
    
    public static void generarArchivo(String nivelLog){
        
        gestionArchivosConfigLog4j.crearArchivoTexto("Config_LOG_Ejecucion", -1); // No aplica cantidad de datos, por ende -1
        
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.rootLogger="+nivelLog+",CONSOLA, File");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.logger.httpclient=ERROR,CONSOLA");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.CONSOLA=org.apache.log4j.ConsoleAppender");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.CONSOLA.layout=org.apache.log4j.PatternLayout");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.CONSOLA.layout.ConversionPattern=%-8r [%-5p] |%-50c| - %m%n");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.File=org.apache.log4j.FileAppender");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.File.File=log/LOG_"+Fecha.fechaYHoraActual()+".log");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.File.layout=org.apache.log4j.PatternLayout");
        gestionArchivosConfigLog4j.escribirLineaEnArchivoTexto("log4j.appender.File.layout.ConversionPattern=%-8r [%-5p] |%-50c| - %m%n");
        
        gestionArchivosConfigLog4j.cerrarArchivoTexto();
    }
}
