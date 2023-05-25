package fes.aragon.Final

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import fes.aragon.Final.Modelo.ContactoModelo
import fes.aragon.Final.Modelo.UsuarioModelo
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddContacto.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddContacto : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var btnSalir: Button
    private lateinit var btnEscribir: Button
    private lateinit var btnTomarFoto: Button

    private lateinit var nombre: TextView
    private lateinit var telefono: TextView
    private lateinit var correo: TextView
    private lateinit var imagenNombre: TextView
    private lateinit var camara: Button
    private lateinit var currentPhotoPath: String

    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_add_contacto, container, false)

        nombre = view.findViewById(R.id.nombre)
        telefono = view.findViewById(R.id.telefono)
        correo = view.findViewById(R.id.correo)
        imagenNombre = view.findViewById(R.id.imagenNombre)

        btnTomarFoto = view.findViewById(R.id.camara)
        btnEscribir = view.findViewById(R.id.almacenar)
        btnSalir = view.findViewById(R.id.cancelar)


        btnTomarFoto.setOnClickListener {

        }

        btnEscribir.setOnClickListener {
            //binding.username.text.isNotEmpty()
            if (nombre.text.isBlank() && (telefono.text.isNotBlank()) || correo.text.isNotBlank()) {
                agregarContacto()
                dismiss()
            } else {
                Toast.makeText(activity, "Por favor llene los campos", Toast.LENGTH_SHORT).show()
                //Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
            }
        }

        btnSalir.setOnClickListener {
            dismiss()
        }

        return view
    }
    private fun agregarContacto() {
        val contacto = ContactoModelo(
            nombre.text.toString(),
            telefono.text.toString(),
            correo.text.toString(),
            //Placeholder
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS-QLlz4u1u01iFebjTQZSWYfsK2CYDKw11xhXjosPB-vLHPJqHknakuJT13vMrkZsbqtk"
        )

        val nombreUsuario = FirebaseAuth.getInstance().currentUser?.email.toString()
        val datosUsuario = db.collection("usuarios").document(nombreUsuario)
        val datosContacto = datosUsuario.collection("contactos")
        datosContacto.document(contacto.nombre.toString())
            .set(contacto)
            .addOnSuccessListener { referencia ->
                println("Documento escrito con exito")
            }.addOnFailureListener { e ->
                println("Error al escribir el documento: " + e)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAddContacto.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentAddContacto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}