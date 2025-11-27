package ma.projet.soapclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import ma.projet.soapclient.R
import ma.projet.soapclient.beans.Compte
import java.text.SimpleDateFormat
import java.util.Locale

class CompteAdapter : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {

    private val comptes = mutableListOf<Compte>()

    var onEditClick: ((Compte) -> Unit)? = null
    var onDeleteClick: ((Compte) -> Unit)? = null

    fun updateComptes(newList: List<Compte>) {
        comptes.clear()
        comptes.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return CompteViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(comptes[position])
    }

    override fun getItemCount(): Int = comptes.size

    inner class CompteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textId: TextView = view.findViewById(R.id.textId)
        private val textSolde: TextView = view.findViewById(R.id.textSolde)
        private val textType: Chip = view.findViewById(R.id.textType)
        private val textDate: TextView = view.findViewById(R.id.textDate)
        private val btnEdit: MaterialButton = view.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = view.findViewById(R.id.btnDelete)

        private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(compte: Compte) {
            textId.text = "Compte N° ${compte.id}"
            textSolde.text = "${compte.solde} DH"
            textType.text = compte.type.name
            textDate.text = sdf.format(compte.dateCreation)

            btnEdit.setOnClickListener { onEditClick?.invoke(compte) }
            btnDelete.setOnClickListener { onDeleteClick?.invoke(compte) }
        }
    }
}
