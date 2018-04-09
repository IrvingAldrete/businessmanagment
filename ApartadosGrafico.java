import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ApartadosGrafico extends javax.swing.JFrame implements Runnable {

    Apartado apartados;

    String hora, minutos, segundos;
    Thread hilo;

    public static String barra = File.separator;
    public static String ubicacion = System.getProperty("user.dir") + barra + "BDClientes" + barra;

    public static File contenedor = new File(ubicacion);
    public static File[] carpetas = contenedor.listFiles();

    public static String[] titulos = {"Nombre", "Apellidos", "Total", "Pagado", "Ultimo Abono", "Restante", "Fecha"};
    public static DefaultTableModel dtm = new DefaultTableModel(null, titulos);
    TableRowSorter trs;

    DefaultTableModel modelo = new DefaultTableModel(); // articulos

    int xMouse, yMouse;  // Mover jFrame

    public ApartadosGrafico() {
        initComponents();
        setLocationRelativeTo(null);
        pnlRegistros.setVisible(false);
        creaCarpeta();
        setTitle("Sistema De Apartados");
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/icon.jpg")).getImage());
        hilo = new Thread(this);
        hilo.start();
        cargarTablaArticulos();
        ActualizarTabla();
        apartados = new Apartado();
        lblFechaTop.setText(fechaActual());
        txtNombre.requestFocus();
    }

    private void cargarTablaArticulos() {
        modelo.addColumn("Articulo");
        modelo.addColumn("Precio");
        tblArticulos.setModel(modelo);
    }

    private void creaCarpeta() {
        File crea_ubicacion = new File(ubicacion);
        File crea_archivo = new File(ubicacion);

        crea_ubicacion.mkdirs();
    }

    public void cargarCuenta() {

        File url = new File(ubicacion + apartados.nombre + " " + apartados.apellido + barra + "Cuenta.txt");
        try {

            FileInputStream fis = new FileInputStream(url);
            Properties mostrar = new Properties();
            mostrar.load(fis);

            apartados.pagadoag = Double.parseDouble(mostrar.getProperty("Pagado"));
            apartados.fechaag = mostrar.getProperty("FechaDeRegistro");
            apartados.restanteag = Double.parseDouble(mostrar.getProperty("Restante"));
            apartados.abonoag = mostrar.getProperty("UltimoAbono");
            apartados.nombre = mostrar.getProperty("Nombre");
            apartados.apellido = mostrar.getProperty("Apellidos");
            apartados.totalag = Double.parseDouble(mostrar.getProperty("Total"));

        } catch (Exception e) {
        }
    }

    private void Actualizar() {

        try {
            FileWriter permite_escrito = new FileWriter(ubicacion + apartados.nombre + " " + apartados.apellido + barra + "Cuenta.txt");

            String Nombre = "Nombre=";
            String Apellidos = "Apellidos=";
            String Total = "Total=";
            String Pagado = "Pagado=";
            String UltimoAbono = "UltimoAbono=";
            String Restante = "Restante=";
            String FechaDeRegistro = "FechaDeRegistro=";

            PrintWriter guardar = new PrintWriter(permite_escrito);

            guardar.println(Nombre + apartados.nombre);
            guardar.println(Apellidos + apartados.apellido);
            guardar.println(Total + apartados.ntotal);
            guardar.println(Pagado + apartados.npagado);
            guardar.println(UltimoAbono + " $" + apartados.nabono + " el " + fechaActual());
            guardar.println(Restante + apartados.nrestante);
            guardar.println(FechaDeRegistro + apartados.fechaag);
            permite_escrito.close();
            ActualizarTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e);
        }
    }

    private void calcularTotal() {

        int count = tblArticulos.getRowCount();

        double total = 0;

        for (int x = 0; x < count; x++) {
            String preciostring = String.valueOf(tblArticulos.getValueAt(x, 1));
            double precio = Double.parseDouble(preciostring);
            total = precio + total;
        }

        apartados.total = total;
    }
    
    private void agregadoAbono(){
        
        String ubicacion = System.getProperty("user.dir") + barra + "BDClientes" + barra + apartados.nombre + " " + apartados.apellido + barra + "Abonos" + barra;

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
                crea.format("%s\r\n%s\r\n%s", "Abono=" + count, "Cantidad=" + apartados.nabono, "Fecha=" + ApartadosGrafico.fechaActual());
                crea.close();

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "El Abono no se pudo hacer correctamente");
            }
    }

    private void crearCuenta() {

        int contadorart = tblArticulos.getRowCount();

        String url = System.getProperty("user.dir") + barra + "BDClientes" + barra + apartados.nombre + " " + apartados.apellido + barra;
        String ubicacion2 = System.getProperty("user.dir") + barra + "BDClientes" + barra + apartados.nombre + " " + apartados.apellido + barra + "Abonos" + barra;

        String archivo = "Cuenta.txt";

        File crea_ubicacion = new File(url);
        File crea_archivo = new File(url + archivo);

        String ai = "Abono 1.txt";  // Abono incicial

        File ubicacion_ai = new File(ubicacion2);

        try {
            if (crea_archivo.exists()) {
                JOptionPane.showMessageDialog(this, "La cuenta ya existe");

                String btns[] = {"Añadir", "Cancelar"};

                int seguro;
                if (contadorart > 1) {
                    seguro = JOptionPane.showOptionDialog(this, "¿Añadir articulos a la cuenta? " + apartados.nombre + " " + apartados.apellido, "Agregar", 0, 0, null, btns, null);
                } else {
                    seguro = JOptionPane.showOptionDialog(this, "¿Añadir articulo a la cuenta? " + apartados.nombre + " " + apartados.apellido, "Agregar", 0, 0, null, btns, null);
                }

                if (seguro == JOptionPane.YES_OPTION) {

                    apartados.nabono = Double.parseDouble(txtImporte.getText());
                    double restanteextra = Double.parseDouble(txtRestante.getText());

                    cargarCuenta();

                    apartados.npagado = apartados.pagadoag + Double.parseDouble(txtImporte.getText());
                    apartados.nrestante = apartados.restanteag + restanteextra;
                    apartados.ntotal = apartados.totalag + Double.parseDouble(txtTotal.getText());

                    Actualizar(); // Actualizarlos a los nuevos/

                    CrearApartado();

                    if (contadorart > 1) {
                        JOptionPane.showMessageDialog(this, "Articulos añadidos a la cuenta de " + apartados.nombre + " " + apartados.apellido);
                    } else {
                        JOptionPane.showMessageDialog(this, "Articulo añadido a la cuenta de " + apartados.nombre + " " + apartados.apellido);
                    }
                    JOptionPane.showMessageDialog(this, "El nuevo restante es de $ " + apartados.nrestante);
                    agregadoAbono();
                    getarticulosCuenta();
                    getabonosCuenta();
                    apartados.imprimirAgregado();
                    limpiarVentana();
                    return;
                }
                if (seguro == JOptionPane.NO_OPTION) {
                }
            } else { // Se crea la cuenta
                crea_ubicacion.mkdirs();
                Formatter crea = new Formatter(url + archivo);
                crea.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s", "Nombre=" + txtNombre.getText(), "Apellidos=" + txtApellido.getText(),
                        "Total=" + apartados.total, "Pagado=" + txtImporte.getText(), "UltimoAbono=" + txtImporte.getText() + " el " + fechaActual(), "Restante=" + txtRestante.getText(), "FechaDeRegistro=" + fechaActual());
                crea.close();
                JOptionPane.showMessageDialog(this, "Cuenta creada exitosamente");
                CrearApartado();

                ubicacion_ai.mkdirs();   //Crea archivo con abono inicial
                Formatter formato = new Formatter(ubicacion2 + ai);
                formato.format("%s\r\n%s\r\n%s", "Abono=" + "1", "Cantidad=" + txtImporte.getText(), "Fecha=" + fechaActual());
                formato.close();

                getArticulos(); // Para el ticket
                apartados.generarTicket();
                JOptionPane.showMessageDialog(this, apartados.ticket);

                apartados.imprimirTicket();

                limpiarVentana();

            }

            ActualizarTabla();
            limpiarVentana();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            limpiarVentana();
        }
    }

    private void CrearApartado() {

        int contadorart = tblArticulos.getRowCount();

        for (int x = 0; x < contadorart; x++) {

            String ubicacion = System.getProperty("user.dir") + barra + "BDClientes" + barra + apartados.nombre + " " + apartados.apellido + barra + "Articulos" + barra;

            int count = 1;
            String archivo = "Articulo " + count + ".txt";
            File crea_ubicacion = new File(ubicacion);
            File crea_archivo = new File(ubicacion + archivo);

            while (crea_archivo.exists()) {            // Si existe, el contador aumenta

                count++;

                archivo = "Articulo " + count + ".txt";
                crea_ubicacion = new File(ubicacion);
                crea_archivo = new File(ubicacion + archivo);

                if (!crea_archivo.exists()) {  // hasta que encuentre uno que no exista
                    break;

                }
            }
            try {
                crea_ubicacion.mkdirs();
                Formatter crea = new Formatter(ubicacion + archivo);
                crea.format("%s\r\n%s\r\n%s", "Articulo=" + tblArticulos.getValueAt(x, 0), "Precio=" + tblArticulos.getValueAt(x, 1), "Fecha=" + fechaActual());
                crea.close();

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "El Registro no se pudo hacer correctamente");
            }
        }
    }

    public static void RegTabla() {

        for (int i = 0; i < carpetas.length; i++) {

            File url = new File(carpetas[i] + barra + "Cuenta.txt");

            try {
                FileInputStream fis = new FileInputStream(url);
                Properties mostrar = new Properties();
                mostrar.load(fis);

                String filas[] = {mostrar.getProperty("Nombre"), mostrar.getProperty("Apellidos"), mostrar.getProperty("Total"), mostrar.getProperty("Pagado"),
                    mostrar.getProperty("UltimoAbono"), mostrar.getProperty("Restante"), mostrar.getProperty("FechaDeRegistro")
                };

                dtm.addRow(filas);

            } catch (Exception e) {
            }
        }

        tblRegistros.setModel(dtm);
    }

    public static void ActualizarTabla() {
        carpetas = contenedor.listFiles();
        dtm.setRowCount(0);
        RegTabla();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        pnlbg = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        btnCerrar = new javax.swing.JLabel();
        pnlside = new javax.swing.JPanel();
        btnHome = new javax.swing.JPanel();
        lblhomeLogo = new javax.swing.JLabel();
        lblInicio = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();
        btnRegistros = new javax.swing.JPanel();
        lblhomeLogo3 = new javax.swing.JLabel();
        lblInicio3 = new javax.swing.JLabel();
        lblMaru1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblFechaTop = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();
        pnlHome = new javax.swing.JPanel();
        btnLimpiar = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        lblArticulo = new javax.swing.JLabel();
        txtArticulo = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        lblRestante = new javax.swing.JLabel();
        lblImporte = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtRestante = new javax.swing.JTextField();
        txtImporte = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        btnContinuar = new javax.swing.JLabel();
        pnltitleHome = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblArticulos = new javax.swing.JTable();
        btneliminarArt = new javax.swing.JLabel();
        pnlRegistros = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRegistros = new javax.swing.JTable();
        btnActualizar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        lblBuscar = new javax.swing.JLabel();
        pnlTitleReg = new javax.swing.JPanel();
        lblTitulo1 = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(1190, 670));
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        pnlbg.setBackground(new java.awt.Color(255, 255, 255));
        pnlbg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlTop.setBackground(new java.awt.Color(255, 255, 255));

        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Delete_32px.png"))); // NOI18N
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCerrarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                .addContainerGap(847, Short.MAX_VALUE)
                .addComponent(btnCerrar)
                .addContainerGap())
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(btnCerrar)
                .addContainerGap())
        );

        pnlbg.add(pnlTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, -1, -1));

        pnlside.setBackground(new java.awt.Color(54, 33, 89));
        pnlside.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnHome.setBackground(new java.awt.Color(85, 65, 118));
        btnHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHomeMouseClicked(evt);
            }
        });

        lblhomeLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblhomeLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Home_24px_1.png"))); // NOI18N

        lblInicio.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblInicio.setForeground(new java.awt.Color(204, 204, 204));
        lblInicio.setText("Inicio");

        javax.swing.GroupLayout btnHomeLayout = new javax.swing.GroupLayout(btnHome);
        btnHome.setLayout(btnHomeLayout);
        btnHomeLayout.setHorizontalGroup(
            btnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblhomeLogo)
                .addGap(26, 26, 26)
                .addComponent(lblInicio)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btnHomeLayout.setVerticalGroup(
            btnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnHomeLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(btnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblInicio)
                    .addComponent(lblhomeLogo))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pnlside.add(btnHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 134, 300, 60));
        pnlside.add(separator, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 96, 240, 20));

        btnRegistros.setBackground(new java.awt.Color(85, 65, 118));
        btnRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrosMouseClicked(evt);
            }
        });

        lblhomeLogo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblhomeLogo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Contacts_24px.png"))); // NOI18N

        lblInicio3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblInicio3.setForeground(new java.awt.Color(204, 204, 204));
        lblInicio3.setText("Registros");

        javax.swing.GroupLayout btnRegistrosLayout = new javax.swing.GroupLayout(btnRegistros);
        btnRegistros.setLayout(btnRegistrosLayout);
        btnRegistrosLayout.setHorizontalGroup(
            btnRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnRegistrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblhomeLogo3)
                .addGap(26, 26, 26)
                .addComponent(lblInicio3)
                .addContainerGap(170, Short.MAX_VALUE))
        );
        btnRegistrosLayout.setVerticalGroup(
            btnRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnRegistrosLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(btnRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblInicio3)
                    .addComponent(lblhomeLogo3))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pnlside.add(btnRegistros, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 300, -1));

        lblMaru1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblMaru1.setForeground(new java.awt.Color(255, 255, 255));
        lblMaru1.setText("Maru");
        pnlside.add(lblMaru1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Calendar_32px_2.png"))); // NOI18N
        jLabel7.setText("Fecha :");
        pnlside.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 590, 90, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Clock_32px.png"))); // NOI18N
        jLabel6.setText("Hora :");
        pnlside.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 630, -1, 30));

        lblFechaTop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFechaTop.setForeground(new java.awt.Color(255, 255, 255));
        pnlside.add(lblFechaTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 590, 170, 30));

        lblHora.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHora.setForeground(new java.awt.Color(255, 255, 255));
        pnlside.add(lblHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 630, 111, 30));

        pnlbg.add(pnlside, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 300, 670));

        pnlHome.setBackground(new java.awt.Color(255, 255, 255));
        pnlHome.setPreferredSize(new java.awt.Dimension(890, 670));

        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(54, 33, 89));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Erase_32px.png"))); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLimpiarMouseClicked(evt);
            }
        });

        lblNombre.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblNombre.setText("Nombre:");

        lblApellido.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblApellido.setText("Apellidos:");

        lblArticulo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblArticulo.setText("Articulos:");

        txtArticulo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtArticulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArticuloKeyPressed(evt);
            }
        });

        txtApellido.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtApellidoKeyPressed(evt);
            }
        });

        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNombreKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel4.setText("$");

        txtPrecio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPrecioKeyPressed(evt);
            }
        });

        lblRestante.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblRestante.setText("Resta:");

        lblImporte.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblImporte.setText("Abono");

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblTotal.setText("Total:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("$");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("$");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("$");

        txtRestante.setEditable(false);
        txtRestante.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtImporte.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtImporte.setText("100");
        txtImporte.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtImporteFocusGained(evt);
            }
        });
        txtImporte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtImporteKeyPressed(evt);
            }
        });

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnContinuar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnContinuar.setForeground(new java.awt.Color(54, 33, 89));
        btnContinuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Forward_32px.png"))); // NOI18N
        btnContinuar.setText("Continuar");
        btnContinuar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnContinuarMouseClicked(evt);
            }
        });
        btnContinuar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnContinuarKeyPressed(evt);
            }
        });

        pnltitleHome.setBackground(new java.awt.Color(122, 72, 221));

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Crear cuenta de apartado");

        javax.swing.GroupLayout pnltitleHomeLayout = new javax.swing.GroupLayout(pnltitleHome);
        pnltitleHome.setLayout(pnltitleHomeLayout);
        pnltitleHomeLayout.setHorizontalGroup(
            pnltitleHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnltitleHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnltitleHomeLayout.setVerticalGroup(
            pnltitleHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnltitleHomeLayout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(null);

        tblArticulos.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tblArticulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblArticulos.setGridColor(new java.awt.Color(255, 255, 255));
        tblArticulos.setSelectionBackground(new java.awt.Color(122, 72, 221));
        jScrollPane2.setViewportView(tblArticulos);

        btneliminarArt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Trash_Can_40px.png"))); // NOI18N
        btneliminarArt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btneliminarArtMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlHomeLayout = new javax.swing.GroupLayout(pnlHome);
        pnlHome.setLayout(pnlHomeLayout);
        pnlHomeLayout.setHorizontalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnltitleHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlHomeLayout.createSequentialGroup()
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlHomeLayout.createSequentialGroup()
                                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlHomeLayout.createSequentialGroup()
                                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(pnlHomeLayout.createSequentialGroup()
                                                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblTotal)
                                                    .addComponent(lblImporte))
                                                .addGap(70, 70, 70))
                                            .addGroup(pnlHomeLayout.createSequentialGroup()
                                                .addComponent(lblRestante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(40, 40, 40)))
                                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel8))
                                        .addGap(27, 27, 27)
                                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtTotal)
                                            .addComponent(txtImporte, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                            .addComponent(txtRestante)))
                                    .addComponent(lblNombre)
                                    .addComponent(lblApellido)
                                    .addComponent(lblArticulo)
                                    .addGroup(pnlHomeLayout.createSequentialGroup()
                                        .addGap(112, 112, 112)
                                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(txtApellido)
                                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(pnlHomeLayout.createSequentialGroup()
                                                .addComponent(txtArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel4)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(37, 37, 37))
                            .addGroup(pnlHomeLayout.createSequentialGroup()
                                .addComponent(jScrollPane2)
                                .addGap(18, 18, 18)))
                        .addComponent(btneliminarArt)
                        .addGap(0, 57, Short.MAX_VALUE))
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(120, Short.MAX_VALUE))
        );
        pnlHomeLayout.setVerticalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHomeLayout.createSequentialGroup()
                .addComponent(pnltitleHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre))
                .addGap(17, 17, 17)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblApellido))
                .addGap(16, 16, 16)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblArticulo)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(btneliminarArt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE))
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)))
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(jLabel8)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(lblImporte))
                .addGap(18, 18, 18)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRestante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(lblRestante))
                .addGap(27, 27, 27)
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar)
                    .addComponent(btnContinuar))
                .addContainerGap())
        );

        pnlbg.add(pnlHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, 890, 610));

        pnlRegistros.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistros.setPreferredSize(new java.awt.Dimension(890, 670));

        tblRegistros.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tblRegistros.setGridColor(new java.awt.Color(255, 255, 255));
        tblRegistros.setSelectionBackground(new java.awt.Color(122, 72, 221));
        tblRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRegistrosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRegistros);

        btnActualizar.setText("Actualizar tabla");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarKeyTyped(evt);
            }
        });

        lblBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8_Search_24px.png"))); // NOI18N

        pnlTitleReg.setBackground(new java.awt.Color(122, 72, 221));

        lblTitulo1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo1.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo1.setText("Registros");

        javax.swing.GroupLayout pnlTitleRegLayout = new javax.swing.GroupLayout(pnlTitleReg);
        pnlTitleReg.setLayout(pnlTitleRegLayout);
        pnlTitleRegLayout.setHorizontalGroup(
            pnlTitleRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTitleRegLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(297, Short.MAX_VALUE))
        );
        pnlTitleRegLayout.setVerticalGroup(
            pnlTitleRegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTitleRegLayout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addComponent(lblTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout pnlRegistrosLayout = new javax.swing.GroupLayout(pnlRegistros);
        pnlRegistros.setLayout(pnlRegistrosLayout);
        pnlRegistrosLayout.setHorizontalGroup(
            pnlRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitleReg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlRegistrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistrosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnActualizar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistrosLayout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        pnlRegistrosLayout.setVerticalGroup(
            pnlRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistrosLayout.createSequentialGroup()
                .addComponent(pnlTitleReg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(pnlRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnActualizar)
                .addContainerGap())
        );

        pnlbg.add(pnlRegistros, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, 890, 610));

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

    private void btnHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeMouseClicked
        pnlRegistros.setVisible(false);
        pnlHome.setVisible(true);
        setColor(btnHome);
        resetColor(btnRegistros);
        txtNombre.requestFocus();
    }//GEN-LAST:event_btnHomeMouseClicked

    private void btnRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrosMouseClicked
        pnlHome.setVisible(false);
        pnlRegistros.setVisible(true);
        setColor(btnRegistros);
        resetColor(btnHome);
        txtBuscar.requestFocus();
    }//GEN-LAST:event_btnRegistrosMouseClicked

    private void txtArticuloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArticuloKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtPrecio.requestFocus();
        }
    }//GEN-LAST:event_txtArticuloKeyPressed

    private void txtApellidoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtArticulo.requestFocus();
        }
    }//GEN-LAST:event_txtApellidoKeyPressed

    private void txtNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtApellido.requestFocus();
        }
    }//GEN-LAST:event_txtNombreKeyPressed

    private void txtPrecioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            double precio;
            String articulo;

            if (!txtArticulo.getText().equals("")) {
                articulo = txtArticulo.getText();
            } else {
                JOptionPane.showMessageDialog(this, "El campo articulo esta vacio");
                txtArticulo.requestFocus();
                return;

            }

            try {
                precio = Double.parseDouble(txtPrecio.getText());
            } catch (NumberFormatException ex) {
                if (txtPrecio.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "El campo precio esta vacio");
                } else {
                    JOptionPane.showMessageDialog(this, "Inserte solo numeros para el campo precio");
                }
                txtPrecio.requestFocus();
                return;
            }

            String datos[] = {articulo, String.valueOf(precio)};

            modelo.addRow(datos);
            txtArticulo.setText("");
            txtPrecio.setText("");
            txtArticulo.requestFocus();
        }
    }//GEN-LAST:event_txtPrecioKeyPressed

    private void txtImporteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            apartados.total = Double.parseDouble(txtTotal.getText());
            apartados.abono = Double.parseDouble(txtImporte.getText());

            if (apartados.abono >= apartados.total) {

                JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
                return;
            } else {

                apartados.restante = apartados.total - apartados.abono;
            }

            txtRestante.setText(String.valueOf(apartados.restante));

            btnContinuar.requestFocus();
        }
    }//GEN-LAST:event_txtImporteKeyPressed

    public static String nombrecuenta = "", apellidocuenta = "";
    
    private void tblRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRegistrosMouseClicked
        
        int seleccion = tblRegistros.getSelectedRow();
        nombrecuenta = String.valueOf(tblRegistros.getValueAt(seleccion, 0));
        apellidocuenta = String.valueOf(tblRegistros.getValueAt(seleccion, 1));
        
        Cuenta abrir = new Cuenta();
        abrir.setVisible(true);


    }//GEN-LAST:event_tblRegistrosMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        ActualizarTabla();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtBuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyTyped

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {

                trs.setRowFilter(RowFilter.regexFilter("(?i)" + txtBuscar.getText(), 0, 1));
            }
        });

        trs = new TableRowSorter(dtm);
        tblRegistros.setRowSorter(trs);
    }//GEN-LAST:event_txtBuscarKeyTyped

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        int x = evt.getXOnScreen();  // Ubicacion (X, Y) de la venata
        int y = evt.getYOnScreen();

        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        xMouse = evt.getX(); // ubicacion del mouse (X, Y)
        yMouse = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void cerrar() {

        WindowEvent cerrar = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(cerrar);
    }

    private void btnContinuarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContinuarMouseClicked

        if (txtNombre.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo nombre esta vacio");
            txtNombre.requestFocus();
            return;
        }
        if (txtApellido.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo apellidos esta vacio");
            txtApellido.requestFocus();
            return;
        }
        int a = tblArticulos.getRowCount(); // Si no hay articulos en la tabla
        if (a == 0) {
            JOptionPane.showMessageDialog(this, "Registre por lo menos un articulo");
            txtArticulo.requestFocus();
            return;
        }

        try {
            apartados.abono = Double.parseDouble(txtImporte.getText());
        } catch (NumberFormatException ex) {
            if (txtImporte.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "El campo abono esta vacio");
            } else {
                JOptionPane.showMessageDialog(this, "Inserte solo numeros para el campo abono");
            }
            txtImporte.requestFocus();
            return;
        }

        calcularTotal();

        apartados.abono = Double.parseDouble(txtImporte.getText());
        apartados.restante = apartados.total - apartados.abono;

        if (apartados.abono >= apartados.total) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
            txtImporte.requestFocus();
            return;

        } else {
            txtRestante.setText(String.valueOf(apartados.restante));
            txtImporte.setText(String.valueOf(apartados.abono));
            txtTotal.setText(String.valueOf(apartados.total));
        }

        apartados.nombre = txtNombre.getText();
        apartados.apellido = txtApellido.getText();

        crearCuenta();
    }//GEN-LAST:event_btnContinuarMouseClicked

    private void btnCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarMouseClicked
        cerrar();
    }//GEN-LAST:event_btnCerrarMouseClicked

    private void btnLimpiarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLimpiarMouseClicked
        limpiarVentana();
        txtNombre.requestFocus();
    }//GEN-LAST:event_btnLimpiarMouseClicked

    private void btneliminarArtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btneliminarArtMouseClicked
        int fila = tblArticulos.getSelectedRow();
        if (fila >= 0) {
            modelo.removeRow(fila);
            txtTotal.setText("");
            txtImporte.setText("");
            txtRestante.setText("");
            txtArticulo.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Indique el articulo a eliminar");
        }

    }//GEN-LAST:event_btneliminarArtMouseClicked

    private void txtImporteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtImporteFocusGained

        if (tblArticulos.getRowCount() <= 0) {
            txtArticulo.requestFocus();
            JOptionPane.showMessageDialog(this, "Registre por lo menos un articulo");

        } else {

            calcularTotal();
            txtTotal.setText(String.valueOf(apartados.total));

            int importe = tblArticulos.getRowCount();
            importe = importe * 100;

            txtImporte.setText(String.valueOf(importe));

            txtImporte.requestFocus();

        }
    }//GEN-LAST:event_txtImporteFocusGained

    private void btnContinuarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnContinuarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtNombre.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo nombre esta vacio");
            txtNombre.requestFocus();
            return;
        }
        if (txtApellido.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo apellidos esta vacio");
            txtApellido.requestFocus();
            return;
        }
        int a = tblArticulos.getRowCount(); // Si no hay articulos en la tabla
        if (a == 0) {
            JOptionPane.showMessageDialog(this, "Registre por lo menos un articulo");
            txtArticulo.requestFocus();
            return;
        }

        try {
            apartados.abono = Double.parseDouble(txtImporte.getText());
        } catch (NumberFormatException ex) {
            if (txtImporte.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "El campo abono esta vacio");
            } else {
                JOptionPane.showMessageDialog(this, "Inserte solo numeros para el campo abono");
            }
            txtImporte.requestFocus();
            return;
        }

        calcularTotal();

        apartados.abono = Double.parseDouble(txtImporte.getText());
        apartados.restante = apartados.total - apartados.abono;

        if (apartados.abono >= apartados.total) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
            txtImporte.requestFocus();
            return;

        } else {
            txtRestante.setText(String.valueOf(apartados.restante));
            txtImporte.setText(String.valueOf(apartados.abono));
            txtTotal.setText(String.valueOf(apartados.total));
        }

        apartados.nombre = txtNombre.getText();
        apartados.apellido = txtApellido.getText();

        crearCuenta();
        }
    }//GEN-LAST:event_btnContinuarKeyPressed

    void setColor(JPanel panel) {
        panel.setBackground(new Color(85, 65, 118));
    }

    void resetColor(JPanel panel) {
        panel.setBackground(new Color(64, 43, 100));
    }

    public static String fechaActual() {

        Date fecha = new Date();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/YYYY");

        return formatoFecha.format(fecha);
    }

    public static String hora() {
        Calendar calendario = new GregorianCalendar();
        Date horaactual = new Date();
        calendario.setTime(horaactual);
        String hora = calendario.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calendario.get(Calendar.HOUR_OF_DAY) : "0" + calendario.get(Calendar.HOUR_OF_DAY);
        String minutos = calendario.get(Calendar.MINUTE) > 9 ? "" + calendario.get(Calendar.MINUTE) : "0" + calendario.get(Calendar.MINUTE);
        String segundos = calendario.get(Calendar.SECOND) > 9 ? "" + calendario.get(Calendar.SECOND) : "0" + calendario.get(Calendar.SECOND);

        String horas = hora + ":" + minutos;
        return horas;

    }

    public void run() {

        Thread current = Thread.currentThread();

        while (current == hilo) {
            hora();
            lblHora.setText(hora());
        }

    }

    public void limpiarVentana() {
        txtTotal.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtArticulo.setText("");
        txtPrecio.setText("");
        txtRestante.setText("");
        txtImporte.setText("100");
        txtBuscar.setText("");
        txtNombre.requestFocus();
        modelo.setRowCount(0);

    }

    public void getArticulos() {

        apartados.contadorarts = tblArticulos.getRowCount(); // Obtenemos el numero de filas de articulos

        apartados.articulo = new String[apartados.contadorarts]; // Arreglo tipo String
        apartados.precio = new double[apartados.contadorarts];  // Arreglo tipo double
        String[] preciostring = new String[apartados.contadorarts];

        for (int x = 0; x < apartados.contadorarts; x++) {

            apartados.articulo[x] = String.valueOf(tblArticulos.getValueAt(x, 0)); // Columna de articulos 0

            preciostring[x] = String.valueOf(tblArticulos.getValueAt(x, 1));

            apartados.precio[x] = Double.parseDouble(preciostring[x]);  // Columna de precios 1

        }

    }
    
    private void getarticulosCuenta() {
      
        String ubi = ubicacion + apartados.nombre + " " + apartados.apellido + barra + "Articulos" + barra;

        File cont = new File(ubi);
        File[] arts = cont.listFiles();

        apartados.contartscuenta = arts.length;
        apartados.articulo = new String[arts.length];
        apartados.articulofecha = new String[arts.length];
        apartados.precio = new double[arts.length];

        for (int i = arts.length - 1; i >= 0; i--) {
            
            try {
                FileInputStream fis = new FileInputStream(arts[i]);
                Properties mostrar = new Properties();
                mostrar.load(fis);

                apartados.articulo[i] = mostrar.getProperty("Articulo");
                apartados.precio[i] = Double.parseDouble(mostrar.getProperty("Precio"));
                apartados.articulofecha[i] = mostrar.getProperty("Fecha");

            } catch (Exception e) {
            }


        
        }
    }
    
    private void getabonosCuenta() {
      
        String ubi = ubicacion + apartados.nombre + " " + apartados.apellido + barra + "Abonos" + barra;

        File cont = new File(ubi);
        File[] abonos = cont.listFiles();
        
        apartados.contabonoscuenta = abonos.length;
        apartados.abonos = new double[abonos.length];
        apartados.abonosfecha = new String[abonos.length];
        apartados.abonoskey = new String[abonos.length];

        for (int i = 0; i < abonos.length; i++) {

            try {
                FileInputStream fis = new FileInputStream(abonos[i]);
                Properties mostrar = new Properties();
                mostrar.load(fis);

                apartados.abonoskey[i] = mostrar.getProperty("Abono");
                apartados.abonos[i] = Double.parseDouble(mostrar.getProperty("Cantidad"));
                apartados.abonosfecha[i] = mostrar.getProperty("Fecha");

            } catch (Exception e) {
            }
                
            }
        }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ApartadosGrafico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ApartadosGrafico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ApartadosGrafico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ApartadosGrafico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ApartadosGrafico().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JLabel btnCerrar;
    private javax.swing.JLabel btnContinuar;
    private javax.swing.JPanel btnHome;
    private javax.swing.JLabel btnLimpiar;
    private javax.swing.JPanel btnRegistros;
    private javax.swing.JLabel btneliminarArt;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblArticulo;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblFechaTop;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblImporte;
    private javax.swing.JLabel lblInicio;
    private javax.swing.JLabel lblInicio3;
    private javax.swing.JLabel lblMaru1;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRestante;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTitulo1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblhomeLogo;
    private javax.swing.JLabel lblhomeLogo3;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlRegistros;
    private javax.swing.JPanel pnlTitleReg;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlbg;
    private javax.swing.JPanel pnlside;
    private javax.swing.JPanel pnltitleHome;
    private javax.swing.JSeparator separator;
    private javax.swing.JTable tblArticulos;
    public static javax.swing.JTable tblRegistros;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtArticulo;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtImporte;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtRestante;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
