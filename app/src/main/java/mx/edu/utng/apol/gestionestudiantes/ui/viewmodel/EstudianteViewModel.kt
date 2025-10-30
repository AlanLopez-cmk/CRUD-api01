package mx.edu.utng.apol.gestionestudiantes.ui.viewmodel


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mx.edu.utng.apol.gestionestudiantes.data.repository.EstudianteRepository
import kotlinx.coroutines.launch
import mx.edu.utng.apol.gestionestudiantes.data.model.Estudiante
import mx.edu.utng.apol.gestionestudiantes.data.model.EstudianteRequest

/**
 * ViewModel que maneja la lógica de negocio y el estado de la UI.
 *
 * Analogía: El ViewModel es como el "cerebro" de la aplicación.
 * - Recibe las acciones del usuario (clicks, inputs)
 * - Decide qué hacer con esas acciones
 * - Actualiza el estado que la UI observa
 * - Se comunica con el Repository para obtener/guardar datos
 *
 * Ventajas del ViewModel:
 * - Sobrevive a cambios de configuración (rotación de pantalla)
 * - Separa la lógica de la UI
 * - Facilita testing
 */
class EstudianteViewModel : ViewModel() {

    private val repository = EstudianteRepository()

    // ========== ESTADOS ==========
    private val _estudiantes = mutableStateOf<List<Estudiante>>(emptyList())
    private val _estudianteSeleccionado = mutableStateOf<Estudiante?>(null)
    private val _isLoading = mutableStateOf(false)
    private val _error = mutableStateOf<String?>(null)
    private val _operacionExitosa = mutableStateOf(false)

    val estudiantes: State<List<Estudiante>> = _estudiantes
    val estudianteSeleccionado: State<Estudiante?> = _estudianteSeleccionado
    val isLoading: State<Boolean> = _isLoading
    val error: State<String?> = _error
    val operacionExitosa: State<Boolean> = _operacionExitosa

    init {
        cargarEstudiantes()
    }

    // ========== OPERACIONES CRUD ==========
    fun cargarEstudiantes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.obtenerEstudiantes()
                .onSuccess { lista ->
                    _estudiantes.value = lista
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar estudiantes"
                }
            _isLoading.value = false
        }
    }

    fun cargarEstudiante(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.obtenerEstudiante(id)
                .onSuccess { estudiante ->
                    _estudianteSeleccionado.value = estudiante
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar estudiante"
                }
            _isLoading.value = false
        }
    }

    fun crearEstudiante(
        nombre: String,
        edad: Int,
        carrera: String,
        promedio: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _operacionExitosa.value = false

            val nuevoEstudiante = EstudianteRequest(
                nombre = nombre,
                edad = edad,
                carrera = carrera,
                promedio = promedio
            )
            repository.crearEstudiante(nuevoEstudiante)
                .onSuccess {
                    _operacionExitosa.value = true
                    cargarEstudiantes()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al crear estudiante"
                }
            _isLoading.value = false
        }
    }

    fun actualizarEstudiante(
        id: Int,
        nombre: String,
        edad: Int,
        carrera: String,
        promedio: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _operacionExitosa.value = false
            
            val estudianteActualizado = EstudianteRequest(
                nombre = nombre,
                edad = edad,
                carrera = carrera,
                promedio = promedio
            )
            repository.actualizarEstudiante(id, estudianteActualizado)
                .onSuccess {
                    _operacionExitosa.value = true
                    cargarEstudiantes()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al actualizar estudiante"
                }
            _isLoading.value = false
        }
    }

    fun eliminarEstudiante(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.eliminarEstudiante(id)
                .onSuccess {
                    cargarEstudiantes()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al eliminar estudiante"
                }
            _isLoading.value = false
        }
    }

    // ========== MÉTODOS AUXILIARES ==========
    fun limpiarError() {
        _error.value = null
    }

    fun resetearOperacionExitosa() {
        _operacionExitosa.value = false
    }

    fun limpiarEstudianteSeleccionado() {
        _estudianteSeleccionado.value = null
    }
}
