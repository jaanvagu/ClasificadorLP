/*
 * @author Jairo Andrés
 * Ultima modificacion: Mayo 7 de 2013
 */

package utiles;

import java.io.*;
import javax.sound.sampled.*;
import org.apache.log4j.Logger;

public class SonidoBuffer extends Thread{
    
    private final static Logger LOG = Logger.getLogger(SonidoBuffer.class);

    AudioFormat audioFormat;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    int tipo;
      
    public SonidoBuffer(int tipo){
        this.tipo = tipo;
    }

    @Override
    public void run(){
        playAudio(tipo);
    }

    public void playAudio(int tipo) {

        try{
            File soundFile;
            if(tipo==1){
              soundFile = new File("C:/alerta.wav");
              //soundFile = new File(SonidoBuffer.class.getResource("/Sonido/alerta.wav").toURI());
            }
            else{
              soundFile = new File("C:/alarma.wav");
              //soundFile = new File(Temporizador.class.getResource("/Sonido/alarma.wav").toURI());
            }
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            audioFormat = audioInputStream.getFormat();
            LOG.debug(audioFormat);

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,audioFormat);

            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
            new PlayThread().start();
        }catch (Exception e) {
            LOG.error("Excepcion en Sonido: "+e.getMessage());
      }
    }


    public class PlayThread extends Thread{

      byte tempBuffer[] = new byte[10000];

        @Override
      public void run(){
        try{
              sourceDataLine.open(audioFormat);
              sourceDataLine.start();

              int cnt;
              while((cnt = audioInputStream.read(tempBuffer,0,tempBuffer.length)) != -1){
                if(cnt > 0){
                  sourceDataLine.write(tempBuffer, 0, cnt);
                }
              }
              sourceDataLine.drain();
              sourceDataLine.close();

        }catch (Exception e) {
            LOG.error("Excepcion buffer sonido: "+e.getMessage());
        }
      }
    }
}