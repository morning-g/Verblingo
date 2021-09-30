/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion_verbos;

/** Representa a una lista enlazada de tipo Nodo.
 * @author Verblingo
 * @version 1.0
*/
public class Lista {
    
    private Nodo cabecera;
    private int numeroNodos;
    
    /**
     * Crea una lista con los atributos por defecto.
     */
    public Lista() {
        cabecera = null;
        numeroNodos = 0;
    }
    
    /**
     * Evalua si la lista esta vacia.
     *
     * @return true si la lista esta vacia, false en cualquier otro caso.
     */
    public boolean isEmpty() {
        return cabecera == null;
    }
    
    /**
     * Obtiene la longitud de la lista.
     *
     * @return un int representando el numero de nodos que contiene la lista.
     */
    public int length() {
        return numeroNodos;
    }
    
    /**
     * Obtiene la cabecera de la lista
     *
     * @return Una referencia tipo Nodo representando al primer elemento de la lista.
     */
    public Nodo getCabecera() {
        return cabecera;
    }
    
    /**
     * Establece la cabecera de la lista.
     *
     * @param cabecera es una referencia del nodo que se quiere establecer como primer elemento de la lista.
     */
    public void setCabecera(Nodo cabecera) {
        this.cabecera = cabecera;
    }
    
    /**
     * Obtiene el nodo que contiene el verbo a buscar.
     *
     * @param verbo Una referencia al objeto tipo Verbo que se desea buscar en la lista.
     * @return Una referencia tipo Nodo al nodo que contiene dentro de si al objeto 
     * que es pasado como parametro.
     */
    public Nodo buscarVerbo(Verbo verbo){
        Nodo p = cabecera;
        while(p != null && !p.getVerbo().getNombre().equals(verbo.getNombre()))
            p = p.getSiguiente();
        return p;
    }
    
    /**
     * Obtiene el objeto tipo Verbo cuyo nombre coincide con el nombre a buscar.
     *
     * @param nombreVerbo Objeto String representando al nombre (pasado simple) 
     * del verbo que se desea buscar en la lista.
     * @return Una referencia de tipo Verbo cuyo .getNombre() coincide con el 
     * valor pasado como parametro.
     */
    public Verbo buscarPorNombre(String nombreVerbo){
        /*Regresa una referencia de tipo Verbo dependiendo si el objeto
            p.Info().getNombre() es igual a la cadena pasada por parametro
        */
        Nodo p = cabecera;
        while(p != null && !p.getVerbo().getNombre().equals(nombreVerbo))
            p = p.getSiguiente();
        return p.getVerbo();
    }
    
    /**
     * Establece el verbo en la posicion adecuada dentro de la lista para que esta prevalezca ordenada alfabeticamente.
     *
     * @param verbo Una referencia tipo Verbo que se desea insertar dentro de la lista.
     */
    public void InsOrden(Verbo verbo) {
            Nodo r = new Nodo(verbo);
            Nodo p = cabecera;
            Nodo q = null;
            while (p != null && verbo.getNombre().compareTo(p.getVerbo().getNombre())>0)
            {
                q = p;
                p = p.getSiguiente();
            }
            if (q == null)
            {
                r.setSiguiente(cabecera);
                cabecera = r;
            }
            else
            {
                q.setSiguiente(r);
                r.setSiguiente(p);
            }
            numeroNodos++;
        }
    
}
