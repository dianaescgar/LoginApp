package escalante.diana.login

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Usuario(var name: String, var correo: String, var fecha: String)
