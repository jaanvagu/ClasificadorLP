/*
 * @author Jairo Andrés
 * Ultima modificacion: Julio 4 de 2013
 */

package estructuras;

import java.util.Vector;

public class Comentario {

    private int idComentario;
    private String autor, mensaje, fuente;
    private Vector<String> etiquetas;

    public Comentario(int idComentario, String autor, String mensaje, String fuente, Vector<String> etiquetas){
        this.idComentario = idComentario;
        this.autor = autor;
        this.mensaje = mensaje;
        this.fuente = fuente; //No es posible leerla, no siempre la tiene.
        this.etiquetas = etiquetas;
    }

    public Comentario(int idComentario, String autor, String mensaje, String fuente){
        this.idComentario = idComentario;
        this.autor = autor;
        this.mensaje = mensaje;
        this.fuente = fuente;
    }
    
    public int obtenerIdComentario(){
        return idComentario;
    }

    public String obtenerAutor(){
        return autor;
    }

    public String obtenerMensaje(){
        return mensaje;
    }

    public String obtenerFuente(){
        return fuente;
    }

    public Vector<String> obtenerEtiquetas(){
        return etiquetas;
    }

    //Método para convertir los atributos de un objeto comentario a String (para imprimirlos).
    public String aString(){
        String comentario = "";
        String lineaInicialYFinal = "\n------------------\n";
        String listaEtiquetas = "Etiquetas: [ ";
        if(!this.etiquetas.isEmpty()){
            for(int i=0; i<(this.etiquetas.size()-1);i++){
                listaEtiquetas += this.etiquetas.elementAt(i)+", ";
            }
            listaEtiquetas += this.etiquetas.elementAt(this.etiquetas.size()-1)+" ]";
        }
        else{
            listaEtiquetas += "SIN ETIQUETAS ]";            
        }

        comentario += lineaInicialYFinal+"["+this.idComentario+"]  | "+this.autor+" | "+this.mensaje+" | "+this.fuente+"\n"
                                        +listaEtiquetas+"\n"+lineaInicialYFinal;
        return comentario;
    }
}
