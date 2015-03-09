/*
 * @author Jairo Andrés
 * Ultima modificacion: Abril 22 de 2013
 */

package preprocesamiento;

import estructuras.Comentario;
import java.util.*;
import java.util.regex.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Preprocesamiento {
    
    private final static Logger LOG = Logger.getLogger(Preprocesamiento.class);    
                
    private Vector<String> listaMensajesProcesados;
    
    private final String[] listaPalabrasVacias = {
        "a","acuerdo","adelante","ademas","adrede","ahi","ahora","alla","alli","alrededor", "antano","antaño","ante","antes",
        "apenas","aproximadamente","aquel","aquella", "aquellas","aquello","aquellos","aqui","arriba","abajo","asi",
        "aun","aunque","b","bajo","bastante","bien","breve","c","casi","cerca","claro","como","con", "conmigo","contigo","contra",
        "cual","cuales","cuando","cuanta","cuantas","cuanto","cuantos","d","de","debajo","del","delante","demasiado",
        "dentro","deprisa","desde","despacio","despues","detras","dia","dias", "donde","dos","durante","e","el","ella",
        "ellas","ellos","en","encima","enfrente", "enseguida","entre","email","es","esa","esas","ese","eso","esos","esta",
        "estado","estados","estas","este","esto","estos","ex", "excepto","f","final","fue","fuera","fueron","g",
        "general","gran","h","ha","habia","habla", "hablan","hace","hacia","han","hasta","hay","hola","horas","hoy","i","incluso","informo",
        "j","junto", "k","l","la","lado","las","le","lejos","lo","los","luego","m","mal","mail","mas","mayor","me","medio", "mejor","menos",
        "menudo","mi","mia","mias","mientras","mio","mios","mis", "mismo","mucho","muy","n","nada","nadie","ninguna","no",
        "nos","nosotras","nosotros","nuestra","nuestras", "nuestro","nuestros","nueva","nuevo","nunca","ñ","o","os","otra","otros","p",
        "pais","para","parte", "pasado","peor","pero","poco","por","porque","pronto","proximo","puede","pues","q","qeu","que", "quien",
        "quienes","quiza","quizas","r","raras","repente","s","salvo", "se","segun","ser","sera","si","sido","siempre",
        "sin","sobre","solamente", "solo","son","soyos","su","supuesto","sus","suya","suyas","suyo","t","tal","tambien", "tampoco",
        "tarde","te","temprano","ti","tiene","todavia","todo","todos","tras","tu", "tus","tuya","tuyas","tuyo","tuyos","u","un","una",
        "unas","uno","unos","usted","ustedes","v","veces", "vez","vosotras","vosotros","vuestra","vuestras","vuestro","vuestros","w","x","y",
        "ya","yo","z"
    };    

    private final String[] listaInicialesRedesSociales = {"@", "#", "rt", "twitter", "tweet", "face", "facebook", "fb"};

    private final String[] listaPatronesOnomatopeyas = {
        "[ja]{2,}", "[je]{2,}", "[ji]{2,}", "[ha]{2,}", "[he]{2,}", "[hi]{2,}", "[wo]{2,}", "[oh]{2,}", "[ah]{2,}", "[o]{2,}",
        "[a]{2,}", "[e]{2,}", "[u]{2,}", "[i]{2,}", "[m]{2,}", "[uf]{2,}", "[uy]{2,}", "[bu]{2,}", "[ura]{3,}", "[mucho]{5,}", "[hola]{4,}"
        //Añadir "[ke]{2,}"
    }; 

    //Constructor que recibe una lista de comentarios, obtiene los mensajes de
    //cada uno de ellos, se los asigna a la variable de clase listaMensajesParaProcesar y finalemente ejecuta
    //algunas funciones de limpieza de datos sobre dicha lista.
    public Preprocesamiento(Vector<Comentario> listaComentarios){   
        PropertyConfigurator.configure("log4j.properties");
        LOG.info("Normalizando...");
        listaMensajesProcesados = new Vector();   
        for(int i=0; i<listaComentarios.size(); i++){
            String mensajeDeComentario = listaComentarios.elementAt(i).obtenerMensaje();
            listaMensajesProcesados.addElement(mensajeDeComentario);
        }

        ejecutarPreprocesamientoSecuencial();
        LOG.info("Preprocesamiento Realizado");
    }

    private void ejecutarPreprocesamientoSecuencial(){        
        ejecutarTipoPreProcesamiento("convertirAMinusculas");
        ejecutarTipoPreProcesamiento("eliminarAcentos");
        ejecutarTipoPreProcesamiento("eliminarRuidoRedesSociales");
        ejecutarTipoPreProcesamiento("eliminarURLs");
        ejecutarTipoPreProcesamiento("eliminarOnomatopeyas");
        ejecutarTipoPreProcesamiento("eliminarCaracteresDiferentesALetras");
//        ejecutarTipoPreProcesamiento("eliminarPalabrasVacias");
        ejecutarTipoPreProcesamiento("eliminarEspaciosEnBlancoAdicionales");
    }

    //Método que recorre la lista de mensajes y ejecuta una función según el tipo que le ingrese como parámetro
    //Se apoya en métodos auxiliares definidos posteriormente.
    private void ejecutarTipoPreProcesamiento(String tipo){
        for(int i=0; i<listaMensajesProcesados.size(); i++){
            StringBuilder mensajePreProcesado = new StringBuilder();
            StringTokenizer tokensMensajeParaProcesar = new StringTokenizer(listaMensajesProcesados.elementAt(i));
            int contador = 0;
            while(tokensMensajeParaProcesar.hasMoreTokens()){
                String palabra = tokensMensajeParaProcesar.nextToken();
                
                if(tipo.equals("eliminarRuidoRedesSociales")){
                    if(!esRuidoTuiter(palabra)){
                        mensajePreProcesado.append(palabra).append(" ");
                    }
                }
                else if (tipo.equals("eliminarURLs")){
                    if(!esURL(palabra)){
                        mensajePreProcesado.append(palabra).append(" ");
                    }
                }
                else if (tipo.equals("eliminarOnomatopeyas")){
                    if(!esOnomatopeya(palabra)){
                        mensajePreProcesado.append(palabra).append(" ");
                    }
                }                                
                else if (tipo.equals("convertirAMinusculas")) {
                    mensajePreProcesado.append(convertirAMinusculas(palabra)).append(" ");
                }
                else if (tipo.equals("eliminarAcentos")) {
                    mensajePreProcesado.append(quitarAcentos(palabra)).append(" ");
                }
                else if(tipo.equals("eliminarCaracteresDiferentesALetras")){
                    mensajePreProcesado.append(eliminarCaracteresDiferentesALetras(palabra)).append(" ");
                }
                else if (tipo.equals("eliminarPalabrasVacias")){
                    if(!esPalabraVacia(palabra)){
                        mensajePreProcesado.append(palabra).append(" ");
                    }
                }
                else if (tipo.equals("eliminarEspaciosEnBlancoAdicionales")){                                                            
                    if((tokensMensajeParaProcesar.countTokens())!=0){
                        mensajePreProcesado.append(palabra.trim()).append(" ");
                    }
                    else{
                        mensajePreProcesado.append(palabra.trim());        
                    }                                        
                }
                else{
                    LOG.error("Tipo de preprocesamiento mal escrito");
                }

                contador++;
            }
            listaMensajesProcesados.setElementAt(mensajePreProcesado.toString(),i);
        }
    }         

    //Identidica si una palabra contiene ruido generado por la red social tuiter.
    private boolean esRuidoTuiter(String palabra){
        for(int i=0; i<listaInicialesRedesSociales.length; i++){
            if(palabra.startsWith(listaInicialesRedesSociales[i])){
                return true;
            }
        }
        return false;
    }

    //Identifica si una palabra es una direccion web o URL
    private boolean esURL(String palabra){
        String recorteDosCaracteres, recorteTresCaracteres;
        recorteTresCaracteres = "#";
        recorteDosCaracteres = "#";
        if(palabra.length()>=4){
            if(palabra.length()>=5){
                recorteTresCaracteres = palabra.substring(palabra.length()-4,palabra.length()-3);
            }
            recorteDosCaracteres = palabra.substring(palabra.length()-3,palabra.length()-2);
        }       

        if(palabra.startsWith("http") || palabra.startsWith("www") || recorteDosCaracteres.equals(".") || recorteTresCaracteres.equals(".")){
            return true;
        }
        else{
            return false;
        }
    }

    //Identifica si una palabra es onomatopeya
    private boolean esOnomatopeya(String palabra){
        for(int i=0; i<listaPatronesOnomatopeyas.length; i++){
            Pattern patron = Pattern.compile(listaPatronesOnomatopeyas[i]);
            Matcher verificador = patron.matcher(palabra);
            if(verificador.matches()){
                return true;        
            }
        }
        return false;
    }    

    //Recibe una palabra y la retorna con cada una de sus letras en minuscula.
    private String convertirAMinusculas(String mensaje){
        return mensaje.toLowerCase();
    }
    
    //Elimina los acentos o tildes que encuentre en una palabra. Es estático para permitir su uso en la clase Lematizar. Y en clase
    //DiccionarioEspanolIngles.
    public static String quitarAcentos(String palabra) {
        palabra = palabra.replaceAll("á","a");
        palabra = palabra.replaceAll("é","e");
        palabra = palabra.replaceAll("í","i");
        palabra = palabra.replaceAll("ó","o");
        palabra = palabra.replaceAll("ú","u");
        palabra = palabra.replaceAll("à","a");
        palabra = palabra.replaceAll("è","e");
        palabra = palabra.replaceAll("ì","i");
        palabra = palabra.replaceAll("ò","o");
        palabra = palabra.replaceAll("ù","u");
        return palabra;
    }

    //Identifica si una palabra es "Palabra Vacia" (Las palabras vacias estan definidas en una lista, que es un atributo de clase).
    private boolean esPalabraVacia(String palabra){
        for(int i=0; i<listaPalabrasVacias.length; i++){
            if(palabra.equals(listaPalabrasVacias[i])){
                return true;
            }
        }
        return false;
    }

    //Filtra caracteres a través de código ASCII, elimina los que no estén contenidos entre la a....z, los que no sean espacios
    //y lo diferente a "ñ"
    private String eliminarCaracteresDiferentesALetras(String palabra){        
        StringBuilder tempPalabra = new StringBuilder();
        for (int i=0; i<palabra.length(); i++){
            int codigoASCIILetra = palabra.codePointAt(i);
            if(codigoASCIILetra>=97 && codigoASCIILetra<=122 || codigoASCIILetra==241 || codigoASCIILetra==32){
                tempPalabra.append(palabra.charAt(i));
            }
        }        
        return tempPalabra.toString();
    }

    
    public Vector<String> obtenerMensajesProcesados(){
        return listaMensajesProcesados;
    }            
}
