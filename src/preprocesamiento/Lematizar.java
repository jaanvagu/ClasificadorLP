/*
 * @author Jairo Andrés
 * Ultima modificacion: Abril 30 de 2013
 */

package preprocesamiento;

import estructuras.ComentarioNormalizado;
import java.util.*;
import org.annolab.tt4j.*;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Lematizar {
    
    private final static Logger LOG = Logger.getLogger(Lematizar.class);

    private Vector<String> listaMensajesOriginales;
    private Vector<String> listaMensajesLematizados;
    private TreeTaggerWrapper treeTagger; //Variable (objeto) de tipo TreeTagger, la cual se encarga de encontrar el lema de una palabra
    private StringBuilder mensajeLematizado;
    private Hashtable<String,String> tablaPalabras_CategoriaLexica;
    private Vector<ComentarioNormalizado> listaComentariosParaObtenerCateLexica;

    //Constructor que recibe una lista de mensajes, y obtiene el lema de cada una de las palabras contenidas en los mensajes.
    //Almacena en una nueva lista de mensajes, los cuales contienen los lemas de las palabras originales.
    public Lematizar(Vector<String> listaMensajes){
        PropertyConfigurator.configure("log4j.properties");
        treeTagger = new TreeTaggerWrapper<String>();        
        listaMensajesOriginales = new Vector(listaMensajes);
        listaMensajesLematizados = new Vector();       
        if(!listaMensajes.isEmpty()){
            ejecutarLematizacion();
            LOG.info("Lematizacion Realizada");
        }
    }

    public Lematizar(){
        PropertyConfigurator.configure("log4j.properties");
        treeTagger = new TreeTaggerWrapper<String>();
        tablaPalabras_CategoriaLexica = new Hashtable<String, String>();
    }    
    
    private void ejecutarLematizacion(){        
        System.setProperty("treetagger.home", "./Archivos_TreeTagger");
        try{
            for(int i=0; i<listaMensajesOriginales.size(); i++){                
                mensajeLematizado = new StringBuilder();
                treeTagger.setModel("./Archivos_TreeTagger/lib/spanish-utf8.par");
                treeTagger.setHandler(new TokenHandler<String>() {                        
                        public void token(String token, String pos, String lemma) {
                            if(!lemma.isEmpty()){
                                lemma = Preprocesamiento.quitarAcentos(lemma);
                                mensajeLematizado.append(lemma).append(" ");
                            }
                            else{
                                LOG.warn("*******No encontró lema*******");
                            }
                        }
                });                                                
                Vector palabrasMensaje = mensajeAVectorPalabras(listaMensajesOriginales.elementAt(i));
                treeTagger.process(palabrasMensaje);
                quitarEspacioEnBlancoAlFinalDeMensaje();
                listaMensajesLematizados.addElement(mensajeLematizado.toString());                                
            }            
        }
        catch(Exception e){
            LOG.error("Error en lematizar: "+e.getMessage());
        }
        finally {
            treeTagger.destroy();
        }
    }
    
    public Hashtable<String,String> generarTablaPalabras_CategoriaLexica(Vector<ComentarioNormalizado> listaComentarios){
//    public void generarTablaPalabras_CategoriaLexica(Vector<ComentarioNormalizado> listaComentarios){

        final Set<String> setSemanticForms = new HashSet<String>();

        listaComentariosParaObtenerCateLexica = new Vector(listaComentarios);
        System.setProperty("treetagger.home", "./Archivos_TreeTagger");
        try{
            for(int i=0; i<listaComentariosParaObtenerCateLexica.size(); i++){                                        
                treeTagger.setModel("./Archivos_TreeTagger/lib/spanish-utf8.par");
                treeTagger.setHandler(new TokenHandler<String>() {                        
                        public void token(String token, String pos, String lemma) {
                            if(!pos.isEmpty()){
                                tablaPalabras_CategoriaLexica.put(token, pos);
                                setSemanticForms.add(pos);
                            }
                        }
                });
                treeTagger.process(listaComentariosParaObtenerCateLexica.elementAt(i).obtenerListaPalabrasEnComentario());                                           
            }

//            System.out.println(setSemanticForms);
//            System.out.println(setSemanticForms.size());

        }
        catch(Exception e){
            LOG.error("Error en generarTablaPalabras_CategoriaLexica: "+e.getMessage());
        }
        finally {
            treeTagger.destroy();
            LOG.info("Finalizó creación de tabla de formas semánticas");
            return tablaPalabras_CategoriaLexica;
        }

//        return tablaPalabras_CategoriaLexica;
    }

    private Vector<String> mensajeAVectorPalabras(String mensaje){
        StringTokenizer tokensMensaje = new StringTokenizer(mensaje);
        Vector<String> palabrasMensaje = new Vector();
        while(tokensMensaje.hasMoreTokens()){
            String tempPalabraMensaje = tokensMensaje.nextToken().trim();
            if(!tempPalabraMensaje.isEmpty()){
                palabrasMensaje.addElement(tempPalabraMensaje);
            }
        }
        return palabrasMensaje;
    }

    private void quitarEspacioEnBlancoAlFinalDeMensaje(){
        StringTokenizer tokensMensaje = new StringTokenizer(mensajeLematizado.toString());
        StringBuilder tempMensaje = new StringBuilder();
        while(tokensMensaje.hasMoreTokens()){
            if(tokensMensaje.countTokens()!=1){
                tempMensaje.append(tokensMensaje.nextToken().trim()).append(" ");
            }
            else{
                tempMensaje.append(tokensMensaje.nextToken().trim());                
            }
        }
        mensajeLematizado = tempMensaje;
    }

    public Vector<String> obtenerListaMensajesLematizados(){
        return listaMensajesLematizados;
    }
    
    public Hashtable<String,String> obtenerTablaPalabras_CategoriaLexica(){
        return tablaPalabras_CategoriaLexica;
    }     
}