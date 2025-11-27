package ma.projet.soapclient

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import ma.projet.soapclient.adapter.CompteAdapter
import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.beans.TypeCompte
import ma.projet.soapclient.ws.Service
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAdd: Button

    private val adapter = CompteAdapter()
    private val service = Service()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // IMPORTANT : activité classique + layout XML
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.btnAdd)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Charger les comptes au démarrage
        loadComptes()

        // Clic sur AJOUTER
        btnAdd.setOnClickListener {
            showAddCompteDialog()
        }

        // Clic sur MODIFIER / SUPPRIMER dans l’adapter
        adapter.onEditClick = { compte ->
            showEditCompteDialog(compte)
        }
        adapter.onDeleteClick = { compte ->
            deleteCompte(compte)
        }
    }

    // ---------- Chargement de la liste ----------
    private fun loadComptes() {
        Thread {
            try {
                val comptes = service.getComptes()      // SOAP
                runOnUiThread {
                    adapter.updateComptes(comptes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Erreur de chargement : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    // ---------- Dialogue AJOUTER ----------
    private fun showAddCompteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup, null)

        val etSolde = dialogView.findViewById<TextInputEditText>(R.id.etSolde)
        val radioCourant = dialogView.findViewById<RadioButton>(R.id.radioCourant)
        val radioEpargne = dialogView.findViewById<RadioButton>(R.id.radioEpargne)

        MaterialAlertDialogBuilder(this)
            .setTitle("Nouveau compte")
            .setView(dialogView)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Ajouter") { dialog, _ ->
                val soldeText = etSolde.text?.toString()?.trim().orEmpty()

                if (soldeText.isEmpty()) {
                    Toast.makeText(this, "Veuillez saisir un solde", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val solde = try {
                    soldeText.toDouble()
                } catch (_: NumberFormatException) {
                    Toast.makeText(this, "Solde invalide", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE

                val compte = Compte(
                    id = 0L,                  // sera rempli côté serveur
                    solde = solde,
                    dateCreation = Date(),
                    type = type
                )

                Thread {
                    try {
                        val ok = service.addCompte(compte)   // ADAPTE le nom si besoin
                        val comptes = service.getComptes()

                        runOnUiThread {
                            if (ok) {
                                adapter.updateComptes(comptes)
                                Toast.makeText(this, "Compte ajouté", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }.start()

                dialog.dismiss()
            }
            .show()
    }

    // ---------- Dialogue MODIFIER ----------
    private fun showEditCompteDialog(compte: Compte) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup, null)

        val etSolde = dialogView.findViewById<TextInputEditText>(R.id.etSolde)
        val radioCourant = dialogView.findViewById<RadioButton>(R.id.radioCourant)
        val radioEpargne = dialogView.findViewById<RadioButton>(R.id.radioEpargne)

        etSolde.setText(compte.solde.toString())
        if (compte.type == TypeCompte.COURANT) {
            radioCourant.isChecked = true
        } else {
            radioEpargne.isChecked = true
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Modifier compte")
            .setView(dialogView)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Enregistrer") { dialog, _ ->
                val soldeText = etSolde.text?.toString()?.trim().orEmpty()
                if (soldeText.isEmpty()) {
                    Toast.makeText(this, "Veuillez saisir un solde", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val solde = try {
                    soldeText.toDouble()
                } catch (_: NumberFormatException) {
                    Toast.makeText(this, "Solde invalide", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE
                val updated = compte.copy(solde = solde, type = type)

                Thread {
                    try {
                        val ok = service.updateCompte(updated)
                        val comptes = service.getComptes()

                        runOnUiThread {
                            if (ok) {
                                adapter.updateComptes(comptes)
                                Toast.makeText(this, "Compte modifié", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }.start()

                dialog.dismiss()
            }
            .show()
    }

    // ---------- SUPPRIMER ----------
    private fun deleteCompte(compte: Compte) {
        Thread {
            try {
                val ok = service.deleteCompte(compte.id)
                val comptes = service.getComptes()

                runOnUiThread {
                    if (ok) {
                        adapter.updateComptes(comptes)
                        Toast.makeText(this, "Compte supprimé", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
