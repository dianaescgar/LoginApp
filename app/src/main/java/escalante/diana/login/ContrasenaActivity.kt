package escalante.diana.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import escalante.diana.login.ui.theme.LoginTheme

class ContrasenaActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth

        setContent {
            LoginTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    PantallaContrasena(
                        auth,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaContrasena(auth: FirebaseAuth, modifier: Modifier = Modifier) {
    var correo by remember() { mutableStateOf("") }
    var isError by remember() { mutableStateOf(false) }
    var context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recuperar Contraseña", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text(text = "Correo Electrónico") },
            modifier =  Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = isError
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button( onClick = {
            if (correo.isNotEmpty()) {
                auth.sendPasswordResetEmail(correo)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Enlace de recuperación enviado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No se pudo enviar", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Favor de llenar el campo", Toast.LENGTH_SHORT).show()
                isError = true
            }
        }
        ) { Text(text = "Enviar enlace de recuperación")}

        Spacer(modifier = Modifier.height(16.dp))

        Button( onClick = {

        }
        ) { Text(text = "Cancelar")}
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview3() {
//    LoginTheme {
//        Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
//            PantallaContrasena(
//                modifier = Modifier.padding(innerPadding)
//            )
//        }
//    }
//}