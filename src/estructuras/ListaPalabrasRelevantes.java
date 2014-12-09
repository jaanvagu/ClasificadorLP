/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 7 de 2013
 */

package estructuras;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;
import preprocesamiento.Lematizar;
import utiles.DiccionarioEspanolIngles;
import utiles.Matematicas;

public class ListaPalabrasRelevantes {
    
    private final static Logger LOG = Logger.getLogger(ListaPalabrasRelevantes.class);
    
    private Vector<ComentarioNormalizado> listaComentariosOriginales;
    private Lematizar lematizar;
    private Hashtable<String,String> tablaPalabras_CategoriaLexica;
    private ArrayList<String> listaPalabrasDiferentes; //Todas las palabras diferentes de una lista de comentarios.
    private Hashtable<String,String> tablaPalabrasRelevantes;

    public ListaPalabrasRelevantes(Vector<ComentarioNormalizado> listaComentarios){
        LOG.info("Generando lista de palabras relevantes");
        listaComentariosOriginales = new Vector(listaComentarios);
        listaPalabrasDiferentes = new ArrayList<String>();
        tablaPalabrasRelevantes = new Hashtable();
        
        lematizar = new Lematizar();
        lematizar.generarTablaPalabras_CategoriaLexica(listaComentarios);
        tablaPalabras_CategoriaLexica = lematizar.obtenerTablaPalabras_CategoriaLexica();
        
        generarListaPalabrasDiferentes();
        generarListaPalabrasRelevantes();
    }
    
    public ArrayList<String> obtenerListaPalabrasRelevantesEnEspanol(){
        ArrayList<String> listaPalabrasRelevantesEnEspanol = new ArrayList<String>();
        Enumeration<String> palabrasRelevantes = tablaPalabrasRelevantes.keys();
        while(palabrasRelevantes.hasMoreElements()){
            String palabra = palabrasRelevantes.nextElement();
            listaPalabrasRelevantesEnEspanol.add(palabra);
        }        
        return listaPalabrasRelevantesEnEspanol;
    }
    
    public ArrayList<String> obtenerListaPalabrasRelevantesTraducidas(){
        ArrayList<String> listaPalabrasRelevantesTraducidas = new ArrayList<String>();
        Enumeration<String> palabrasRelevantes = tablaPalabrasRelevantes.keys();
        while(palabrasRelevantes.hasMoreElements()){
            String palabra = palabrasRelevantes.nextElement();
            if(DiccionarioEspanolIngles.tieneTraduccionEspAIng(palabra)){
                listaPalabrasRelevantesTraducidas.add(DiccionarioEspanolIngles.traducirPalabraEspAIng(palabra));
            }
        }        
        return listaPalabrasRelevantesTraducidas;
    }
    
    private void generarListaPalabrasRelevantes(){
        DiccionarioEspanolIngles.cargarDiccionarios("DiccionarioEspanol_Ingles");
//        int cantMaximaDeComentariosEnQueAparece = Matematicas.calcularCantidadAPartirDePorcentajeQueRepresentaDeTotal
//                (listaComentariosOriginales.size(), 1);
//        int cantMaximaDeComentariosEnQueAparece = 60;
        int delTotalTieneTraduccion = 0;
        for(int i=0; i<listaPalabrasDiferentes.size(); i++){
            String tempPalabra = listaPalabrasDiferentes.get(i);
            String categoriaLexica = tablaPalabras_CategoriaLexica.get(tempPalabra);
            
            if(DiccionarioEspanolIngles.tieneTraduccionEspAIng(tempPalabra)){
                if((categoriaLexica.equals("ADJ")) ||
                        (categoriaLexica.equals("ADV")) ||
                        (categoriaLexica.equals("NP")) ||
                        (categoriaLexica.equals("NC")) ||
                        (categoriaLexica.endsWith("adj"))
                        ){
//                    if(contarEnCuantosComentariosAparecePalabra(tempPalabra) <= cantMaximaDeComentariosEnQueAparece){
                        tablaPalabrasRelevantes.put(tempPalabra, categoriaLexica);
//                    }
                }
                delTotalTieneTraduccion++;
            }
        }
        LOG.debug("Total Palabras: "+listaPalabrasDiferentes.size());
        LOG.debug("Del total tienen traduccion: "+delTotalTieneTraduccion+
                " ("+Matematicas.calcularPorcentajeQueRepresentaCantidadRespectoTotal(delTotalTieneTraduccion, 
                listaPalabrasDiferentes.size())+"%)");
        LOG.debug("Cant Palabras Relevantes en todos los comentarios: "+tablaPalabrasRelevantes.size());
    }
    
    private int contarEnCuantosComentariosAparecePalabra(String palabra){
        int cantidadApariciones = 0;
        for(int i=0; i<listaComentariosOriginales.size(); i++){
            Vector<String> listaPalabrasComentario = listaComentariosOriginales.elementAt(i).obtenerListaPalabrasEnComentario();
            for(int j=0; j<listaPalabrasComentario.size(); j++){
                String tempPalabra = listaPalabrasComentario.elementAt(j);
                if(tempPalabra.equals(palabra)){
                    cantidadApariciones++;
                    j = listaPalabrasComentario.size();
                }
            }
        }
        return cantidadApariciones;
    }
    
    private void generarListaPalabrasDiferentes(){
        for(int i=0; i<listaComentariosOriginales.size(); i++){
            Vector<String> listaPalabrasComentario = listaComentariosOriginales.elementAt(i).obtenerListaPalabrasEnComentario();
            for(int j=0; j<listaPalabrasComentario.size(); j++){
                String tempPalabra = listaPalabrasComentario.elementAt(j);
                if(!listaPalabrasDiferentes.contains(tempPalabra)){
                    listaPalabrasDiferentes.add(tempPalabra);
                }
            }
        }
    }
    
    public boolean esPalabraRelevante(String palabra){
        if(tablaPalabrasRelevantes.containsKey(palabra)){
            return true;
        }
        else{
            return false;
        }
    }
    
    public String obtenerCategoriaLexicaPalabraRelevante(String palabra){
        try{
            String categoLexica = tablaPalabrasRelevantes.get(palabra);
            return categoLexica;                    
        }
        catch(Exception e){
            LOG.warn("Palabra no relevante. "+e.getMessage());
            return null;
        }
    }          
}