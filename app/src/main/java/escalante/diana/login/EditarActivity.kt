package escalante.diana.login

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
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import escalante.diana.login.ui.theme.LoginTheme

class EditarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var uid = Firebase.auth.currentUser?.uid ?: ""
        var myRef = Firebase.database.getReference("usuarios").child(uid)

        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaEditar(
                        myRef,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaEditar(myRef: DatabaseReference, modifier: Modifier = Modifier) {
    var nuevoNombre by remember() { mutableStateOf("") }
    var nuevaFechaNacimiento by remember() { mutableStateOf("") }
    var isError by remember() { mutableStateOf(false) }
    var context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar Datos", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nuevoNombre,
            onValueChange = { nuevoNombre = it },
            label = { Text(text = "Nombre Completo") },
            modifier =  Modifier.fillMaxWidth(),
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nuevaFechaNacimiento,
            onValueChange = { nuevaFechaNacimiento = it },
            label = { Text(text = "Fecha de Nacimiento") },
            placeholder = { Text(text = "DD/MM/AAAA")},
            modifier =  Modifier.fillMaxWidth(),
            isError = isError
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button( onClick = {
            if (!(nuevoNombre.isNotEmpty() && nuevaFechaNacimiento.isNotEmpty())) {
                Toast.makeText(context, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
            }

            val edad = calcularEdad(nuevaFechaNacimiento)

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

            myRef.child("name").setValue(nuevoNombre)
            myRef.child("fecha").setValue(nuevaFechaNacimiento)

            val intent = Intent(context, PrincipalActivity::class.java)
            context.startActivity(intent)
        }
        ) { Text(text = "Actualizar")}
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    LoginTheme {
//        Greeting("Android")
//    }
//}