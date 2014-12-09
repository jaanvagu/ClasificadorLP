/*
 * @author Jairo Andrés
 * Ultima modificacion: Julio 4 de 2013
 */

package utiles;

import dbpedia_spotlight.ConsultaSPARQL;
import dbpedia_spotlight.GestionarDBPediaSpotlight;
import entrada_salida.GestionarArchivos;
import estructuras.ComentarioNormalizado;
import estructuras.ListaPalabrasRelevantes;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.log4j.Logger;
import wordnet.GestionarWordNet;

public class ExtenderComentarios {
    
    private final static Logger LOG = Logger.getLogger(ExtenderComentarios.class);
    
    private Vector<ComentarioNormalizado> listaComentariosExtendidos;
    private ListaPalabrasRelevantes listaPalabrasRelevantes;
    private GestionarDBPediaSpotlight gestionDBPediaSpotlight;
    private ConsultaSPARQL consultaSPARQL;
    private GestionarWordNet gestionWordNet;

    public ExtenderComentarios(Vector<ComentarioNormalizado> listaComentariosOriginales){
        listaComentariosExtendidos = new Vector(listaComentariosOriginales);
        DiccionarioEspanolIngles.cargarDiccionarios("DiccionarioEspanol_Ingles");
        listaPalabrasRelevantes = new ListaPalabrasRelevantes(listaComentariosOriginales);
        gestionDBPediaSpotlight = new GestionarDBPediaSpotlight();
        consultaSPARQL = new ConsultaSPARQL();
        gestionWordNet = new GestionarWordNet();
    }
    
    public Vector<ComentarioNormalizado> extender(boolean DBPedia, boolean wordNet){
        if(DBPedia && wordNet){
            LOG.info("Extendiendo con DBPedia y WordNet ("+listaComentariosExtendidos.size()+" datos)");
        }
        else if(DBPedia && !wordNet){
            LOG.info("Extendiendo solo con DBPedia ("+listaComentariosExtendidos.size()+" datos)");
        }
        else if(!DBPedia && wordNet){
            LOG.info("Extendiendo solo con WordNet ("+listaComentariosExtendidos.size()+" datos)");
        }
        
        if(DBPedia){
            //Texto en Español
//            gestionDBPediaSpotlight.identificarConceptosEnTexto(listaPalabrasRelevantes.obtenerListaPalabrasRelevantesEnEspanol());
            //Texto en inglés
            gestionDBPediaSpotlight.identificarConceptosEnTexto(listaPalabrasRelevantes.obtenerListaPalabrasRelevantesTraducidas());
        }
        for(int i=0; i<listaComentariosExtendidos.size(); i++){
            ComentarioNormalizado comentarioOriginal = listaComentariosExtendidos.elementAt(i);
            Vector<String> etiquetasComentarioOriginal_i = comentarioOriginal.obtenerEtiquetas();
            Vector<String> listaPalabrasComentarioOriginal_i = comentarioOriginal.obtenerListaPalabrasEnComentario();
            LOG.debug("Original:  "+listaPalabrasComentarioOriginal_i);
            
            Vector<String> listaPalabrasOriginalesYPalabrasDeExtension_i = new Vector(listaPalabrasComentarioOriginal_i);
            
            //DBPEDIA
            if(DBPedia){
                listaPalabrasOriginalesYPalabrasDeExtension_i.addAll(
                        obtenerPalabrasParaExtenderComentarioDBPedia(listaPalabrasComentarioOriginal_i));
            }
            
            //WORDNET
            if(wordNet){
                listaPalabrasOriginalesYPalabrasDeExtension_i.addAll(
                        obtenerPalabrasParaExtenderComentarioWordNet(listaPalabrasComentarioOriginal_i));
            }
            
            ComentarioNormalizado comentarioExtendido = new ComentarioNormalizado(comentarioOriginal.obtenerIdComentario(),
                                                                                  listaPalabrasOriginalesYPalabrasDeExtension_i,
                                                                                  etiquetasComentarioOriginal_i);
            listaComentariosExtendidos.setElementAt(comentarioExtendido, i);
            LOG.debug("Procesado: "+i+"\n-------------------------------------------------------------");
//            System.out.print(i+", ");
        }
        System.out.println();
        GestionarArchivos ga = new GestionarArchivos();
        if(DBPedia && wordNet){
            ga.guardarComentariosNormalizados(listaComentariosExtendidos, "Extendidos DBPyWN "+listaComentariosExtendidos.size());
        }
        else if(DBPedia && !wordNet){
            ga.guardarComentariosNormalizados(listaComentariosExtendidos, "Extendidos DBP "+listaComentariosExtendidos.size());
        }
        else if(!DBPedia && wordNet){
            ga.guardarComentariosNormalizados(listaComentariosExtendidos, "Extendidos WN "+listaComentariosExtendidos.size());
        }
        LOG.info("Extension terminada");
        return listaComentariosExtendidos;
    }
    
    private Vector<String> obtenerPalabrasParaExtenderComentarioWordNet(Vector<String> listaPalabrasComentarioOriginal_i){
        Vector<String> listaPalabrasParaIdentificarSynsets = new Vector();
        for(int i=0; i<listaPalabrasComentarioOriginal_i.size(); i++){
            String tempPalabra = listaPalabrasComentarioOriginal_i.elementAt(i);
            if(listaPalabrasRelevantes.esPalabraRelevante(tempPalabra)){
                String palabraTraducida = DiccionarioEspanolIngles.traducirPalabraEspAIng(tempPalabra);
                if(!listaPalabrasParaIdentificarSynsets.contains(palabraTraducida)){
                    listaPalabrasParaIdentificarSynsets.add(palabraTraducida);
                }
            }
        }
        Vector<String> listaPalabrasExtension = new Vector();
        if(!listaPalabrasParaIdentificarSynsets.isEmpty()){
            LOG.debug("Cant Relevantes obtenidas: "+listaPalabrasParaIdentificarSynsets.size());
            LOG.debug("Relevantes obtenidas: "+listaPalabrasParaIdentificarSynsets);
            Vector<String> synsetsIdentificados = gestionWordNet.obtenerSynsetsEnEspanolDeListaPalabras(listaPalabrasParaIdentificarSynsets);
            if(!synsetsIdentificados.isEmpty()){
                LOG.debug("Cant synsets: "+synsetsIdentificados.size());
                LOG.debug("Synsets: "+synsetsIdentificados);
                listaPalabrasExtension = synsetsIdentificados;
            }else{LOG.debug("No se obtuvieron synsets");}
        }else{
            LOG.debug("No se obtuvieron palabras relevantes.");
        }
        return listaPalabrasExtension;
    }
    
    private Vector<String> obtenerPalabrasParaExtenderComentarioDBPedia(Vector<String> listaPalabrasComentarioOriginal_i){
        ArrayList<String> linksConceptosIdentificados = new ArrayList<String>();
        for(int i=0; i<listaPalabrasComentarioOriginal_i.size(); i++){
            String tempPalabra = listaPalabrasComentarioOriginal_i.elementAt(i);
            String palabraTraducida = DiccionarioEspanolIngles.traducirPalabraEspAIng(tempPalabra);
            if(listaPalabrasRelevantes.esPalabraRelevante(tempPalabra)){
                String linkConcepto = gestionDBPediaSpotlight.obtenerLinkConceptoPalabra(palabraTraducida);
                if(!(linkConcepto.equals("NO_CONCEPTO")) && !(linksConceptosIdentificados.contains(linkConcepto))){
                    linksConceptosIdentificados.add(linkConcepto);
                }
            }
        }
        Vector<String> listaPalabrasExtension = new Vector();
        if(!linksConceptosIdentificados.isEmpty()){
            LOG.debug("Cant Links: "+linksConceptosIdentificados.size());
            LOG.debug("Links: "+linksConceptosIdentificados);
            listaPalabrasExtension = obtenerPropiedadesDeConceptosDBPedia(linksConceptosIdentificados);
            LOG.debug("Cant Propiedades: "+listaPalabrasExtension.size());
            LOG.debug("Propiedades: "+listaPalabrasExtension);
        }else{
            LOG.debug("No se obtuvieron conceptos para comentario i.");
        }
        return listaPalabrasExtension;
    }
    
     private Vector<String> obtenerPropiedadesDeConceptosDBPedia(ArrayList<String> linksConceptosIdentificados){
        Vector<String> propiedadesConceptos = new Vector();
        for(int i=0; i<linksConceptosIdentificados.size(); i++){
            consultaSPARQL.ejecutarConsulta(linksConceptosIdentificados.get(i));
            ArrayList<String> resultadoConsulta = consultaSPARQL.obtenerResultadoDeConsulta();
            for(int j=0; j<resultadoConsulta.size(); j++){
                String propiedad = resultadoConsulta.get(j);
                if(!propiedadesConceptos.contains(propiedad)){
                    propiedadesConceptos.addElement(resultadoConsulta.get(j));
                }
            }
        }
        return propiedadesConceptos;
    }
}