/* 
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 7 de 2013
 */

package wordnet;

import edu.smu.tspell.wordnet.*;
import java.util.Vector;
import org.apache.log4j.Logger;
import utiles.DiccionarioEspanolIngles;

public class GestionarWordNet {  
    
    private final static Logger LOG = Logger.getLogger(GestionarWordNet.class);    
    
    private WordNetDatabase baseDatosWordNet;

    public GestionarWordNet() {
        System.setProperty("wordnet.database.dir", "C:/WordNet/2.1/dict/");
        baseDatosWordNet = WordNetDatabase.getFileInstance();
        DiccionarioEspanolIngles.cargarDiccionarios("DiccionarioIngles_Espanol");
    }      
    
    public Vector<String> obtenerSynsetsEnInglesDeListaPalabras(Vector<String> listaPalabrasEnIngles){
        Vector<String> listaSynsetsEnIngles = new Vector();
        
        for(int i=0; i<listaPalabrasEnIngles.size(); i++){
//            System.out.println("\n");
            String palabra_i = listaPalabrasEnIngles.elementAt(i);
            Synset[] synsetsPalabra_i = baseDatosWordNet.getSynsets(palabra_i,SynsetType.NOUN);
            if(synsetsPalabra_i.length > 0){
                for(int j=0; j<synsetsPalabra_i.length; j++){
//                    System.out.println(synsetsPalabra_i[j]);
                    String[] formasDeLaPalabra = synsetsPalabra_i[j].getWordForms();
                    for(int k=0; k<formasDeLaPalabra.length; k++){
                        String forma_k_Palabra = formasDeLaPalabra[k];
                        if(!forma_k_Palabra.toLowerCase().equals(palabra_i)){
                            String synset = "@"+forma_k_Palabra.replaceAll(" ", "_");
                            if(!listaSynsetsEnIngles.contains(synset)){
                                listaSynsetsEnIngles.addElement(synset);
                            }
                        }
                    }
                }
            }
            else{
                LOG.debug("No existe synset para: "+palabra_i);
            }
        }
        return listaSynsetsEnIngles;
    }
    
    public Vector<String> obtenerSynsetsEnEspanolDeListaPalabras(Vector<String> listaPalabrasEnIngles){
        Vector<String> listaSynsetsEnEspanol = new Vector();
        
        for(int i=0; i<listaPalabrasEnIngles.size(); i++){
            String palabra_i = listaPalabrasEnIngles.elementAt(i);
            Synset[] synsetsPalabra_i = baseDatosWordNet.getSynsets(palabra_i,SynsetType.NOUN);
            if(synsetsPalabra_i.length > 0){
                for(int j=0; j<synsetsPalabra_i.length; j++){
                    String[] formasDeLaPalabra = synsetsPalabra_i[j].getWordForms();
                    for(int k=0; k<formasDeLaPalabra.length; k++){
                        String forma_k_Palabra = formasDeLaPalabra[k];
                        if(!forma_k_Palabra.toLowerCase().equals(palabra_i)){
                            String synset = forma_k_Palabra;
                            String synsetTraducido = DiccionarioEspanolIngles.traducirPalabraIngAEsp(synset);
                            if(!(synsetTraducido.equals("NO_TRADUCCION")) && !(listaSynsetsEnEspanol.contains(synsetTraducido))){
                                listaSynsetsEnEspanol.addElement(synsetTraducido);
                            }
                        }
                    }
                }
            }
            else{
                LOG.debug("No existe synset para: "+palabra_i);
            }
        }
        return listaSynsetsEnEspanol;
    }
    
    public static void main(String[] args){
        GestionarWordNet g = new GestionarWordNet();
        Vector<String> v = new Vector();
        v.addElement("share");
        v.addElement("car");
        v.addElement("save");
        
        System.out.println(g.obtenerSynsetsEnInglesDeListaPalabras(v));
        System.out.println(g.obtenerSynsetsEnEspanolDeListaPalabras(v));
    }
}