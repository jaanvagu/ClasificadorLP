/*
 * @author Jairo Andrés
 * Ultima modificacion: Abril 25 de 2013
 */

package estructuras;

import java.util.*;

public class VectorFrecuenciasPalabras {

    private Vector<Integer> vectorFrecuenciasPalabras;

    public VectorFrecuenciasPalabras(int tamanioVectorPalabras){
        vectorFrecuenciasPalabras = new Vector(tamanioVectorPalabras);
        for(int i=0; i<tamanioVectorPalabras; i++){
            vectorFrecuenciasPalabras.addElement(0);        
        }
    }

    public void aumentarFrecuenciaEnPosicion(int posicion, int cantidad){
        int frecuenciaActual = vectorFrecuenciasPalabras.elementAt(posicion);        
        int frecuenciaNueva = frecuenciaActual + cantidad;
        vectorFrecuenciasPalabras.setElementAt(frecuenciaNueva, posicion);
    }

    public void aumentarFrecuenciaEnPosicionDeComentarioVacio(){
        int frecuenciaActual = vectorFrecuenciasPalabras.elementAt(vectorFrecuenciasPalabras.size()-1);
        int frecuenciaNueva = ++frecuenciaActual;
        vectorFrecuenciasPalabras.setElementAt(frecuenciaNueva, vectorFrecuenciasPalabras.size()-1);
    }

    public int elementoEn(int i){
        return vectorFrecuenciasPalabras.elementAt(i);
    }

    public int tamanio(){
        return vectorFrecuenciasPalabras.size();
    }

    public Vector<Integer> obtenerVectorFrecuenciasPalabras(){
        return vectorFrecuenciasPalabras;
    }
}
