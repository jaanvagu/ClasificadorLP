/*
 * @author Jairo Andrés
 * Ultima modificacion: Abril 29 de 2013
 */

package LP;

import entrada_salida.GestionarArchivos;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.FileChannel;
import org.apache.log4j.Logger;

public class GestionarLabelPropagation {
    
    private final static Logger LOG = Logger.getLogger(GestionarLabelPropagation.class);
    
    private String comandoDeEjecucion;
    private Process procesoEjecucion;    
    
    public GestionarLabelPropagation(){        
        comandoDeEjecucion = "sbt.bat";
        GestionarArchivos ga = new GestionarArchivos();
        ga.establecerRutaArchivoPruebaLP_enArchivoSBT();
    }
    
    private FileChannel getChannel(InputStream in) throws NoSuchFieldException, IllegalAccessException {
        Field f = FilterInputStream.class.getDeclaredField("in");
        f.setAccessible(true);
        while (in instanceof FilterInputStream){
            in = (InputStream)f.get((FilterInputStream)in);
        }
        return ((FileInputStream)in).getChannel();
    }
    
    //Ejecuta como proceso externo el archivo sbt.bat, el cual se encarga de llamar a la clase principal de la 
    //herramienta JUNTO Label Propagation
    public void ejecutarLabelPropagation(){
        LOG.info("Ejecutando Label Propagation");
        try{
            procesoEjecucion = Runtime.getRuntime().exec(comandoDeEjecucion);
            Thread outThread = new Thread(new StreamCopier(procesoEjecucion.getInputStream(), System.out));
            outThread.start();
            Thread errThread = new Thread(new StreamCopier(procesoEjecucion.getErrorStream(), System.out));
            errThread.start();
            Thread inThread = new Thread(new InputCopier(getChannel(System.in), procesoEjecucion.getOutputStream()));
            inThread.start();
            System.in.close();
            outThread.join();
            errThread.join();
            inThread.join();
            LOG.info("Finalizo ejecucion Label Propagation");
        }
        catch(Exception e){
            LOG.error("No fue posible ejecutar Label Propagation. "+e.getMessage());
            e.printStackTrace();
        }
    }
    
    //Clase axiliar para indicar que se ejecuta un subproceso externo.
    private class StreamCopier implements Runnable{
        private InputStream in;
        private OutputStream out;

        public StreamCopier(InputStream in, OutputStream out){
            this.in = in;
            this.out = out;
             
        }
        
        @Override
        public void run(){
            try{
                int n;
                byte[] buffer = new byte[4096];
                while ((n = in.read(buffer)) != -1) {
                    //Imprimir salida de la consola
//                    out.write(buffer, 0, n);
//                    out.flush();
                }
            }
            catch (IOException e){
                LOG.error("Aqui 1 "+e);
            }
        }
    }
    
    //Clase axiliar para indicar que se ejecuta un subproceso externo.
    private class InputCopier implements Runnable{
        private FileChannel in;
        private OutputStream out;

        public InputCopier(FileChannel in, OutputStream out){
            this.in = in;
            this.out = out;
        }
        
        @Override
        public void run(){
            try{
                int n;
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer.array(), 0, n);
                    out.flush();
                }
                out.close();
            }
            catch(AsynchronousCloseException e){
                LOG.error("Aqui 2 "+e);
            }
            catch (IOException e){}            
        }
    }
}
