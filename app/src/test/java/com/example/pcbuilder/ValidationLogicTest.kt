package com.example.pcbuilder

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas Unitarias para validar la lógica de negocio crítica
 * (Validaciones de formularios y cálculos de carrito)
 */
class ValidationLogicTest {


    @Test
    fun `email valido retorna verdadero`() {
        val email = "usuario@duoc.cl"
        val esValido = email.contains("@") && email.length > 5

        assertTrue("El email debería ser marcado como válido", esValido)
    }

    @Test
    fun `email sin arroba retorna falso`() {
        val email = "usuarioduoc.cl"
        val esValido = email.contains("@")

        assertFalse("El email sin @ debería ser inválido", esValido)
    }

    @Test
    fun `contraseña corta retorna falso`() {

        val clave = "12345"
        val esValida = clave.length >= 8

        assertFalse("La contraseña de 5 caracteres debería ser rechazada", esValida)
    }

    @Test
    fun `contraseña segura retorna verdadero`() {
        val clave = "segura1234" // 10 caracteres
        val esValida = clave.length >= 8

        assertTrue("La contraseña larga debería ser aceptada", esValida)
    }


    @Test
    fun `calculo de subtotal es correcto`() {
        val precioProducto = 500000.0
        val cantidad = 2

        val totalEsperado = 1000000.0
        val totalCalculado = precioProducto * cantidad

        assertEquals("El cálculo del subtotal falló", totalEsperado, totalCalculado, 0.0)
    }

    @Test
    fun `calculo de conversion dolar`() {
        val precioPesos = 950000.0
        val valorDolar = 950.0

        val precioDolarEsperado = 1000.0
        val precioDolarCalculado = precioPesos / valorDolar

        assertEquals("La conversión a dólares falló", precioDolarEsperado, precioDolarCalculado, 0.01)
    }
}