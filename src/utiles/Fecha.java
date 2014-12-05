/* 
 * @author Jairo Andrés
 * Ultima modificacion: Octubre 19 de 2013
 */

package utiles;

import java.util.Calendar;

public class Fecha {

    public static String fechaYHoraActual(){
        String fechaYHoraActual;
        Calendar calendar = Calendar.getInstance();
        fechaYHoraActual = ""+calendar.get(Calendar.DATE)+"-"+calendar.get(Calendar.MONTH)
                          +"-"+calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.HOUR)
                          +";"+calendar.get(Calendar.MINUTE)+";"+calendar.get(Calendar.SECOND);
        return fechaYHoraActual;
    }
}
