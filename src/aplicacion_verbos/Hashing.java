package aplicacion_verbos;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** Representa una tabla Hash creada a partir de un arreglo de listas enlazadas.
 * @author Verblingo
 * @version 1.0
*/
public class Hashing {
    
    Lista[] tablaHash;
    
    /**
     * Ordena alfabeticamente un arreglo de tipo Verbo, clasificando cada verbo segun su inicial e insertandolo en la lista correspondiente.
     *
     * @param arreglo_verbo Un arreglo que contiene todos los verbos sin un orden o una clasificacion previa.
     */ 
    public void Ordenar(Verbo[] arreglo_verbo) {
        tablaHash = new Lista[26];//26 de longitud por las 26 letras del abecedario
        
        //inicializar objetos Lista dentro del arreglo tablaHash
        for (int i = 0; i < tablaHash.length; i++)
            tablaHash[i] = new Lista();
        
        for (int i = 0; i < arreglo_verbo.length; i++) {
            char c = arreglo_verbo[i].getNombre().charAt(0);
            int indice = (int) c - 65;
            tablaHash[indice].InsOrden(arreglo_verbo[i]);
        }
        int j=0;
        for (int i = 0; i < tablaHash.length; i++) {
            Nodo p = tablaHash[i].getCabecera();
            while (p != null) {
                arreglo_verbo[j++] = p.getVerbo();;
                p = p.getSiguiente();
            }
        }
    }
    
    /**
     * Obtiene un arreglo de listas enlazadas ordenado alfabeticamente.
     *
     * @return Un objeto Lista[] representando las listas enlazadas ordenadas alfabeticamente,
     * donde cada una de ellas tiene verbos que empiezan con una sola inicial (A-Z).
     * @param arreglo_verbo Un arreglo que contiene todos los verbos sin un orden o una clasificacion previa.
     */
    public Lista[] Lista(Verbo[]arreglo_verbo) {
        tablaHash = new Lista[26];
        for (int i = 0; i < tablaHash.length; i++)
            tablaHash[i] = new Lista();
        
        for (int i = 0; i <= arreglo_verbo.length; i++) {
            char c = Character.toUpperCase(arreglo_verbo[i].getNombre().charAt(0));
            int indice = (int) c - 65;
            tablaHash[indice].InsOrden(arreglo_verbo[i]);
        }
        return tablaHash;
    }
}
