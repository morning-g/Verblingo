/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion_verbos;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**Frame principal de la aplicacion.
 * @author Verblingo
 * @version 1.0
*/
public class JFrame_Verbos extends javax.swing.JFrame {
    
    /*Declaracion de variables globales y componentes que son utilizados
        dentro de JFrame_Verbos
    */
    
    //variables publicas (JFrame_Test puede acceder a ellas)
    
    public HashSet<String> hs_verbos_guardados; //Contiene los nombres de los verbos guardados por el usuario
    public ArrayList<Verbo> hs_verbos_a_practicar; //Almacena los verbos a practicar. Se mandan mediante jButTest
    public Lista[] lista_verbos; //Arreglo de listas, cada elemento contiene una lista de verbos segun la inicial (A-Z)
    
    private Verbo verbo_actual; //verbo que esta mostrado en la pantalla
    private final int total_niveles = 3;//Irregular, BasicRegular, HardRegular
    private JButton[] menuIzq = new JButton[total_niveles];//Menu de botones mostrado del lado izq. De la pantalla
    private JButton[] botonesABC;//Botones con inicial A-Z
    
    private Color color_azul = new Color( 21, 67, 96); //Color muy utilizado por diferentes componentes
    private int nivel_seleccionado = 0; //0->Irregular, 1->BasicRegular, 2->HardRegular
    private DefaultTableModel modelo; //modelo para jTableLista
    
    //Etiquetas para poner el ejemplo y la descripcion de un verbo
    private JLabel jLabelEjemplo;
    private JLabel jLabelEjemplo2;
    private JLabel jLabelDescripcion;
    private JLabel jLabelDescripcion2;
    
    private JButton jButConjugacion;
    private JButton jButTest;
    private JButton jButExit;
    /**
     * Crea un JFrame_Verbos y se inicializan valores.
     * Se mandan a llamar metodos para inicializar componentes.
     */
    public JFrame_Verbos() {
        initComponents();
        
        hs_verbos_guardados = new HashSet<String>();
        modelo = (DefaultTableModel) jTableLista.getModel();//obtener modelo de jTableLista
        
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(  214, 234, 248  ));
        
        leerArchivo("Irregular"); //Abrir por defecto la aplicacion con el archivo 'Irregular.txt'
        
        iniciarBotones();
        
        ponerEtiquetasEjemplo();
        ponerEtiquetasDescripcion();
        
        agregarBotonesABC();
        agregarMenuIzq();
        iniciarInfo();
        iniciarTabla();
        
        menuIzq[0].doClick(); //Se da click al boton de 'Irregular' para que empiece por defecto
    }
    
    /**
     * Metodo de accion del boton jButExit.
     * Primero se asegura de guardar la informacion que no ha sido guardada 
     * y despues cierra JFrame_Verbos.
     * @since version 1.0
     */
    public void jButExitAccion(){
        ActionListener oyente = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar_verbos_aprendidos(hs_verbos_guardados);
                dispose();
            }
        };
        jButExit.addActionListener(oyente);
    }
    
    /**
     * Metodo de accion del boton jButConjugacion
     * Obtiene la informacion del verbo actual y abre una pagina web
     * donde se muestran todas las conjugaciones correspondientes.
     */
    public void jButConjugacionAccion(){
        ActionListener oyente = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String verbo_nombre = verbo_actual.getNombre(); //Obtener nombre del verbo actual
                    
                    String url = "https://conjugator.reverso.net/conjugation-english-verb-" +
                            verbo_nombre + ".html"; //URL con el nombre del verbo actual

                    //obtener nombre del sistema operativo en que se esta corriendo el programa
                    String sistemaOperativo = System.getProperty("os.name").toLowerCase();

                    //Crear objeto Runtime para ejecutar el comando de abrir acceder a una URL
                    Runtime rt = Runtime.getRuntime();
                    if (sistemaOperativo.contains("linux")) {
                        rt.exec("xdg-open " + url);
                    } else if (sistemaOperativo.contains("windows")) {
                        rt.exec("cmd /c start " + url);
                    } else if (sistemaOperativo.contains("mac")) {
                        rt.exec("open " + url);
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        jButConjugacion.addActionListener(oyente);
    }
    
    /**
     * Metodo de accion del boton jButTest Pregunta al usuario si desea realizar
     * un test de verbos personalizados o verbos aleatorios. En base a la
     * respuesta abre un JFrame_Test para realizar un examen.
     */
    public void jButTestAccion(){
        try{
        JFrame_Verbos jf_verbos = this;
        ActionListener oyente = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String respuesta_usuario = "0"; //inicializar la respuesta con un valor no valido
                
                /*Preguntar al usuario por una opcion, hasta que se ingrese una respuesta
                valida o hasta que se de click en Cancelar
                */
                do {
                    respuesta_usuario = JOptionPane.showInputDialog( jf_verbos,
                            "Teclea 1 para practicar verbos aleatorios."
                            + "\nTecla 2 para practicar verbos personalizados (guardados).", 
                            DISPOSE_ON_CLOSE); //Abrir JOptionPane para guardar la respuesta del usuario
                    if(respuesta_usuario == null) //Si el usuario da click en cancelar solamente se cierra el JOptionPane
                        return;
                }while (!respuesta_usuario.equals("1") && !respuesta_usuario.equals("2"));
                
                if(respuesta_usuario.equals("2") && hs_verbos_guardados.isEmpty()){
                    /*Si el usuario quiere practicar verbos personalizados pero no
                    tiene guardado ninguno solamente se regresa el control a JFrame_Verbos
                    */
                    JOptionPane.showMessageDialog(rootPane, "No tienes ningun verbo guardado para practicar.");
                    return;
                }else{ 
                    /*Si el usuario escogio la opcion de verbos al azar se escogen 10 verbos
                    aleatorios a practicar. Se esconde JFrame_Verbos y se actica JFrame_Test
                    */
                    
                    generarTestRandom(respuesta_usuario);
                    JFrame_Test jft = new JFrame_Test(jf_verbos);
                    jft.setVisible(true);
                    jf_verbos.setVisible(false);
                }
                
}
        };
        
        jButTest.addActionListener(oyente);
        
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
    }
    
    /**
     * Escoge verbos de forma pseudoaleatoria para practicarlos.
     *
     * @param eleccion_usuario Objeto String que contiene la eleccion del
     * usuario segun lo obtenido en jButTestAccion
     * @see #jButTestAccion()
     */
    public void generarTestRandom(String eleccion_usuario){
        //Objeto HashSet para guardar temporalmente los verbos a practicar
        hs_verbos_a_practicar = new ArrayList<Verbo>();
        
        if(eleccion_usuario.equals("1")){
            while (hs_verbos_a_practicar.size() < 10) { //Hacer hasta que se tengan 10 verbos en hs_verbos_a_practicar
                
                //Crear objeto azar entre 0 y 25 (Son 26 letras del abecedario)
                Random azar = new Random();
                int index = azar.nextInt(13);
                
                //Apuntar a la cabecera de la lista con el indice que se genero aleatoriamente
                Nodo p = lista_verbos[index].getCabecera();

                //Si existen verbos en la lista correspondiente...
                if (p != null) {
                    
                    //Obtener numero al azar entre 0 y la longitud de la lista actual
                    int limit = azar.nextInt(lista_verbos[index].length());
                    //int limit = StdRandom.uniform(0, lista_verbos[index].length());

                    
                    //Recorrer lista hasta llegar al limite generado anteriormente
                    for (int j = 0; j < limit; j++)
                        p = p.getSiguiente();
                    
                    
                    /*Si el verbo escogido aleatoriamente no esta dentro de 
                     hs_verbos_a_practicar entonces añadirlo, en caso contrario
                     se hace nada para evitar duplicados
                    */
                    if(!hs_verbos_a_practicar.contains(p.getVerbo()))
                        hs_verbos_a_practicar.add(p.getVerbo());
                }

            }
        }else if (eleccion_usuario.equals("2")){
            int total_verbos_dificiles = 10; //establecer la cantidad de verbos a practicar con valor de 10 por defecto
            
            if(hs_verbos_guardados.size() < 10)//Si hay menos de 10 verbos guardados 
                total_verbos_dificiles = hs_verbos_guardados.size(); //cambiar la cantidad de verbos a practicar a la cantidad de verbos guardados
            
           //arreglo que contendra todos los verbos guardados por el usuario 
            String[] arreglo_verbos = new String[hs_verbos_guardados.size()];
            
            //copiar los verbos guardados en el nuevo arreglo arreglo_verbos
            int i = 0;
            for (String e:hs_verbos_guardados)
                arreglo_verbos[i++] = e;
            
            //Ordenar aleatoriamente arreglo_verbos
            Random r = new Random();
            for (int j = arreglo_verbos.length; j > 0; j--) {
                int posicion = r.nextInt(j);
                String tmp = arreglo_verbos[j - 1];
                arreglo_verbos[j - 1] = arreglo_verbos[posicion];
                arreglo_verbos[posicion] = tmp;
            }
            
            /*Añadir los primeros valores de arreglo_verbos desde 0 hasta 
            total_verbos_dificiles y añadirlos a hs_verbos_a_practicar
            */
            for(int j = 0; j < total_verbos_dificiles; j++){
                int index = arreglo_verbos[j].charAt(0) - 65;
                hs_verbos_a_practicar.add(lista_verbos[index].buscarPorNombre(arreglo_verbos[j]));
            }
                
        }
    }
    
    /**
     * Metodo para crear botones jButTest, jButExit y jButConjugacion. Crea los
     * botones correspondientes, establece su texto, fuente, tamaño, posicion y
     * los añade a JFrame_Verbos.
     */
    public void iniciarBotones(){
        //Obtener paquete en forma de arreglo de las fuentes disponibles
        Font [] f = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        Font fuente1 = f[41]; //fuente escogida
        
        //codigo jButTest
        jButTest = new JButton();
        jButTest.setFont(new Font(fuente1.getName(), Font.PLAIN, 16));
        jButTest.setForeground(Color.white);
        jButTest.setBackground(new Color( 46, 64, 83 ));
        this.add(jButTest);
        jButTest.setText("Test");
        jButTest.setBounds(690, 20, 115, 80);
        
        
        //codigo de jButExit y jButConjugacion
        
        jButExit = new JButton("Salir");
        jButConjugacion = new JButton("Conjugaciones");
        
        jButExit.setForeground(Color.white);
        jButConjugacion.setForeground(Color.white);
        
        String nombre = jButTest.getFont().getName();
        Font fuente = new Font(nombre,Font.PLAIN, 16);
        
        jButExit.setFont(fuente);
        jButConjugacion.setFont(fuente);
        
        this.add(jButExit);
        this.add(jButConjugacion);
        
        jButExit.setBounds(0, 340, 150, 80);
        jButExit.setBackground(new Color(21, 67, 96));
        
        jButConjugacion.setBounds(0, 260, 150, 80);
        jButConjugacion.setBackground(new Color(21, 67, 96));
        
        //llamar a los metodos donde se establecen las acciones a realizar para cada boton
        jButConjugacionAccion();
        jButTestAccion();
        jButExitAccion();
    }
    
    /**
     * Metodo para crear las etiquetas donde se pondra la descripcion del verbo.
     * Crea las etiquetas correspondientes, establece su texto, fuente, tamaño,
     * posicion y las añade a jPanel1.
     */
    public void ponerEtiquetasDescripcion(){
        //extrar fuente
        Font fuente = new Font(jButTest.getFont().getName(), Font.BOLD, 16);
        
        jLabelDescripcion = new JLabel(" ");
        jLabelDescripcion2 = new JLabel(" ");
        
        jLabelDescripcion.setFont(fuente);
        jLabelDescripcion2.setFont(fuente);
        
        jLabelDescripcion.setHorizontalAlignment(SwingConstants.CENTER);
        jLabelDescripcion2.setHorizontalAlignment(SwingConstants.CENTER);
        
        jPanel1.add(jLabelDescripcion);
        jPanel1.add(jLabelDescripcion2);
        
        //Obtener posicion de jLabelEjemplo para jLabelDescripcion2 40 px mas arriba
        Point l = jLabelEjemplo.getLocation();
        jLabelDescripcion2.setBounds(10, l.y - 40, jPanel1.getWidth() - 10, 20);
        
        //Obtener posicion de jLabelDescripcion2 para poner jLabelDescripcion mas arriba
        l = jLabelDescripcion2.getLocation();
        jLabelDescripcion.setBounds(10, l.y - 20, jPanel1.getWidth() - 10, 20);
        
    }
    
    /**
     * Metodo para crear las etiquetas donde se pondra la oracion de ejemplo del
     * verbo. Crea las etiquetas correspondientes, establece su texto, fuente,
     * tamaño, posicion y las añade a jPanel1.
     */
    public void ponerEtiquetasEjemplo(){
        //Extraer fuente a utilizar
        Font fuente = new Font(jButTest.getFont().getName(), Font.PLAIN, 16);
        
        jLabelEjemplo = new JLabel(" ");
        jLabelEjemplo2 = new JLabel(" ");
        JLabel etiquetaEj = new JLabel("Ejemplo:");
        
        etiquetaEj.setFont(fuente);
        jLabelEjemplo2.setFont(fuente);
        jLabelEjemplo.setFont(fuente);
        
        jPanel1.add(jLabelEjemplo2);
        jPanel1.add(jLabelEjemplo);
        jPanel1.add(etiquetaEj);
        
        /*
        Obtener posicion de jLabelTraducir para colocar las etiquetas jLabelEjemplo
            y jLabelEjemplo2 encima de ella.
        */
        Point l = jLabelTraducir.getLocation();
        jLabelEjemplo2.setBounds(l.x-10, l.y - 30, 550, 20);
        
        l = jLabelEjemplo2.getLocation();
        jLabelEjemplo.setBounds(l.x, l.y - 20, 550, 20);
        
        l = jLabelEjemplo.getLocation();
        etiquetaEj.setBounds(l.x-80, l.y, 80, 20);
    }
    
    /**
     * Añade el evento de click de renglon a jTableLista. Cada renglon de esta
     * tabla tiene las conjugaciones de un mismo verbo, por lo que al ser
     * seleccionado un renglon se mostrara en pantalla toda su informacion.
     */
    public void iniciarTabla(){
        jTableLista.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent event) {
            if(jTableLista.getSelectedRow() >= 0){ //validar que la tabla tenga al menos 1 renglon
                //extrar nombre(presente simple) que esta en la columna 0 del renglon seleccionado
                String nombre_verbo = jTableLista.getValueAt(jTableLista.getSelectedRow(), 0).toString();
                
                verbo_actual = BusqHash.Buscar(lista_verbos, nombre_verbo);
                actualizarInfoVerbo();
            }
        }
    });
    }
    
    /**
     * Actualiza la informacion del verbo actual segun el nivel seleccionado. 
     * El primer verbo de Irregular es Arise. 
     * El primer verbo de BasicRegular es Abandon. 
     * El primer verbo de HardRegular es Abolish
     */
    public void iniciarInfo(){
        String vb = "";
        if(nivel_seleccionado == 0)
            vb = "Arise";
        else if (nivel_seleccionado == 1)
            vb = "Abandon";
        else 
            vb = "Abolish";
        verbo_actual = BusqHash.Buscar(lista_verbos, vb);
        actualizarInfoVerbo();
    }
      
    /**
     * Metodo de accion para los botones de los tres niveles.
     *
     * @param boton Objeto JButton representando el boton al que se le desea
     * añadir la accion.
     * @param nombre_archivo Objeto String representando el nombre del archivo
     * segun el nivel {Irregular, BasicRegular, HardRegular}
     * @param nivel_actual Valor entero que representa el nivel al que se
     * cambiara el usuario{0:Irregular, 1:BasicRegular, 2:HardRegular}
     */
    public void jButNivelVerbos(JButton boton, String nombre_archivo, int nivel_actual){
        ActionListener oyente = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                //se crea una copia de los verbos guardados del nivel actual
                HashSet hs_aux = copiarHashSet();
                
                leerArchivo(nombre_archivo);
                leerArchivo_aprendidos(nombre_archivo+"_aprendidos");
                
                //se guardan los verbos aprendidos contenidos en la copia
                guardar_verbos_aprendidos(hs_aux);
                
                actualizarInfoVerbo();
                nivel_seleccionado  = nivel_actual; //se actualiza el nivel
                iniciarInfo();
                
                //Se actualizan los colores de los botones, el boton seleccionado es mas oscuro
                for(JButton b:menuIzq)
                    b.setBackground(color_azul);
                boton.setBackground(new Color( 46, 64, 83 ));
                
                actualizarTablaNivel(0);//actualizar la tabla
                
            }
        };
        boton.addActionListener(oyente);
    }
    
    /**
     * Crea el menu de la zona izquierda de la pantalla, compuesto de 3 botones:
     * El primero es para acceder al nivel Irregular. El segundo es para acceder
     * al nivel BasicRegular. El tercero es para acceder al nivel HardRegular
     */
    public void agregarMenuIzq(){
        int N = total_niveles; //total_niveles = 3
        int y = 20;
        
        //Extraer fuente a utilizar
        String nombre = jButTest.getFont().getName();
        Font fuente = new Font(nombre,Font.PLAIN, 16);
        
        //Crear cada boton, establecer la fuente y tamaño de su texto, ademas de su posicion y color de fondo
        for(int i = 0; i < N; i++){
            menuIzq[i] = new JButton();
            menuIzq[i].setForeground(Color.white);
            menuIzq[i].setFont(fuente);
            this.add(menuIzq[i]);
            menuIzq[i].setBounds(0, y, 150, 80);
            menuIzq[i].setBackground(color_azul);
            y += 80;
        }
        
        menuIzq[0].setText("Irregular");
        menuIzq[1].setText("Basic regular");
        menuIzq[2].setText("Hard regular");
        
        //Establecer su accion mediante el metodo jButNivelVerbos
        jButNivelVerbos(menuIzq[0], "Irregular",0);
        jButNivelVerbos(menuIzq[1], "BasicRegular",1);
        jButNivelVerbos(menuIzq[2], "HardRegular",2);
        
    }
    
    /**
     * Crea los botones de la parte superior, cada uno representando a una letra
     * del abecedario (A-Z)
     */
    public void agregarBotonesABC(){
        botonesABC = new JButton[26]; //crear arreglo de botones
        int x = 160;
        
        //Crear cada boton, establecer la fuente y tamaño de su texto, ademas de su posicion y color de fondo
        for(int i = 0; i < botonesABC.length; i++){
            char c = (char) (i+65);
            botonesABC[i] = new JButton(c+"");
            botonesABC[i].setBackground(new Color( 46, 64, 83 ));
            botonesABC[i].setForeground(Color.white);
            this.add(botonesABC[i]);
            if(i < 13){
                botonesABC[i].setBounds(x, 20, 40, 40);
            }
            else{
                if(i == 13) x = 160;
                botonesABC[i].setBounds(x, 60, 40, 40);
            }
            x += 40;
            
            /*
            Añadir la accion de que cada que se haga click en el boton, se muestre en pantalla
            solamente los verbos que empiezan con la inicial que representa el boton.
            */
            ActionListener oyente = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = (int) c - 65;
                    Lista lista_letra = lista_verbos[index];
                    Nodo p = lista_letra.getCabecera();
                    
                    if(p == null){
                        JOptionPane.showMessageDialog(rootPane, "No hay verbos con esta inicial.");
                        return;
                    }
                    
                    modelo.setRowCount(0);
                    verbo_actual = p.getVerbo();
                    actualizarInfoVerbo();;
                    Object[] renglon = new Object[3];
                    while(p != null){
                        renglon[0] = p.getVerbo().getNombre();
                        renglon[1] = p.getVerbo().getPasado_simple();
                        renglon[2] = p.getVerbo().getPasado_participio();
                        modelo.addRow(renglon);
                        p = p.getSiguiente();
                    }
                }
            };

            botonesABC[i].addActionListener(oyente);
            
        }
        
    }
    
    /**
     * Actualiza los datos de la tabla segun el nivel seleccionado.
     *
     * @param index_p Entero que representa el indice(0-25) que se pasara a
     * lista_verbos para utilizar la lista correspondiente.
     */
    public void actualizarTablaNivel(int index_p){
        int index = index_p;
        Lista lista_letra = lista_verbos[index];
        Nodo p = lista_letra.getCabecera();//obtener referencia a la cabecera de la lista

        modelo.setRowCount(0);
        verbo_actual = p.getVerbo();//se pone como verbo actual al primer elemento de la lista
        actualizarInfoVerbo();
        
        Object[] renglon = new Object[3];//arreglo para guardar las tres conjugaciones de un verbo

        //navegar por la lista e ir añadiendo a la tabla los datos del verbo
        while (p != null) {
            renglon[0] = p.getVerbo().getNombre();
            renglon[1] = p.getVerbo().getPasado_simple();
            renglon[2] = p.getVerbo().getPasado_participio();
            modelo.addRow(renglon);
            p = p.getSiguiente();
        }
    }
    
    /**
     * Actualiza las etiquetas y los botones que estan relacionados a la
     * informacion de un verbo.
     */
    public void actualizarInfoVerbo() {
        if (hs_verbos_guardados.contains(verbo_actual.getNombre())) {
            /*Si el verbo actual esta dentro de los verbos guardados por el usuario
            se mostrara su nombre en letras rojas y el boton jButGuardar mostrara la
            opcion de APRENDIDO
            */
            jLabelVerbo.setForeground(Color.red);
            jButGuardar.setText("APRENDIDO");
        } else {
            /*En caso contrario se mostrara el nombre con letras negras, y el usuario
            vera el boton con el texto GUARDAR
            */
            
            jButGuardar.setText("GUARDAR");
            jLabelVerbo.setForeground(Color.BLACK);
        }
        jLabelTraducir.setText("");
        jLabelVerbo.setText(verbo_actual.getNombre());
        jLabelSimple.setText(verbo_actual.getPasado_simple() + " / "
                + verbo_actual.getPasado_participio());

        /*Obtener la descripcion del verbo actual y llamar al metodo ajustarEtiquetas
        que se encarga de sincronizar ambas etiquetas
        */
        String descripcion = verbo_actual.getDescripcion();
        ajustarEtiquetas(jLabelDescripcion, jLabelDescripcion2, 60, descripcion);

        /*Obtener la oracion de ejemplo del verbo actual y llamar al metodo ajustarEtiquetas
        que se encarga de sincronizar ambas etiquetas
        */
        String ejemplo = verbo_actual.getEjemplos();
        ajustarEtiquetas(jLabelEjemplo, jLabelEjemplo2, 70, ejemplo);

    }
    
    /**
     * Sincroniza las etiquetas pasadas por parametro para que puedan mostrar
     * una cadena de longitud grande.
     *
     * @param label1 Objeto JLabel que representa la primer etiqueta (superior).
     * @param label2 Objeto JLabel que representa la segunda etiqueta
     * (inferior).
     * @param longitud_permitida Entero que representa la longitud de caracteres
     * permitidos en label1.
     * @param cadena Objeto String que representa la cadena que se desea
     * sincronizar en ambas etiquetas.
     */
    public void ajustarEtiquetas(JLabel label1, JLabel label2, int longitud_permitida, String cadena){
        label1.setText("");
        label2.setText("");
        
        int longitud_cadena = cadena.length();

        if (longitud_cadena > longitud_permitida) {
            String[] cadena_dividida = cadena.split(" "); //se separa la cadena en palabras
            
            String frase1 = "";
            String prueba = "";
            int contador_de_caracteres = 0; 
            
            while (true) {
                //Se van añadiendo las palabras una por una en una variable de prueba
                prueba = frase1.concat(cadena_dividida[contador_de_caracteres++] + " ");
                if (prueba.length() <= longitud_permitida) //si prueba tiene menos caracteres de los permitidos se pasa a frase1
                    frase1 = prueba;
                else //sino se rompe el ciclo
                    break;
            }
            
            label1.setText(frase1);

            String frase2 = "";
            //se sigue escribiendo la frase2 apartir del caracter que se dejo guardado en el ciclo anterior
            for (int i = contador_de_caracteres - 1; i < cadena_dividida.length; i++)
                frase2 = frase2.concat(cadena_dividida[i]) + " ";
            

            label2.setText(frase2);
        } else {
            label1.setText(cadena);
        }
    }
    
    /**
     * Copia los valores de los verbos guardados hasta el momento.
     *
     * @return Un objeto HashSet que es una copia de
     * hs_verbos_guardados.
     */
    public HashSet<String> copiarHashSet(){
        HashSet<String> hs_copia = new HashSet<String>();
        for (String e:hs_verbos_guardados) {
            String e_new = new String(e);
            hs_copia.add(e_new);
        }
        return hs_copia;
    }
    
    /**
     * Guarda en un archivo los verbos aprendidos hasta el momento.
     *
     * @param verbos_guardados Objeto HashSet que contiene los verbos
     * guardados por el usuario.
     */
    public void guardar_verbos_aprendidos(HashSet<String> verbos_guardados){
        String nombre_archivo = "";
        if(nivel_seleccionado == 0)
            nombre_archivo = "Irregular_aprendidos";
        else if (nivel_seleccionado == 1)
            nombre_archivo = "BasicRegular_aprendidos";
        else 
            nombre_archivo = "HardRegular_aprendidos";
        
        PrintWriter pw = null;
        try{    
            pw = new PrintWriter("archivos_verbos/"+nombre_archivo+".txt");  
            
            for(String e: verbos_guardados)
                pw.println(e);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            if (pw != null)
                pw.close();
        }
    }
    
    /**
     * Lee el archivo correspondiente que contiene los verbos guardados por
     * el usuario.
     *
     * @param nombre_archivo Objeto String que representa el nombre del archivo
     */
    public void leerArchivo_aprendidos(String nombre_archivo){
        FileReader fr = null;
        BufferedReader br = null;
        hs_verbos_guardados.clear();//asegurar que no hs_verbos_guardados no tenga otros verbos
        try{
            File f = new File("archivos_verbos/"+nombre_archivo+".txt");
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            while(br.ready())
                hs_verbos_guardados.add(br.readLine());//agregar los nombres leidos a hs_verbos_guardados
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Lee el archivo correspondiente que contiene todos los verbos de un nivel.
     *
     * @param nombre_archivo Objeto String que representa el nombre del archivo.
     * {Irregular, BasicRegular, HardRegular}
     */
    public void leerArchivo(String nombre_archivo){
        FileReader fr = null;
        BufferedReader br = null;
        try {
            File f = new File("archivos_verbos/"+nombre_archivo+".txt");
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            //se crea un objeto Lista[] que contendra los verbos ordenados alfabeticamente
            lista_verbos = new Lista[26];
            for (int i = 0; i < lista_verbos.length; i++)
                lista_verbos[i] = new Lista();
            
            /*
            Mientras haya informacion en el archivo de texto se leera renglon 
            por renglon la informacion de un verbo, despues se insertaran dichos
            verbos segun su inicial dentro de lista_verbos
            */
            while (br.ready()) {
                String[] datos = br.readLine().split("/");
                Verbo x = new Verbo(datos[0],datos[1],datos[2],
                                datos[3],datos[4],datos[5]);
                char c = x.getNombre().charAt(0);
                int indice = (int) c - 65;
                lista_verbos[indice].InsOrden(x);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if(br!= null)
                try {
                    br.close();
                    if(fr != null)
                        fr.close();
            } catch (IOException ex) {
                Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void crearPDF() {

        if (hs_verbos_guardados.size() == 0) {
            JOptionPane.showMessageDialog(rootPane, "No tienes verbos guardados!");
            return;
        }

        Document document = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        chooser.setDialogTitle("Guarda tu lista de verbos");
        chooser.setApproveButtonText("Guardar"); 
        
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            try {
                document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(new File(chooser.getSelectedFile(), "lista_de_verbos.pdf")));

                document.open();

                Paragraph intro = new Paragraph("Lista de verbos");
                Paragraph space = new Paragraph(" ");

                PdfPTable table = new PdfPTable(4);
                float[] columnWidths = new float[]{17f, 15f, 15f, 30f};
                table.setWidths(columnWidths);

                PdfPCell c1 = new PdfPCell(new Paragraph("Simple present"));
                PdfPCell c2 = new PdfPCell(new Paragraph("Simple past"));
                PdfPCell c3 = new PdfPCell(new Paragraph("Past participle"));
                PdfPCell c4 = new PdfPCell(new Paragraph("Significado"));

                table.addCell(c1);
                table.addCell(c2);
                table.addCell(c3);
                table.addCell(c4);

                Verbo[] arreglo_verbos = new Verbo[hs_verbos_guardados.size()];
                int i = 0;
                for (String e : hs_verbos_guardados) {
                    Verbo actual = BusqHash.Buscar(lista_verbos, e);
                    arreglo_verbos[i++] = actual;
                }
                Arrays.sort(arreglo_verbos);

                for (Verbo actual : arreglo_verbos) {
                    c1 = new PdfPCell(new Paragraph(actual.getNombre()));
                    c2 = new PdfPCell(new Paragraph(actual.getPasado_simple()));
                    c3 = new PdfPCell(new Paragraph(actual.getPasado_participio()));
                    c4 = new PdfPCell(new Paragraph(actual.getTraduccion()));
                    table.addCell(c1);
                    table.addCell(c2);
                    table.addCell(c3);
                    table.addCell(c4);
                }

                document.add(intro);
                document.add(space);
                document.add(table);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (document != null) {
                    document.close();
                }
                JOptionPane.showMessageDialog(rootPane, "Archivo lista_de_verbos.pdf guardado con exito.");
            }
        
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelVerbo = new javax.swing.JLabel();
        jLabelSimple = new javax.swing.JLabel();
        jButTraducir = new javax.swing.JButton();
        jLabelTraducir = new javax.swing.JLabel();
        jButGuardar = new javax.swing.JButton();
        jButNext = new javax.swing.JButton();
        jLabelVerbo1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLista = new javax.swing.JTable();
        jButBuscar = new javax.swing.JButton();
        jTextBuscar = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuImpimir = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuEjecutar = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuGorditas = new javax.swing.JMenuItem();
        jMenuVerbos = new javax.swing.JMenuItem();
        jMenuAcercaVerblingo = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuAcercaAplicacion = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuUsuario = new javax.swing.JMenuItem();
        jMenuTecnico = new javax.swing.JMenuItem();
        jMenuReportar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(212, 230, 241));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(31, 97, 141)));
        jPanel1.setForeground(new java.awt.Color(31, 97, 141));

        jPanel2.setBackground(new java.awt.Color(212, 230, 241));
        jPanel2.setPreferredSize(new java.awt.Dimension(250, 100));

        jLabelVerbo.setFont(new java.awt.Font("Capture it", 1, 36)); // NOI18N
        jLabelVerbo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVerbo.setText("Verb");
        jLabelVerbo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelVerbo.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabelSimple.setFont(new java.awt.Font("OpenDyslexic", 2, 18)); // NOI18N
        jLabelSimple.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSimple.setText("Simple/Perfect");
        jLabelSimple.setPreferredSize(new java.awt.Dimension(200, 22));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelSimple, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jLabelVerbo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelVerbo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSimple, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButTraducir.setBackground(new java.awt.Color(40, 55, 71));
        jButTraducir.setForeground(new java.awt.Color(255, 255, 255));
        jButTraducir.setText("TRADUCIR");
        jButTraducir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButTraducirActionPerformed(evt);
            }
        });

        jLabelTraducir.setBackground(new java.awt.Color(255, 255, 255));
        jLabelTraducir.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        jLabelTraducir.setForeground(new java.awt.Color(0, 0, 0));
        jLabelTraducir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jButGuardar.setBackground(new java.awt.Color(40, 55, 71));
        jButGuardar.setForeground(new java.awt.Color(255, 255, 255));
        jButGuardar.setText("GUARDAR");
        jButGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButGuardarActionPerformed(evt);
            }
        });

        jButNext.setBackground(new java.awt.Color(40, 55, 71));
        jButNext.setForeground(new java.awt.Color(255, 255, 255));
        jButNext.setText("SIGUIENTE");
        jButNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButNextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButTraducir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTraducir, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(jButGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButNext))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButTraducir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTraducir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabelVerbo1.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        jLabelVerbo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVerbo1.setText("Verb");
        jLabelVerbo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelVerbo1.setPreferredSize(new java.awt.Dimension(100, 30));

        jPanel3.setBackground(new java.awt.Color(212, 230, 241));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(31, 97, 141)));

        jTableLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Present simple", "Past Simple", "Past Participle"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLista.setGridColor(new java.awt.Color(102, 153, 255));
        jScrollPane1.setViewportView(jTableLista);

        jButBuscar.setBackground(new java.awt.Color(40, 55, 71));
        jButBuscar.setForeground(new java.awt.Color(255, 255, 255));
        jButBuscar.setText("BUSCAR VERBO");
        jButBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButBuscarActionPerformed(evt);
            }
        });

        jTextBuscar.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(jTextBuscar))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTextBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButBuscar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jMenuImpimir.setText("Imprimir");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Imprimir lista");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenuImpimir.add(jMenuItem5);

        jMenuBar1.add(jMenuImpimir);

        jMenuEjecutar.setText("Aplicaciones");

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Ejecutor");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenuEjecutar.add(jMenuItem6);

        jMenuGorditas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuGorditas.setText("Gorditas");
        jMenuGorditas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuGorditasActionPerformed(evt);
            }
        });
        jMenuEjecutar.add(jMenuGorditas);

        jMenuVerbos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuVerbos.setText("Verbos");
        jMenuVerbos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuVerbosActionPerformed(evt);
            }
        });
        jMenuEjecutar.add(jMenuVerbos);

        jMenuBar1.add(jMenuEjecutar);

        jMenuAcercaVerblingo.setText("Acerca De");

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("VerbLingo");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenuAcercaVerblingo.add(jMenuItem8);

        jMenuAcercaAplicacion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuAcercaAplicacion.setText("Aplicacion");
        jMenuAcercaAplicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAcercaAplicacionActionPerformed(evt);
            }
        });
        jMenuAcercaVerblingo.add(jMenuAcercaAplicacion);

        jMenuBar1.add(jMenuAcercaVerblingo);

        jMenu4.setText("Ayuda");

        jMenuUsuario.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuUsuario.setText("Manual Usuario");
        jMenuUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuUsuarioActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuUsuario);

        jMenuTecnico.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuTecnico.setText("Manual Tecnico");
        jMenuTecnico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuTecnicoActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuTecnico);

        jMenuReportar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuReportar.setText("Reportar Problema");
        jMenuReportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuReportarActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuReportar);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(163, 163, 163)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(253, 253, 253)
                    .addComponent(jLabelVerbo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(253, 253, 253)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(107, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(188, 188, 188)
                    .addComponent(jLabelVerbo1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(189, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButTraducirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButTraducirActionPerformed
        // Muestra la traduccion del verbo actual
        jLabelTraducir.setText(verbo_actual.getTraduccion());
    }//GEN-LAST:event_jButTraducirActionPerformed

    private void jButBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButBuscarActionPerformed
        // Busca el verbo deseado dentro de todos los verbos del nivel actual
        try {
            String nombre_verbo = jTextBuscar.getText().trim().toLowerCase();
            int valor_char = (int) nombre_verbo.charAt(0) - 32;

            if (valor_char < 0) {
                return;
            }

            nombre_verbo = ((char) valor_char)
                    + nombre_verbo.substring(1, nombre_verbo.length());

            Verbo aux = verbo_actual;
            verbo_actual = BusqHash.Buscar(lista_verbos, nombre_verbo);
            if (verbo_actual != null) {
                aux = verbo_actual;
                int index = (int) verbo_actual.getNombre().charAt(0) - 65;
                botonesABC[index].doClick();
                verbo_actual = aux;
                actualizarInfoVerbo();
            } else {
                verbo_actual = aux;
                JOptionPane.showMessageDialog(rootPane, "Verbo no encontrado.");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Error al buscar el verbo. Revisa que este bien escrito.");
        }
        
    }//GEN-LAST:event_jButBuscarActionPerformed

    private void jButGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButGuardarActionPerformed
        // Guarda un verbo seleccionado por el usuario
        if(!hs_verbos_guardados.contains(verbo_actual.getNombre())){
            hs_verbos_guardados.add(verbo_actual.getNombre());
            jLabelVerbo.setForeground(Color.red);
            jButGuardar.setText("APRENDIDO");
        }
            
        else{
            hs_verbos_guardados.remove(verbo_actual.getNombre());
            jLabelVerbo.setForeground(Color.BLACK);
            jButGuardar.setText("GUARDAR");
        }
            
    }//GEN-LAST:event_jButGuardarActionPerformed

    private void jButNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButNextActionPerformed
        // Accede al siguiente verbo en la lista actual, si el siguiente verbo
        // esta marcado como guardado entonces se lo saltara
        
        int index = verbo_actual.getNombre().charAt(0) - 65;
        Nodo p = lista_verbos[index].buscarVerbo(verbo_actual);
        char c = (char)(index+65);
        
        while(p.getSiguiente() != null && hs_verbos_guardados.contains(p.getSiguiente().getVerbo().getNombre())){
            System.out.println(p.getVerbo().getNombre());
            p = p.getSiguiente();
        }
        
        if (p.getSiguiente() == null || hs_verbos_guardados.contains(p.getSiguiente().getVerbo().getNombre())){
            JOptionPane.showMessageDialog(rootPane, "Todos los verbos con la inicial "
                    + c + " han sido vistos.");
            return;
        }
        if(!hs_verbos_guardados.contains(p.getSiguiente().getVerbo().getNombre())){
            verbo_actual = p.getSiguiente().getVerbo();
            actualizarInfoVerbo();
        }
    }//GEN-LAST:event_jButNextActionPerformed

    private void jMenuTecnicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuTecnicoActionPerformed
        // TODO add your handling code here:
        String archivo = "dist/javadoc/index.html";
        try {
           
            String url = archivo;

            //obtener nombre del sistema operativo en que se esta corriendo el programa
            String sistemaOperativo = System.getProperty("os.name").toLowerCase();
            
            //Crear objeto Runtime para ejecutar el comando de abrir acceder a una URL
            Runtime rt = Runtime.getRuntime();
            if(sistemaOperativo.contains("linux"))
                rt.exec("xdg-open " + url);
            else if(sistemaOperativo.contains("windows"))
                rt.exec("cmd /c start " + url);
            else if(sistemaOperativo.contains("mac"))
                rt.exec("open " + url);
        } catch (IOException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuTecnicoActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // crear un pdf de la lista de verbos que el usuario guardo
        crearPDF();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        AcercaDeVerbLingo advl = new AcercaDeVerbLingo();
        advl.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuAcercaAplicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAcercaAplicacionActionPerformed
        AcercaDeApp ada = new AcercaDeApp();
        ada.setVisible(true);
    }//GEN-LAST:event_jMenuAcercaAplicacionActionPerformed

    private void jMenuReportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuReportarActionPerformed
        JOptionPane.showMessageDialog(null, "Para reportar alguna falla de la aplicacion "+
                                            "\npuedes enviarnos un correo a:"+
                                            "\n\ncontacto.verblingo@gmail.com"
                                            +"\n\n¡Gracias!");
    }//GEN-LAST:event_jMenuReportarActionPerformed

    private void jMenuGorditasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuGorditasActionPerformed
        // TODO add your handling code here:
        String directorio_actual = System.getProperty("user.dir");
        directorio_actual = directorio_actual.substring(2);
        
        String separador = "\\";
        
        String []directorio_nuevo = directorio_actual.replaceAll(Pattern.quote(separador), "\\\\").split("\\\\");
        String directorio_gorditas = "";
        for (int i = 0; i < directorio_nuevo.length-1; i++) {
            directorio_gorditas += directorio_nuevo[i]+"\\";
        }
        
        directorio_gorditas += "Gorditas\\";
        
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("java -jar C:\\"+directorio_gorditas+"Gorditas.jar");
            this.dispose();
        } catch (IOException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuGorditasActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
                String directorio_actual = System.getProperty("user.dir");
        directorio_actual = directorio_actual.substring(2);
        
        String separador = "\\";
        
        String []directorio_nuevo = directorio_actual.replaceAll(Pattern.quote(separador), "\\\\").split("\\\\");
        String directorio_gorditas = "";
        for (int i = 0; i < directorio_nuevo.length-1; i++) {
            directorio_gorditas += directorio_nuevo[i]+"\\";
        }
        
        directorio_gorditas += "appEjecutar\\";
        
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("java -jar C:\\"+directorio_gorditas+"appEjecutar.jar");
            this.dispose();
        } catch (IOException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuVerbosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuVerbosActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(rootPane, "Ya te encuentras en la aplicacion de verbos");
    }//GEN-LAST:event_jMenuVerbosActionPerformed

    private void jMenuUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuUsuarioActionPerformed
        // TODO add your handling code here:
        String archivo = "manuales/manual_usuario.pdf";
        try {
           
            String url = archivo;

            //obtener nombre del sistema operativo en que se esta corriendo el programa
            String sistemaOperativo = System.getProperty("os.name").toLowerCase();
            
            //Crear objeto Runtime para ejecutar el comando de abrir acceder a una URL
            Runtime rt = Runtime.getRuntime();
            if(sistemaOperativo.contains("linux"))
                rt.exec("xdg-open " + url);
            else if(sistemaOperativo.contains("windows"))
                rt.exec("cmd /c start " + url);
            else if(sistemaOperativo.contains("mac"))
                rt.exec("open " + url);
        } catch (IOException ex) {
            Logger.getLogger(JFrame_Verbos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuUsuarioActionPerformed

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
            java.util.logging.Logger.getLogger(JFrame_Verbos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Verbos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Verbos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Verbos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_Verbos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButBuscar;
    private javax.swing.JButton jButGuardar;
    private javax.swing.JButton jButNext;
    private javax.swing.JButton jButTraducir;
    private javax.swing.JLabel jLabelSimple;
    private javax.swing.JLabel jLabelTraducir;
    private javax.swing.JLabel jLabelVerbo;
    private javax.swing.JLabel jLabelVerbo1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuAcercaAplicacion;
    private javax.swing.JMenu jMenuAcercaVerblingo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEjecutar;
    private javax.swing.JMenuItem jMenuGorditas;
    private javax.swing.JMenu jMenuImpimir;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuReportar;
    private javax.swing.JMenuItem jMenuTecnico;
    private javax.swing.JMenuItem jMenuUsuario;
    private javax.swing.JMenuItem jMenuVerbos;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableLista;
    private javax.swing.JTextField jTextBuscar;
    // End of variables declaration//GEN-END:variables
}
