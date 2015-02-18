
/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 4 de 2013
 */

package utiles;

import entrada_salida.GestionarArchivos;
import estructuras.ComentarioNormalizado;
import extraccion_caracteristicas.GestionarVectorPalabras;
import java.util.*;
import org.apache.log4j.Logger;
import preprocesamiento.Preprocesamiento;

public class DistribuirDatos {
    
    private final static Logger LOG = Logger.getLogger(DistribuirDatos.class);
     
    private Vector<ComentarioNormalizado> listaComentarios;
    private Vector<ComentarioNormalizado> nuevosComentariosDistribuidos;
    private Vector<ComentarioNormalizado> listaComentariosTodosDiferentes;
    
    private ArrayList<String> listaDeEtiquetasTotales;
    private ArrayList<String> listaDeEtiquetasIncluidasEnNuevosComentarios;
    
    private Hashtable<String,Integer> tablaFrecuenciasEtiquetasTotales;     
    private Hashtable<String,Integer> tablaFrecuenciasEtiquetasUtiles;
    
    private int comentarioUtiles;
        
    public DistribuirDatos(Vector<ComentarioNormalizado> listaComentarios) {        
        this.listaComentarios = new Vector(listaComentarios);
        nuevosComentariosDistribuidos = new Vector();
        listaComentariosTodosDiferentes = new Vector();
        listaDeEtiquetasTotales = new ArrayList();   
        listaDeEtiquetasIncluidasEnNuevosComentarios = new ArrayList();
        tablaFrecuenciasEtiquetasTotales = new Hashtable<String, Integer>();                
        tablaFrecuenciasEtiquetasUtiles = new Hashtable<String, Integer>();   
    }

    //Método que distribuye proporcionalmente una cantidad determinada de comentarios, respecto al número total.
    //Ejemplo: si se tienen 2000 comentarios y se requiere trabajar con tan solo 200, se calcula cuántas
    //veces debe aparecer una etiqueta de acuerdo a sus apariciones totales.
    public Vector<ComentarioNormalizado> generarDistribucionProporcionalNDatos(int N){
        Hashtable<String,Integer> tablaCantMaximaPorEtiqueta = new Hashtable();
        generarFrecuenciasEtiquetasTotales();
        
        int porcentajeDelTotal = Matematicas.calcularPorcentajeQueRepresentaCantidadRespectoTotal(N, listaComentarios.size());
        tablaCantMaximaPorEtiqueta = llenarTablaCantMaximaPorEtiqueta(porcentajeDelTotal);
        
        int aumentoGradualCantEtiquetas = 0;
        int comentariosIncluidos = 0;
        int i = 0;
        
        
        while(comentariosIncluidos<N){
            if(i>=listaComentarios.size()){
                i = 0;
                aumentoGradualCantEtiquetas++;
                LOG.debug(" Aumento gradual: "+aumentoGradualCantEtiquetas);
            }
            String etiqueta = listaComentarios.elementAt(i).obtenerEtiquetas().elementAt(0).toLowerCase();
            ComentarioNormalizado tempComentario = listaComentarios.elementAt(i);

            if( (contarAparicionEtiqueta(etiqueta) < (tablaCantMaximaPorEtiqueta.get(etiqueta) + aumentoGradualCantEtiquetas)) &&
                    (!existeComentarioEnVector(tempComentario,nuevosComentariosDistribuidos))
                    ){
                nuevosComentariosDistribuidos.addElement(tempComentario);
                comentariosIncluidos++;
            }
            i++;
        }
        guardarListaNuevosComentarios("DistribuidosProporcional "+N);
        LOG.info("Distribucion proporcional finalizada. N="+nuevosComentariosDistribuidos.size());
        generarFrecuenciasEtiquetasUtiles(nuevosComentariosDistribuidos);
        listaComentarios = new Vector(nuevosComentariosDistribuidos);
        return nuevosComentariosDistribuidos;
    }
    
    //Método que distribuye uniformemente los comentarios de un paquete de acuerdo a un N. 
    //Se encarga de asignar la misma cantidad de apariciones por cada una de las etiquetas, es decir,
    //si se tiene que N=100 y 10 etiquetas contiene el paquete en total, cada una de ellas aparecerá 10 veces.
    public Vector<ComentarioNormalizado> generarDistribucionUniformeNDatos(int N){
        LOG.info("Distribuyendo datos");
        generarFrecuenciasEtiquetasTotales();

        double dN = N+0.0;
        double cantEtiquetas = tablaFrecuenciasEtiquetasTotales.size()+0.0;
        double dCantMax = dN/cantEtiquetas;
        int cantMaximaPorEtiqueta = Matematicas.aproximarAEntero(dCantMax);
        LOG.debug("Cant max por etiqueta: "+cantMaximaPorEtiqueta);
        
        int aumentoGradualCantEtiquetas = 0;
        int comentariosIncluidos = 0;
        int i = 0;
        
        while(comentariosIncluidos<N){
            if(i>=listaComentarios.size()){
                i = 0;
                aumentoGradualCantEtiquetas++;
                LOG.debug(" Aumento gradual: "+aumentoGradualCantEtiquetas);
            }
            String etiqueta = listaComentarios.elementAt(i).obtenerEtiquetas().elementAt(0).toLowerCase();
            ComentarioNormalizado tempComentario = listaComentarios.elementAt(i);
            
            if( (contarAparicionEtiqueta(etiqueta) < (cantMaximaPorEtiqueta + aumentoGradualCantEtiquetas)) &&
                    (!existeComentarioEnVector(tempComentario,nuevosComentariosDistribuidos))
                    ){
                nuevosComentariosDistribuidos.addElement(tempComentario);
                comentariosIncluidos++;
            }
            i++;
        }
        guardarListaNuevosComentarios("DistribuidosUniforme "+N);
        LOG.info("Distribucion uniforme finalizada. N="+nuevosComentariosDistribuidos.size());
        generarFrecuenciasEtiquetasUtiles(nuevosComentariosDistribuidos);
        listaComentarios = new Vector(nuevosComentariosDistribuidos);
        return nuevosComentariosDistribuidos;
    }
    
    //Método que distribuye uniformemente los comentarios de un paquete de acuerdo a un N y a una cantidad definida de etiquetas.
    //Se encarga de incluir sólo la cantidad de etiquetas especificada y las distribuye uniformemente, por ejemplo:
    //Si N=100 y cantEtiquetas=5, entonces por cada etiqueta habrán 20 comentarios.
    //Además, si un paquete tiene 10 etiquetas en total, y cantEtiquetas=5, incluye las 5 con mayor frecuencia.
    public Vector<ComentarioNormalizado> generarDistribucionUniformeNDatos(int N, int cantEtiquetas){
        LOG.info("Distribuyendo datos");
        generarFrecuenciasEtiquetasTotales();
        ArrayList<Integer> frecuenciasEtiquetasOrdenadas = new ArrayList(tablaFrecuenciasEtiquetasTotales.values());
        Collections.sort(frecuenciasEtiquetasOrdenadas); //Lista de las frecuencias de las etiquetas ordenada ascendentemente para tomar
                                                        //las de mayor frecuencia.

        double dN = N+0.0;
        double dCantMax = dN/cantEtiquetas;
        int cantMaximaPorEtiqueta = Matematicas.aproximarAEntero(dCantMax);
        LOG.debug("Cant max por etiqueta: "+cantMaximaPorEtiqueta);
        
        int aumentoGradualCantEtiquetas = 0;
        int comentariosIncluidos = 0;
        int i = 0;
        
        ArrayList<String> etiquetasDiferentesIncluidas = new ArrayList<String>();
        
        while(comentariosIncluidos<N){
            if(i>=listaComentarios.size()){
                i = 0;
                aumentoGradualCantEtiquetas++;
                LOG.debug(" Aumento gradual: "+aumentoGradualCantEtiquetas);
            }
            String etiqueta = listaComentarios.elementAt(i).obtenerEtiquetas().elementAt(0).toLowerCase();
            ComentarioNormalizado tempComentario = listaComentarios.elementAt(i);
            
            if( (contarAparicionEtiqueta(etiqueta) < (cantMaximaPorEtiqueta + aumentoGradualCantEtiquetas)) &&
                    (!existeComentarioEnVector(tempComentario,nuevosComentariosDistribuidos))
                    ){
                if(etiquetasDiferentesIncluidas.size() == cantEtiquetas){
                    if(etiquetasDiferentesIncluidas.contains(etiqueta)){
                        nuevosComentariosDistribuidos.addElement(tempComentario);
                        comentariosIncluidos++;
                    }
                }
                else{
                    if(frecuenciasEtiquetasOrdenadas.size() > cantEtiquetas){
                        int frecuenciaEtiqueta = tablaFrecuenciasEtiquetasTotales.get(etiqueta);
                        int posFrecuenciaListaOrdenada = frecuenciasEtiquetasOrdenadas.indexOf(frecuenciaEtiqueta);
                        int posMinima = frecuenciasEtiquetasOrdenadas.size()-cantEtiquetas;
                        if(posFrecuenciaListaOrdenada >= (posMinima)){
                            nuevosComentariosDistribuidos.addElement(tempComentario);
                            comentariosIncluidos++;
                            if(!(etiquetasDiferentesIncluidas.contains(etiqueta))){
                                etiquetasDiferentesIncluidas.add(etiqueta);
                            }
                        }
                    }
                    else{
                        nuevosComentariosDistribuidos.addElement(tempComentario);
                        comentariosIncluidos++;
                    }
                }
            }
            i++;
        }
        guardarListaNuevosComentarios("DistribuidosUniformeCanDefi "+N);
        LOG.info("Distribucion uniforme finalizada. N="+nuevosComentariosDistribuidos.size());
        generarFrecuenciasEtiquetasUtiles(nuevosComentariosDistribuidos);
        listaComentarios = new Vector(nuevosComentariosDistribuidos);
        return nuevosComentariosDistribuidos;
    }
    
    private Hashtable<String,Integer> llenarTablaCantMaximaPorEtiqueta(int porcentajePorEtiqueta){
        Hashtable<String,Integer> tablaHash = new Hashtable<String,Integer>();
        for(int i=0; i<listaDeEtiquetasTotales.size(); i++){            
            int cantPorEtiqueta = Matematicas.calcularCantidadAPartirDePorcentajeQueRepresentaDeTotal(
                    tablaFrecuenciasEtiquetasTotales.get(listaDeEtiquetasTotales.get(i)),porcentajePorEtiqueta);            
            tablaHash.put(listaDeEtiquetasTotales.get(i), cantPorEtiqueta);
        }
        return tablaHash;
    }

    private int contarAparicionEtiqueta(String eti){
        int apariciones = 0;
        for(int i=0; i<nuevosComentariosDistribuidos.size(); i++){
            if(eti.equals(nuevosComentariosDistribuidos.elementAt(i).obtenerEtiquetas().elementAt(0).toLowerCase())){
                apariciones++;
            }
        }
        return apariciones;
    }

    private boolean existeComentarioEnVector(ComentarioNormalizado cn, Vector<ComentarioNormalizado> vector){
        for(int i=0; i<vector.size(); i++){
            Vector<String> a = cn.obtenerListaPalabrasEnComentario();
            Vector<String> b = vector.elementAt(i).obtenerListaPalabrasEnComentario();            
            if(a.equals(b)){                
                return true;
            }
        }
        return false;
    }
    
    private void guardarListaNuevosComentarios(String tipo){
        GestionarArchivos ga = new GestionarArchivos();
        ga.guardarComentariosNormalizados(nuevosComentariosDistribuidos, tipo);
    }
    
    private void generarFrecuenciasEtiquetasTotales(){
        for(int i=0; i<listaComentarios.size(); i++){
            ComentarioNormalizado tempComentario = listaComentarios.elementAt(i);
            if(!tempComentario.obtenerEtiquetas().isEmpty()){
                String tempEtiqueta = tempComentario.obtenerEtiquetas().elementAt(0).toLowerCase();
                if(!tablaFrecuenciasEtiquetasTotales.containsKey(tempEtiqueta)){
                    tablaFrecuenciasEtiquetasTotales.put(tempEtiqueta, 1);
                    listaDeEtiquetasTotales.add(tempEtiqueta);
                }
                else{
                    int frecuenciaActual = tablaFrecuenciasEtiquetasTotales.get(tempEtiqueta);
                    tablaFrecuenciasEtiquetasTotales.put(tempEtiqueta, ++frecuenciaActual);
                }
            }
        }
//        for(int i=0; i<listaDeEtiquetasTotales.size(); i++){
//            System.out.println(listaDeEtiquetasTotales.get(i)+"\t"+tablaFrecuenciasEtiquetasTotales.get(listaDeEtiquetasTotales.get(i)));
//        }
        LOG.debug("Cant Eti Totales: "+listaDeEtiquetasTotales.size()+"\n");
    }
    
    private void generarFrecuenciasEtiquetasUtiles(Vector<ComentarioNormalizado> listaOrigen){
        listaComentariosTodosDiferentes = new Vector();
        listaDeEtiquetasIncluidasEnNuevosComentarios = new ArrayList<String>();
        tablaFrecuenciasEtiquetasUtiles = new Hashtable<String, Integer>();
        
        for(int i=0; i<listaOrigen.size(); i++){
            ComentarioNormalizado tempComentario = listaOrigen.elementAt(i);
            if( !(tempComentario.obtenerEtiquetas().isEmpty()) &&
                    !(tempComentario.obtenerListaPalabrasEnComentario().isEmpty()) &&
                    !(existeComentarioEnVector(tempComentario,listaComentariosTodosDiferentes))
                    ){
                String tempEtiqueta = tempComentario.obtenerEtiquetas().elementAt(0).toLowerCase();
                if(!(tablaFrecuenciasEtiquetasUtiles.containsKey(tempEtiqueta))){
                    tablaFrecuenciasEtiquetasUtiles.put(tempEtiqueta, 1);
                    listaDeEtiquetasIncluidasEnNuevosComentarios.add(tempEtiqueta);
                    listaComentariosTodosDiferentes.add(tempComentario);
                }
                else{
                    int frecuenciaActual = tablaFrecuenciasEtiquetasUtiles.get(tempEtiqueta);
                    tablaFrecuenciasEtiquetasUtiles.put(tempEtiqueta, ++frecuenciaActual);
                    listaComentariosTodosDiferentes.add(tempComentario);
                }
            }
        }        
        for(int i=0; i<listaDeEtiquetasIncluidasEnNuevosComentarios.size(); i++){
            LOG.debug("> "+listaDeEtiquetasIncluidasEnNuevosComentarios.get(i)+"\t"+tablaFrecuenciasEtiquetasUtiles.get(listaDeEtiquetasIncluidasEnNuevosComentarios.get(i)));
//            System.out.println(listaDeEtiquetasIncluidasEnNuevosComentarios.get(i)+"\t"+tablaFrecuenciasEtiquetasUtiles.get(listaDeEtiquetasIncluidasEnNuevosComentarios.get(i)));
        }
    }
    
    public Vector<ComentarioNormalizado> generarListaSoloComentarioUtiles(){
        generarFrecuenciasEtiquetasUtiles(listaComentarios);
        int sinEti = 0;
        int repetidos = 0;
        int vacios = 0;
        
        for(int i=0; i<listaComentarios.size(); i++){
            ComentarioNormalizado tempComentario = listaComentarios.elementAt(i);
            if( !(tempComentario.obtenerEtiquetas().isEmpty()) &&
                    !(tempComentario.obtenerListaPalabrasEnComentario().isEmpty()) &&
                    !(existeComentarioEnVector(tempComentario,nuevosComentariosDistribuidos))
                    ){
                String tempEtiqueta = tempComentario.obtenerEtiquetas().elementAt(0).toLowerCase();
                if((tablaFrecuenciasEtiquetasUtiles.get(tempEtiqueta)) >= 35){
                    nuevosComentariosDistribuidos.addElement(tempComentario);
                    if(!listaDeEtiquetasIncluidasEnNuevosComentarios.contains(tempEtiqueta)){
                        listaDeEtiquetasIncluidasEnNuevosComentarios.add(tempEtiqueta);
                    }
                }
            }
            else{
                if(tempComentario.obtenerEtiquetas().isEmpty()){
                    sinEti++;
                }
                if(tempComentario.obtenerListaPalabrasEnComentario().isEmpty()){
                    vacios++;
                }
                if(existeComentarioEnVector(tempComentario,nuevosComentariosDistribuidos)){
                    repetidos++;
                }
            }
        }
        comentarioUtiles = nuevosComentariosDistribuidos.size();
        guardarListaNuevosComentarios("Utiles Normalizados");
        LOG.debug("Tamaño Consolidado= "+listaComentarios.size());
        LOG.debug("Utiles= "+comentarioUtiles+"\n");
        LOG.debug("Comentarios Repetidos: "+repetidos);
        LOG.debug("Vacíos: "+vacios);
        LOG.debug("Sin Etiqueta: "+sinEti);
//        generarFrecuenciasEtiquetasUtiles(nuevosComentariosDistribuidos);
        return nuevosComentariosDistribuidos;
    }
    
    public Vector<ComentarioNormalizado> eliminarComentariosConPalabrasRepetidasEnExceso(){
        int borrados = 0;
        GestionarVectorPalabras gVectorPalabras = new GestionarVectorPalabras(listaComentarios);
        gVectorPalabras.contruirVectorDePalabras();
        gVectorPalabras.generarVectoresDeFrecuenciasDePalabras();
        ArrayList<Integer> posicionesAEliminar = new ArrayList<Integer>();
        for(int i=0; i<listaComentarios.size(); i++){
            int fre = Collections.max(gVectorPalabras.obtenerListaVectoresDeFrecuencias().elementAt(i).obtenerVectorFrecuenciasPalabras());
            if(fre>8){
                posicionesAEliminar.add(i);
            }
        }
        for(int i=0; i<posicionesAEliminar.size(); i++){
            int posAEliminar = (posicionesAEliminar.get(i) - i);
            listaComentarios.remove(posAEliminar);
            borrados++;
        }
        LOG.debug("Borrados por palabras en exceso: "+borrados);
        LOG.debug("Nuevo tamanio: "+listaComentarios.size());
        GestionarArchivos ga = new GestionarArchivos();
        ga.guardarComentariosNormalizados(listaComentarios, "Utiles Normalizados");
        return listaComentarios;
    }
    
    //Genera una nueva lista de comentarios a partir de una dada, quitando de la original los que contengan etiquetas de baja precisión.
    public Vector<ComentarioNormalizado> eliminarComentariosConEtiquetasBajaPrecision(){
        LOG.debug("Cant Original: "+listaComentarios.size());
        ArrayList<String> etiquetasMalas = new ArrayList<String>();
        if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Balance")){
            etiquetasMalas.add("seguridad");
            etiquetasMalas.add("composicion");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Coca_Cola")){
            etiquetasMalas.add("organico");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Dog_Chow")){
            etiquetasMalas.add("presentaciones");
            etiquetasMalas.add("desarrollo");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Don_Julio")){
            etiquetasMalas.add("comunidad");
            etiquetasMalas.add("organico");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Kotex")){
            etiquetasMalas.add("mensajes de campaña");
            etiquetasMalas.add("presentaciones");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Nike")){
            etiquetasMalas.add("generico");
            etiquetasMalas.add("resistencia");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Sony")){
            etiquetasMalas.add("producto");
            etiquetasMalas.add("conectividad");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Top_Terra")){
            etiquetasMalas.add("precio");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }

        for(int i=0; i<listaComentarios.size(); i++){
            String etiqueta = Preprocesamiento.quitarAcentos(listaComentarios.elementAt(i).obtenerEtiquetas().elementAt(0).toLowerCase());
            if(!etiquetasMalas.contains(etiqueta)){
                nuevosComentariosDistribuidos.addElement(listaComentarios.elementAt(i));
                System.out.print(etiqueta+", ");
            }
        }
        System.out.println();
        LOG.debug("Cant Nueva   : "+nuevosComentariosDistribuidos.size());
        guardarListaNuevosComentarios("Utiles Normalizados Sin_Eti_Ruido");
        return nuevosComentariosDistribuidos;
    }
    
    //Etiquetas que no corresponden a categorías defininas.
    public Vector<ComentarioNormalizado> eliminarComentariosConEtiquetasRuido(){
        
        LOG.debug("Cant Original: "+listaComentarios.size());
        ArrayList<String> etiquetasRuido = new ArrayList<String>();
        if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Balance")){
            etiquetasRuido.add("otros");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Coca_Cola")){
            etiquetasRuido.add("campaña");
            etiquetasRuido.add("comunidad talk");
            etiquetasRuido.add("otros");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Dog_Chow")){
            etiquetasRuido.add("campaña");
            etiquetasRuido.add("otros");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Don_Julio")){
            etiquetasRuido.add("otros");
            etiquetasRuido.add("organico");
            etiquetasRuido.add("comunidad");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Kotex")){
            etiquetasRuido.add("mensajes de campaña");
            etiquetasRuido.add("otros");
            etiquetasRuido.add("intencion de uso");
            etiquetasRuido.add("comunidad talk");
            etiquetasRuido.add("presentaciones");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Nike")){
            etiquetasRuido.add("generico");
            etiquetasRuido.add("campaña");
            etiquetasRuido.add("otros");
            etiquetasRuido.add("historias");
            etiquetasRuido.add("respaldomarca");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Sony")){
            etiquetasRuido.add("otros");
            etiquetasRuido.add("producto");
            etiquetasRuido.add("comunidad talk");
            etiquetasRuido.add("redes sociales");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        else if(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().equals("Top_Terra")){
            etiquetasRuido.add("otros");
            System.out.println(GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios());
        }
        Hashtable<String,Integer> tabla = new Hashtable<String, Integer>();
        for(int i=0; i<listaComentarios.size(); i++){
            String etiqueta = Preprocesamiento.quitarAcentos(listaComentarios.elementAt(i).obtenerEtiquetas().elementAt(0).toLowerCase());
            if(!etiquetasRuido.contains(etiqueta)){
                nuevosComentariosDistribuidos.addElement(listaComentarios.elementAt(i));
                //System.out.print(etiqueta+", ");
            }
            else{
                tabla.put(etiqueta, 1);
            }
        }
        System.out.println(tabla);
        LOG.debug("Cant Nueva   : "+nuevosComentariosDistribuidos.size());
        guardarListaNuevosComentarios("Utiles Normalizados Sin_Eti_Ruido");
        return nuevosComentariosDistribuidos;
    }
}