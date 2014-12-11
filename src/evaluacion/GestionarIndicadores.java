/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 7 de 2013
 */

package evaluacion;

import entrada_salida.GestionarArchivos;
import java.util.*;
import org.apache.log4j.Logger;
import utiles.Matematicas;

public class GestionarIndicadores {
    
    private final static Logger LOG = Logger.getLogger(GestionarIndicadores.class);

    private Vector<String> listaEtiquetasCorrespondientes;
    private Vector<String> listaEtiquetasEstimadas;
    private Hashtable<String,Integer> tabla_Etiqueta_AparicionesTotalesEstimadas;
    private Hashtable<String,Integer> tabla_Etiqueta_AparicionesCorrectas;    
    private Hashtable<String,Integer> tabla_Etiqueta_AparicionesTotalesCorrespondientes;
    private Hashtable<String,Double> tabla_Etiqueta_indicadorPrecision;
    private Hashtable<String,Double> tabla_Etiqueta_indicadorRecall;
    private Hashtable<String,Double> tabla_Etiqueta_indicadorFScore;
    
    private Enumeration<String> etiDiferentes;

    public GestionarIndicadores(Vector<String> listaEtiquetasCorrespondientes, Vector<String> listaEtiquetasEstimadas){
        this.listaEtiquetasCorrespondientes = listaEtiquetasCorrespondientes;
        this.listaEtiquetasEstimadas = listaEtiquetasEstimadas;        
        tabla_Etiqueta_AparicionesCorrectas = new Hashtable();
        tabla_Etiqueta_AparicionesTotalesEstimadas = new Hashtable();
        tabla_Etiqueta_AparicionesTotalesCorrespondientes = new Hashtable();
        tabla_Etiqueta_indicadorPrecision = new Hashtable();
        tabla_Etiqueta_indicadorRecall = new Hashtable();
        tabla_Etiqueta_indicadorFScore = new Hashtable();
        construirTablasApariciones();
    }
    
    public GestionarIndicadores(Hashtable<String,Integer> tablaEstimadas, Hashtable<String,Integer> tablaCorrectas, 
            Hashtable<String,Integer> tablaCorrespondientes){        
        tabla_Etiqueta_AparicionesTotalesEstimadas = tablaEstimadas;
        tabla_Etiqueta_AparicionesCorrectas = tablaCorrectas;
        tabla_Etiqueta_AparicionesTotalesCorrespondientes = tablaCorrespondientes; 
        tabla_Etiqueta_indicadorPrecision = new Hashtable();
        tabla_Etiqueta_indicadorRecall = new Hashtable();
        tabla_Etiqueta_indicadorFScore = new Hashtable();
    }
    
    public void calcularIndicadores(String algoritmo){
        if(algoritmo.equals("LP")){
            LOG.info("Indicadores Label Propagation");
            calcularIndicadorPrecisionParaEtiquetas();
            calcularIndicadorRecallParaEtiquetas();
            calcularIndicadorFScoreParaEtiquetas();
//            imprimirResultadosObtenidos(algoritmo);
            calcularAciertosTotales();
        }
        else if(algoritmo.equals("SVM")){
            LOG.info("Indicadores SVM");
            calcularIndicadorPrecisionParaEtiquetas();
            calcularIndicadorRecallParaEtiquetas();
            calcularIndicadorFScoreParaEtiquetas();
            calcularAciertosTotales();
        }        
    }

    //Cálacula el indicador "precision" para las etiquetas de una lista de comentarios. 
    private void calcularIndicadorPrecisionParaEtiquetas(){
        double indicadorPrecisionEtiqueta_i = 0;
        etiDiferentes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.keys();
        while(etiDiferentes.hasMoreElements()){
            String etiqueta = etiDiferentes.nextElement();
            int aparicionesCorrectas = 0;
            int aparicionesTotales = 0;
            if(!(tabla_Etiqueta_AparicionesCorrectas.get(etiqueta) == null)){
                aparicionesCorrectas = tabla_Etiqueta_AparicionesCorrectas.get(etiqueta);            
                aparicionesTotales = tabla_Etiqueta_AparicionesTotalesEstimadas.get(etiqueta);
                indicadorPrecisionEtiqueta_i = (aparicionesCorrectas+0.0)/(aparicionesTotales+0.0);
            }     
            else{
                indicadorPrecisionEtiqueta_i = 0;
            }
            tabla_Etiqueta_indicadorPrecision.put(etiqueta, indicadorPrecisionEtiqueta_i);
        }                                
    }

    //Cálacula el indicador "recall" para las etiquetas de una lista de comentarios.
    private void calcularIndicadorRecallParaEtiquetas(){
        double indicadorRecallEtiqueta_i = 0;
        etiDiferentes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.keys();
        while(etiDiferentes.hasMoreElements()){
            String etiqueta = etiDiferentes.nextElement();
            int aparicionesCorrectas = 0;
            int aparicionesTotalesCorrespondientes = 0;
            if(!(tabla_Etiqueta_AparicionesCorrectas.get(etiqueta) == null)){
                aparicionesCorrectas = tabla_Etiqueta_AparicionesCorrectas.get(etiqueta);
                aparicionesTotalesCorrespondientes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.get(etiqueta);
                indicadorRecallEtiqueta_i = (aparicionesCorrectas+0.0)/(aparicionesTotalesCorrespondientes+0.0);
            }
            else{
                indicadorRecallEtiqueta_i = 0;
            }
            tabla_Etiqueta_indicadorRecall.put(etiqueta, indicadorRecallEtiqueta_i);
        }        
    }

    //Cálacula el indicador "FScore" para las etiquetas de una lista de comentarios.
    private void calcularIndicadorFScoreParaEtiquetas(){
        double indicadorFScoreEtiqueta_i;
        etiDiferentes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.keys();
        while(etiDiferentes.hasMoreElements()){
            String etiqueta = etiDiferentes.nextElement();
            double precision;
            double recall;
            precision = tabla_Etiqueta_indicadorPrecision.get(etiqueta);
            recall = tabla_Etiqueta_indicadorRecall.get(etiqueta);
            if((precision+recall) != 0.0){
                indicadorFScoreEtiqueta_i = ( 2 * ( (precision*recall) / (precision+recall) ) );
            }
            else{
                 indicadorFScoreEtiqueta_i = 0.0;
            }
            tabla_Etiqueta_indicadorFScore.put(etiqueta, indicadorFScoreEtiqueta_i);
        }                
    }

    //Método que se encarga de contar las apariciones de cada una de las etiquetas propagadas por LP,
    // y además, de contar cuántas de dichas propagadas, fueron correctas.
    private void construirTablasApariciones(){
        for(int i=0; i<listaEtiquetasEstimadas.size(); i++){
            String etiquetaCorrespondiente = listaEtiquetasCorrespondientes.elementAt(i);
            String etiquetaPropagadaLP = listaEtiquetasEstimadas.elementAt(i);
            if(tabla_Etiqueta_AparicionesTotalesEstimadas.get(etiquetaPropagadaLP) == null){
                tabla_Etiqueta_AparicionesTotalesEstimadas.put(etiquetaPropagadaLP, 1);
                tabla_Etiqueta_AparicionesCorrectas.put(etiquetaPropagadaLP, 0);
            }
            else{
                int aparicionesTotalesLPActual = tabla_Etiqueta_AparicionesTotalesEstimadas.get(etiquetaPropagadaLP);
                tabla_Etiqueta_AparicionesTotalesEstimadas.put(etiquetaPropagadaLP, ++aparicionesTotalesLPActual);
            }
            
            if(etiquetaCorrespondiente.equals(etiquetaPropagadaLP)){                
                int aparicionesCorrectasActual = tabla_Etiqueta_AparicionesCorrectas.get(etiquetaPropagadaLP);
                tabla_Etiqueta_AparicionesCorrectas.put(etiquetaPropagadaLP, ++aparicionesCorrectasActual);                
            }
            
            if(tabla_Etiqueta_AparicionesTotalesCorrespondientes.get(etiquetaCorrespondiente) == null){
                tabla_Etiqueta_AparicionesTotalesCorrespondientes.put(etiquetaCorrespondiente, 1);
            }
            else{
                int aparicionesTotalesCorrespondientesActual = tabla_Etiqueta_AparicionesTotalesCorrespondientes.get(etiquetaCorrespondiente);
                tabla_Etiqueta_AparicionesTotalesCorrespondientes.put(etiquetaCorrespondiente, ++aparicionesTotalesCorrespondientesActual);
            }
        }        
    }
        
    public void calcularAciertosTotales(){
        etiDiferentes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.keys();
        int correctas = 0, total = 0;
        while(etiDiferentes.hasMoreElements()){
            String etiqueta = etiDiferentes.nextElement();
            if(tabla_Etiqueta_AparicionesCorrectas.containsKey(etiqueta)){
                correctas += tabla_Etiqueta_AparicionesCorrectas.get(etiqueta);
            }
            total += tabla_Etiqueta_AparicionesTotalesCorrespondientes.get(etiqueta);
        }
        System.out.println("|"+GestionarArchivos.obtenerNombreDelConsolidadoSinEspacios().toUpperCase()+"|");
        System.out.println("Efectividad\t"+Matematicas.calcularPorcentajeQueRepresentaCantidadRespectoTotal(correctas,total)+"%");
        System.out.println("{"+correctas+" aciertos de "+total+"}");
    }
    
    public void imprimirResultadosObtenidos(String algoritmo){
        etiDiferentes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.keys();
        System.out.println("\nEtiqueta\tEstimadas por "+algoritmo+"\tCorrectas\tCorrespondientes\tPrecision\tRecall\tFScore");        
        while(etiDiferentes.hasMoreElements()){
            String tempEtiqueta = etiDiferentes.nextElement();
            String strPrecision, strRecall, strFscore;
            int estimada = 0, correctas = 0, correspondientes;
            double precision, recall, fscore;                  
            
            if(tabla_Etiqueta_AparicionesTotalesEstimadas.containsKey(tempEtiqueta)){
                estimada = tabla_Etiqueta_AparicionesTotalesEstimadas.get(tempEtiqueta);
            }
            if(tabla_Etiqueta_AparicionesCorrectas.containsKey(tempEtiqueta)){
                correctas = tabla_Etiqueta_AparicionesCorrectas.get(tempEtiqueta);
            }
            correspondientes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.get(tempEtiqueta);
            precision = tabla_Etiqueta_indicadorPrecision.get(tempEtiqueta);
            recall = tabla_Etiqueta_indicadorRecall.get(tempEtiqueta);
            fscore = tabla_Etiqueta_indicadorFScore.get(tempEtiqueta);
            strPrecision = (""+precision).replaceAll("\\.", ",");
            strRecall = (""+recall).replaceAll("\\.", ",");
            strFscore = (""+fscore).replaceAll("\\.", ",");
            
            System.out.println(tempEtiqueta+"\t"+estimada+"\t"+correctas+"\t"+correspondientes+"\t"+strPrecision+"\t"+strRecall+"\t"+strFscore);
        }
    }
    
    public void imprimirIndicadoresParaGrafica(){ 
        etiDiferentes = tabla_Etiqueta_AparicionesTotalesCorrespondientes.keys();
        double sumatoriaPrecision = 0.0, sumatoriaRecall = 0.0, sumatoriaFscore = 0.0;
        System.out.println("Etiqueta\tPrecision\tRecall\tFScore");        
        while(etiDiferentes.hasMoreElements()){
            String tempEtiqueta = etiDiferentes.nextElement();
            String strPrecision, strRecall, strFscore;
            double precision, recall, fscore;                                    
            precision = tabla_Etiqueta_indicadorPrecision.get(tempEtiqueta);
            recall = tabla_Etiqueta_indicadorRecall.get(tempEtiqueta);
            fscore = tabla_Etiqueta_indicadorFScore.get(tempEtiqueta);
            sumatoriaPrecision += precision;
            sumatoriaRecall += recall;
            sumatoriaFscore += fscore;
            strPrecision = ""+precision;
            strRecall = ""+recall;
            strFscore = ""+fscore;
            strPrecision = strPrecision.replaceAll("\\.", ",");
            strRecall = strRecall.replaceAll("\\.", ",");
            strFscore = strFscore.replaceAll("\\.", ",");
            System.out.println(tempEtiqueta+"\t"+strPrecision+"\t"+strRecall+"\t"+strFscore);
        }
        double promedioPrecision = sumatoriaPrecision/(tabla_Etiqueta_AparicionesTotalesCorrespondientes.size());
        double promedioRecall = sumatoriaRecall/(tabla_Etiqueta_AparicionesTotalesCorrespondientes.size());
        double promedioFscore = sumatoriaFscore/(tabla_Etiqueta_AparicionesTotalesCorrespondientes.size());
        String strPromedioPrecision = (""+promedioPrecision).replaceAll("\\.", ",");
        String strPromedioRecall = (""+promedioRecall).replaceAll("\\.", ",");
        String strPromedioFscore = (""+promedioFscore).replaceAll("\\.", ",");
        System.out.println("Promedio\t"+strPromedioPrecision+"\t"+strPromedioRecall+"\t"+strPromedioFscore);
        System.out.println();
    }
}