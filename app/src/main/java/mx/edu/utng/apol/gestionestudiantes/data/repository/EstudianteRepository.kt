package mx.edu.utng.apol.gestionestudiantes.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.edu.utng.apol.gestionestudiantes.data.model.Estudiante
import mx.edu.utng.apol.gestionestudiantes.data.model.EstudianteRequest
import mx.edu.utng.apol.gestionestudiantes.data.remote.RetrofitClient


class EstudianteRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Obtiene la lista de todos los estudiantes
     * @return Result con lista de estudiantes o excepción en caso de error
     */
    suspend fun obtenerEstudiantes(): Result<List<Estudiante>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerEstudiantes()

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene un estudiante específico por ID
     */
    suspend fun obtenerEstudiante(id: Int): Result<Estudiante> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerEstudiante(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Estudiante no encontrado"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Crea un nuevo estudiante
     */
    suspend fun crearEstudiante(estudiante: EstudianteRequest): Result<Estudiante> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearEstudiante(estudiante)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear estudiante"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Actualiza un estudiante existente
     */
    suspend fun actualizarEstudiante(id: Int, estudiante: EstudianteRequest): Result<Estudiante> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.actualizarEstudiante(id, estudiante)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar estudiante"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Elimina un estudiante
     */
    suspend fun eliminarEstudiante(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.eliminarEstudiante(id)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al eliminar estudiante"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}