package utiles;


import entrada_salida.GestionarArchivos;
import estructuras.ComentarioNormalizado;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openrdf.query.algebra.Str;
import preprocesamiento.Lematizar;
import preprocesamiento.Preprocesamiento;

import java.io.StringReader;
import java.util.*;

public class Util {

    private final static Logger LOG = Logger.getLogger(Util.class);

    private static GestionarArchivos gestionarArchivos = new GestionarArchivos();

    public static void escribirComentariosEnJson(Vector<ComentarioNormalizado> listaComentariosNormalizados){

        Hashtable<String,ArrayList<String>> tabla_etiqueta_mensajes = new Hashtable<String, ArrayList<String>>();

        JSONObject jsonObject = new JSONObject();


        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);

            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);
            if(etiqueta.equals("negaivo")){
                etiqueta = "negativo";
            }
            if(etiqueta.equals("nwg")){
                etiqueta = "negativo";
            }
            if(etiqueta.equals("posivo")){
                etiqueta = "positivo";
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

        gestionarArchivos.crearArchivoJson(GestionarArchivos.nombreArchivo + "_adj_sent");
        gestionarArchivos.escribirLineaEnArchivoTexto(jsonObject.toJSONString());
        gestionarArchivos.cerrarArchivoTexto();

    }

    public static void generarJsonAdjSentimiento(Vector<ComentarioNormalizado> listaComentariosNormalizados){

        Hashtable<String, Set<String>> tabla_etiqueta_adjetivos = new Hashtable<String, Set<String>>();

        JSONObject jsonObject = new JSONObject();

        Lematizar lematizar = new Lematizar();
        Hashtable<String, String> tabla_formas_semanticas = lematizar.generarTablaPalabras_CategoriaLexica(listaComentariosNormalizados);

        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);

            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);
            if(etiqueta.equals("negaivo")){
                etiqueta = "negativo";
            }
            if(etiqueta.equals("nwg")){
                etiqueta = "negativo";
            }
            if(etiqueta.equals("posivo")){
                etiqueta = "positivo";
            }

            Set<String> adjetivos = tabla_etiqueta_adjetivos.get(etiqueta);
            if(adjetivos == null){
                adjetivos = new HashSet<String>();
            }
            for(int j=0; j<comentario.obtenerListaPalabrasEnComentario().size(); j++){
                String palabra = comentario.obtenerListaPalabrasEnComentario().elementAt(j);
                if(esAdjetivo(tabla_formas_semanticas, palabra)){
                    adjetivos.add(palabra);
                }
            }
            tabla_etiqueta_adjetivos.put(etiqueta, adjetivos);

        }

        Enumeration<String> enumEtiquetas = tabla_etiqueta_adjetivos.keys();
        while (enumEtiquetas.hasMoreElements()){
            JSONArray jsonArray = new JSONArray();
            String etiqueta = enumEtiquetas.nextElement();
            Set<String> adejtivos = tabla_etiqueta_adjetivos.get(etiqueta);
            jsonArray.addAll(adejtivos);
            jsonObject.put(etiqueta, jsonArray);
        }

        gestionarArchivos.crearArchivoJson(GestionarArchivos.nombreArchivo + "_adj_sent");
        gestionarArchivos.escribirLineaEnArchivoTexto(jsonObject.toJSONString());
        gestionarArchivos.cerrarArchivoTexto();

    }

    public static void generarJsonEntrenamiento(Vector<ComentarioNormalizado> listaComentariosNormalizados){

        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> mensajes = new ArrayList<String>();
        ArrayList<Integer> sentimientoLista = new ArrayList<Integer>(); //0: Negativo  | 1: Positivo | 2: Neutral

        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);
            int id = comentario.obtenerIdComentario();
            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);
            int sentimiento = 5;
            if(etiqueta.equals("negativo"))
                sentimiento = 0;
            else if(etiqueta.equals("positivo"))
                sentimiento = 1;
            else if(etiqueta.equals("neutro"))
                sentimiento = 2;
            String mensajeTemp = "";
            for(int j=0; j<comentario.obtenerListaPalabrasEnComentario().size(); j++){
                mensajeTemp += " "+comentario.obtenerListaPalabrasEnComentario().elementAt(j);
            }
            ids.add(id);
            mensajes.add(mensajeTemp.trim());
            sentimientoLista.add(sentimiento);
        }

        JSONObject jsonObject = new JSONObject();
        JSONArray idListJson = new JSONArray();
        JSONArray mensajesListJson = new JSONArray();
        JSONArray sentimientoListJson = new JSONArray();

        idListJson.addAll(ids);
        mensajesListJson.addAll(mensajes);
        sentimientoListJson.addAll(sentimientoLista);

        jsonObject.put("id", idListJson);
        jsonObject.put("review", mensajesListJson);
        jsonObject.put("sentiment", sentimientoListJson);

        gestionarArchivos.crearArchivoJson("json_train_no_stemmer");
        gestionarArchivos.escribirLineaEnArchivoTexto(jsonObject.toJSONString());
        gestionarArchivos.cerrarArchivoTexto();

        LOG.info("Json de entrenamiento generado");
    }

    public static void cantidadDeComentariosPorEtiqueta(Vector<ComentarioNormalizado> listaComentariosNormalizados){
        Hashtable<String,Integer> tabla_etiqueta_cantidad = new Hashtable<String, Integer>();

        int cont = 0;

        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);
            String etiqueta = comentario.obtenerEtiquetas().elementAt(0);

            if(etiqueta.equals("negaivo")){
                etiqueta = "negativo";
            }
            if(etiqueta.equals("nwg")){
                etiqueta = "negativo";
            }
            if(etiqueta.equals("posivo")){
                etiqueta = "positivo";
            }

            if(tabla_etiqueta_cantidad.containsKey(etiqueta)){
                cont++;
                int temp = tabla_etiqueta_cantidad.get(etiqueta);
                tabla_etiqueta_cantidad.put(Preprocesamiento.quitarAcentos(etiqueta),++temp);
            }
            else{
                cont++;
                tabla_etiqueta_cantidad.put(Preprocesamiento.quitarAcentos(etiqueta), 1);
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

        System.out.println(tabla_etiqueta_cantidad);
        System.out.println(cont);
    }

    public static Vector<ComentarioNormalizado> mezclarListasComentarios(ArrayList<Vector <ComentarioNormalizado>> listas){
        Vector<ComentarioNormalizado> listaResultante = new Vector<ComentarioNormalizado>();

        for (int i=0; i<listas.size(); i++){
            listaResultante.addAll(listas.get(i));
        }

        return listaResultante;
    }

    public static boolean esAdjetivo(Hashtable<String, String> tabla_formas_semanticas, String palabra){
        if (tabla_formas_semanticas.containsKey(palabra.trim().toLowerCase())){
            if (tabla_formas_semanticas.get(palabra).equals("ADJ")){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

}
