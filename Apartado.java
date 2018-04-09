import br.com.adilson.util.Extenso;
import br.com.adilson.util.PrinterMatrix;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

/*
    Este es un programa para gestionar los apartados de un negocio.
    Fecha de creación: 11/12/2017        
    Fecha de ultima modificación: 11/12/2017
    Autor: Irving Aldrete
 */
public class Apartado {

    String nombre, apellido, ticket, nombreLocal = "Maru Boutique";
    double restante, total, abono, pagado;

    //Agregar apartado - variables
    String nombreag, apellidoag, fechaag, abonoag;
    double restanteag, totalag, pagadoag, nrestante, nabono, ntotal, npagado;

    int contadorarts;
    int contadorabonos;
    
    int contartscuenta;
    int contabonoscuenta;
    
    String[] articulo;
    String[] articulofecha;
    double[] precio;
    
    double[] abonos;
    String[] abonosfecha;
    String[] abonoskey;
    
    int line = 5;

    public Apartado()//Constructor
    {
        nombre = "";
        apellido = "";
    }

    public void calcularAbono() {

        npagado = pagadoag + abono;
        nrestante = restanteag - abono;
    }

    String articulos = "";

    public void generarTicket() {

        for (int x = 0; x < contadorarts; x++) {
            String agregar = articulo[x] + "   $" + precio[x] + "\n";
            articulos = articulos + agregar;
        }

        ticket = "--------------------- " + nombreLocal + " ---------------------"
                + "\n"
                + "\nCliente: " + nombre + " " + apellido
                + "\n"
                + "\nArtículos:" 
                + "\n"
                + articulos
                + "\nTotal: " + total
                + "\nAbono: " + abono
                + "\n"
                + "\nResta: " + restante;
        
        articulos = "";  // Para que se limpie la variable

    }

    public void imprimirTicket() {
        
        PrinterMatrix printer = new PrinterMatrix();

        Extenso e = new Extenso();

        e.setNumber(101.85);

        //Definir el tamanho del papel para la impresion  aca 15 lineas y 40 columnas
        printer.setOutSize(24+(contadorarts*2) + line, 40);
        //Imprimir * de la 2da linea a 25 en la columna 1;
        // printer.printCharAtLin(2, 25, 1, "*");
        //Imprimir * 1ra linea de la columa de 1 a 80
        printer.printCharAtCol(1, 1, 40, "=");
        printer.printCharAtCol(2, 1, 40, "=");
        //Imprimir Encabezado nombre del La Empresa
        printer.printTextWrap(3, 3, 12, 40, nombreLocal);
        //printer.printTextWrap(linI, linE, colI, colE, null);
        printer.printTextWrap(5, 5, 1, 22, "Fecha: " + ApartadosGrafico.fechaActual());
        printer.printTextWrap(5, 5, 27, 38, "Hora: " + ApartadosGrafico.hora());
        printer.printTextWrap(6, 6, 1, 38, "Cliente: " + nombre + " " + apellido);
        printer.printTextWrap(8, 8, 1, 38, "Articulos:");

        int i = 0;
        for (int x = 0; x<contadorarts; x++) {
            printer.printTextWrap(9 + i, 9 + i, 1, 38, "- "+articulo[x] + "  $ " + precio[x]);
            i = i +2; // Para el salto de linea
        }
        
        printer.printTextWrap(9+i, 9+i, 1, 38, "Total:   " + "$ " + total);//11
        printer.printTextWrap(9+i+1, 9+i+1, 1, 38, "Importe: " + "$ " + abono);//12
        printer.printTextWrap(9+i+2, 9+i+2, 1, 38, "Resta:   " + "$ " + restante);//14
        printer.printTextWrap(9+i+4, 9+i+3, 1, 38, "Puebla 92-A Col. Centro");//15

        printer.printCharAtCol(9+i+6, 1, 40, "="); // 18
        printer.printCharAtCol(9+i+7, 1, 40, "="); // 19

        printer.toFile("impresion.txt");

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("impresion.txt");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (inputStream == null) {
            return;
        }

        DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(inputStream, docFormat, null);

        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        if (defaultPrintService != null) {
            DocPrintJob printJob = defaultPrintService.createPrintJob();
            try {
                printJob.print(document, attributeSet);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("No existen impresoras instaladas");
        }

        //inputStream.close();
    }

    public void imprimirAbono() {
        
        PrinterMatrix printer = new PrinterMatrix();

        Extenso e = new Extenso();

        e.setNumber(101.85);

        //Definir el tamanho del papel para la impresion  aca 15 lineas y 40 columnas
        printer.setOutSize(27+ (contadorarts*2) + contadorabonos + line, 40);
        //Imprimir * de la 2da linea a 25 en la columna 1;
        // printer.printCharAtLin(2, 25, 1, "*");
        //Imprimir * 1ra linea de la columa de 1 a 80
        printer.printCharAtCol(1, 1, 40, "=");
        printer.printCharAtCol(2, 1, 40, "=");
        //Imprimir Encabezado nombre del La Empresa
        printer.printTextWrap(3, 3, 12, 40, nombreLocal);
        //printer.printTextWrap(linI, linE, colI, colE, null);
        printer.printTextWrap(5, 5, 1, 22, "Fecha: " + ApartadosGrafico.fechaActual());
        printer.printTextWrap(5, 5, 27, 38, "Hora: " + ApartadosGrafico.hora());
        printer.printTextWrap(6, 6, 1, 38, "Cliente: " + nombre + " " + apellido);
        printer.printTextWrap(8, 8, 1, 38, "Articulos:");

        int i = 0;
        for (int x = 0; x<contadorarts; x++) {
            printer.printTextWrap(9 + i, 9 + i, 1, 38, "- "+articulo[x] + "  $ " + precio[x]);
            i = i+2; // Para el salto de linea
        }
        
        printer.printTextWrap(9+i, 9+i, 1, 38, "ABONOS:");

        int a = 0;
        for (int x = 0; x<contadorabonos; x++) {
            printer.printTextWrap(10 + i +a, 10 + i+a, 1, 38, "- "+abonoskey[x] + "  $ " + abonos[x]+" el "+abonosfecha[x]); //12
            a++;
        }
        
        a = a+i; // Salto de linea
        
        printer.printTextWrap(10+a+1, 10+a+1, 1, 38, "Total:   " + "$ " + totalag); // 14
        printer.printTextWrap(10+a+2, 10+a+2, 1, 38, "Ultimo Abono: " + "$ " + abono); //15
        printer.printTextWrap(10+a+3, 10+a+3, 1, 38, "Pagado:   " + "$ " + npagado); //16
        printer.printTextWrap(10+a+4, 10+a+4, 1, 38, "Restante:   " + "$ " + nrestante); //17
        printer.printTextWrap(10+a+6, 10+a+6, 1, 38, "Puebla 92-A Col. Centro");//19

        printer.printCharAtCol(10+a+8, 1, 40, "="); // 21
        printer.printCharAtCol(10+a+9, 1, 40, "="); // 22

        printer.toFile("impresion.txt");

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("impresion.txt");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (inputStream == null) {
            return;
        }

        DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(inputStream, docFormat, null);

        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        if (defaultPrintService != null) {
            DocPrintJob printJob = defaultPrintService.createPrintJob();
            try {
                printJob.print(document, attributeSet);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("No existen impresoras instaladas");
        }

        //inputStream.close();
    }
    
    public void imprimirAgregado() {
        
        PrinterMatrix printer = new PrinterMatrix();

        Extenso e = new Extenso();

        e.setNumber(101.85);

        //Definir el tamanho del papel para la impresion  aca 15 lineas y 40 columnas
        printer.setOutSize(27+ (contartscuenta*2) + contabonoscuenta + line, 40);
        //Imprimir * de la 2da linea a 25 en la columna 1;
        // printer.printCharAtLin(2, 25, 1, "*");
        //Imprimir * 1ra linea de la columa de 1 a 80
        printer.printCharAtCol(1, 1, 40, "=");
        printer.printCharAtCol(2, 1, 40, "=");
        //Imprimir Encabezado nombre del La Empresa
        printer.printTextWrap(3, 3, 12, 40, nombreLocal);
        //printer.printTextWrap(linI, linE, colI, colE, null);
        printer.printTextWrap(5, 5, 1, 22, "Fecha: " + ApartadosGrafico.fechaActual());
        printer.printTextWrap(5, 5, 27, 38, "Hora: " + ApartadosGrafico.hora());
        printer.printTextWrap(6, 6, 1, 38, "Cliente: " + nombre + " " + apellido);
        printer.printTextWrap(8, 8, 1, 38, "Articulos:");

        int i = 0;
        for (int x = 0; x<contartscuenta; x++) {
            printer.printTextWrap(9 + i, 9 + i, 1, 38, "- "+articulo[x] + "  $ " + precio[x]);
            i = i+2; // Para el salto de linea
        }
        
        printer.printTextWrap(9+i, 9+i, 1, 38, "ABONOS:");

        int a = 0;
        for (int x = 0; x<contabonoscuenta; x++) {
            printer.printTextWrap(10 + i +a, 10 + i+a, 1, 38, "- "+abonoskey[x] + "  $ " + abonos[x]+" el "+abonosfecha[x]); //12
            a++;
        }
        
        a = a+i; // Salto de linea
        
        printer.printTextWrap(10+a+1, 10+a+1, 1, 38, "Total:   " + "$ " + ntotal); // 14
        printer.printTextWrap(10+a+2, 10+a+2, 1, 38, "Ultimo Abono: " + "$ " + nabono); //15
        printer.printTextWrap(10+a+3, 10+a+3, 1, 38, "Pagado:   " + "$ " + npagado); //16
        printer.printTextWrap(10+a+4, 10+a+4, 1, 38, "Restante:   " + "$ " + nrestante); //17
        printer.printTextWrap(10+a+6, 10+a+6, 1, 38, "Puebla 92-A Col. Centro");//19

        printer.printCharAtCol(10+a+8, 1, 40, "="); // 21
        printer.printCharAtCol(10+a+9, 1, 40, "="); // 22

        printer.toFile("impresion.txt");

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("impresion.txt");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (inputStream == null) {
            return;
        }

        DocFlavor docFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc document = new SimpleDoc(inputStream, docFormat, null);

        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        if (defaultPrintService != null) {
            DocPrintJob printJob = defaultPrintService.createPrintJob();
            try {
                printJob.print(document, attributeSet);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("No existen impresoras instaladas");
        }

        //inputStream.close();
    }
}
