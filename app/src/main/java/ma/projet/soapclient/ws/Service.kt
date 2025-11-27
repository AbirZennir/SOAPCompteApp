package ma.projet.soapclient.ws

import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.beans.TypeCompte
import java.util.Date

class Service {

    companion object {
        private var currentId: Long = 1L

        // Liste locale en mémoire (pour tester sans serveur)
        private val comptes = mutableListOf<Compte>().apply {
            add(
                Compte(
                    id = currentId++,
                    solde = 2000.0,
                    dateCreation = Date(),
                    type = TypeCompte.COURANT
                )
            )
            add(
                Compte(
                    id = currentId++,
                    solde = 5000.0,
                    dateCreation = Date(),
                    type = TypeCompte.EPARGNE
                )
            )
        }
    }

    // --------- SIMULATION DES APPELS SOAP ---------

    fun getComptes(): List<Compte> {
        // ici plus de TODO(), on renvoie la liste locale
        return comptes.toList()
    }

    fun addCompte(compte: Compte): Boolean {
        val newCompte = compte.copy(id = currentId++)
        comptes.add(newCompte)
        return true
    }

    fun updateCompte(compte: Compte): Boolean {
        val index = comptes.indexOfFirst { it.id == compte.id }
        if (index == -1) return false
        comptes[index] = compte
        return true
    }

    fun deleteCompte(id: Long): Boolean {
        return comptes.removeIf { it.id == id }
    }
}
