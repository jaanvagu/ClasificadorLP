/*
 * @author Jairo Andrés
 * Ultima modificacion: Julio 4 de 2013
 */

package main;

import LP.GestionarLabelPropagation;
import LP.GestionarSemillasLP;
import entrada_salida.EscribirExcelSalida;
import entrada_salida.GestionarArchivos;
import entrada_salida.LeerArchivoCSV;
import entrada_salida.LeerArchivoSalidaLP;
import estructuras.Comentario;
import estructuras.ComentarioNormalizado;
import evaluacion.GestionarIndicadores;
import extraccion_caracteristicas.GestionarDistancias;
import extraccion_caracteristicas.GestionarVectorPalabras;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openrdf.sail.rdbms.schema.HashTable;
import preprocesamiento.Lematizar;
import preprocesamiento.Preprocesamiento;
import svm.SVM;
import utiles.ArchivoConfiguracionLP;
import utiles.ArchivoConfiguracionLog4j;
import utiles.Constantes;
import utiles.DistribuirDatos;
import utiles.ExtenderComentarios;
import utiles.Fecha;
import utiles.SonidoBuffer;
import verificaciones.GestionarVerificaciones;

public class Main {
    static SonidoBuffer sonido = new SonidoBuffer(2);
    
    private final static Logger LOG = Logger.getLogger(Main.class);

    private static GestionarArchivos gestionArchivos;
    private static LeerArchivoCSV leerArchivoCSV;
    private static Preprocesamiento preprocesamiento;
    private static Lematizar lematizar;
    private static ExtenderComentarios extenderComentarios;
    private static DistribuirDatos distribuir;
    private static GestionarVectorPalabras gestionVectorPalabras;
    private static GestionarDistancias gestionDistancias;
    private static GestionarLabelPropagation gestionLabelPropagation;
    private static GestionarSemillasLP gestionSemillasLP;
    private static LeerArchivoSalidaLP leerArchivoSalidaLP;
    private static GestionarIndicadores gestionIndicadores;
    private static SVM SVM;
    private static GestionarVerificaciones gestionarVerificaciones;
    private static EscribirExcelSalida escribirExcelSalida;

    //Variable que almacena los comentarios de la siguiente manera:
    //  Lista de Comentarios Normalizados = [ComentarioNormalizado(1), ComentarioNormalizado(2), ..., ComentarioNormalizado(N)  ]
    //  Donde ComentarioNormalizado(i):
    //      Lista de palabras del comentario = [palabra1, palabra2, ..., palabraN]
    //      Etiquetas = [etiqueta1, etiqueta2, ..., etiquetaN]
    public static Vector<ComentarioNormalizado> listaComentariosNormalizados;

    //Método que construye la lista de comentarios normalizados, a partir de los comentarios preprocesados y lematizados,
    //y las etiquetas relacionadas a cada comentario. 
    private static void normalizarComentarios(Vector<String> listaComentariosPreprocesadosYLematizados,
                                              Vector<Comentario> listaComentariosOriginal){
        listaComentariosNormalizados = new Vector();
        for(int i=0; i<listaComentariosOriginal.size(); i++){
            ComentarioNormalizado comentarioNormalizadoActual;
            comentarioNormalizadoActual = new ComentarioNormalizado(listaComentariosOriginal.elementAt(i).obtenerIdComentario(),
                                                                    listaComentariosPreprocesadosYLematizados.elementAt(i),
                                                                    listaComentariosOriginal.elementAt(i).obtenerEtiquetas());
            listaComentariosNormalizados.addElement(comentarioNormalizadoActual);
        }
        gestionArchivos = new GestionarArchivos();
//        gestionArchivos.guardarComentariosNormalizados(listaComentariosNormalizados,"Normalizado");
        LOG.info("Normalizacion terminada");
    }

    private static void distribuir_EjecucionSecuencial(){
        String[] nombresArchivos = {"Balance","Coca Cola","Dog Chow","Don Julio","Kotex","Nike","Sony","Top Terra"};
//        int[] cantDatos =          {   1000,      1000,       1000,      600,      1000,  1000,  1000,    1000};
        int[] cantDatos =          {   400,      400,       400,      352,      400,  400,  400,    400};
//        int[] cantDatos =          {   400,      400,       400,       400,        400,   400,   400,     400};
        for(int i=0; i<nombresArchivos.length; i++){
            gestionArchivos = new GestionarArchivos();
            listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Utiles Normalizados Sin_Eti_Ruido/",
                    "Consolidado "+nombresArchivos[i] + " - Utiles Normalizados Sin_Eti_Ruido" + " " + Constantes.version2_0, false);
            distribuir = new DistribuirDatos(listaComentariosNormalizados);
//            distribuir.generarListaSoloComentarioUtiles();
            listaComentariosNormalizados = distribuir.generarDistribucionUniformeNDatos(cantDatos[i]);
//            listaComentariosNormalizados = distribuir.generarDistribucionUniformeNDatos(cantDatos[i],10);
//            listaComentariosNormalizados = distribuir.eliminarComentariosConPalabrasRepetidasEnExceso();
//            distribuir.eliminarComentariosConEtiquetasBajaPrecision();
        }
    }
    
    private static void extender_EjecucionSecuencial(){
        String[] nombresArchivos = {"Balance","Coca Cola","Dog Chow","Don Julio","Kotex","Nike","Sony","Top Terra"};
        String[] cantDatos = {"400","400","400","352","400","400","400","400"};
        for(int i=0; i<nombresArchivos.length; i++){//nombresArchivos.length
            gestionArchivos = new GestionarArchivos();
            listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Distribuidos Uniformemente/",
                    "Consolidado "+nombresArchivos[i] + " - DistribuidosUniforme "+cantDatos[i]+" "+Constantes.version2_0, false);
            
            extenderComentarios = new ExtenderComentarios(listaComentariosNormalizados);
            extenderComentarios.extender(true, true);
        }
    }
    
    private static void configParaLP_EjecucionSecuencial(){
        String[] nombresArchivos = {"Balance","Coca Cola","Dog Chow","Don Julio","Kotex","Nike","Sony","Top Terra"};
//        String[] cantDatos = {      "1000",      "1000",   "1000",      "600",   "1000", "1000","1000",   "1000"};
        String[] cantDatos = {"400","400","400","352","400","400","400","400"};
        for(int i=0; i<nombresArchivos.length; i++){
            gestionArchivos = new GestionarArchivos();
            listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("",
                    "Consolidado "+nombresArchivos[i] + " - Extendidos DBPyWN "+cantDatos[i]+" "+Constantes.version2_0, false);
            
            gestionVectorPalabras = new GestionarVectorPalabras(listaComentariosNormalizados);
            gestionVectorPalabras.contruirVectorDePalabras();
            gestionVectorPalabras.generarVectoresDeFrecuenciasDePalabras();
            
            gestionDistancias = new GestionarDistancias();
            gestionDistancias.calcularSimilitudCosenoEntreCadaParDeComentarios(gestionVectorPalabras.obtenerListaVectoresDeFrecuencias());
            gestionSemillasLP = new GestionarSemillasLP(listaComentariosNormalizados);
            gestionSemillasLP.generarArchivoSemillas(20);
            gestionSemillasLP.generarArchivoSolucion();
            ArchivoConfiguracionLP.generarArchivo(listaComentariosNormalizados.size());
        }
    }
    
    private static void indicadoresLP_EjecucionSecuencial(){
        String[] nombresArchivos = {"Balance","Coca Cola","Dog Chow","Don Julio","Kotex","Nike","Sony","Top Terra"};
//        String[] cantDatos = {       "1000",  "1000",       "1000",    "600",     "1000","1000","1000", "1000"};
        String[] cantDatos =       {"400","400","400","352","400","400","400","400"};
        for(int i=0; i<nombresArchivos.length; i++){
            gestionArchivos = new GestionarArchivos();
            listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("",
                    "Consolidado "+nombresArchivos[i] + " - Extendidos DBPyWN "+cantDatos[i]+" "+Constantes.version2_0, false);
            
            leerArchivoSalidaLP = new LeerArchivoSalidaLP(false);
            gestionIndicadores = new GestionarIndicadores(leerArchivoSalidaLP.obtenerListaEtiquetasCorrespondientes(), 
                    leerArchivoSalidaLP.obtenerListaEtiquetasPropagadasLP());
            gestionIndicadores.calcularIndicadores("LP");
            gestionIndicadores.imprimirIndicadoresParaGrafica();
        }
    }
    
    private static void SVM_EjecucionSecuencial(){
        String[] nombresArchivos = {"Balance","Coca Cola","Dog Chow","Don Julio","Kotex","Nike","Sony","Top Terra"};
//        String[] cantDatos = {       "1000",  "1000",       "1000",    "600",     "1000","1000","1000", "1000"};
        String[] cantDatos =       {"400","400","400","352","400","400","400","400"};
        for(int i=0; i<nombresArchivos.length; i++){
            gestionArchivos = new GestionarArchivos();
            listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("",
//                    "Consolidado "+nombresArchivos[i] + " - DistribuidosUniforme "+cantDatos[i]+" "+Constantes.version2_0, false);
                    "Consolidado "+nombresArchivos[i] + " - Extendidos DBPyWN "+cantDatos[i]+" "+Constantes.version2_0, false);
            
            gestionVectorPalabras = new GestionarVectorPalabras(listaComentariosNormalizados);
            gestionVectorPalabras.contruirVectorDePalabras();
            gestionVectorPalabras.generarVectoresDeFrecuenciasDePalabras();
            
            SVM = new SVM(listaComentariosNormalizados, gestionVectorPalabras.obtenerListaVectoresDeFrecuencias());
            SVM.ejecutarSVM(20);
            
            gestionIndicadores = new GestionarIndicadores(SVM.getTabla_Etiqueta_AparicionesTotalesEstimadas(),SVM.getTabla_Etiqueta_AparicionesCorrectas(), SVM.getTabla_Etiqueta_AparicionesTotalesCorrespondientes());
            gestionIndicadores.calcularIndicadores("SVM");
            gestionIndicadores.imprimirIndicadoresParaGrafica();
        }
    }
    
    public static void main(String[] args) {
        
        ArchivoConfiguracionLog4j.generarArchivo(); // Archivo de configuración para generar el Log de ejecución
        PropertyConfigurator.configure("log4j.properties");

        // ********* LECTURA Y PREPROCESAMIENTO *********
        leerArchivoCSV = new LeerArchivoCSV();
        leerArchivoCSV.leerYAlmacenarLineasCSV();
        preprocesamiento = new Preprocesamiento(leerArchivoCSV.obtenerListaComentariosLeidos());
//        lematizar = new Lematizar(preprocesamiento.obtenerMensajesProcesados());
//        normalizarComentarios(lematizar.obtenerListaMensajesLematizados(), leerArchivoCSV.obtenerListaComentariosLeidos());
        normalizarComentarios(preprocesamiento.obtenerMensajesProcesados(), leerArchivoCSV.obtenerListaComentariosLeidos());

        // ********* CARGAR COMENTARIOS NORMALIZADOS *********
//        gestionArchivos = new GestionarArchivos();
//        gestionArchivos.limpiaCarpetaArchivosPrueba(400);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Extendidos WN Distribuidos Uniforme 10_Etiquetas", "", true);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Distribuidos Uniformemente", "", true);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Utiles Normalizados Sin_Eti_Ruido", "", true);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("","", true);
        
        // ********* EXTENSIÓN DE COMENTARIOS *********
//        extenderComentarios = new ExtenderComentarios(listaComentariosNormalizados);
//        listaComentariosNormalizados = new Vector(extenderComentarios.extender(false, true));

        // ********* DISTRIBUCIÓN DE DATOS *********
        distribuir = new DistribuirDatos(listaComentariosNormalizados);
//        distribuir.eliminarComentariosConEtiquetasRuido();
//        distribuir.eliminarComentariosConEtiquetasBajaPrecision();
//        listaComentariosNormalizados = distribuir.generarDistribucionUniformeNDatos(400,7);
        listaComentariosNormalizados = distribuir.generarDistribucionUniformeNDatos(400);
//        distribuir.generarDistribucionProporcionalNDatos(100);
//        listaComentariosNormalizados = new Vector(distribuir.eliminarComentariosConPalabrasRepetidasEnExceso());
//        distribuir.generarListaSoloComentarioUtiles();
        
        // ********* IMPRIMIR TEXTO DE LOS COMENTARIOS *********

        Vector<Comentario> comentarios = leerArchivoCSV.obtenerListaComentariosLeidos();
        Hashtable<String,Integer> tabla_etiqueta_cantidad = new Hashtable<String, Integer>();
        Hashtable<String,ArrayList<String>> tabla_etiqueta_mensajes = new Hashtable<String, ArrayList<String>>();

        int cont = 0;

        JSONObject jsonObject = new JSONObject();


        for(int i=0; i<listaComentariosNormalizados.size(); i++){

            /*System.out.println("[" + listaComentariosNormalizados.elementAt(i).obtenerIdComentario() + "]  " +
                                listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario()
                               +"\n|ETIQUETA: "+listaComentariosNormalizados.elementAt(i).obtenerEtiquetas().elementAt(0)+"|");*/

            ComentarioNormalizado comentario = listaComentariosNormalizados.elementAt(i);
            cont++;
            if(tabla_etiqueta_cantidad.containsKey(comentario.obtenerEtiquetas().elementAt(0))){
                int temp = tabla_etiqueta_cantidad.get(comentario.obtenerEtiquetas().elementAt(0));
                tabla_etiqueta_cantidad.put(Preprocesamiento.quitarAcentos(comentario.obtenerEtiquetas().elementAt(0)),
                                            ++temp);
            }
            else{
                tabla_etiqueta_cantidad.put(Preprocesamiento.quitarAcentos(comentario.obtenerEtiquetas().elementAt(0)),
                                            1);
            }

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

        System.out.println("\n\n"+cont);
        System.out.println(tabla_etiqueta_cantidad);
        System.out.println("\n"+jsonObject);
        
        // ********* EXTRACCIÓN DE CARACTERÍSTICAS *********
//        gestionVectorPalabras = new GestionarVectorPalabras(listaComentariosNormalizados);
//        gestionVectorPalabras.contruirVectorDePalabras();
//        gestionVectorPalabras.generarVectoresDeFrecuenciasDePalabras();
//        gestionVectorPalabras.obtenerListaVectoresDeFrecuencias();
        
        // ********* SVM EJECUCIÓN *********
//        SVM = new SVM(listaComentariosNormalizados, gestionVectorPalabras.obtenerListaVectoresDeFrecuencias());
//        SVM.ejecutarSVM(20);
        
//        // ********* INDICADORES SVM *********
//        gestionIndicadores = new GestionarIndicadores(SVM.getTabla_Etiqueta_AparicionesTotalesEstimadas(),SVM.getTabla_Etiqueta_AparicionesCorrectas(), SVM.getTabla_Etiqueta_AparicionesTotalesCorrespondientes());
//        gestionIndicadores.calcularIndicadores("SVM");
//        gestionIndicadores.imprimirIndicadoresParaGrafica();
        
        // ********* GRAFO, SEMILLAS Y CONFIGURACIÓN LABEL PROPAGATION *********
//        gestionDistancias = new GestionarDistancias();
//        gestionDistancias.calcularSimilitudCosenoEntreCadaParDeComentarios(gestionVectorPalabras.obtenerListaVectoresDeFrecuencias());
//        gestionSemillasLP = new GestionarSemillasLP(listaComentariosNormalizados);
//        gestionSemillasLP.generarArchivoSemillas(20);
//        gestionSemillasLP.generarArchivoSolucion();
//        ArchivoConfiguracionLP.generarArchivo(listaComentariosNormalizados.size());
        
//        // ********* LABEL PROPAGATION EJECUCIÓN *********
//        gestionLabelPropagation = new GestionarLabelPropagation();
//        gestionLabelPropagation.ejecutarLabelPropagation();
//        
//        // ********* INDICADORES PARA LABEL PROPAGATION *********
//        leerArchivoSalidaLP = new LeerArchivoSalidaLP(false);
//        gestionIndicadores = new GestionarIndicadores(leerArchivoSalidaLP.obtenerListaEtiquetasCorrespondientes(), leerArchivoSalidaLP.obtenerListaEtiquetasPropagadasLP());
//        gestionIndicadores.calcularIndicadores("LP");
//        gestionIndicadores.imprimirIndicadoresParaGrafica();
//        
        // ********* EJECUCIONES SECUENCIALES *********
//        extender_EjecucionSecuencial();
//        distribuir_EjecucionSecuencial();
//        configParaLP_EjecucionSecuencial();
//        indicadoresLP_EjecucionSecuencial();
//        SVM_EjecucionSecuencial();
        
        // ********* GENERAR ARCHIVO EXCEL SALIDA *********
//        escribirExcelSalida = new EscribirExcelSalida(leerArchivoCSV.obtenerListaComentariosLeidos(), listaComentariosNormalizados);
//        escribirExcelSalida.escribirArchivoSalidaXLS();
        
        // ********* FINALIZACIONES *********
//        sonido.start();
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
        System.setErr(null);
        System.setOut(null);
        System.exit(0);
    }
}