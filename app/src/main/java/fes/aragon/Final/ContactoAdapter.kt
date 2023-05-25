package fes.aragon.Final

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fes.aragon.Final.Modelo.ContactoModelo
import fes.aragon.Final.databinding.ContactosItemBinding
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ContactoAdapter(private var contactos: MutableList<ContactoModelo>,
              private  val oyente: OnClickListener)
    :RecyclerView.Adapter<ContactoAdapter.ViewHolder>(){

    private lateinit var context:Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var binding= ContactosItemBinding.bind(view)

        fun oyente(usuarioBass: ContactoModelo){
            binding.root.setOnClickListener{
                oyente.onClick(usuarioBass)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val vista= LayoutInflater.from(context).inflate(R.layout.contactos_item,parent,false)
        return ViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return contactos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contacto=contactos.get(position)
        with(holder){
            binding.nombre.text=contacto.nombre
            binding.telefono.text=contacto.telefono
            binding.correo.text=contacto.correo
            oyente(contacto)
            Glide.with(context)
                .load(contacto.url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .circleCrop()
                .into(binding.imgView)
        }
    }
}