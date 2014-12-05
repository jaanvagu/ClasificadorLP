/*
 * @author Jairo Andrés
 * Ultima modificacion: Abril 27 de 2013
 */

package extraccion_caracteristicas;

import entrada_salida.GestionarArchivos;
import estructuras.VectorFrecuenciasPalabras;
import java.util.*;
import main.Main;
import org.apache.log4j.Logger;

public class GestionarDistancias {
    
    private final static Logger LOG = Logger.getLogger(GestionarDistancias.class);
    
    private Vector<Double> listaDeDistanciasEntreComentarios = new Vector();
    private GestionarArchivos gestionArchivos;

    public void calcularDistanciaEuclideanaEntreCadaParDeComentarios(Vector<VectorFrecuenciasPalabras> vectorFrecuenciasComentarios){
        int N = vectorFrecuenciasComentarios.size();
        for(int i=0; i<(N-1); i++){
            for(int j=(i+1); j<N; j++){
                VectorFrecuenciasPalabras vectorA = vectorFrecuenciasComentarios.elementAt(i);
                VectorFrecuenciasPalabras vectorB = vectorFrecuenciasComentarios.elementAt(j);
                calcularDistanciaEuclideanaEntreDosComentarios(vectorA, vectorB);
            }
        }
    }    

    public double calcularDistanciaEuclideanaEntreDosComentarios(
            VectorFrecuenciasPalabras vectorFrecuenciasComentarioA, VectorFrecuenciasPalabras vectorFrecuenciasComentarioB){
        
        double distanciaEuclideana = 0;
        for(int i=0; i<vectorFrecuenciasComentarioA.tamanio(); i++){
            int punto_i_ComentarioA = vectorFrecuenciasComentarioA.elementoEn(i);
            int punto_i_ComentarioB = vectorFrecuenciasComentarioB.elementoEn(i);
            if(punto_i_ComentarioA!=0 || punto_i_ComentarioB!=0){
                double restaPuntos = Math.pow( ((punto_i_ComentarioA+0.0) - (punto_i_ComentarioB+0.0)),  2);
                distanciaEuclideana += restaPuntos;                    
            }
        }        
        distanciaEuclideana = Math.sqrt(distanciaEuclideana);    
        listaDeDistanciasEntreComentarios.addElement(distanciaEuclideana);
        return distanciaEuclideana;
    }

    public Vector<Double> obtenerListaDeDistanciasEntreComentarios(){
        return listaDeDistanciasEntreComentarios;
    }
    
    public void calcularDistanciaEuclideanaInvertidaEntreCadaParDeComentarios(Vector<VectorFrecuenciasPalabras> vectorFrecuenciasComentarios){
        int N = vectorFrecuenciasComentarios.size();    
        double umbralDistancias = obtenerUmbralDeDistancias();
        gestionArchivos = new GestionarArchivos();
        gestionArchivos.crearArchivoTexto("input_graph",Main.listaComentariosNormalizados.size());
        LOG.info("Calculando Distancia Euclideana Invertida entre cada par de Comentarios...");
        for(int i=0; i<(N-1); i++){
            for(int j=(i+1); j<N; j++){
                int x = i+1;
                int y = j+1;
                VectorFrecuenciasPalabras vectorA = vectorFrecuenciasComentarios.elementAt(i);
                VectorFrecuenciasPalabras vectorB = vectorFrecuenciasComentarios.elementAt(j);
                double distancia = calcularDistanciaEuclideanaInvertidaEntreDosComentarios(vectorA, vectorB, umbralDistancias);
                gestionArchivos.escribirLineaEnArchivoTexto("N"+x+"\t"+"N"+y+"\t"+distancia);
            }
        }
        gestionArchivos.cerrarArchivoTexto();
    }  

    //Método que convierte la distancia euclideana, en una función de similitud. Usa un umbral para transformar las distancias.
    // Umbral = U;  Distancia = D
    // Distancia Invertida = U - D
    public double calcularDistanciaEuclideanaInvertidaEntreDosComentarios(
            VectorFrecuenciasPalabras vectorFrecuenciasComentarioA, VectorFrecuenciasPalabras vectorFrecuenciasComentarioB, 
            double umbralDistancias){
        
        double distanciaEuclideanaInvertida = 0;
        for(int i=0; i<vectorFrecuenciasComentarioA.tamanio(); i++){
            int punto_i_ComentarioA = vectorFrecuenciasComentarioA.elementoEn(i);
            int punto_i_ComentarioB = vectorFrecuenciasComentarioB.elementoEn(i);
            if(punto_i_ComentarioA!=0 || punto_i_ComentarioB!=0){
                double restaPuntos = Math.pow( ((punto_i_ComentarioA+0.0) - (punto_i_ComentarioB+0.0)),  2);
                distanciaEuclideanaInvertida += restaPuntos;                    
            }
        }        
        distanciaEuclideanaInvertida = Math.sqrt(distanciaEuclideanaInvertida);
        distanciaEuclideanaInvertida = umbralDistancias - distanciaEuclideanaInvertida;
        if(distanciaEuclideanaInvertida==0 || distanciaEuclideanaInvertida==0.0){
            distanciaEuclideanaInvertida = 0.000000001;
        }
        
        return distanciaEuclideanaInvertida;
    }
    
    public double obtenerUmbralDeDistancias(){
        
        double numeroMayor = 0.0;
        for(int i=0; i<listaDeDistanciasEntreComentarios.size(); i++){            
            if(listaDeDistanciasEntreComentarios.elementAt(i)>numeroMayor){
                numeroMayor = listaDeDistanciasEntreComentarios.elementAt(i);            
            }
        }
        LOG.info("Umbral= "+numeroMayor);
        return numeroMayor;        
    }
    
    public void calcularSimilitudCosenoEntreCadaParDeComentarios(Vector<VectorFrecuenciasPalabras> vectorFrecuenciasComentarios){
        int N = vectorFrecuenciasComentarios.size();
        gestionArchivos = new GestionarArchivos();
        gestionArchivos.crearArchivoTexto("input_graph",Main.listaComentariosNormalizados.size());
        
        LOG.info("Calculando Similitud Coseno entre cada par de Comentarios...");
        for(int i=0; i<(N-1); i++){
            for(int j=(i+1); j<N; j++){
                int x = i+1;
                int y = j+1;
                VectorFrecuenciasPalabras vectorA = vectorFrecuenciasComentarios.elementAt(i);
                VectorFrecuenciasPalabras vectorB = vectorFrecuenciasComentarios.elementAt(j);
                double producto_A_B = productoEscalarVectorial(vectorA, vectorB);
                if(producto_A_B == 0 || producto_A_B == 0.0){
                    producto_A_B = 0.01;
                }
                double cardinalidad_A = cardinalidadVectorial(vectorA);
                double cardinalidad_B = cardinalidadVectorial(vectorB);
                double similitud = (producto_A_B) / (cardinalidad_A*cardinalidad_B);                
                gestionArchivos.escribirLineaEnArchivoTexto("N"+x+"\t"+"N"+y+"\t"+similitud);
            }
            if(i==1){
                System.out.print("\n"+i+", ");
            }
            else if(i%100 == 0){
                System.out.print(i+", ");
            }
        }
        System.out.println();
        gestionArchivos.cerrarArchivoTexto();
        LOG.info("Similitudes calculadas");
    } 
    
    public double productoEscalarVectorial(VectorFrecuenciasPalabras vectorA, VectorFrecuenciasPalabras vectorB){               
        double productoEscalar = 0;
        for (int i=0; i<vectorA.tamanio(); i++){
            double multiplicacionAiBi = (vectorA.elementoEn(i)+0.0) * (vectorB.elementoEn(i)+0.0);           
            productoEscalar += multiplicacionAiBi;
        }        
        return productoEscalar;
    }
    
    public double cardinalidadVectorial(VectorFrecuenciasPalabras vector){               
        double cardinalidad = 0;
        for (int i=0; i<vector.tamanio(); i++){
            double elemento_i_Cuadrado = Math.pow(vector.elementoEn(i)+0.0, 2);
            cardinalidad += elemento_i_Cuadrado;
        }
        cardinalidad = Math.sqrt(cardinalidad);
        return cardinalidad;
    } 
}
