package fes.aragon.Final

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

/**
 * A simple [Fragment] subclass.
 * Use the [EliminarUsuarioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EliminarUsuarioFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var usrPos: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            usrPos = it.getInt("usuarioId")
        }

    }
    private lateinit var botonCancelar: Button
    private lateinit var botonBorrar: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_borrar_usuario,container,false)
        println(usrPos.toString())
        botonCancelar = view.findViewById(R.id.cancelar)
        botonCancelar.setOnClickListener {
            dismiss()
        }
        botonBorrar = view.findViewById(R.id.borrar)
        botonBorrar.setOnClickListener {
        usrPos
            Thread{
               /* val usuario: Usuario = UsuarioApplication.database.usuarioDao().getUsuarioById(usrPos)
                UsuarioApplication.database.usuarioDao().deleteUsuario(usuario)*/
            }.start()
            dismiss()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EliminarUsuarioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EliminarUsuarioFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }/*
    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if(activity is DialogInterface.OnDismissListener){
            (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }*/
}