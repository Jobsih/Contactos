package fes.aragon.Final

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fes.aragon.Final.Modelo.UsuarioModelo
import fes.aragon.Final.databinding.ActivityUpdateUserBinding

class UpdateUser : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateUserBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var btnVolver:MaterialButton
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        validate()

        binding.volver.setOnClickListener {
            finish()
        }

        auth = Firebase.auth
    }
    private fun validate() {
//por correo
        //Crea un nuevo usuario
        if (binding.correo.text.isNotEmpty() && binding.password.text.isNotEmpty()){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.correo.text.toString().filterNot { it.isWhitespace() },
                binding.password.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    FirestoreUsuario()
                    opciones(it.result?.user?.email ?: "", TipoProvedor.CORREO)
                }else{
                    alert("Problema al crear usuario")
                }
            }
        }

        binding.updateUser.setOnClickListener {
            if (!binding.correo.text.toString().isEmpty() && !binding.password.text.toString().isEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.correo.text.toString(),
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

        iniciarActividad()
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

    private fun FirestoreUsuario() {
        // Firestore
        val usuario = UsuarioModelo(
            binding.correo.text.toString(), //correo
            binding.nombre.text.toString(), //nombre
            FirebaseAuth.getInstance().currentUser?.uid.toString()
        )

        db.collection("usuarios")
            .document(binding.correo.text.toString()).set(usuario)
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

}