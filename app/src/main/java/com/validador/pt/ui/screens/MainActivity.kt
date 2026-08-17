package com.validador.pt.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.validador.pt.R
import com.validador.pt.data.model.ValidationType
import com.validador.pt.ui.theme.ValidadorPTTheme
import com.validador.pt.ui.viewmodels.ValidatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set locale based on system or saved preference
        com.validador.pt.util.LocaleManager.applyLocale(this)
        
        setContent {
            ValidadorPTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ValidatorApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatorApp() {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<ValidatorViewModel>()
    val state by viewModel.state.collectAsState()
    
    val context = LocalContext.current
    
    val tabTitles = listOf(
        context.getString(R.string.tab_nif),
        context.getString(R.string.tab_iban),
        context.getString(R.string.tab_nib)
    )
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = { Text(context.getString(R.string.app_name)) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
        // TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> NifTabContent(viewModel, state, focusManager)
                1 -> IbanTabContent(viewModel, state, focusManager)
                2 -> NibTabContent(viewModel, state, focusManager)
            }
        }
        
        // Legal disclaimer (permanent)
        Box(
            modifier = Modifier
                .background(Color(0xFFFFEB3B))
                .padding(8.dp)
        ) {
            Text(
                text = context.getString(R.string.disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun NifTabContent(
    viewModel: ValidatorViewModel,
    state: com.validador.pt.ui.state.ValidatorUiState,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = state.inputValue,
            onValueChange = { viewModel.setInput(it, ValidationType.NIF) },
            label = { Text(context.getString(R.string.hint_nif)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.validate(ValidationType.NIF)
                }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.validate(ValidationType.NIF)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.btn_validate))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        state.validationResult?.let { result ->
            ResultCard(result, context.getString(R.string.label_valid_nif))
        }
    }
}

@Composable
fun IbanTabContent(
    viewModel: ValidatorViewModel,
    state: com.validador.pt.ui.state.ValidatorUiState,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = state.inputValue,
            onValueChange = { viewModel.setInput(it, ValidationType.IBAN) },
            label = { Text(context.getString(R.string.hint_iban)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.validate(ValidationType.IBAN)
                }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.validate(ValidationType.IBAN)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.btn_validate))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        state.validationResult?.let { result ->
            ResultCard(result, context.getString(R.string.label_valid_iban))
        }
    }
}

@Composable
fun NibTabContent(
    viewModel: ValidatorViewModel,
    state: com.validador.pt.ui.state.ValidatorUiState,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = state.inputValue,
            onValueChange = { viewModel.setInput(it, ValidationType.NIB) },
            label = { Text(context.getString(R.string.hint_nib)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.validate(ValidationType.NIB)
                }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.validate(ValidationType.NIB)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.btn_validate))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        state.validationResult?.let { result ->
            ResultCard(result, context.getString(R.string.label_valid_nib))
        }
    }
}

@Composable
fun ResultCard(result: String, label: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.contains("válido", ignoreCase = true) || 
                               result.contains("valid", ignoreCase = true))
                Color(0xFF4CAF50)
            else
                MaterialTheme.colorScheme.error
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = result,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}