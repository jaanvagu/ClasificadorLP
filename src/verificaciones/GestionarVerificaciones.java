/*
 * @author Jairo Andrés
 * Ultima modificacion: Octubre 20 de 2013
 */

package verificaciones;

import estructuras.ComentarioNormalizado;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class GestionarVerificaciones {
    
    private Vector<ComentarioNormalizado> listaComentariosNormalizado;
    public static Hashtable<Integer,String> tabla_IdComentario_Etiqueta;
    public static Hashtable<String,Integer> tabla_Nodo_IdComentario;
    
    public static int asignacionesCorrectas;
    public static int etiquetasSalidaLPCorrectas;

    public GestionarVerificaciones(Vector<ComentarioNormalizado> listaComentariosNormalizados){
        this.listaComentariosNormalizado = listaComentariosNormalizados;
        tabla_IdComentario_Etiqueta = new Hashtable<Integer, String>();
        tabla_Nodo_IdComentario = new Hashtable<String, Integer>();
        asignacionesCorrectas = 0;
        etiquetasSalidaLPCorrectas = 0;
        construirTablas();
    }
    
    private void construirTablas(){
        for(int i=0; i<listaComentariosNormalizado.size(); i++){
            tabla_IdComentario_Etiqueta.put(listaComentariosNormalizado.elementAt(i).obtenerIdComentario(),
                                            listaComentariosNormalizado.elementAt(i).obtenerEtiquetas().elementAt(0));
            tabla_Nodo_IdComentario.put( "N"+(i+1), listaComentariosNormalizado.elementAt(i).obtenerIdComentario() );
        }
    }
    
    public static void verificarSalidaLP(String lineaLeida){
        StringTokenizer stLineaLeida = new StringTokenizer(lineaLeida);
        StringBuilder sbEtiquetaLeida = new StringBuilder();
        String nodo_i = stLineaLeida.nextToken();
        while(true){
            String parte = stLineaLeida.nextToken();
            if(parte.startsWith("1")) {
                break;
            }
            sbEtiquetaLeida.append(parte).append(" ");
        }
        int idComentario = tabla_Nodo_IdComentario.get(nodo_i);
        System.out.println(tabla_IdComentario_Etiqueta.get(idComentario)+"|"+sbEtiquetaLeida.toString());
        if(tabla_IdComentario_Etiqueta.get(idComentario).equals(sbEtiquetaLeida.toString().trim())){
            etiquetasSalidaLPCorrectas++;
            System.out.println("Sumó");
        }
    }
}