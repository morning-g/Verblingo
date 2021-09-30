/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion_verbos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**Frame que contiene el test para el usuario.
 * @author Verblingo
 * @version 1.0
*/
public class JFrame_Test extends javax.swing.JFrame {

    //variables globales de JFrame_Test
    
    private JFrame_Verbos jf_verbos; //Referencia a frame principal
    
    private JButton[] botones_opciones;
    private JButton[] etiquetas_progreso;
    private JButton jButRegresar;
    
    private Verbo[] lista_verbos;
    private Verbo verbo_actual;
    private int numero_verbo_actual = 0;
    private int respuestas_correctas = 0;
    private String verbo_seleccionado = "";
    
    private JLabel jLabelDescripcion1;
    private JLabel jLabelDescripcion2;
    private JLabel jLabelTraduccion;
    
    /**
     * Crea un nuevo JFrame_Test y se inicializan valores.
     * Se mandan a llamar metodos para inicializar componentes.
     */
    public JFrame_Test(JFrame_Verbos jfv) {
        initComponents();
        
        jf_verbos = jfv; //inicializar referencia tipo JFrame_Verbos
        
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(  214, 234, 248  ));
        
        etiquetasProgreso(jf_verbos.hs_verbos_a_practicar.size());
        
        iniciarEtiquetas();
        
        jButRegresar();
        
        guardarBotonesEnArreglo();
        
        iniciarVerbos();
        
        jButNext.setEnabled(false);
    }

    /**
     * Guarda los botones jButVerbo1, jButVerbo2, jButVerbo3 en un arreglo
     * JButton para facilitar su manipulacion.
     */
    public void guardarBotonesEnArreglo(){
        botones_opciones = new JButton[3];
        botones_opciones[0] = jButVerbo1;
        botones_opciones[1] = jButVerbo2;
        botones_opciones[2] = jButVerbo3;
        for(JButton b:botones_opciones)
            accionBotonOpcion(b);
    }
    
    /**
     * Inicializa lista_verbos con los verbos tiene
     * jf_verbos.hs_verbos_a_practica. Ademas actualiza la informacion con los
     * datos del primer elemento.
     */
    public void iniciarVerbos(){
        int i = 0;
        lista_verbos = new Verbo[jf_verbos.hs_verbos_a_practicar.size()];
        for(Verbo e:jf_verbos.hs_verbos_a_practicar)
            lista_verbos[i++] = e;
        actualizarInfoVerbo(0);
    }
    
    /**
     * Actualiza las etiquetas y los componentes segun el numero de verbo
     * deseado.
     *
     * @param numero_verbo Entero que representa el numero de verbo contenido en
     * lista_verbos[]
     */
    public void actualizarInfoVerbo(int numero_verbo){
        verbo_actual = lista_verbos[numero_verbo];
        String str = verbo_actual.getDescripcion();
        ajustarEtiquetas(jLabelDescripcion1, jLabelDescripcion2, 65, str);
        jLabelTraduccion.setText("Significado: " + verbo_actual.getTraduccion());
        ponerVerbosAleatorios();
    }
    
    /**
     * Pone de forma aleatoria los nombres de dos verbos obtenidos tambien
     * aleatoriamente junto con el nombre del verbo correcto dentro de los
     * botones que estan en botones_opciones[].
     */
    public void ponerVerbosAleatorios(){
        int [] numerosAleatorios = {0,1,2};
        Random r = new Random();
        for (int i = numerosAleatorios.length; i > 0; i--) {
            int posicion = r.nextInt(i);
            int tmp = numerosAleatorios[i - 1];
            numerosAleatorios[i - 1] = numerosAleatorios[posicion];
            numerosAleatorios[posicion] = tmp;
        }
        botones_opciones[numerosAleatorios[0]].setText(verbo_actual.getNombre());
        int i = 1;
        for(Verbo v:generarDosVerbosAleatorios())
            botones_opciones[numerosAleatorios[i++]].setText(v.getNombre()); 
    }
    
    /**
     * Genera dos verbos de forma aleatoria
     * 
     * @return Objeto HashSet que contiene los dos verbos generados.
     */
    public HashSet<Verbo> generarDosVerbosAleatorios(){
        HashSet<Verbo> hs_dos_verbos = new HashSet<Verbo>();
        Random azar = new Random();
        while (hs_dos_verbos.size() < 2) {
            int index = azar.nextInt(13);
            Nodo p = jf_verbos.lista_verbos[index].getCabecera();

            if (p != null) {
                int limit = azar.nextInt(jf_verbos.lista_verbos[index].length());

                for (int j = 0; j < limit; j++)
                    p = p.getSiguiente();
                
                if(!hs_dos_verbos.contains(p.getVerbo()))
                    hs_dos_verbos.add(p.getVerbo());
            }
        }
        return hs_dos_verbos;
    }
    
    /**
     * Metodo para crear las etiquetas donde se pondra la descripcion y la
     * traduccion del verbo. Crea las etiquetas correspondientes, establece su
     * texto, fuente, tamaño, posicion y las añade a jPanel1.
     */
    public void iniciarEtiquetas(){
        Font fuente = new Font(Font.DIALOG, Font.BOLD, 16);
        jLabelDescripcion1 = new JLabel("");
        jLabelDescripcion2 = new JLabel("");
        jLabelTraduccion = new JLabel("");
        
        jLabelDescripcion1.setFont(fuente);
        jLabelDescripcion2.setFont(fuente);
        jLabelTraduccion.setFont(fuente);
        
        jLabelDescripcion1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabelDescripcion2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabelTraduccion.setHorizontalAlignment(SwingConstants.CENTER);
        
        jPanel1.add(jLabelDescripcion1);
        jPanel1.add(jLabelDescripcion2);
        jPanel1.add(jLabelTraduccion);
 
        jLabelDescripcion2.setBounds(10, jPanel1.getHeight() - 65, jPanel1.getWidth() - 10, 20);
        
        Point l = jLabelDescripcion2.getLocation();
        jLabelDescripcion1.setBounds(10, l.y - 20, jPanel1.getWidth() - 10, 20);
        
        jLabelTraduccion.setBounds(10, l.y + 30, jPanel1.getWidth() - 10, 20);
        
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
    public void ajustarEtiquetas(JLabel label1, JLabel label2, int longitud_permitida, String cadena) {
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
     * Metodo de creacion y accion del boton jButRegresar. Permite regresar a
     * JFrame_Verbos.
     */
    public void jButRegresar(){
        jButRegresar = new JButton("Regresar");
        this.add(jButRegresar);
        jButRegresar.setBounds(10,20, 110, 40);
        jButRegresar.setBackground(new Color( 46,64,83 ));
        jButRegresar.setForeground(Color.white);
        jButRegresar.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        
        JFrame_Test jf_test = this;
        
        ActionListener oyente = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf_test.dispose();
                jf_verbos.setVisible(true);
            }
        };
        jButRegresar.addActionListener(oyente);
        
    }
    
    /**
     * Metodo de accion de los botones de botones_opciones[]. Permite distinguir
     * que boton ha sido seleccionado y se pasa su texto a verbo_seleccionado
     */
    public void accionBotonOpcion(JButton boton){
        ActionListener oyente = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(JButton b:botones_opciones)
                    b.setBackground(new Color( 21, 67, 96));
                boton.setBackground(new Color( 46, 64, 83 ));;
                verbo_seleccionado = boton.getText();
            }
        };
        boton.addActionListener(oyente);
    }
    
    /**
     * Crea y muestra en pantalla los botones que serviran como etiquetas de
     * progreso para el test.
     * 
     * @param numero_etiquetas Entero que representa la cantidad de etiquetas a
     * crear, segun jf_verbos.hs_verbos_a_practicar.size()
     */
    public void etiquetasProgreso(int numero_etiquetas){
        etiquetas_progreso = new JButton[numero_etiquetas]; 
        
        int x = 130;
        for(int i = 0; i < etiquetas_progreso.length; i++){
            etiquetas_progreso[i] = new JButton();
            etiquetas_progreso[i].setBackground(new Color( 212, 230, 241 ));
            etiquetas_progreso[i].setForeground(Color.white);
            this.add(etiquetas_progreso[i]);
            if(i < 13){
                etiquetas_progreso[i].setBounds(x, 20, 40, 40);
            }
            x += 45;
        }
    }
    
    /**
     * Deshabilita los componentes correspondientes.
     */
    public void bloquearComponentes(){
        for (JButton b : botones_opciones) {
            b.setEnabled(false);
        }
        jTextSimple.setEnabled(false);
        jTextParticiple.setEnabled(false);
        jButCheck.setEnabled(false);
    }
    
    /**
     * Limpia los componentes relacionados con un verbo.
     */
    public void limpiarComponentes(){
        jTextSimple.setText("");
        jTextParticiple.setText("");
        for(JButton b:botones_opciones)
            b.setBackground(new Color( 21, 67, 96));
    }
    
    /**
     * Habilita los componentes correspondientes.
     */
    public void desbloquearComponentes(){
        for (JButton b : botones_opciones) {
            b.setEnabled(true);
        }
        jTextSimple.setEnabled(true);
        jTextParticiple.setEnabled(true);
        jButCheck.setEnabled(true);
        
    }
    
    /**
     * Evalua si algunos componentes estan seleccionados o vacios.
     * 
     * @return false si hay algun componente sin seleccionar o un campo vacio.
     * true si todo esta en orden.
     */
    private boolean componentesEstanSeleccionados(){
        if(verbo_seleccionado.isEmpty() || jTextSimple.getText().isEmpty() ||
                jTextParticiple.getText().isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Asegurate de tener seleccionado el verbo "
                    + "y llenar los campos de las conjugaciones.");
            return false;
        }
        return true;           
    }
    
    /**
     * Muestra los resultados obtenidos por el usuario al finalizar el test.
     */
    private void mostrarResultados(){
        
        double porcentaje_resultado = respuestas_correctas * 100 / 
                jf_verbos.hs_verbos_a_practicar.size();
        
        String mensaje = "Respuestas correctas: " + respuestas_correctas + "/" +
                jf_verbos.hs_verbos_a_practicar.size();
        
        JOptionPane.showMessageDialog(rootPane, mensaje);
        
        if(porcentaje_resultado == 100)
            JOptionPane.showMessageDialog(rootPane, "¡Felicidades! Obtuviste un puntaje perfecto.\n"
                    + "Sigue practicando para no perder ritmo.");
        else if (porcentaje_resultado > 80)
            JOptionPane.showMessageDialog(rootPane, "¡Bien hecho! Tu puntaje es muy bueno.\n"
                    + "Sigue practicando para lograr la perfeccion.");
        else if (porcentaje_resultado > 60)
            JOptionPane.showMessageDialog(rootPane, "¡Vas por buen camino! Tu puntaje es aprobatorio.\n"
                    + "Sigue practicando para mejorar.");
        else if (porcentaje_resultado > 40)
            JOptionPane.showMessageDialog(rootPane, "¡Ups! Puedes hacerlo mejor.\n"
                    + "Dedica un poco mas tiempo a la practica y veras los resultados.");
        else
            JOptionPane.showMessageDialog(rootPane, "¡No te desanimes!\n"
                    + "Dedica un poco mas tiempo a la practica y veras los resultados.");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new javax.swing.JInternalFrame();
        jPanel1 = new javax.swing.JPanel();
        jButVerbo1 = new javax.swing.JButton();
        jButVerbo3 = new javax.swing.JButton();
        jButVerbo2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextSimple = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextParticiple = new javax.swing.JTextField();
        jButCheck = new javax.swing.JButton();
        jButNext = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(214, 234, 248));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        jPanel1.setForeground(new java.awt.Color(214, 234, 248));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );

        jButVerbo1.setBackground(new java.awt.Color(21, 67, 96));
        jButVerbo1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButVerbo1.setForeground(new java.awt.Color(255, 255, 255));
        jButVerbo1.setText("Display");

        jButVerbo3.setBackground(new java.awt.Color(21, 67, 96));
        jButVerbo3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButVerbo3.setForeground(new java.awt.Color(255, 255, 255));
        jButVerbo3.setText("Interview");

        jButVerbo2.setBackground(new java.awt.Color(21, 67, 96));
        jButVerbo2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButVerbo2.setForeground(new java.awt.Color(255, 255, 255));
        jButVerbo2.setText("Bake");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel1.setText("Simple Past");

        jTextSimple.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel2.setText("Past Participle");

        jTextParticiple.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N

        jButCheck.setBackground(new java.awt.Color(46, 64, 83));
        jButCheck.setFont(new java.awt.Font("Dialog", 1, 15)); // NOI18N
        jButCheck.setForeground(new java.awt.Color(255, 255, 255));
        jButCheck.setText("CHECAR");
        jButCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButCheckActionPerformed(evt);
            }
        });

        jButNext.setBackground(new java.awt.Color(46, 64, 83));
        jButNext.setFont(new java.awt.Font("Dialog", 1, 15)); // NOI18N
        jButNext.setForeground(new java.awt.Color(255, 255, 255));
        jButNext.setText("SIG ->");
        jButNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButNextActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Selecciona el verbo correspondiente a la descripcion y escribe sus conjugaciones.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextSimple)
                            .addComponent(jButVerbo1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextParticiple)
                            .addComponent(jButVerbo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButVerbo3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButVerbo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButVerbo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButVerbo3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextSimple, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextParticiple, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButNext, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButCheckActionPerformed
        // Revisa las respuestas del usuario
        try{
            String resp_simple = jTextSimple.getText().trim().toLowerCase();
            String resp_participle = jTextParticiple.getText().trim().toLowerCase();
            if(!componentesEstanSeleccionados())
                return;
            if (verbo_seleccionado.equals(verbo_actual.getNombre()) &&
                    resp_simple.equals(verbo_actual.getPasado_simple()) &&
                    resp_participle.equals(verbo_actual.getPasado_participio())){
                JOptionPane.showMessageDialog(rootPane, "¡CORRECTO!");
                etiquetas_progreso[numero_verbo_actual].setBackground(Color.green);
                respuestas_correctas++;
            }else{
                JOptionPane.showMessageDialog(rootPane, "¡INCORRECTO!");
                jTextSimple.setText(verbo_actual.getPasado_simple());
                jTextParticiple.setText(verbo_actual.getPasado_participio());
                etiquetas_progreso[numero_verbo_actual].setBackground(Color.red);
            }
            bloquearComponentes();
            jButNext.setEnabled(true);
        } catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
        
        
    }//GEN-LAST:event_jButCheckActionPerformed
     
    private void jButNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButNextActionPerformed
        // Accedes al siguiente verbo para realizar su evaluacion
        if(numero_verbo_actual < jf_verbos.hs_verbos_a_practicar.size()-1){
            verbo_seleccionado = "";
            actualizarInfoVerbo(++numero_verbo_actual);
            desbloquearComponentes();
            limpiarComponentes();
            jButNext.setEnabled(false);
        }else{
            JOptionPane.showMessageDialog(rootPane, "Practica finalizada\nPresiona OK para"
                    + " ver tus resultados.");
            mostrarResultados();
        }

    }//GEN-LAST:event_jButNextActionPerformed

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
            java.util.logging.Logger.getLogger(JFrame_Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrame_Test(new JFrame_Verbos()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButCheck;
    private javax.swing.JButton jButNext;
    private javax.swing.JButton jButVerbo1;
    private javax.swing.JButton jButVerbo2;
    private javax.swing.JButton jButVerbo3;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextParticiple;
    private javax.swing.JTextField jTextSimple;
    // End of variables declaration//GEN-END:variables
}
