package fes.aragon.Final

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import fes.aragon.Final.Modelo.ContactoModelo
import fes.aragon.Final.databinding.ActivityMainBinding

enum class TipoProvedor {
    CORREO,
    GOOGLE
}
class ActivityMain : AppCompatActivity(), OnClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter:ContactoAdapter
    private  lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    private  val userEmail=auth.currentUser?.email

    var idU:String=""
    var nameU:String=""
    var phoneU:String=""
    var emailU:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // En vez de traer el correo del Bundle, podemos verificarlo desde
        var email: String? = FirebaseAuth.getInstance().currentUser?.email.toString()

        inicio(email ?: "")

        obtenerContactos()
    }

    private fun obtenerContactos() {
        recyclerView =findViewById(R.id.recyclerview)
        recyclerView.layoutManager =LinearLayoutManager(this)

        var bundle: Bundle? = intent.extras

        val nombreUsuario = FirebaseAuth.getInstance().currentUser?.email.toString()
        val datosUsuario = db.collection("usuarios").document(nombreUsuario)
        val datosContacto = datosUsuario.collection("contactos")
        datosContacto.addSnapshotListener { resultado,e ->
            if (e != null) {
                //Log.w(TAG, "Listen failed.", e)
                println("Listen falló: ${e}" )
                return@addSnapshotListener
            }

            val contactoList = mutableListOf<ContactoModelo>()
            for (documento in resultado!!){
                //Guarda lo que obtiene en un object
                val contactito = documento.toObject<ContactoModelo>()
                contactoList.add(contactito)
                adapter = ContactoAdapter(contactoList, this@ActivityMain)
                recyclerView.adapter = adapter
            }
            println("Documentos traidos con exito")

        }
           /* // datosContacto.document(contacto.nombre.toString())
            //.set(contacto)
            .addOnSuccessListener { resultado ->


            }.addOnFailureListener { e ->
                println("Error al traer los contactos: " + e)
            }*/

    }

    //private fun inicio(email: String, provedor: String) {
    private fun inicio(email: String) {

        binding.mail.text = "Correo: ${email}"
        //binding.provedor.text = provedor
        binding.closeSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        binding.agregar.setOnClickListener{
            println("Evento corre")
            var frag = FragmentAddContacto()
            frag.isCancelable = true
            frag.show(supportFragmentManager, "Datos de entrada")
        }

        binding.editar.setOnClickListener {
            if (!nameU.isEmpty() && !phoneU.isEmpty() && !emailU.isEmpty()) {
                val bundle = Bundle()
                bundle.putString("id", userEmail.toString())
                bundle.putString("nombre", nameU)
                bundle.putString("telefono", phoneU)
                bundle.putString("correo", emailU)
                var fr = FragmentAddContacto()
                fr.arguments = bundle
                fr.isCancelable = false
                fr.show(supportFragmentManager, "Datos de entrada")
            }
        }
        binding.eliminar.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id", nameU.toString())
            bundle.putString("delete", idU)
            var fr = FragmentEliminarContacto()
            fr.arguments = bundle
            fr.show(supportFragmentManager, "Datos de entrada")
        }
    }



    override fun onClick(contact: ContactoModelo) {
        idU=contact.id.toString()
        nameU= contact.nombre.toString()
        phoneU= contact.telefono.toString()
        emailU= contact.correo.toString()
    }
}



