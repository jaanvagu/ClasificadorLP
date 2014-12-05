/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 6 de 2013
 */

package utiles;

import entrada_salida.GestionarArchivos;
import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import preprocesamiento.Preprocesamiento;

public class DiccionarioEspanolIngles implements Serializable{   
    
    private final static Logger LOG = Logger.getLogger(DiccionarioEspanolIngles.class);
    
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document documentoXML;
    private Hashtable<String,Integer> tablaPalabrasEspanol_IdIngles;
    private Hashtable<Integer,String> tablaIdIngles_PalabrasIngles;
    
    public static Hashtable<String, String> diccionarioEspanolIngles;
    public static Hashtable<String, String> diccionarioInglesEspanol;

    public void construirDiccionarioEspanolIngles(){
        diccionarioEspanolIngles = new Hashtable<String, String>();
        tablaPalabrasEspanol_IdIngles = new Hashtable<String, Integer>();
        tablaIdIngles_PalabrasIngles = new Hashtable<Integer, String>();
        leerXML("diccionarios/PalabrasEspañol.xml");
        generarTablaPalabrasEspanol_IdIngles();
        leerXML("diccionarios/PalabrasIngles.xml");
        generarTablaIdIngles_PalabrasIngles();
        
        Enumeration<String> palabrasEspanol = tablaPalabrasEspanol_IdIngles.keys();
        while(palabrasEspanol.hasMoreElements()){
            String palabra_i_Espanol = palabrasEspanol.nextElement();
            int id_i_Ingles = tablaPalabrasEspanol_IdIngles.get(palabra_i_Espanol);
            String palabra_i_Ingles;
            try{
                palabra_i_Ingles = tablaIdIngles_PalabrasIngles.get(id_i_Ingles);
                diccionarioEspanolIngles.put(palabra_i_Espanol, palabra_i_Ingles);
            }
            catch(Exception e){
                //LOG.error("Problema en la relacion espanol-ingles. "+e.getMessage());
            }
        }
        LOG.debug("Tamaño Diccionario: "+diccionarioEspanolIngles.size());
        guardarDiccionario("DiccionarioEspanol_Ingles");
        LOG.info("Diccionario construido y alamacenado en directorio local");
    }
    
    public static void construirDiccionarioInglesEspanol(){
        diccionarioInglesEspanol = new Hashtable<String, String>();
        Enumeration<String> palabrasEspanol = diccionarioEspanolIngles.keys();
        while(palabrasEspanol.hasMoreElements()){
            String palabra_i_Espanol = palabrasEspanol.nextElement();
            String palabra_i_Ingles = diccionarioEspanolIngles.get(palabra_i_Espanol);
            diccionarioInglesEspanol.put(palabra_i_Ingles, palabra_i_Espanol);
        }
        guardarDiccionario("DiccionarioIngles_Espanol");
    }
        
    private void leerXML(String rutaArchivo){
        factory = DocumentBuilderFactory.newInstance();
        documentoXML = null;
        try{
            File archivoXML = new File(rutaArchivo);
            builder = factory.newDocumentBuilder();
            LOG.debug("Leyendo XML: "+archivoXML.getName());
            documentoXML = builder.parse(archivoXML);                        
        }
        catch(Exception spe){
            LOG.error("Archivo no accesible o formato XML incorrecto. "+spe.getMessage());
        }        
    }
    
    private void generarTablaPalabrasEspanol_IdIngles(){
        Node nodoRaiz = documentoXML.getFirstChild();
        NodeList listaNodosHijos = nodoRaiz.getChildNodes();
        LOG.debug("Leyendo Nodos hijos: "+listaNodosHijos.getLength());
        for (int i=0; i<listaNodosHijos.getLength(); i++){
           Node nodoHijo_i = listaNodosHijos.item(i);
           NamedNodeMap atributos = nodoHijo_i.getAttributes();
           
           try{
               Node atributoPalabraEspanol = atributos.getNamedItem("traduccion");
               Node atributoIdIngles = atributos.getNamedItem("id_gloss");
               String palabraEspanol = atributoPalabraEspanol.getNodeValue();
               int idIngles = Integer.parseInt(atributoIdIngles.getNodeValue());
               tablaPalabrasEspanol_IdIngles.put(palabraEspanol, idIngles);
           }
           catch(Exception e){
           }
        }
        LOG.debug("Elementos TablaEsID: "+tablaPalabrasEspanol_IdIngles.size());
    }
    
    private void generarTablaIdIngles_PalabrasIngles(){
        Node nodoRaiz = documentoXML.getFirstChild();
        NodeList listaNodosHijos = nodoRaiz.getChildNodes();
        LOG.debug("Leyendo Nodos hijos: "+listaNodosHijos.getLength());
        for (int i=0; i<listaNodosHijos.getLength(); i++){
           Node nodoHijo_i = listaNodosHijos.item(i);
           NamedNodeMap atributos = nodoHijo_i.getAttributes();
           
           try{
               Node atributoPalabraIngles = atributos.getNamedItem("word");
               Node atributoId = atributos.getNamedItem("id");
               try{
                   String palabraIngles = atributoPalabraIngles.getNodeValue();
                   int id = Integer.parseInt(atributoId.getNodeValue());
                   tablaIdIngles_PalabrasIngles.put(id, palabraIngles);
               }
               catch(Exception e){
                   LOG.error("Problema al insertar valores en tabla Es_Id. "+e.getMessage());
               }
           }
           catch(Exception e){
           }
        }
        LOG.debug("Elementos TablaIdIng: "+tablaIdIngles_PalabrasIngles.size());
    }
    
    public static void guardarDiccionario(String tipo){
        GestionarArchivos ga = new GestionarArchivos();
        if(tipo.equals("DiccionarioEspanol_Ingles")){
            ga.guardarDiccionarioEspIng(diccionarioEspanolIngles, tipo);
        }
        else{
            ga.guardarDiccionarioEspIng(diccionarioInglesEspanol, tipo);
        }
        LOG.debug(tipo+" guardado");
    }
    
    public static void cargarDiccionarios(String tipo){
        GestionarArchivos ga = new GestionarArchivos();
        if(tipo.equals("DiccionarioEspanol_Ingles")){
            diccionarioEspanolIngles = ga.cargarDiccionario(tipo);
            LOG.debug(tipo+" cargado de directorio local. [Palabras: "+diccionarioEspanolIngles.size()+"]");
        }
        else{
            diccionarioInglesEspanol = ga.cargarDiccionario(tipo);
            LOG.debug(tipo+" cargado de directorio local. [Palabras: "+diccionarioInglesEspanol.size()+"]");
        }
    }
    
    public static String traducirPalabraEspAIng(String palabraEs){
        if(diccionarioEspanolIngles.containsKey(palabraEs)){
            String palabraIng = diccionarioEspanolIngles.get(palabraEs);
            return palabraIng;
        }
        else{
            return "NO_TRADUCCION";
        }
    }
    
    public static boolean tieneTraduccionEspAIng(String palabra){
        if(diccionarioEspanolIngles.containsKey(palabra)){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static String traducirPalabraIngAEsp(String palabraIn){
        if(diccionarioInglesEspanol.containsKey(palabraIn)){
            String palabraIng = diccionarioInglesEspanol.get(palabraIn);
            return palabraIng;
        }
        else{
            return "NO_TRADUCCION";
        }
    }
    
    public static boolean tieneTraduccionIngAEsp(String palabraIn){
        if(diccionarioInglesEspanol.containsKey(palabraIn)){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static void limpiar(){
        Hashtable<String,String> temporal = new Hashtable<String, String>();
        Enumeration<String> palabras = diccionarioEspanolIngles.keys();
        while(palabras.hasMoreElements()){
            String palabraEs = palabras.nextElement();
            String palabraIn = diccionarioEspanolIngles.get(palabraEs);
            palabraEs = palabraEs.trim().toLowerCase();
            palabraEs = Preprocesamiento.quitarAcentos(palabraEs);
            palabraIn = palabraIn.trim();
            if(!(palabraEs.isEmpty()) && !(palabraIn.isEmpty())){
                temporal.put(palabraEs, palabraIn);
            }
        }
        diccionarioEspanolIngles.clear();
        diccionarioEspanolIngles = temporal;
        LOG.debug("Diccionario limpio. [Palabras: "+diccionarioEspanolIngles.size()+"]");
        guardarDiccionario("DiccionarioEspanol_Ingles");
    }
    
    private boolean contieneTilde(String palabra){
        if( (palabra.contains("á")) || (palabra.contains("é")) || (palabra.contains("í")) || (palabra.contains("ó")) || (palabra.contains("ú"))
                || (palabra.contains("à")) || (palabra.contains("è")) || (palabra.contains("ì")) || (palabra.contains("ò"))
                 || (palabra.contains("ù"))){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static void main(String[] args) {
        cargarDiccionarios("DiccionarioEspanol_Ingles");
        cargarDiccionarios("DiccionarioIngles_Espanol");
//        diccionarioEspanolIngles.put("experimentar", "experience");
//        diccionarioInglesEspanol.put("experience", "experimentar");
//        guardarDiccionario("DiccionarioEspanol_Ingles");
//        guardarDiccionario("DiccionarioIngles_Espanol");
        System.out.println(traducirPalabraEspAIng("avion"));
        System.out.println(traducirPalabraIngAEsp("plane"));
    }
}