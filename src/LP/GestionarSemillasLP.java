/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 3 de 2013
 */

package LP;

import entrada_salida.GestionarArchivos;
import estructuras.ComentarioNormalizado;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import main.Main;
import org.apache.log4j.Logger;
import utiles.Matematicas;
import verificaciones.GestionarVerificaciones;

public class GestionarSemillasLP {
    
    private final static Logger LOG = Logger.getLogger(GestionarSemillasLP.class);
    
    private Vector<ComentarioNormalizado> listaComentariosNormalizados;
    private Vector<String> listaDeEtiquetasTotales;
    private Vector<String> listaEtiquetasUsadasComoSemilla;
    private Vector<Integer> listaPosicionesSemillas;
    private Hashtable<String,Integer> tablaFrecuenciasEtiquetasTotales;
    private Hashtable<String,Integer> tablaCantMaximaPorEtiqueta;
    private GestionarArchivos gestionArchivos;

    public GestionarSemillasLP(Vector<ComentarioNormalizado> listaComentarios) {        
        listaComentariosNormalizados = new Vector(listaComentarios);
        listaDeEtiquetasTotales = new Vector();
        listaEtiquetasUsadasComoSemilla = new Vector();
        listaPosicionesSemillas = new Vector();
        tablaFrecuenciasEtiquetasTotales = new Hashtable();
        tablaCantMaximaPorEtiqueta = new Hashtable();
        gestionArchivos = new GestionarArchivos();      
    }                        

    //Método que genera un archivo semilla, con todas las posibles etiquetas distintas en una lista de comentarios,
    // es decir, si existes E1, E2, E3,... E10 cada uno con una frecuencia de 10, y el porcentaje dado como parámetro es 10 (10%),
    // entonces, el archivo semilla estará contenido por E1, E2, E3,... E10, con una frecuencia de 1 cada una.
    public void generarArchivoSemillas(int porcentaje){
        gestionArchivos.crearArchivoTexto("seeds",Main.listaComentariosNormalizados.size());
        int porcentajeSemillas = Matematicas.calcularCantidadAPartirDePorcentajeQueRepresentaDeTotal
                (listaComentariosNormalizados.size(),porcentaje);
        
        generarFrecuenciasEtiquetasTotales();
        llenarTablaCantMaximaPorEtiqueta(porcentaje);
        
        int aumentoGradualSemillasPorEtiqueta = 0;
        int contador = 0;
        int i = 0;
        while(contador < porcentajeSemillas){
            if(i>=listaComentariosNormalizados.size()){
                i = 0;
                aumentoGradualSemillasPorEtiqueta++;
                LOG.debug(" Aumento gradual semilla: "+aumentoGradualSemillasPorEtiqueta);
            }
            int x = i+1;
            String etiqueta = listaComentariosNormalizados.elementAt(i).obtenerEtiquetas().elementAt(0);
            if(contarAparicionEtiqueta(etiqueta) < (tablaCantMaximaPorEtiqueta.get(etiqueta) + aumentoGradualSemillasPorEtiqueta) &&
                    !(listaPosicionesSemillas.contains(i))
                    ){
                //Verificación
//                if( listaComentariosNormalizados.elementAt(i).obtenerIdComentario() == GestionarVerificaciones.tabla_Nodo_IdComentario.get("N"+x)){
//                    if(etiqueta.equals( 
//                            GestionarVerificaciones.tabla_IdComentario_Etiqueta.get(listaComentariosNormalizados.elementAt(i).obtenerIdComentario()) )){
//                        GestionarVerificaciones.asignacionesCorrectas++;
//                    }
//                }
                gestionArchivos.escribirLineaEnArchivoTexto("N"+x+"\t"+etiqueta+"\t"+"1.0");
                listaEtiquetasUsadasComoSemilla.addElement(etiqueta);
                listaPosicionesSemillas.addElement(i);
                contador++;
            }              
            i++;
        }                
        gestionArchivos.cerrarArchivoTexto();        
        LOG.info("Semillas generadas");
        LOG.debug(" Total semillas: "+listaEtiquetasUsadasComoSemilla.size());
        //Verificación del proceso de generación de semillas
//        LOG.debug(" Asignaciones correctas: "+GestionarVerificaciones.asignacionesCorrectas);
        imprimirSemillasPorEti();
    }

    //Método que genera un archivo con las etiquetas correspondientes a una lista de comentarios.
    // Suponga que una lista tiene 10 comentarios, donde 2 de ellos fueron pasados como semilla, entonces
    // se generará un archivo solución con las etiquetas que le corresponden a los 8 comentarios restantes.
    // Las etiquetas que se establecen como solución, obedecen a anotacions manuales.
    public void generarArchivoSolucion(){
        gestionArchivos.crearArchivoTexto("gold_labels",Main.listaComentariosNormalizados.size());   
        for(int i=0; i<listaComentariosNormalizados.size(); i++){
            String etiqueta = listaComentariosNormalizados.elementAt(i).obtenerEtiquetas().elementAt(0);
            int x = i+1;
            if(!listaPosicionesSemillas.contains(i)){                        
                gestionArchivos.escribirLineaEnArchivoTexto("N"+x+"\t"+etiqueta+"\t"+"1.0");                             
            }
        }                                        
        gestionArchivos.cerrarArchivoTexto();           
        LOG.info("Gold labels generadas");                             
    }
    
    private int contarAparicionEtiqueta(String eti){
        int apariciones = 0;
        for(int i=0; i<listaEtiquetasUsadasComoSemilla.size(); i++){
            if(eti.equals(listaEtiquetasUsadasComoSemilla.elementAt(i))){
                apariciones++;
            }
        }
        return apariciones;
    }
    
    private void generarFrecuenciasEtiquetasTotales(){
        for(int i=0; i<listaComentariosNormalizados.size(); i++){           
            ComentarioNormalizado tempComentario = listaComentariosNormalizados.elementAt(i);            
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
    }
    
    private void llenarTablaCantMaximaPorEtiqueta(int porcentajePorEtiqueta){        
        for(int i=0; i<listaDeEtiquetasTotales.size(); i++){            
            int cantPorEtiqueta = Matematicas.calcularCantidadAPartirDePorcentajeQueRepresentaDeTotal(tablaFrecuenciasEtiquetasTotales.get(listaDeEtiquetasTotales.get(i)),porcentajePorEtiqueta);            
            tablaCantMaximaPorEtiqueta.put(listaDeEtiquetasTotales.get(i), cantPorEtiqueta);
        }         
    }
    
    private void imprimirSemillasPorEti(){
        Hashtable<String,Integer> t = new Hashtable<String, Integer>();
        for(int i=0; i<listaEtiquetasUsadasComoSemilla.size();i++){
            String eti = listaEtiquetasUsadasComoSemilla.elementAt(i);
            if(!t.containsKey(eti)){
                t.put(eti, 1);
            }
            else{
                int freActual = t.get(eti);
                t.put(eti, ++freActual);
            }
        }
        Enumeration<String> etis = t.keys();
        while(etis.hasMoreElements()){
            String eti = etis.nextElement();
            LOG.debug(eti+"\t"+t.get(eti));
        }
    }
}
