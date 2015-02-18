/* 
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 5 de 2013
 */

package entrada_salida;

import estructuras.ComentarioNormalizado;
import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.Main;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import utiles.Constantes;

public class GestionarArchivos {    
    
    private final static Logger LOG = Logger.getLogger(GestionarArchivos.class);

    private JFileChooser selectorArchivo;
    private FileNameExtensionFilter filtroExtensionArchivo;
    private File archivo;
    private FileWriter escritor;
    private BufferedWriter bufferEscritor;
    private int opcionSeleccionada;
    private String rutaArchivo;    

    public static String nombreArchivo;        

    public GestionarArchivos() {
        //PropertyConfigurator.configure("log4j.properties");
    }        

    //Método que obtiene la ruta de un archivo, a partir de un selector visual de archivos.
    public String obtenerRutaArchivoCSV(String tipo){
        selectorArchivo = new JFileChooser("/home/meridean-hp/Escritorio");
        filtroExtensionArchivo = new FileNameExtensionFilter("Archivos de texto (."+tipo+")", tipo);
        selectorArchivo.setFileFilter(filtroExtensionArchivo);
        opcionSeleccionada = selectorArchivo.showOpenDialog(new JTextArea());
        
        if (opcionSeleccionada == JFileChooser.APPROVE_OPTION){
            if (obtenerTipoArchivo(selectorArchivo.getSelectedFile().getName()).equals(tipo)){
                rutaArchivo = selectorArchivo.getSelectedFile().getAbsolutePath();                
                LOG.info("Procesando: "+selectorArchivo.getSelectedFile().getName());
            }
            else{
                rutaArchivo = "vacia";
                System.err.println("El archivo cargado no es de tipo "+tipo);
                JOptionPane.showMessageDialog(null,"El archivo cargado no es de tipo "+tipo,"Información",JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
        else{
            System.exit(0);
        }        
        
        return rutaArchivo;
    }

    //Retorna la extensión del archivo (txt, csv, pdf, etc.)
    private String obtenerTipoArchivo(String nombreCompletoArchivo){
        String extension;
        int posPunto = nombreCompletoArchivo.lastIndexOf(".");
        if(posPunto == -1){
            JOptionPane.showMessageDialog(null,"El archivo no es de tipo csv","Información",JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        nombreArchivo = nombreCompletoArchivo.substring(0,posPunto);
        extension = nombreCompletoArchivo.substring(posPunto+1,nombreCompletoArchivo.length());        
        return extension;
    }

    //Método que convierte el contenido de un archivo a formato UTF-8
    public InputStreamReader convertirAFormatoUTF8(String rutaArchivo){
        try{
            FileInputStream fis = new FileInputStream(rutaArchivo);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            return isr;
        }
        catch (Exception e){
            LOG.error("Error al convertir formato de archivo a UTF8: "+e.getMessage());
            return null;
        }
    }   
    
    //Método que almacena en un directorio local los comentarios despues de procesados (normalizados), se guardan los
    //objetos, no el texto literal. Se usa la Serializacion para guardar directamente los objetos.
    public void guardarComentariosNormalizados(Vector<ComentarioNormalizado> listaAGuardarComentariosNormalizados, String tipo){
        try{
            String temprutaArchivo;
            // ***  LINUX ***
//            if(tipo.equals("Normalizado")){
//                temprutaArchivo = "/home/jairo/Escritorio/Archivos Guardados/"+nombreArchivo+" - "+tipo;
//            }else{
//                temprutaArchivo = "/home/jairo/Escritorio/Archivos Guardados/Consolidado "+obtenerNombreCompletoDelConsolidado()+" - "+tipo;
//            }
                        
            // ***  WINDOWS ***
            if(tipo.equals("Normalizado")){
                temprutaArchivo = "./Archivos_Guardados/"+nombreArchivo+" - "+tipo+" "+Constantes.version2_0;
            }else{
                temprutaArchivo = "./Archivos_Guardados/Consolidado "+obtenerNombreCompletoDelConsolidado()
                                 +" - "+tipo+" "+Constantes.version2_0;
            }
            
            FileOutputStream archivoDeSalida = new FileOutputStream(temprutaArchivo);
            ObjectOutputStream objetoSalida = new ObjectOutputStream(archivoDeSalida);
            
            objetoSalida.writeObject(listaAGuardarComentariosNormalizados);
           
            objetoSalida.close();
        }catch(Exception e){
            LOG.error("Error al guardar lista de comentarios normalizados\n"+e.getMessage());
        }
    }

    public Vector<ComentarioNormalizado> cargarComentariosNormalizados(String subCarpeta, String nombreConsolidado, boolean elegirRuta){
        String temprutaArchivo;
        if(elegirRuta){
            temprutaArchivo = obtenerRutaArchivo
                    ("/home/meridean-hp/repositorios/ClasificadorLP/Archivos_Guardados/"+subCarpeta+nombreConsolidado);
        }
        else{
            temprutaArchivo = "C:/Users/JairoAndrés/Desktop/Archivos Guardados/"+subCarpeta+nombreConsolidado;
            nombreArchivo = nombreConsolidado;
            System.out.println();
            LOG.info("Procesando: "+nombreConsolidado);
        }
        Vector<ComentarioNormalizado> listaComentariosLeidos = new Vector();
        try{
            FileInputStream input = new FileInputStream(temprutaArchivo);
            ObjectInputStream objectInput = new ObjectInputStream(input);
            Object objetoLeido = objectInput.readObject();
            listaComentariosLeidos = (Vector<ComentarioNormalizado>) objetoLeido;
        }
        catch(Exception e){
            LOG.error("Error cargar archivo Comentario Normalizado: "+e.getMessage());            
        }
        return listaComentariosLeidos;
    }
    
    //Método que almacena en el directorio del proyecto el diccionario (Tabla Hash) espanol-ingles, se guarda el
    //objeto, no el texto literal.
    public void guardarDiccionarioEspIng(Hashtable<String,String> diccionario, String tipo){
        try{
            String temprutaArchivo;
            // *** LINUX ***
//            temprutaArchivo = "diccionarioEs_Ing/diccionarioEspanol_Ingles";
                        
            // *** WINDOWS ***           
            temprutaArchivo = "diccionarios/"+tipo;           
            
            FileOutputStream archivoDeSalida = new FileOutputStream(temprutaArchivo);
            ObjectOutputStream objetoSalida = new ObjectOutputStream(archivoDeSalida);
            
            objetoSalida.writeObject(diccionario);
           
            objetoSalida.close();
        }catch(Exception e){
            LOG.error("Error al guardar diccionario\n"+e.getMessage());
        }
    }
    
    public Hashtable<String,String> cargarDiccionario(String tipo){
        String temprutaArchivo = "diccionarios/"+tipo;     
        Hashtable<String,String> diccionario = new Hashtable<String,String>();
        try{
            FileInputStream input = new FileInputStream(temprutaArchivo);
            ObjectInputStream objectInput = new ObjectInputStream(input);
            Object objetoLeido = objectInput.readObject();
            diccionario = (Hashtable<String,String>) objetoLeido;
        }
        catch(Exception e){
            LOG.error("Error cargar: "+tipo+": "+e.getMessage());
        }
        return diccionario;
    }

    //Método que crea un archivo para escribir en el texto plano, y lo almacena en un directorio local.
    public void crearArchivoTexto(String identificador, int cantDatos){
        try{           
            // ***  LINUX ***
//            if(identificador.equals("configuracion")){
//                archivo = new File("/home/jairo/Escritorio/Datos_Prueba/"+obtenerNombreDelConsolidadoSinEspacios()+"_config_"+cantDatos);
//            }
//            else if(identificador.equals("TextoDBPedia")){
//                archivo = new File("lib/DBPedia_Spotlight/eval/Archivos_Texto_Conceptos/Texto.txt");                                                  
//            }
//            else{
//                archivo = new File("/home/jairo/Escritorio/Datos_Prueba/Datos_"+obtenerNombreDelConsolidadoSinEspacios()+"/"+identificador+"_"+nombreArchivo+"_"+cantDatos);
//            }
            
            // ***  WINDOWS ***
            if(identificador.equals("configuracion")){
                archivo = new File("C:/Label_Propagation/Datos_Prueba/"+obtenerNombreDelConsolidadoSinEspacios()+"_config_"+cantDatos);
            }
            else if(identificador.equals("TextoDBPedia")){
                archivo = new File("lib/DBPedia_Spotlight v2.0/eval/Archivos_Texto_Conceptos/Texto.txt");
            }
            else if(identificador.equals("Config_LOG_Ejecucion")){
                archivo = new File("log4j.properties");
            }
            else{
                File directorio = new File("C:/Label_Propagation/Datos_Prueba/Datos_"+obtenerNombreDelConsolidadoSinEspacios());
                if(!directorio.exists()){
                    directorio.mkdir();
                }
                archivo = new File("C:/Label_Propagation/Datos_Prueba/Datos_"+obtenerNombreDelConsolidadoSinEspacios()+"/"+identificador+"_"+obtenerNombreDelConsolidadoSinEspacios()+"_"+cantDatos);
            }

            escritor = new FileWriter(archivo);
            bufferEscritor = new BufferedWriter(escritor);
        }
        catch(Exception e){
            LOG.error("Error al guardar Archivo texto: "+e.getMessage());
        }
    }

    public void crearArchivoJson(String nombreArchivo){
        try{
            archivo = new File("./json/" + nombreArchivo +".json");
            escritor = new FileWriter(archivo);
            bufferEscritor = new BufferedWriter(escritor);
        } catch (Exception e){
            System.err.println("Problema creando file json" + e.getMessage());
        }
    }

    //Método que cierra un archivo después de que se termino de escribir en él.
    public void cerrarArchivoTexto(){
        try{
            bufferEscritor.close();
        }
        catch(Exception e){
            LOG.error("Error al cerrar Archivo texto: "+e.getMessage());
        }
    }

    //Método que escribe linea de texto plano en un archivo almacenado en directorio local.
    public void escribirLineaEnArchivoTexto(String linea){
        try{                        
            bufferEscritor.write(linea);
            bufferEscritor.newLine();            
        }
        catch(Exception e){
            LOG.error("Error al escribir linea Archivo texto: "+e.getMessage());
        }
    }
    
    public void establecerRutaArchivoPruebaLP_enArchivoSBT(){
        try{
            archivo = new File("C:/Label_Propagation/sbt.bat");
            escritor = new FileWriter(archivo);
            bufferEscritor = new BufferedWriter(escritor);
            bufferEscritor.write("cd C:\\Label_Propagation");
            bufferEscritor.newLine();
            bufferEscritor.write("java -Xmx512M -jar \"C:\\Label_Propagation\\bin\\sbt-launch.jar\" "
                    + "\"run-main upenn.junto.app.JuntoConfigRunner "
                    + "Datos_Prueba\\"+obtenerNombreDelConsolidadoSinEspacios()+"_config_"+Main.listaComentariosNormalizados.size()+"\"");
            bufferEscritor.close();
        }
        catch(Exception e){
            LOG.error("Error en generacion archivo SBT: "+e.getMessage());            
        }
    }
    
    // Ventana para selección de archivos locales
    public String obtenerRutaArchivo(String identificador){
        // ***  LINUX ***                    
//        selectorArchivo = new JFileChooser("/home/jairo/Escritorio/Datos_Prueba");
        
        // ***  WINDOWS ***
        selectorArchivo = new JFileChooser(identificador);
        
        opcionSeleccionada = selectorArchivo.showOpenDialog(new JTextArea());

        if (opcionSeleccionada == JFileChooser.APPROVE_OPTION){            
            try{
                rutaArchivo = selectorArchivo.getSelectedFile().getAbsolutePath();
                nombreArchivo = selectorArchivo.getSelectedFile().getName();
                LOG.info("Procesando: "+selectorArchivo.getSelectedFile().getName());
            }catch(Exception e){
                LOG.error(e.getMessage());
            }
        }
        else{
            System.exit(0);
        }

        return rutaArchivo;
    }
    
    private String obtenerNombreCompletoDelConsolidado(){
        StringTokenizer stNombreCompletoArchivo = new StringTokenizer(nombreArchivo); 
        stNombreCompletoArchivo.nextToken();
        String nombreArchivoRecortado = "";        
        int cantTokens = stNombreCompletoArchivo.countTokens();
        for(int i=1; i<cantTokens; i++){
            if(stNombreCompletoArchivo.hasMoreElements()){
                String tmpToken = stNombreCompletoArchivo.nextToken();
                if(!tmpToken.trim().equals("-")){
                    nombreArchivoRecortado += tmpToken.trim()+" ";
                }
                else{
                    break;
                }
            }
        }        
        return nombreArchivoRecortado.trim();
    }
    
    public static String obtenerNombreDelConsolidadoSinEspacios(){
        StringTokenizer stNombreCompletoArchivo = new StringTokenizer(nombreArchivo); 
        stNombreCompletoArchivo.nextToken();
        String nombreArchivoRecortado = "";        
        int cantTokens = stNombreCompletoArchivo.countTokens();
        for(int i=1; i<cantTokens; i++){
            if(stNombreCompletoArchivo.hasMoreElements()){
                String tmpToken = stNombreCompletoArchivo.nextToken();
                if(!tmpToken.trim().equals("-")){
                    nombreArchivoRecortado += tmpToken.trim()+" ";
                }
                else{
                    break;
                }
            }
        }
        nombreArchivoRecortado = nombreArchivoRecortado.trim().replaceAll(" ", "_");
        if(nombreArchivoRecortado.length() == 0){
            return nombreArchivo;
        }
        return nombreArchivoRecortado;
    }
    
    public void limpiaCarpetaArchivosPrueba(int cantDatos){
        String[] nombresArchivos = {"Balance","Coca_Cola","Dog_Chow","Don_Julio","Kotex","Nike","Sony","Top_Terra"};
        for(int i=0; i<nombresArchivos.length; i++){
            String archivo_i = nombresArchivos[i];
            File config = new File("C:/Label_Propagation/Datos_Prueba/"+archivo_i+"_config_"+cantDatos);
            File graf = new File("C:/Label_Propagation/Datos_Prueba/Datos_"+archivo_i+"/input_graph_"+archivo_i+"_"+cantDatos);
            File seed = new File("C:/Label_Propagation/Datos_Prueba/Datos_"+archivo_i+"/seeds_"+archivo_i+"_"+cantDatos);
            File gold = new File("C:/Label_Propagation/Datos_Prueba/Datos_"+archivo_i+"/gold_labels_"+archivo_i+"_"+cantDatos);
            File label = new File("C:/Label_Propagation/Datos_Prueba/Datos_"+archivo_i+"/label_prop_"+archivo_i+"_"+cantDatos);
            
            config.delete();
            graf.delete();
            seed.delete();
            gold.delete();
            label.delete();
        }
    }
}