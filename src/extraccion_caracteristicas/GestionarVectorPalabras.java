/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 6 de 2013
 */

package extraccion_caracteristicas;

import estructuras.ComentarioNormalizado;
import estructuras.VectorFrecuenciasPalabras;
import java.util.*;
import org.apache.log4j.Logger;
import utiles.Matematicas;

public class GestionarVectorPalabras {
    
    private final static Logger LOG = Logger.getLogger(GestionarVectorPalabras.class);

    private Vector<ComentarioNormalizado> listaComentariosNormalizados;

    private Vector<String> vectorPalabras;
    private Vector<VectorFrecuenciasPalabras> listaVectoresDeFrecuencias;
    
    //Constructor que recibe como parametro la lista de comentarios normalizados.
    public GestionarVectorPalabras(Vector<ComentarioNormalizado> listaComentariosNormalizados){
        vectorPalabras = new Vector();
        listaVectoresDeFrecuencias = new Vector();
        this.listaComentariosNormalizados = new Vector(listaComentariosNormalizados);
    }

    //Contruye un vector de palabras, con todas las palabras diferentes contenidas en una lista de comentarios.
    //La ultima posicion contiene la frecuencia de los comentarios vacios (despues de realizartles preprocesamiento quedaron vacios).
    public void contruirVectorDePalabras(){
        for(int i=0; i<listaComentariosNormalizados.size(); i++){
            for(int j=0; j<listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario().size(); j++){
                String palabra =  listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario().elementAt(j);
                if(!palabraEstaGuardada(palabra)){
                    vectorPalabras.addElement(palabra);
                }
            }            
        }
        vectorPalabras.addElement("ComentarioVacio");        
        LOG.info("Vector de palabras creado");                
    }

    //Crea un vector de frecuencias de palabras para cada uno de los comentarios de la lista de comentarios normalizados
    //de acuerdo al vector de palabras. En otras palabras, se cuentan las ocurrencias de las palabras en cada comentario.
    //Ejemplo:
    //Comentario 1 : [palabra1, palabra2]
    //Comentario 2 : [palabra3, palabra4]
    //Comentario 3 : [palabra1, palabra3]
    //Vector de palabras:                 [palabra1, palabra2, palabra3, palabra4]
    //Vector de frecuencias Comentario 1: [   1    ,    1    ,    0    ,    0    ]
    //Vector de frecuencias Comentario 2: [   0    ,    0    ,    1    ,    1    ]
    //Vector de frecuencias Comentario 3: [   1    ,    0    ,    1    ,    0    ]
    public void generarVectoresDeFrecuenciasDePalabras(){
        int dbpedia = 0, wordnet = 0;
        for(int i=0; i<listaComentariosNormalizados.size(); i++){
            VectorFrecuenciasPalabras vectorFrecuenciasPalabras = new VectorFrecuenciasPalabras(vectorPalabras.size());
            for(int j=0; j<listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario().size(); j++){
                String palabra =  listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario().elementAt(j);
                int posicionPalabra = vectorPalabras.indexOf(palabra);
                if(palabra.startsWith("@")){
                    wordnet++;
                    vectorFrecuenciasPalabras.aumentarFrecuenciaEnPosicion(posicionPalabra,1);
                }
                else if(palabra.startsWith("#")){
                    dbpedia++;
                    vectorFrecuenciasPalabras.aumentarFrecuenciaEnPosicion(posicionPalabra,1);
                }
                else{
                    vectorFrecuenciasPalabras.aumentarFrecuenciaEnPosicion(posicionPalabra,1);
                }                
            }
            if(esComentarioVacio(listaComentariosNormalizados.elementAt(i))){
                //System.out.println("\n*************** ENTRO A VACÍO ****************\n");
                vectorFrecuenciasPalabras.aumentarFrecuenciaEnPosicionDeComentarioVacio();
            }
            
            //Ver correspondencia NODO-->ID_Comentario. Ejemplo:   N1-->320
//            System.out.println("N"+(i+1) + " --> " + listaComentariosNormalizados.elementAt(i).obtenerIdComentario());

            listaVectoresDeFrecuencias.add(vectorFrecuenciasPalabras);
        }
        LOG.info("DB: "+dbpedia+"\tWN: "+wordnet);
        LOG.info("Frecuencias generadas");
    }

    //Verifica si la plabra ya ha sido almacenada en el vector de palabras.
    public boolean palabraEstaGuardada(String palabra){
        if(vectorPalabras.contains(palabra)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean esComentarioVacio(ComentarioNormalizado comentario){
        if(comentario.obtenerListaPalabrasEnComentario().isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }

    public Vector<ComentarioNormalizado> obtenerListaComentariosNormalizados(){
        return listaComentariosNormalizados;
    }

    public Vector<String> obtenerVectorPalabras(){
        return vectorPalabras;
    }

    public Vector<VectorFrecuenciasPalabras> obtenerListaVectoresDeFrecuencias(){
//        Vector<Integer> frecuencias = new Vector();
//        for(int i=0;i<listaVectoresDeFrecuencias.size();i++){
//            int fre = Collections.max(listaVectoresDeFrecuencias.elementAt(i).obtenerVectorFrecuenciasPalabras());
//            frecuencias.addElement(fre);
//            if(fre>3){
//                Vector<String> palabras = listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario();
//                Vector<Integer> frecPalabras = listaVectoresDeFrecuencias.elementAt(i).obtenerVectorFrecuenciasPalabras();
////                System.out.println("["+fre+"]"+" "+palabras);
//                for(int j=0; j<frecPalabras.size(); j++){
//                    int fre_i = frecPalabras.elementAt(j);
//                    if(fre_i>3){
//                        System.out.println("|"+vectorPalabras.elementAt(j)+"|: "+fre_i);
//                    }
//                }
//            }
//        }
//        Collections.sort(frecuencias);
//        for(int i=0; i<frecuencias.size(); i++){
//            int fre = frecuencias.elementAt(i);
//            if(fre>3){
//                System.out.print(fre+", ");
//            }
//        }
//        System.out.println("\n"+Matematicas.calcularPromedioListaEnteros(frecuencias));
        return listaVectoresDeFrecuencias;
    }
}