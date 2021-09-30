/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion_verbos;

/** Representa a un verbo en ingles.
 * @author Verblingo
 * @version 1.0
*/
public class Verbo implements Comparable{
    
    /**
     * Crea un verbo que por defecto no tiene valores.
     */
    //public Verbo(){}
    
    private String nombre;
    private String pasado_simple;
    private String pasado_participio;
    private String traduccion;
    private String descripcion;
    private String ejemplos;
    
    /**
     * Crea un verbo con los atributos especificados.
     *
     * @param nombre Nombre del verbo (presente simple).
     * @param pasado_simple Pasado simple del verbo.
     * @param pasado_participio Pasado participio del verbo.
     * @param descripcion Descripcion del verbo.
     * @param ejemplos Oracion de ejemplo utilizando el verbo.
     * @param traduccion Traduccion al español del verbo.
     */
    public Verbo(String nombre, String pasado_simple, String pasado_participio, String descripcion, String ejemplos, String traduccion) {
        this.nombre = nombre;
        this.pasado_simple = pasado_simple;
        this.pasado_participio = pasado_participio;
        this.descripcion = descripcion;
        this.ejemplos = ejemplos;
        this.traduccion = traduccion;
    }
    
    /**
     * Obtiene el nombre del verbo.
     *
     * @return Una objeto String representando nombre del verbo (presente simple).
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre (pasado simple) del verbo.
     *
     * @param nombre Un objeto String que contiene el nombre del verbo.
     */  
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el pasado simple del verbo.
     *
     * @return Un objeto String representando del pasado simple del verbo.
     */
    public String getPasado_simple() {
        return pasado_simple;
    }

    /**
     * Establece el pasado simple del verbo.
     *
     * @param pasado_simple Un objeto String que contiene el pasado simple del verbo.
     */  
    public void setPasado_simple(String pasado_simple) {
        this.pasado_simple = pasado_simple;
    }

    /**
     * Obtiene el pasado perfecto del verbo.
     *
     * @return Un objeto String representando del pasado perfecto del verbo.
     */    
    public String getPasado_participio() {
        return pasado_participio;
    }

    /**
     * Establece el pasado participio del verbo.
     *
     * @param pasado_participio Un objeto String que contiene el pasado participio del verbo.
     */  
    public void setPasado_perfecto(String pasado_participio) {
        this.pasado_participio = pasado_participio;
    }

    
    /**
     * Obtiene la traduccion del verbo.
     *
     * @return Una objeto String representando la traduccion en español del verbo.
     */    
    public String getTraduccion() {
        return traduccion;
    }

    /**
     * Establece la traduccion a español del verbo.
     *
     * @param traduccion Un objeto String que contiene la traduccion del verbo.
     */  
    public void setTraduccion(String traduccion) {
        this.traduccion = traduccion;
    }
    
    /**
     * Obtiene el la descripcion del verbo.
     *
     * @return Un objeto String representando la descripcion del verbo.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripcion del verbo.
     *
     * @param descripcion Un objeto String que contiene la descripcion del verbo.
     */  
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene una oracion de ejemplo la cual utiliza el verbo.
     *
     * @return Un objeto String representando la oracion de ejemplo con el verbo.
     */    
    public String getEjemplos() {
        return ejemplos;
    }

    /**
     * Establece una oracion de ejemplo utilizando el verbo.
     *
     * @param ejemplos Un objeto String que contiene la oracion de ejemplo con el verbo.
     */  
    public void setEjemplos(String ejemplos) {
        this.ejemplos = ejemplos;
    }

    @Override
    public int compareTo(Object o) {
        Verbo vo = (Verbo)o;
        return this.nombre.compareTo(vo.nombre);
    }
    
    
    
}
