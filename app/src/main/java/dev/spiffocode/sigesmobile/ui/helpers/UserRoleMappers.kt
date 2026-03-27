package dev.spiffocode.sigesmobile.ui.helpers

import dev.spiffocode.sigesmobile.data.remote.dto.UserRole


public fun UserRole.toText(): String{
    return when(this){
        UserRole.INSTITUTIONAL_STAFF -> "Personal Institucional"
        UserRole.STUDENT -> "Estudiante"
        UserRole.ADMIN -> "Administrador"

    }
}