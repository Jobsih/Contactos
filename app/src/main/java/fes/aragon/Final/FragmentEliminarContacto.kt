package fes.aragon.Final

import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEliminarContacto.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEliminarContacto : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var btnBorrar:Button
    private lateinit var btnSalir: Button

    private lateinit var nombreBundle: String
    private lateinit var idBundle: String
    private lateinit var fotoBundle: String

    private var miUrl:String = ""

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            //nombre
            //id
            //foto

            nombreBundle = it.getString("nombre").toString()
            idBundle = it.getString("id").toString()
            fotoBundle = it.getString("foto").toString()


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_eliminar_contacto, container, false)

        btnSalir = view.findViewById(R.id.cancelar)
        btnSalir.setOnClickListener{dismiss()}

        btnBorrar =view.findViewById(R.id.borrar)
        btnBorrar.setOnClickListener {
            val nombreUsuario = FirebaseAuth.getInstance().currentUser?.email.toString()
            val datosUsuario = db.collection("usuarios").document(nombreUsuario)
            val datosContacto = datosUsuario.collection("contactos")
                .document(idBundle)
                .delete()
                .addOnSuccessListener { referencia ->
                    println("Editado con exito")
                    Log.d(ContentValues.TAG, "Eliminado con éxito") }
                .addOnFailureListener { e ->
                    println("Error al eliminar el documento: " + e)
                    Log.d(ContentValues.TAG, "Error al escribir el documento: " + e) }
            if (fotoBundle.isNotBlank()){
                storage.getReferenceFromUrl(fotoBundle).delete()
            }
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
         * @return A new instance of fragment FragmentEliminarContacto.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentEliminarContacto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if (activity is DialogInterface.OnDismissListener) {
            (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }
}