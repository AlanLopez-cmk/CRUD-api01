package mx.edu.utng.apol.gestionestudiantes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utng.apol.gestionestudiantes.ui.screens.DetalleEstudianteScreen
import mx.edu.utng.apol.gestionestudiantes.ui.screens.FormularioScreen
import mx.edu.utng.apol.gestionestudiantes.ui.screens.ListaEstudiantesScreen
import mx.edu.utng.apol.gestionestudiantes.ui.theme.GestionEstudiantesTheme
import mx.edu.utng.apol.gestionestudiantes.ui.viewmodel.EstudianteViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: EstudianteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestionEstudiantesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: EstudianteViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val estudiantes by viewModel.estudiantes
    val estudianteSeleccionado by viewModel.estudianteSeleccionado
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val operacionExitosa by viewModel.operacionExitosa

    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "Cerrar"
                )
                viewModel.limpiarError()
            }
        }
    }

    LaunchedEffect(operacionExitosa) {
        if (operacionExitosa) {
            navController.popBackStack()
            viewModel.resetearOperacionExitosa()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            ListaEstudiantesScreen(
                estudiantes = estudiantes,
                isLoading = isLoading,
                error = error,
                onEstudianteClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onAgregarClick = {
                    viewModel.limpiarEstudianteSeleccionado()
                    navController.navigate("nuevo")
                },
                onEliminarClick = { id ->
                    viewModel.eliminarEstudiante(id)
                },
                onRecargarClick = {
                    viewModel.cargarEstudiantes()
                }
            )
        }

        composable(
            route = "detalle/{estudianteId}",
            arguments = listOf(navArgument("estudianteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val estudianteId = backStackEntry.arguments?.getInt("estudianteId") ?: 0
            LaunchedEffect(estudianteId) {
                viewModel.cargarEstudiante(estudianteId)
            }
            DetalleEstudianteScreen(
                estudiante = estudianteSeleccionado,
                isLoading = isLoading,
                onBackClick = { navController.popBackStack() },
                onEditarClick = { id -> navController.navigate("editar/$id") }
            )
        }

        composable("nuevo") {
            FormularioScreen(
                estudiante = null,
                isLoading = isLoading,
                onBackClick = { navController.popBackStack() },
                onGuardarClick = { nombre, edad, carrera, promedio ->
                    viewModel.crearEstudiante(nombre, edad, carrera, promedio)
                }
            )
        }

        composable(
            route = "editar/{estudianteId}",
            arguments = listOf(navArgument("estudianteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val estudianteId = backStackEntry.arguments?.getInt("estudianteId") ?: 0
            LaunchedEffect(estudianteId) {
                viewModel.cargarEstudiante(estudianteId)
            }
            FormularioScreen(
                estudiante = estudianteSeleccionado,
                isLoading = isLoading,
                onBackClick = { navController.popBackStack() },
                onGuardarClick = { nombre, edad, carrera, promedio ->
                    viewModel.actualizarEstudiante(estudianteId, nombre, edad, carrera, promedio)
                }
            )
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}