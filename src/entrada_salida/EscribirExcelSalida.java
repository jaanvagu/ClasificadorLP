/*
 * @author Jairo Andrés
 * Ultima modificacion: Octubre 29 de 2013
 */

package entrada_salida;

import estructuras.Comentario;
import estructuras.ComentarioNormalizado;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

public class EscribirExcelSalida {
    
    private final static Logger LOG = Logger.getLogger(EscribirExcelSalida.class);
    
    private Vector<Comentario> listaComentariosOriginales;
    private Vector<ComentarioNormalizado> listaComentariosOriginalesNormalizados;
    private LeerArchivoSalidaLP leerArchivoSalidaLP;
    private Hashtable<String,String> tabla_Nodo_etiqueta;
    private HSSFWorkbook libro;
    private HSSFSheet hoja;
    private HSSFRow fila;
    private HSSFCell celda;
    private HSSFRichTextString contenidoCelda;
    private Map<String, CellStyle> styles;
    
    public EscribirExcelSalida(Vector<Comentario> listaComentariosOriginales, 
                               Vector<ComentarioNormalizado> listaComentariosOriginalesNormalizados){
        this.listaComentariosOriginales = new Vector<Comentario>(listaComentariosOriginales);
        this.listaComentariosOriginalesNormalizados = new Vector<ComentarioNormalizado>(listaComentariosOriginalesNormalizados);
        leerArchivoSalidaLP = new LeerArchivoSalidaLP(false);
        tabla_Nodo_etiqueta = leerArchivoSalidaLP.obtenerTabla_Nodo_Etiqueta();
        libro = new HSSFWorkbook();
        hoja = libro.createSheet();
        styles = createStyles(libro);
    }
    
    public void escribirArchivoSalidaXLS(){
         try{
            LOG.info("Escrbiendo archivo de salida XLS");
            escribirEncabezado();

            String autor;
            String mensaje;
            String etiqueta;
            for(int i=0; i<listaComentariosOriginalesNormalizados.size(); i++){
                ComentarioNormalizado comentarioNormalizado_i = listaComentariosOriginalesNormalizados.elementAt(i);
                int idComentario = comentarioNormalizado_i.obtenerIdComentario();
                // El id del comentario menos 2, corresponde a la i en el vector de comentarios originales.
                Comentario comentarioById = listaComentariosOriginales.elementAt(idComentario-2);
                autor = comentarioById.obtenerAutor();
                mensaje = comentarioById.obtenerMensaje();
                etiqueta = tabla_Nodo_etiqueta.get("N"+(i+1));
                
                //Fila
                fila = hoja.createRow(i+1); // Arranca en i+1 porque el encabezado está en la fila 0, entonces toma desde la 1
                    //Celda Autor
                    celda = fila.createCell(0);
                    contenidoCelda = new HSSFRichTextString(autor);
                    celda.setCellValue(contenidoCelda);
                    celda.setCellStyle(styles.get("celda_normal"));

                    //Celda Mensaje
                    celda = fila.createCell(1);
                    contenidoCelda = new HSSFRichTextString(mensaje);
                    celda.setCellValue(contenidoCelda);
                    celda.setCellStyle(styles.get("celda_normal"));

                    //Celda Etiqueta
                    celda = fila.createCell(2);
                    contenidoCelda = new HSSFRichTextString(etiqueta);
                    celda.setCellValue(contenidoCelda);
                    celda.setCellStyle(styles.get("celda_normal"));
            }
            // Se da formato a las columnas (ancho)
            hoja.autoSizeColumn(0);
            hoja.setColumnWidth(1, 30000); // Se ajusta el ancho estático a la columna de "Mensajes"
            hoja.autoSizeColumn(2);
            hoja.autoSizeColumn(3);
            
            guardarArchivoXLS();
            LOG.info("Archivo XLS con etiquetas propagadas generado exitosamente");
        }
        catch(Exception e){
            LOG.error("Error escribiendo salida excel: "+e.getMessage());
        }
    }
    
    private void escribirEncabezado(){
        //Fila
        fila = hoja.createRow(0);
            //Celda Autor
            celda = fila.createCell(0);
            contenidoCelda = new HSSFRichTextString("Autor");
            celda.setCellValue(contenidoCelda);
            celda.setCellStyle(styles.get("titulo"));
          
            //Celda Mensaje
            celda = fila.createCell(1);
            contenidoCelda = new HSSFRichTextString("Mensaje");
            celda.setCellValue(contenidoCelda);
            celda.setCellStyle(styles.get("titulo"));

            //Celda Etiqueta
            celda = fila.createCell(2);
            contenidoCelda = new HSSFRichTextString("Etiqueta");
            celda.setCellValue(contenidoCelda);
            celda.setCellStyle(styles.get("titulo"));
    }
    
    private void guardarArchivoXLS(){
        try{
            FileOutputStream fichero = new FileOutputStream("C:/Users/JairoAndrés/Desktop/Consolidado Etiquetado.xls");
            libro.write(fichero);
            fichero.close();
        }
        catch(Exception e){
            LOG.error("Error guardando salida excel: "+e.getMessage());
        }
    }
    
    // Método que crea un Mapa Hash de estilos para hojas de EXCEL
    private static Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        DataFormat df = wb.createDataFormat();

        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        styles.put("titulo", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(headerFont);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("header_date", style);

        Font font1 = wb.createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(font1);
        styles.put("cell_b", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font1);
        styles.put("cell_b_centered", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font1);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_b_date", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_g", style);

        Font font2 = wb.createFont();
        font2.setColor(IndexedColors.BLUE.getIndex());
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(font2);
        styles.put("cell_bb", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_bg", style);

        Font font3 = wb.createFont();
        font3.setFontHeightInPoints((short)14);
        font3.setColor(IndexedColors.DARK_BLUE.getIndex());
        font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(font3);
        style.setWrapText(true);
        styles.put("cell_h", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setWrapText(true);
        styles.put("celda_normal", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        styles.put("cell_normal_centered", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setWrapText(true);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_normal_date", style);

        style = createBorderedStyle(wb);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setIndention((short)1);
        style.setWrapText(true);
        styles.put("cell_indented", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("cell_blue", style);

        return styles;
    }
    
    // Crea un estilo de bordes para una celda.
    private static CellStyle createBorderedStyle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
}