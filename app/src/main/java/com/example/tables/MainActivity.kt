package com.example.tables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

// ---------------------------
// Modelos de datos
// ---------------------------
@Serializable
data class Movimiento(
    val id: Int,
    val tipo: String, // "ingreso" o "egreso"
    val monto: Double,
    val fecha: String,
    val descripcion: String,
    val usuario: String
)

@Serializable
data class MovimientoResponse(
    val movimientos: List<Movimiento>
)

// ---------------------------
// Fuente de datos en JSON
// ---------------------------
private val jsonData = """
{
  "movimientos": [
    {
      "id": 1,
      "tipo": "ingreso",
      "monto": 2500.50,
      "fecha": "2025-09-01T10:30:00Z",
      "descripcion": "Venta de producto",
      "usuario": "Ronald"
    },
    {
      "id": 2,
      "tipo": "egreso",
      "monto": 300.00,
      "fecha": "2025-09-02T12:45:00Z",
      "descripcion": "Compra de insumos",
      "usuario": "Pedro"
    },
    {
      "id": 3,
      "tipo": "ingreso",
      "monto": 1500.00,
      "fecha": "2025-09-03T14:10:00Z",
      "descripcion": "Servicio técnico",
      "usuario": "María"
    },
     {
      "id": 4,
      "tipo": "ingreso",
      "monto": 100.00,
      "fecha": "2025-09-03T14:10:00Z",
      "descripcion": "Servicio técnico",
      "usuario": "Abel"
    },
    {
      "id": 5,
      "tipo": "ingreso",
      "monto": 100.00,
      "fecha": "2025-09-03T14:10:00Z",
      "descripcion": "Servicio técnico",
      "usuario": "Abel"
    },
    {
      "id": 6,
      "tipo": "ingreso",
      "monto": 1100.00,
      "fecha": "2025-09-03T14:10:00Z",
      "descripcion": "Servicio técnico",
      "usuario": "Abel"
    }
  ]
}
""".trimIndent()

// ---------------------------
// Utilidad: Calcula saldo total
// ---------------------------
private fun calcularSaldoTotal(movimientos: List<Movimiento>): Double {
    // Suma ingresos y resta egresos
    var total = 0.0
    for (mov in movimientos) {
        total += if (mov.tipo.equals("ingreso", ignoreCase = true)) mov.monto else -mov.monto
    }
    return total
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configuración de JSON: tolerante a campos desconocidos y nombres flexibles
        val json = Json { ignoreUnknownKeys = true }
        // Cargar y parsear los datos desde la variable jsonData
        val data = json.decodeFromString<MovimientoResponse>(jsonData)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HistorialScreen(data.movimientos)
                }
            }
        }
    }
}

// ---------------------------
// UI principal con Compose
// ---------------------------
@Composable
private fun HistorialScreen(movimientos: List<Movimiento>) {
    val saldo = remember(movimientos) { calcularSaldoTotal(movimientos) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Encabezado con saldo actual
        Text(
            text = "Saldo actual",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "${String.format("%,.2f", saldo)}",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = if (saldo >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Encabezado de tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Fecha", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Descripción", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
            Text("Monto", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            Text("Usuario", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }

        HorizontalDivider(thickness = 1.dp, color = Color.Gray)

        // Lista en forma de tabla
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(movimientos, key = { it.id }) { mov ->
                MovimientoRow(mov)
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
private fun MovimientoRow(mov: Movimiento) {
    val esIngreso = mov.tipo.equals("ingreso", ignoreCase = true)
    val montoColor = if (esIngreso) Color(0xFF2E7D32) else Color(0xFFC62828)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(mov.fecha.take(10), modifier = Modifier.weight(1f)) // Solo YYYY-MM-DD
        Text(mov.descripcion, modifier = Modifier.weight(2f))
        Text(
            text = String.format("%,.2f", mov.monto),
            color = montoColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
        Text(
            mov.usuario,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
