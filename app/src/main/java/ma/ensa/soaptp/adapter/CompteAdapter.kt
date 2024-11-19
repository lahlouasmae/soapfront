package ma.ensa.soaptp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import ma.ensa.soaptp.R
import ma.ensa.soaptp.beans.Compte
import java.text.SimpleDateFormat
import java.util.Locale

class CompteAdapter : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {
    private var comptes = mutableListOf<Compte>()
    var onEditClick: ((Compte) -> Unit)? = null
    var onDeleteClick: ((Compte) -> Unit)? = null

    fun updateComptes(newComptes: List<Compte>) {
        comptes.clear()
        comptes.addAll(newComptes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return CompteViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(comptes[position])
    }

    override fun getItemCount() = comptes.size
    fun removeCompte(compte: Compte) {
        val position = comptes.indexOf(compte)
        if (position >= 0) {
            comptes.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    inner class CompteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvId: TextView = view.findViewById(R.id.tvId)
        private val tvSolde: TextView = view.findViewById(R.id.tvSolde)
        private val tvType: Chip = view.findViewById(R.id.tvType)
        private val tvDate: TextView = view.findViewById(R.id.tvDate)
        private val btnEdit: MaterialButton = view.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = view.findViewById(R.id.btnDelete)

        fun bind(compte: Compte) {
            tvId.text = "Compte N° ${compte.id}"
            tvSolde.text = "${compte.solde} DH"
            tvType.text = compte.type.name
            tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(compte.dateCreation)

            btnEdit.setOnClickListener { onEditClick?.invoke(compte) }
            btnDelete.setOnClickListener { onDeleteClick?.invoke(compte) }
        }
    }

}