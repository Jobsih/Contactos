package fes.aragon.Final
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fes.aragon.Final.Modelo.UsuarioModelo
import fes.aragon.Final.databinding.LoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        validate()
        sesiones()
        auth = Firebase.auth
    }

    private fun sesiones() {
        val preferencias =
            getSharedPreferences(getString(R.string.file_preferencia), Context.MODE_PRIVATE)
        var email: String? = preferencias.getString("email", null)
        var provedor: String? = preferencias.getString("provedor", null)
        if (email != null && provedor != null) {
            opciones(email, TipoProvedor.valueOf(provedor))
        }
    }

    private fun validate() {
//por correo
        //Crea un nuevo usuario
        if (binding.username.text.isNotEmpty() && binding.password.text.isNotEmpty()){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.username.text.toString(),
                binding.password.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    FirestoreUsuario()
                    /*val usuario = UsuarioModelo(
                        binding.username.text.toString(), //correo
                        binding.nombre.text.toString(), //nombre
                        FirebaseAuth.getInstance().currentUser?.uid.toString())
                    db.collection("usuarios")
                        .document(binding.username.text.toString()).set(usuario)
                        .addOnSuccessListener { referencia->
                            println("Como que si") }
                        .addOnFailureListener {
                                e -> println("Como que no")
                            Log.w(ContentValues.TAG, "Error writing document", e) }*/

                    opciones(it.result?.user?.email ?: "", TipoProvedor.CORREO)
                }else{
                    alert("Problema al crear usuario")
                }
            }
        }

        binding.updateUser.setOnClickListener {
            if (!binding.username.text.toString().isEmpty() && !binding.password.text.toString().isEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener {
                    if (it.isComplete) {
                        try {
                            FirestoreUsuario()

                            opciones(it.result?.user?.email ?: "", TipoProvedor.CORREO)
                        } catch (e: Exception) {
                            alert("Problema al registrar usuario")
                        }
                    } else {
                        alert("Problema al registrar usuario")
                    }
                }
            }
        }
//establecer enlace
        //Inicia sesion existente
        binding.loginbtn.setOnClickListener {
            if (!binding.username.text.toString().isEmpty() && !binding.password.text.toString()
                    .isEmpty()
            ) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        println("llegamos ---------------")
                        opciones(it.result?.user?.email ?: "", TipoProvedor.CORREO)
                    } else {
                        alert("Correo o contraseña equivocadas")
                    }
                }
            }
        }

        iniciarActividad()
    }

    private fun FirestoreUsuario() {
        // Firestore
        val usuario = UsuarioModelo(
            binding.username.text.toString(), //correo
            binding.nombre.text.toString(), //nombre
            FirebaseAuth.getInstance().currentUser?.uid.toString()
        )

        db.collection("usuarios")
            .document(binding.username.text.toString()).set(usuario)
            .addOnSuccessListener { referencia ->
                println("Llegó")
            }
            .addOnFailureListener { e ->
                println("No llegó")
                Log.w(ContentValues.TAG, "Error writing document", e)
            }
    }

    private fun alert(msg: String) {
        val bulder = AlertDialog.Builder(this)
        bulder.setTitle("Mensaje")
        bulder.setMessage(msg)
        bulder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = bulder.create()
        dialog.show()
    }

    private fun opciones(email: String, provedor: TipoProvedor) {
        var pasos: Intent = Intent(this, ActivityMain::class.java).apply {
            putExtra("email", email)
            putExtra("provedor", provedor.name)
        }
        startActivity(pasos)
    }
    override fun onStart() {
        super.onStart()
        binding.layoutAcceso.visibility = View.VISIBLE
        // Check if user is signed in (non-null) and update UI accordingly. (Para el correo)
        val currentUser = auth.currentUser
        if(currentUser != null) {
            alert("Por favor inicie sesión")
        }
    }
    private fun iniciarActividad() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                        if (account != null) {
                            val credenciales =
                                GoogleAuthProvider.getCredential(account.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(credenciales)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        opciones(account.email ?: "", TipoProvedor.GOOGLE)
                                    } else {
                                        alert("Inicie sesión, por favor")
                                    }
                                }
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(this, "Sign in failed: " + e.statusCode, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }
}