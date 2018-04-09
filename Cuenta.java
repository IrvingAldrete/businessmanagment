
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Cuenta extends javax.swing.JFrame {

    Apartado apartado;

    DefaultTableModel modelo = new DefaultTableModel();
    DefaultTableModel modeloabonos = new DefaultTableModel();
    String barra = File.separator;
    String ubicacion = System.getProperty("user.dir") + barra + "BDClientes" + barra;

    File contenedor = new File(ubicacion + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta + barra + "Abonos" + barra);
    File[] abonosc = contenedor.listFiles();

    public Cuenta() {
        initComponents();
        apartado = new Apartado();
        setLocationRelativeTo(null);
        cargarTabla();
        tablaAbonos();
        lblNombre.setText("Cuenta de " + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta);
        setTitle("Cuenta de " + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta);
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/icon.jpg")).getImage());
        mostrarAbonos();
        mostrarArticulos();
        mostrardatosCuenta();
        txtAbonar.requestFocus();
    }

    private void cargarTabla() {
        modelo.addColumn("Articulo");
        modelo.addColumn("Precio");
        modelo.addColumn("Fecha");
        tblArticulos.setModel(modelo);
    }

    private void tablaAbonos() {
        modeloabonos.addColumn("Abono");
        modeloabonos.addColumn("Cantidad");
        modeloabonos.addColumn("Fecha");
        tblAbonos.setModel(modeloabonos);
    }

    private void actualizarAbonos() {
        abonosc = contenedor.listFiles();
        modeloabonos.setRowCount(0);
        mostrarAbonos();
    }

    private void mostrarAbonos() {

        for (File abonos : abonosc) {
            try {

                FileInputStream fis = new FileInputStream(abonos);
                Properties mostrar = new Properties();
                mostrar.load(fis);

                String abono = mostrar.getProperty("Abono");
                String cantidad = mostrar.getProperty("Cantidad");
                String fecha = mostrar.getProperty("Fecha");

                String datos[] = {abono, cantidad, fecha};
                modeloabonos.addRow(datos);

            } catch (Exception e) {
            }
        }
    }

    private void mostrarArticulos() {

        File folder = new File(ubicacion + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta + barra + "Articulos" + barra);

        File[] arts = folder.listFiles();

        for (File articulos : arts) {
            try {

                FileInputStream fis = new FileInputStream(articulos);
                Properties mostrar = new Properties();
                mostrar.load(fis);

                String articulo = mostrar.getProperty("Articulo");
                String precio = mostrar.getProperty("Precio");
                String fecha = mostrar.getProperty("Fecha");

                String datos[] = {articulo, precio, fecha};
                modelo.addRow(datos);

            } catch (Exception e) {
            }
        }

    }
    
    private void mostrardatosCuenta(){
        
        File url = new File(ubicacion + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta + barra + "Cuenta.txt");
        try {

            FileInputStream datos = new FileInputStream(url);
            Properties mostrar = new Properties();
            mostrar.load(datos);

            String Total = mostrar.getProperty("Total");
            String Pagado = mostrar.getProperty("Pagado");
            String Restante = mostrar.getProperty("Restante");

            txtTotal.setText(Total);
            txtPagado.setText(Pagado);
            txtRestante.setText(Restante);

        } catch (Exception e) {
        }
    }

    public void cargarCuenta() {

        File url = new File(ubicacion + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta + barra + "Cuenta.txt");
        try {

            FileInputStream fis = new FileInputStream(url);
            Properties mostrar = new Properties();
            mostrar.load(fis);

            apartado.pagadoag = Double.parseDouble(mostrar.getProperty("Pagado"));
            apartado.fechaag = mostrar.getProperty("FechaDeRegistro");
            apartado.restanteag = Double.parseDouble(mostrar.getProperty("Restante"));
            apartado.abonoag = mostrar.getProperty("UltimoAbono");
            apartado.nombre = mostrar.getProperty("Nombre");
            apartado.apellido = mostrar.getProperty("Apellidos");
            apartado.totalag = Double.parseDouble(mostrar.getProperty("Total"));

        } catch (Exception e) {
        }
    }

    public void Actualizar() {

        try {
            FileWriter permite_escrito = new FileWriter(ubicacion + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta + barra + "Cuenta.txt");

            String Nombre = "Nombre=";
            String Apellidos = "Apellidos=";
            String Total = "Total=";
            String Pagado = "Pagado=";
            String UltimoAbono = "UltimoAbono=";
            String Restante = "Restante=";
            String FechaDeRegistro = "FechaDeRegistro=";

            PrintWriter guardar = new PrintWriter(permite_escrito);

            guardar.println(Nombre + apartado.nombre);
            guardar.println(Apellidos + apartado.apellido);
            guardar.println(Total + apartado.totalag);
            guardar.println(Pagado + apartado.npagado);
            guardar.println(UltimoAbono + " $" + apartado.abono + " el " + ApartadosGrafico.fechaActual());
            guardar.println(Restante + apartado.nrestante);
            guardar.println(FechaDeRegistro + apartado.fechaag);
            permite_escrito.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e);
        }
    }

    private void Abonar() {

        if (txtAbonar.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo Abono esta vacío");
            txtAbonar.requestFocus();
            return;
        }

        if (ApartadosGrafico.nombrecuenta.equals("") || ApartadosGrafico.apellidocuenta.equals("")) {
            JOptionPane.showMessageDialog(this, "Indique el cliente a abonar");
        } else {

            apartado.abono = Double.parseDouble(txtAbonar.getText());

            cargarCuenta();
            apartado.calcularAbono();                                      // Se calcula el total pagado y el nuevo restante
            Actualizar();

            String ubicacion = System.getProperty("user.dir") + barra + "BDClientes" + barra + apartado.nombre + " " + apartado.apellido + barra + "Abonos" + barra;

            int count = 1;
            String archivo = "Abono " + count + ".txt";
            File crea_ubicacion = new File(ubicacion);
            File crea_archivo = new File(ubicacion + archivo);

            while (crea_archivo.exists()) {            // Si existe, el contador aumenta

                count++;

                archivo = "Abono " + count + ".txt";
                crea_ubicacion = new File(ubicacion);
                crea_archivo = new File(ubicacion + archivo);

                if (!crea_archivo.exists()) {  // hasta que encuentre uno que no exista
                    break;

                }
            }
            try {
                crea_ubicacion.mkdirs();
                Formatter crea = new Formatter(ubicacion + archivo);
                crea.format("%s\r\n%s\r\n%s", "Abono=" + count, "Cantidad=" + apartado.abono, "Fecha=" + ApartadosGrafico.fechaActual());
                crea.close();
                JOptionPane.showMessageDialog(this, "Abono añadido correctamente");
                ApartadosGrafico.ActualizarTabla();
                actualizarAbonos();
                mostrardatosCuenta();

                getArticulos();
                getAbonos();

                if (apartado.nrestante <= 0) {
                    apartado.abono = Double.parseDouble(txtAbonar.getText());
                    apartado.imprimirAbono();
                    Eliminar();
                    return;
                }

                apartado.imprimirAbono();

                txtAbonar.setText("");
                txtAbonar.requestFocus();

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "El Abono no se pudo hacer correctamente");
                txtAbonar.setText("");
                txtAbonar.requestFocus();
            }
        }

    }

    private void Eliminar() {

        File url = new File(System.getProperty("user.dir") + barra + "BDClientes" + barra + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta);

        String btns[] = {"Eliminar", "Cancelar"};
        if (ApartadosGrafico.nombrecuenta.equals("") || ApartadosGrafico.apellidocuenta.equals("")) {

            JOptionPane.showMessageDialog(this, "Indique la cuenta desee eliminar");

        } else {
            if (url.exists()) {

                int seguro = JOptionPane.showOptionDialog(this, "¿Estas seguro de eliminar la cuenta? " + ApartadosGrafico.nombrecuenta + " " + ApartadosGrafico.apellidocuenta, "Eliminación", 0, 0, null, btns, null);

                if (seguro == JOptionPane.YES_OPTION) {

                    if (url.isDirectory()) {
                        File[] fs = url.listFiles();// /Abonos/, /Articulos/, cuenta.txt
                        for (File c : fs) {
                            c.delete();

                            if (c.isDirectory()) {// /Abonos/, /Articulos/
                                File[] archvios = c.listFiles();// Inicial.txt, Articulo 1.txt

                                for (File del : archvios) {
                                    del.delete();
                                }
                                c.delete(); // Se borran las carpetas
                            }

                        }
                        url.delete();
                        JOptionPane.showMessageDialog(this, "Cuenta eliminada");
                        dispose();
                        ApartadosGrafico.ActualizarTabla();
                    }

                    if (seguro == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Esa cuenta no existe");
            }
        }

    }
    
    private void getArticulos() {

        apartado.contadorarts = tblArticulos.getRowCount(); // Obtenemos el numero de filas de articulos

        apartado.articulo = new String[apartado.contadorarts]; // Arreglo tipo String
        apartado.precio = new double[apartado.contadorarts];  // Arreglo tipo double
        String[] preciostring = new String[apartado.contadorarts];

        for (int x = 0; x < apartado.contadorarts; x++) {

            apartado.articulo[x] = String.valueOf(tblArticulos.getValueAt(x, 0)); // Columna de articulos 0

            preciostring[x] = String.valueOf(tblArticulos.getValueAt(x, 1));

            apartado.precio[x] = Double.parseDouble(preciostring[x]);  // Columna de precios 1

        }

    }
    
    private void getAbonos() {

        apartado.contadorabonos = tblAbonos.getRowCount(); // Obtenemos el numero de filas de articulos

        
        apartado.abonoskey = new String[apartado.contadorabonos];
        apartado.abonosfecha = new String[apartado.contadorabonos]; // Arreglo tipo String
        apartado.abonos = new double[apartado.contadorabonos];  // Arreglo tipo double
        String[] stringabonos = new String[apartado.contadorabonos];

        for (int x = 0; x < apartado.contadorabonos; x++) {

            apartado.abonoskey[x] = String.valueOf(tblAbonos.getValueAt(x, 0));
            
            stringabonos[x] = String.valueOf(tblAbonos.getValueAt(x, 1));

            apartado.abonosfecha[x] = String.valueOf(tblAbonos.getValueAt(x, 2)); // Columna de articulos 0

            apartado.abonos[x] = Double.parseDouble(stringabonos[x]);  // Columna de precios 1

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlbg = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        lblIcon = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblArticulos = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();
        lblPagado = new javax.swing.JLabel();
        lblRestante = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        txtPagado = new javax.swing.JLabel();
        txtRestante = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAbonos = new javax.swing.JTable();
        lblAbono = new javax.swing.JLabel();
        btnAbonar = new javax.swing.JButton();
        txtAbonar = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        pnlbg.setBackground(new java.awt.Color(255, 255, 255));

        pnlTop.setBackground(new java.awt.Color(122, 72, 221));

        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_User_64px.png"))); // NOI18N

        lblNombre.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNombre)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                        .addComponent(lblNombre)
                        .addGap(10, 10, 10)))
                .addContainerGap())
        );

        tblArticulos.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tblArticulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblArticulos.setGridColor(new java.awt.Color(255, 255, 255));
        tblArticulos.setSelectionBackground(new java.awt.Color(122, 72, 221));
        jScrollPane1.setViewportView(tblArticulos);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblTotal.setText("Total:");

        lblPagado.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblPagado.setText("Pagado:");

        lblRestante.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblRestante.setText("Restante:");

        txtTotal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        txtPagado.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        txtRestante.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        tblAbonos.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tblAbonos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblAbonos.setGridColor(new java.awt.Color(255, 255, 255));
        tblAbonos.setSelectionBackground(new java.awt.Color(122, 72, 221));
        jScrollPane2.setViewportView(tblAbonos);

        lblAbono.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblAbono.setText("Abono:");

        btnAbonar.setText("Abonar");
        btnAbonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbonarActionPerformed(evt);
            }
        });

        txtAbonar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlbgLayout = new javax.swing.GroupLayout(pnlbg);
        pnlbg.setLayout(pnlbgLayout);
        pnlbgLayout.setHorizontalGroup(
            pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlbgLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlbgLayout.createSequentialGroup()
                                .addComponent(btnEliminar)
                                .addGap(197, 197, 197)
                                .addComponent(lblAbono)
                                .addComponent(txtAbonar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAbonar))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlbgLayout.createSequentialGroup()
                                .addComponent(lblTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlbgLayout.createSequentialGroup()
                                .addComponent(lblPagado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPagado, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlbgLayout.createSequentialGroup()
                                .addComponent(lblRestante)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlbgLayout.setVerticalGroup(
            pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlbgLayout.createSequentialGroup()
                .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(txtTotal))
                .addGap(18, 18, 18)
                .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPagado)
                    .addComponent(txtPagado))
                .addGap(18, 18, 18)
                .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRestante)
                    .addComponent(txtRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlbgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAbono)
                    .addComponent(btnAbonar)
                    .addComponent(txtAbonar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlbg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlbg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbonarActionPerformed
        Abonar();
    }//GEN-LAST:event_btnAbonarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        Eliminar();
    }//GEN-LAST:event_btnEliminarActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbonar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAbono;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPagado;
    private javax.swing.JLabel lblRestante;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlTop;
    public javax.swing.JPanel pnlbg;
    private javax.swing.JTable tblAbonos;
    private javax.swing.JTable tblArticulos;
    private javax.swing.JTextField txtAbonar;
    private javax.swing.JLabel txtPagado;
    private javax.swing.JLabel txtRestante;
    private javax.swing.JLabel txtTotal;
    // End of variables declaration//GEN-END:variables
}
