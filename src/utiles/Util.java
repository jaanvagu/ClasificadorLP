package utiles;


import estructuras.ComentarioNormalizado;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import preprocesamiento.Preprocesamiento;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Util {

    public static void escribirComentariosEnJson(Vector<ComentarioNormalizado> listaComentariosNormalizados){

        Hashtable<String,ArrayList<String>> tabla_etiqueta_mensajes = new Hashtable<String, ArrayList<String>>();

        JSONObject jsonObject = new JSONObject();


        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);

            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);
            ArrayList<String> mensajes = tabla_etiqueta_mensajes.get(etiqueta);
            if(mensajes == null){
                mensajes = new ArrayList<String>();
            }
            String mensajeTemp = "";
            for(int j=0; j<comentario.obtenerListaPalabrasEnComentario().size(); j++){
                mensajeTemp += " "+comentario.obtenerListaPalabrasEnComentario().elementAt(j);
            }
            mensajes.add(mensajeTemp.trim());
            tabla_etiqueta_mensajes.put(etiqueta, mensajes);

        }

        Enumeration<String> enumEtiquetas = tabla_etiqueta_mensajes.keys();
        while (enumEtiquetas.hasMoreElements()){
            JSONArray jsonArray = new JSONArray();
            String etiqueta = enumEtiquetas.nextElement();
            ArrayList<String> mensajes = tabla_etiqueta_mensajes.get(etiqueta);
            jsonArray.addAll(mensajes);
            jsonObject.put(etiqueta, jsonArray);
        }

        System.out.println("\n"+jsonObject);
    }
}
