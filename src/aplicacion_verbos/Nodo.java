/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion_verbos;

/** Representa a un nodo que contendra una referencia a un objeto de tipo Verbo y una referencia a un objeto de tipo Nodo con el que se enlazara.
 * @author Verblingo
 * @version 1.0
*/

public class Nodo {
    
    private Verbo verbo;
    private Nodo siguiente;
    
    /**
     * Crea un Nodo con los atributos especificados.
     *
     * @param verbo Objeto Verbo a ser referenciado.
     */
    public Nodo(Verbo verbo) {
        this.verbo = verbo;
        siguiente = null;
    }
    
    
    /**
     * Obtiene el objeto verbo.
     *
     * @return Una referencia al objeto de tipo Verbo contenida en el nodo.
     */
    public Verbo getVerbo() {
        return verbo;
    }
    
    /**
     * Obtiene el objeto siguiente.
     *
     * @return Una referencia de tipo Nodo al nodo que esta vinculado con el nodo actual.
     */
    public Nodo getSiguiente() {
        return siguiente;
    }
    
    /**
     * Establece el verbo que tiene el nodo
     *
     * @param verbo Una referencia de tipo Verbo que sirve para vincular el verbo deseado.
     */ 
    public void setVerbo(Verbo verbo) {
        this.verbo = verbo;
    }
    
    /**
     * Establece el nodo siguiente que tiene el nodo actual.
     *
     * @param siguiente Una referencia de tipo Nodo al que se debe de enlazar el nodo actual.
     */ 
    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}
