# 📘 Clase: prueba unitaria de un servicio

Archivo de ejemplo: `billing-ms/src/test/java/personal/billing_ms/service/impl/BillingTariffServiceImplTest.java`
Servicio probado: `billing-ms/src/main/java/personal/billing_ms/service/impl/BillingTariffServiceImpl.java`

## 1. ¿Qué es una prueba unitaria?

Es una prueba que revisa **una sola clase por separado**. No levanta Spring, no se conecta a PostgreSQL, Kafka ni Eureka. Por eso corre en milisegundos.

Pero el servicio necesita un `BillingTariffRepository` para funcionar. Como no queremos usar una base de datos real, lo reemplazamos por un **mock**: un objeto falso al que le decimos qué responder.

## 2. Las piezas

| Pieza | Para qué sirve |
|---|---|
| `@ExtendWith(MockitoExtension.class)` | Hace que JUnit 5 use Mockito |
| `@Mock BillingTariffRepository` | Crea un repositorio falso. Si no le dices qué responder, devuelve `null`, `false` o `Optional.empty()` |
| `@InjectMocks BillingTariffServiceImpl` | Crea el servicio **real** y le pasa el mock por el constructor (que genera `@RequiredArgsConstructor`) |
| `when(...).thenReturn(...)` | Le dice al mock qué responder cuando lo llamen |
| `assertEquals`, `assertThrows` | Revisan el resultado |
| `verify(...)` | Revisa **si se llamó** a un método del mock y cuántas veces |

## 3. El patrón AAA (lo más importante)

Todo test sigue tres pasos:

```java
// ARRANGE: preparas los datos y le dices al mock qué responder
when(billingTariffRepository.existsById(1L)).thenReturn(false);

// ACT: llamas al método que estás probando, solo una vez
BillingTariffResponse response = service.createTariff(request);

// ASSERT: revisas el resultado
assertEquals("PEN", response.currency());
```

## 4. Los 3 tests del ejemplo

**Test 1: caso feliz** (`createTariff_cuandoNoExiste_guardaYDevuelveRespuesta`)

Mira el código de `createTariff`:

```java
if (billingTariffRepository.existsById(...)) throw ...;   // camino A
... billingTariffRepository.save(tariff)                   // camino B
```

Hacemos que `existsById` devuelva `false` para que el código siga por el camino B. Con `thenAnswer(inv -> inv.getArgument(0))`, el `save` devuelve el mismo objeto que recibe, como si la base de datos lo hubiera guardado.

También usamos `ArgumentCaptor` para "atrapar" la entidad que se envió a `save` y revisar que se armó bien.

**Test 2: caso de error** (`createTariff_cuandoYaExiste_lanzaBusinessExceptionYNoGuarda`)

Ahora `existsById` devuelve `true`, así que el código va por el camino A.

- `assertThrows` revisa que se lance una `BusinessException`, y de paso te la devuelve para que revises su `code` y `status` (400).
- `verify(repo, never()).save(any())` revisa que **nunca** se haya guardado nada. Esto es importante: no basta con que lance el error, tampoco debe guardar datos.

**Test 3: no encontrado** (`getTariff_cuandoNoExiste_lanzaNotFound`)

`findById(99L)` devuelve `Optional.empty()`, entonces el `orElseThrow` lanza el error 404.

> 💡 **Regla de oro:** por cada `if`, `orElseThrow` o camino distinto dentro del método, escribe al menos un test.

## 5. Cómo ejecutarlo

```bash
cd billing-ms
./mvnw test -Dtest=BillingTariffServiceImplTest
```

---

# 📝 Tu ejercicio

En **el mismo archivo** de test, agrega pruebas para `updateTariff` y `getPriceByAppointmentTypeId` (los dos están en `BillingTariffServiceImpl.java`).

**Ejercicio 1: `updateTariff`, caso feliz**

- Crea una `BillingTariff` existente con el builder (id `1L`, precio `50.00`, `"PEN"`).
- Haz que `findById(1L)` la devuelva dentro de un `Optional.of(...)`.
- Haz que `save` devuelva lo que recibe.
- Llama a `updateTariff(1L, new UpdateBillingTariffRequest(new BigDecimal("75.00"), "USD"))`.
- ✅ Revisa que la respuesta tenga el precio `75.00` y la moneda `"USD"`.

**Ejercicio 2: `updateTariff`, no existe**

- `findById` devuelve vacío.
- ✅ Revisa que lance `BusinessException` con `BILLING_TARIFF_NOT_FOUND`.
- ✅ Revisa con `verify` que **nunca** se llamó a `save`.

**Ejercicio 3: `getPriceByAppointmentTypeId`**

- Haz un test donde devuelve el precio correcto y otro donde lanza el error.

**🌟 Reto extra:** prueba `getTariffs()` con `findAll()` devolviendo 2 tarifas, y revisa que la lista tenga tamaño 2 (`response.size()`).

**Pista para nombrar tus tests:** `metodo_condicion_resultadoEsperado`, por ejemplo `updateTariff_cuandoNoExiste_lanzaNotFound`.
