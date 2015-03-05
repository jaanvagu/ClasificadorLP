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
import preprocesamiento.Lematizar;
import preprocesamiento.Preprocesamiento;
import svm.SVM;
import utiles.*;
import verificaciones.GestionarVerificaciones;

public class Main {

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
        gestionArchivos.guardarComentariosNormalizados(listaComentariosNormalizados,"Normalizado");
        LOG.info("Normalizacion terminada");
    }

    public static void main(String[] args) {
        
        ArchivoConfiguracionLog4j.generarArchivo(); // Archivo de configuración para generar el Log de ejecución
        PropertyConfigurator.configure("log4j.properties");

        // ********* LECTURA Y PREPROCESAMIENTO *********
//        leerArchivoCSV = new LeerArchivoCSV();
//        leerArchivoCSV.leerYAlmacenarLineasCSV();
//        preprocesamiento = new Preprocesamiento(leerArchivoCSV.obtenerListaComentariosLeidos());
//        lematizar = new Lematizar(preprocesamiento.obtenerMensajesProcesados());
//        normalizarComentarios(lematizar.obtenerListaMensajesLematizados(), leerArchivoCSV.obtenerListaComentariosLeidos());
//        normalizarComentarios(preprocesamiento.obtenerMensajesProcesados(), leerArchivoCSV.obtenerListaComentariosLeidos());


        // ********* CARGAR COMENTARIOS NORMALIZADOS *********
        gestionArchivos = new GestionarArchivos();
//        gestionArchivos.limpiaCarpetaArchivosPrueba(400);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Extendidos WN Distribuidos Uniforme 10_Etiquetas", "", true);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Distribuidos Uniformemente", "", true);
//        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("Utiles Normalizados Sin_Eti_Ruido", "", true);
        listaComentariosNormalizados = gestionArchivos.cargarComentariosNormalizados("","", true);


        // ********** MEZCLA DE LISTAS DE COMENTARIOS
//        Vector<ComentarioNormalizado> listaComentariosNormalizados2 = gestionArchivos.cargarComentariosNormalizados("","", true);
//        Vector<ComentarioNormalizado> listaComentariosNormalizados3 = gestionArchivos.cargarComentariosNormalizados("","", true);
//        ArrayList<Vector <ComentarioNormalizado>> listas = new ArrayList<Vector<ComentarioNormalizado>>();
//        listas.add(listaComentariosNormalizados);
//        listas.add(listaComentariosNormalizados2);
//        listas.add(listaComentariosNormalizados3);
//        Vector<ComentarioNormalizado> listaResultante = Util.mezclarListasComentarios(listas);
//        gestionArchivos = new GestionarArchivos();
//        gestionArchivos.guardarComentariosNormalizados(listaResultante, "mezcla");
        
        // ********* EXTENSIÓN DE COMENTARIOS *********
//        extenderComentarios = new ExtenderComentarios(listaComentariosNormalizados);
//        listaComentariosNormalizados = new Vector(extenderComentarios.extender(false, true));

        // ********* DISTRIBUCIÓN DE DATOS *********
//        distribuir = new DistribuirDatos(listaComentariosNormalizados);
//        listaComentariosNormalizados = distribuir.generarListaSoloComentarioUtiles();
//        distribuir = new DistribuirDatos(listaComentariosNormalizados);
//        listaComentariosNormalizados = distribuir.eliminarComentariosConPalabrasRepetidasEnExceso();
//        distribuir.eliminarComentariosConEtiquetasRuido();
//        distribuir.eliminarComentariosConEtiquetasBajaPrecision();
//        listaComentariosNormalizados = distribuir.generarDistribucionUniformeNDatos(400,7);
//        listaComentariosNormalizados = distribuir.generarDistribucionUniformeNDatos(2000);
//        distribuir.generarDistribucionProporcionalNDatos(100);
//        listaComentariosNormalizados = new Vector(distribuir.eliminarComentariosConPalabrasRepetidasEnExceso());
//        distribuir.generarListaSoloComentarioUtiles();
        
        // ********* IMPRIMIR TEXTO DE LOS COMENTARIOS *********
//        for(int i=0; i<listaComentariosNormalizados.size(); i++) {
//            System.out.println(listaComentariosNormalizados.elementAt(i).obtenerListaPalabrasEnComentario());
//        }

//        Lematizar lematizar = new Lematizar();
//        lematizar.generarTablaPalabras_CategoriaLexica(listaComentariosNormalizados);

        // ********* ESCRIBIR JSON CON COMENTARIOS POR CATEGORÍA ***********
//        Util.generarJsonEntrenamiento(listaComentariosNormalizados);
//        Util.escribirComentariosEnJson(listaComentariosNormalizados);
        Util.cantidadDeComentariosPorEtiqueta(listaComentariosNormalizados);
//        Util.generarJsonAdjSentimiento(listaComentariosNormalizados);


        // ********* EXTRACCIÓN DE CARACTERÍSTICAS *********
//        gestionVectorPalabras = new GestionarVectorPalabras(listaComentariosNormalizados);
//        gestionVectorPalabras.contruirVectorDePalabras();
//        gestionVectorPalabras.generarVectoresDeFrecuenciasDePalabras();
//        gestionVectorPalabras.obtenerListaVectoresDeFrecuencias();
        
        // ********* SVM EJECUCIÓN ********
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

        // ********* GENERAR ARCHIVO EXCEL SALIDA *********
//        escribirExcelSalida = new EscribirExcelSalida(leerArchivoCSV.obtenerListaComentariosLeidos(), listaComentariosNormalizados);
//        escribirExcelSalida.escribirArchivoSalidaXLS();
        
        // ********* FINALIZACIONES *********
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
        System.setErr(null);
        System.setOut(null);
        System.exit(0);
        System.exit(0);
    }
}