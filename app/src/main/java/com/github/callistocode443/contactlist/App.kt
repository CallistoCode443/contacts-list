package com.github.callistocode443.contactlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.checkSelfPermission
import com.github.callistocode443.contactlist.ui.theme.ContactListTheme
import androidx.core.net.toUri
import kotlin.collections.listOf

@Preview
@Composable
fun AppPreview() {
    ContactListTheme {
        App()
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var contacts by remember { mutableStateOf(listOf<Contact>()) }

    var hasPermission by remember {
        mutableStateOf(
            checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasPermission = isGranted
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            try {
                contacts = context.fetchAllContacts()
                Toast.makeText(
                    context,
                    "Найден(о) ${contacts.size} контакт(ов)",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("App", "Ошибка при загрузке контактов", e)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF277477))
        )
        if (hasPermission && contacts.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contacts) { contact ->
                    ContactItem(contact)
                }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No contacts found")
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = ("tel:" + contact.phoneNumber).toUri()
                context.startActivity(intent)
            }
            .padding(16.dp)
    ) {
        Text(text = contact.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = contact.phoneNumber)
    }
}