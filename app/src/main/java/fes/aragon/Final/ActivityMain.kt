package fes.aragon.Final

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import fes.aragon.Final.databinding.ActivityMainBinding

enum class TipoProvedor {
    CORREO,
    GOOGLE
}
class ActivityMain : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//datos que manda la actividad
        var bundle: Bundle? = intent.extras
        // En vez de traer el correo del Bundle, podemos verificarlo desde
        var email: String? = FirebaseAuth.getInstance().currentUser?.email.toString()
        //var email: String? = bundle?.getString("email")
        //var provedor: String? = bundle?.getString("provedor")
        //inicio(email ?: "", provedor ?: "")
        inicio(email ?: "")
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
    }
}



