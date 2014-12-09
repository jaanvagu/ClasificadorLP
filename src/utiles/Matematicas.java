/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 1 de 2013
 */

package utiles;

import java.util.Vector;

public class Matematicas {
    
    public static int calcularCantidadAPartirDePorcentajeQueRepresentaDeTotal(int numeroTotal, int porcentaje){
         int numeroCaculado;
         double dNumero = numeroTotal+0.0;
         double dPorcentaje = porcentaje+0.0;
         double dPorcentajeCalculado = dNumero*(dPorcentaje/100);
         numeroCaculado = aproximarAEntero(dPorcentajeCalculado);
         return numeroCaculado;
    }

    public static int calcularPorcentajeQueRepresentaCantidadRespectoTotal(int cantidad, int numeroTotal){
         int porcentajeCalculado;
         double dNumeroTotal = numeroTotal+0.0;
         double dCantidad = cantidad+0.0;
         double dPorcentajeCalculado = (dCantidad/dNumeroTotal)*100;
         porcentajeCalculado = aproximarAEntero(dPorcentajeCalculado);
         return porcentajeCalculado;
    }
    
    public static int aproximarAEntero(double numero){
        numero = Math.rint(numero);
        int iNumero = (int) numero;
        return iNumero;
    }
    
    public static double calcularPromedioListaEnteros(Vector<Integer> listaEnteros){
        double sumatoria = 0.0;
        double cantElementos = listaEnteros.size() + 0.0;
        for(int i=0; i<listaEnteros.size(); i++){
            int entero_i = listaEnteros.elementAt(i);
            sumatoria += entero_i + 0.0;
        }
        double promedio = sumatoria/cantElementos;
        return promedio;
    }
}