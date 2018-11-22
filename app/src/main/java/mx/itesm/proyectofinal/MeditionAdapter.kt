package mx.itesm.proyectofinal

import Database.Medicion
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row.view.*

class MeditionAdapter(var context: Context,
                      val listener: CustomItemClickListener): RecyclerView.Adapter<MeditionAdapter.MeditionViewHolder>() {

    lateinit var medicion:Medicion
    var numberOfItems = 0
    var mediciones:List<Medicion>? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MeditionAdapter.MeditionViewHolder {
        val medicionViewHolder: MeditionAdapter.MeditionViewHolder

        val rowView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row, viewGroup, false)

        medicionViewHolder = MeditionViewHolder(rowView)
        return medicionViewHolder
    }

    override fun getItemCount(): Int {
        return numberOfItems
    }

    override fun onBindViewHolder(p0: MeditionViewHolder, p1: Int) {
        p0.bind(p1)
    }

    fun setMedicion(medicionList: List<Medicion>){
        mediciones = medicionList
        numberOfItems = medicionList.size
    }

    inner class MeditionViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init { itemView.setOnClickListener(this) }

        fun bind (index:Int) {
            medicion = mediciones?.get(index)!!

            itemView.card_initials.text = medicion.iniciales
            itemView.card_date.text = medicion.fecha
            itemView.card_calc.text = medicion.appSistolica.toString() + "/" + medicion.appDiastolica.toString()
            itemView.card_manual.text = medicion.manSistolica.toString() + "/" + medicion.manDiastolica.toString()
        }

        override fun onClick(v: View?) {
            val medicionesLocal = mediciones?.get(adapterPosition)
            listener.onCustomItemClick(medicionesLocal!!)
        }
    }
}