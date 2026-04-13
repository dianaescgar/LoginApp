package escalante.diana.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import escalante.diana.login.ui.theme.LoginTheme

class RegistroActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        database = Firebase.database.reference

        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistro(
                        auth,
                        database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun calcularEdad(fechaNacimiento: String): Int {
    val partes = fechaNacimiento.split("/")

    if (partes.size != 3) return -1

    val dia = partes[0].toIntOrNull() ?: return -1
    val mes = partes[1].toIntOrNull() ?: return -1
    val anio = partes[2].toIntOrNull() ?: return -1

    if (mes < 1 || mes > 12) return -1
    if (dia < 1 || dia > 31) return -1

    val hoy = java.util.Calendar.getInstance()
    val anioActual = hoy.get(java.util.Calendar.YEAR)
    val mesActual = hoy.get(java.util.Calendar.MONTH) + 1
    val diaActual = hoy.get(java.util.Calendar.DAY_OF_MONTH)

    var edad = anioActual - anio

    if (mesActual < mes || (mesActual == mes && diaActual < dia)) {
        edad--
    }

    return edad
}

@Composable
fun PantallaRegistro(auth: FirebaseAuth, database: DatabaseReference, modifier: Modifier = Modifier) {
    var nombre by remember() { mutableStateOf("") }
    var correo by remember() { mutableStateOf("") }
    var contra by remember() { mutableStateOf("") }
    var verificarContra by remember() { mutableStateOf("") }
    var fechaNacimiento by remember() { mutableStateOf("") }
    var isError by remember() { mutableStateOf(false) }
    var context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre Completo") },
            modifier =  Modifier.fillMaxWidth(),
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text(text = "Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier =  Modifier.fillMaxWidth(),
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it },
            label = { Text(text = "Contraseña") },
            modifier =  Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = verificarContra,
            onValueChange = { verificarContra = it },
            label = { Text(text = "Verificar Contraseña") },
            modifier =  Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text(text = "Fecha de Nacimiento") },
            placeholder = { Text(text = "DD/MM/AAAA")},
            modifier =  Modifier.fillMaxWidth(),
            isError = isError
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button( onClick = {
            if (!(nombre.isNotEmpty() && correo.isNotEmpty() && contra.isNotEmpty() && verificarContra.isNotEmpty() && fechaNacimiento.isNotEmpty())) {
                Toast.makeText(context, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
            }

            if (contra != verificarContra) {
                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                isError = true
            }

            val edad = calcularEdad(fechaNacimiento)

            if (edad == -1) {
                Toast.makeText(context, "Formato de fecha inválido (DD/MM/AAAA)", Toast.LENGTH_SHORT).show()
                isError = true
                return@Button
            }

            if (edad < 18) {
                Toast.makeText(context, "Debes ser mayor de 18 años", Toast.LENGTH_SHORT).show()
                isError = true
                return@Button
            }

            auth.createUserWithEmailAndPassword(correo, contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var userID = auth.currentUser?.uid ?: "anonimo"
                        var usuario = Usuario(nombre, correo, fechaNacimiento)

                        database.child("usuarios").child(userID).setValue(usuario)

                        Toast.makeText(context, "El usuario ha sido creado con éxito", Toast.LENGTH_SHORT).show()

                        val intent = Intent(context, MainActivity::class.java)
                        auth.signOut()
                        intent.putExtra("nombre", nombre)
                        intent.putExtra("correo", correo)

                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    } else {
                        Toast.makeText(context, "No se pudo ingresar", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        ) { Text(text = "Registrarse")}
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview2() {
//    LoginTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            PantallaRegistro(
//                modifier = Modifier.padding(innerPadding)
//            )
//        }
//    }
//}