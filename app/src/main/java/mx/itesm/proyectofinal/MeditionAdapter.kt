package mx.itesm.proyectofinal

import Database.Medicion
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row.view.*

class MeditionAdapter(val mediciones: List<Medicion>,
                      val listener: CustomItemClickListener): RecyclerView.Adapter<MeditionAdapter.MeditionViewHolder>() {

    lateinit var medicion:Medicion

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MeditionViewHolder {
        val rowView = LayoutInflater.from(p0.context).inflate(R.layout.row, p0, false)

        return MeditionViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return mediciones.size
    }

    override fun onBindViewHolder(p0: MeditionViewHolder, p1: Int) {
        p0.bind(p1)
    }

    inner class MeditionViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
            val medicionesLocal = mediciones[adapterPosition]
            listener.onCustomItemClick(medicionesLocal)
        }

        init { itemView.setOnClickListener(this) }

        fun bind (index:Int) {
            medicion = mediciones[index]

            itemView.id_paciente.text = medicion._id.toString()
            itemView.fecha_paciente.text = medicion.fecha
            itemView.paciente_resultado_auto.text = medicion.appSistolica.toString() + "/" + medicion.appDiastolica.toString()
            itemView.paciente_resultado_manual.text = medicion.manSistolica.toString() + "/" + medicion.manDiastolica.toString()
        }
    }
}