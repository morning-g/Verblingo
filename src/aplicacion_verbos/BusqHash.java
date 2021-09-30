/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacion_verbos;

/** Utilizada para realizar una busqueda Hash.
 * @author Verblingo
 * @version 1.0
*/
public class BusqHash {
    
    /**
     * Busca un objeto String dentro de una arreglo de listas enlazadas
     *
     * @param tabla Una referencia a la tabla hash donde cada elemento tabla[i] es una
     * lista enlazada que contiene los verbos de una sola inicial (A-Z), ordenados alfabeticamente.
     * @param nombre_de_verbo Representa un objeto String que contiene el nombre (pasado simple) 
     * del verbo que se desea localizar.
     * @return Una referencia tipo Verbo al objeto cuyo nombre (pasado simple) coincide con nombre_de_verbo.
     */ 
    public static Verbo Buscar(Lista[] tabla, String nombre_de_verbo)
        {
            char c = Character.toUpperCase(nombre_de_verbo.charAt(0));
            int indice = (int) c - 65;
            Nodo p = tabla[indice].getCabecera();
            while (p != null)
                if (p.getVerbo().getNombre().equals(nombre_de_verbo))
                    return p.getVerbo();
                else
                    p = p.getSiguiente();
            return null;
        }
    
    
    /**
     * Obtiene la lista enlazada que contiene los verbos de una inicial deseada.
     *
     * @param tabla Una referencia a la tabla hash donde cada elemento tabla[i]
     * es una lista enlazada que contiene los verbos de una sola inicial (A-Z),
     * ordenados alfabeticamente.
     * @param inicial Representa la inicial de los verbos que se desean buscar.
     * @return Una referencia al objeto Lista que contiene los verbos cuya inicial 
     * es el valor pasado como parametro.
     */
    public static Lista buscarPorLetra(Lista[] tabla, char inicial){
        int indice = (int) inicial - 65;
        return tabla[indice];
    }
    
}
