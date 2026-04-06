package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.spiffocode.sigesmobile.data.remote.dto.UserRole
import dev.spiffocode.sigesmobile.ui.helpers.toText
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme

@Composable
public fun UserRoleChip(
    userRole: UserRole,
    shape: CornerBasedShape = MaterialTheme.shapes.medium
){

    val backgroundTint = if (userRole == UserRole.ADMIN)
       MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.background.copy(alpha = 0.5f)


    val onBackgroundTint = if (userRole == UserRole.ADMIN)
        MaterialTheme.colorScheme.background
    else MaterialTheme.colorScheme.secondary

    val icon = if (userRole == UserRole.ADMIN)
        Icons.Default.Shield
    else Icons.Default.Person


    Row(
        modifier = Modifier
            .clip(shape)
            .background(backgroundTint)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = onBackgroundTint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(userRole.toText(), style = MaterialTheme.typography.labelLarge, color = onBackgroundTint)
    }
}



@Composable
@Preview(showBackground = true, backgroundColor = 0xffe1cfff)
public fun UserRoleChipStudentPreview(){
    SigesmobileTheme {
        UserRoleChip(
            userRole = UserRole.STUDENT
        )
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xffe1cfff)
public fun UserRoleChipInstitutionalStaffPreview(){
    SigesmobileTheme {
        UserRoleChip(
            userRole = UserRole.INSTITUTIONAL_STAFF
        )
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xffe1cfff)
public fun UserRoleChipAdminPreview(){
    SigesmobileTheme {
        UserRoleChip(
            userRole = UserRole.ADMIN
        )
    }
}