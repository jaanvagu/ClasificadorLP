package utiles;


import entrada_salida.GestionarArchivos;
import estructuras.ComentarioNormalizado;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import preprocesamiento.Preprocesamiento;

import java.util.*;

public class Util {

    private static GestionarArchivos gestionarArchivos = new GestionarArchivos();

    public static void escribirComentariosEnJson(Vector<ComentarioNormalizado> listaComentariosNormalizados){

        Hashtable<String,ArrayList<String>> tabla_etiqueta_mensajes = new Hashtable<String, ArrayList<String>>();

        JSONObject jsonObject = new JSONObject();


        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);

            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);
            if(etiqueta.equals("trabajo") || etiqueta.equals("liderazgo")){
                break;
            }
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

        gestionarArchivos.crearArchivoJson("Comentarios_prueba_7000");
        gestionarArchivos.escribirLineaEnArchivoTexto(jsonObject.toJSONString());
        gestionarArchivos.cerrarArchivoTexto();

    }

    public static void generarJsonEntrenamiento(Vector<ComentarioNormalizado> listaComentariosNormalizados){

        Hashtable<String,ArrayList<String>> tabla_etiqueta_mensajes = new Hashtable<String, ArrayList<String>>();

        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);

            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);
            if(etiqueta.equals("trabajo")){
                break;
            }
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
            for(int j=0; j<mensajes.size(); j++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", mensajes.get(j));
                jsonArray.add(jsonObject);
            }
            gestionarArchivos.crearArchivoJson(etiqueta);
            gestionarArchivos.escribirLineaEnArchivoTexto(jsonArray.toJSONString());
            gestionarArchivos.cerrarArchivoTexto();
        }
    }


    public static void cantidadDeComentariosPorEtiqueta(Vector<ComentarioNormalizado> listaComentariosNormalizados){
        Hashtable<String,Integer> tabla_etiqueta_cantidad = new Hashtable<String, Integer>();

        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);
            if(tabla_etiqueta_cantidad.containsKey(comentario.obtenerEtiquetas().elementAt(0))){
                int temp = tabla_etiqueta_cantidad.get(comentario.obtenerEtiquetas().elementAt(0));
                tabla_etiqueta_cantidad.put(Preprocesamiento.quitarAcentos(comentario.obtenerEtiquetas().elementAt(0)),
                        ++temp);
            }
            else{
                tabla_etiqueta_cantidad.put(Preprocesamiento.quitarAcentos(comentario.obtenerEtiquetas().elementAt(0)),
                        1);
            }
        }

        Enumeration<String> etisEnum = tabla_etiqueta_cantidad.keys();
        Map<Integer, String> map = new TreeMap<Integer, String>(
                //Se define nuevo comparador para que ordene descendentemente
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer integer, Integer integer2) {
                        return integer2.compareTo(integer);

                    }
                }
        );
        while(etisEnum.hasMoreElements()){
            String eti = etisEnum.nextElement();
            map.put(tabla_etiqueta_cantidad.get(eti), eti);
        }

        System.out.println(tabla_etiqueta_cantidad.size()+"\n"+map.size()+"\n"+map);
    }

}
