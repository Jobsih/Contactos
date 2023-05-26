package fes.aragon.Final

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import fes.aragon.Final.Modelo.ContactoModelo
import java.io.ByteArrayOutputStream


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEditarContacto.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEditarContacto : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var btnSalir: Button
    private lateinit var btnEscribir: Button
    private lateinit var btnTomarFoto: Button

    private lateinit var nombreCampo: TextView
    private lateinit var telefonoCampo: TextView
    private lateinit var correoCampo: TextView
    private lateinit var imagenPreview: ImageView
    private lateinit var camara: Button
    private lateinit var currentPhotoPath: String
    //private lateinit var miUrl: String
    private var miUrl:String = ""
    private lateinit var recien :String

    private lateinit var nombre:String
    private lateinit var telefono:String
    private lateinit var correo:String
    private lateinit var fotoBundle:String
    private lateinit var idBundle:String


    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            idBundle = it.getString("id").toString()
            nombre = it.getString("nombre").toString()
            telefono=it.getString("telefono").toString()
            correo=it.getString("correo").toString()
            fotoBundle=it.getString("url").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_add_contacto, container, false)
        nombreCampo = view.findViewById(R.id.nombre)
        telefonoCampo = view.findViewById(R.id.telefono)
        correoCampo = view.findViewById(R.id.correo)
        imagenPreview = view.findViewById(R.id.preview)
        miUrl = fotoBundle
        Picasso.get().load(if(fotoBundle.isBlank()) null else fotoBundle).placeholder(R.drawable.sinfoto).error(R.drawable.sinfoto).into(imagenPreview)
        nombreCampo.text=nombre
        telefonoCampo.text=telefono
        correoCampo.text=correo

        btnTomarFoto = view.findViewById(R.id.camara)
        btnEscribir = view.findViewById(R.id.almacenar)
        btnSalir = view.findViewById(R.id.cancelar)

        btnSalir.setOnClickListener {
            dismiss()
        }

        btnTomarFoto.setOnClickListener {
            checkCameraPermission()
        }

        btnEscribir.setOnClickListener {
            //binding.username.text.isNotEmpty()
            if (nombreCampo.text.isNotBlank() && (telefonoCampo.text.isNotBlank()) or correoCampo.text.isNotBlank()) {
                editarContacto()
                dismiss()
            } else {
                Toast.makeText(activity, "Por favor llene los campos", Toast.LENGTH_SHORT).show()
                //Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
            }
        }


        return  view
    }

    private fun editarContacto() {
        val contacto = ContactoModelo(
            nombreCampo.text.toString(),
            telefonoCampo.text.toString(),
            correoCampo.text.toString(),
            //Placeholder
            fotoBundle,
            idBundle
        )

        val nombreUsuario = FirebaseAuth.getInstance().currentUser?.email.toString()
        val datosUsuario = db.collection("usuarios").document(nombreUsuario)
        val datosContacto = datosUsuario.collection("contactos")
        datosContacto.document(idBundle).update(mapOf(
            "nombre" to contacto.nombre,
            "telefono" to contacto.telefono,
            "correo" to contacto.correo,
            "url" to miUrl,
            "id" to idBundle
        ))
            // datosContacto.document(contacto.nombre.toString())
            //.set(contacto)
            .addOnSuccessListener { referencia ->
                println("Editado con exito")
                Log.d(TAG, "Editado con éxito")
                storage.getReferenceFromUrl(fotoBundle).delete()
                Log.d(TAG, miUrl)

            }.addOnFailureListener { e ->
                println("Error al escribir el documento: " + e)
                Log.d(TAG, "Error al escribir el documento: " + e)
            }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PERMISSION_CAMERA = 2
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentEditarContacto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // Función para verificar y solicitar los permisos de la cámara
    private fun checkCameraPermission() {
        val permission = Manifest.permission.CAMERA
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission),
                FragmentEditarContacto.REQUEST_PERMISSION_CAMERA
            )
        } else {
            openCamera()
        }
    }

    // Función para abrir la cámara y capturar la foto
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, FragmentEditarContacto.REQUEST_IMAGE_CAPTURE)
        }
    }

    // Función para guardar la foto en Firebase Storage
    private fun uploadPhotoToStorage(bitmap: Bitmap) {
        // Comprime el bitmap en un formato adecuado, como JPEG
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        // Genera un nombre único para la imagen
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        // Crea una referencia al archivo en Firebase Storage
        val imageRef = storageReference.child("images/$fileName")

        // Sube los datos de la imagen a Firebase Storage
        val uploadTask = imageRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            // Continúa con la tarea para obtener la URL del archivo
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                miUrl = downloadUri.toString()
                Picasso.get().load(miUrl).placeholder(R.drawable.sinfoto).error(R.drawable.sinfoto).into(imagenPreview)
                // Utiliza la URL del archivo como sea necesario
            } else {
                // Error al obtener la URL del archivo
            }
        }
    }

    // Función para recibir el resultado de la captura de la foto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FragmentEditarContacto.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Llama a la función para subir la foto a Firebase Storage
            uploadPhotoToStorage(imageBitmap)
        }
    }
}