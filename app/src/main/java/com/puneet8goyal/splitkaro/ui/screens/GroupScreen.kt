package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.viewmodel.GroupViewModel

@Composable
fun GroupScreen(viewModel: GroupViewModel = hiltViewModel(), onGroupClick: (Long) -> Unit) {
    val groups by viewModel.groups.collectAsState()
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Groups",
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        if (isLoading) {
            Text(
                text = "Loading groups...",
                modifier = Modifier.padding(8.dp),
                color = Color.Gray
            )
        } else if (groups.isEmpty()) {
            Text(
                text = "No groups yet! Create one to start.",
                modifier = Modifier.padding(8.dp),
                color = Color.Gray
            )
        } else {
            LazyColumn {
                items(groups) { group ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onGroupClick(group.id) }
                    ) {
                        Text(
                            text = group.name,
                            modifier = Modifier.padding(12.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.createGroup("New Group") }, // Temporary group name
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !isLoading
        ) {
            Text("Create Group")
        }

        if (snackbarMessage.isNotEmpty()) {
            Text(
                text = snackbarMessage,
                color = if (snackbarMessage.contains(
                        "success",
                        ignoreCase = true
                    )
                ) Color(0xFF4CAF50) else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}