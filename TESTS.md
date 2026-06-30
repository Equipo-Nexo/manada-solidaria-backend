# Documentacion de Tests — Manada Solidaria Backend

## Indice

1. [Infraestructura de Tests](#infraestructura-de-tests)
2. [Tests de Aplicacion](#1-tests-de-aplicacion)
3. [Tests de Autenticacion (Auth)](#2-tests-de-autenticacion-auth)
4. [Tests de Ping (Health Check / Autorizacion)](#3-tests-de-ping-health-check--autorizacion)
5. [Tests de Publicaciones de Animales (Animal Posts)](#4-tests-de-publicaciones-de-animales-animal-posts)
6. [Tests de Campanas](#5-tests-de-campanas)

---

## Infraestructura de Tests

Antes de detallar cada test, se describe la infraestructura comun que soporta la ejecucion de las pruebas.

| Clase | Ubicacion | Descripcion |
|---|---|---|
| `BaseIntegrationTest` | `common/integrations/base/` | Clase base abstracta para todos los tests de integracion. Levanta el contexto Spring (`@SpringBootTest`), configura `MockMvc` con Spring Security, y provee utilidades para serializar objetos a JSON y generar credenciales Basic Auth. |
| `BaseAuthenticatedIntegrationTest` | `common/integrations/base/` | Extiende `BaseIntegrationTest`. En su `@BeforeEach` realiza un login real contra `/auth/login` con las credenciales del usuario `admin` y almacena el `accessToken` JWT para que los tests hijos lo usen en sus requests autenticadas. |
| `MockBaseDataUtils` | `common/utils/` | Provee constantes compartidas (e.g. `INVALID_ACCESS_TOKEN`: un JWT firmado con clave incorrecta) y los casos de prueba parametrizados para el endpoint `/ping`. |
| `MockAuthDataUtils` | `auth/utils/` | Define constantes de usuarios/contrasenas y los proveedores de argumentos (`@MethodSource`) para los tests parametrizados de login y signup. |
| `MockAnimalPostDataUtils` | `animal_posts/utils/` | Contiene payloads JSON validos e invalidos para crear publicaciones de animales, y los proveedores de argumentos para los tests parametrizados de creacion, transicion de estado y filtrado. |
| `MockCampaignDataUtils` | `campaigns/utils/` | Define objetos `CreateCampaignRequest` validos e invalidos, proveedores de argumentos para los tests parametrizados de campanas, y metodos factory para construir modelos de campana directamente en la base de datos. |

---

## 1. Tests de Aplicacion

**Clase:** `ManadaSolidariaBackendApplicationTests`
**Tipo:** Test unitario / Smoke test
**Ubicacion:** `src/test/java/.../ManadaSolidariaBackendApplicationTests.java`

| # | Metodo | Titulo | Descripcion |
|---|--------|--------|-------------|
| 1 | `contextLoads()` | Carga del contexto de Spring | Verifica que el contexto de la aplicacion Spring Boot se levanta correctamente sin errores. Es un smoke test que asegura que toda la configuracion, beans y dependencias se resuelven sin fallos. |

---

## 2. Tests de Autenticacion (Auth)

**Clase:** `AuthControllerTest`
**Tipo:** Tests de integracion
**Ubicacion:** `src/test/java/.../auth/integrations/AuthControllerTest.java`
**Extiende:** `BaseIntegrationTest` (no requiere autenticacion previa)

### 2.1 Login (`POST /auth/login`)

| # | Caso | Titulo | Descripcion |
|---|------|--------|-------------|
| 1 | `loginTests` | Usuario existente con contrasena correcta — login exitoso | Envia credenciales Basic Auth validas (`admin`/`admin`) al endpoint `/auth/login`. Verifica que responde `200 OK` y que el body contiene un `accessToken` JWT. |
| 2 | `loginTests` | Usuario existente con contrasena incorrecta — 401 | Envia credenciales con un username valido pero contrasena incorrecta. Verifica que responde `401 Unauthorized`. |
| 3 | `loginTests` | Usuario inexistente — 401 | Envia credenciales con un username que no existe en el sistema. Verifica que responde `401 Unauthorized`. |

### 2.2 Signup (`POST /auth/signup`)

| # | Caso | Titulo | Descripcion |
|---|------|--------|-------------|
| 4 | `signupTests` | Username nuevo — registro exitoso | Envia un `CreateUserRequest` con un username que no existe, contrasenas coincidentes y email valido. Verifica que responde `201 Created`. |
| 5 | `signupTests` | Usuario rescatista — se crea con rol RESCUER | Envia un request con el rol `RESCUER` explicitamente asignado. Verifica que el usuario se crea exitosamente (`201 Created`) con dicho rol en lugar del rol por defecto `COMMUNITY`. |
| 6 | `signupTests` | Username ya existente — error | Intenta registrar un usuario con un username que ya existe (`admin`). Verifica que responde `400 Bad Request`. |
| 7 | `signupTests` | Contrasenas no coinciden — error | Envia un request donde `password` y `repeatedPassword` son distintos. Verifica que responde `400 Bad Request`. |
| 8 | `signupTests` | Email no enviado — error | Envia un request sin el campo `email`. Verifica que responde `400 Bad Request`. |

---

## 3. Tests de Ping (Health Check / Autorizacion)

**Clase:** `PingControlletTest`
**Tipo:** Tests de integracion
**Ubicacion:** `src/test/java/.../common/integrations/PingControlletTest.java`
**Extiende:** `BaseAuthenticatedIntegrationTest`

| # | Caso | Titulo | Descripcion |
|---|------|--------|-------------|
| 1 | `ping_shouldReturnPong` | Ping con token valido — responde "pong" | Envia un `GET /ping` con un JWT valido obtenido del login. Verifica que responde `200 OK` y que el cuerpo de la respuesta es exactamente `"pong"`. |
| 2 | `ping_shouldReturnPong` | Ping con token invalido — 401 | Envia un `GET /ping` con un JWT invalido (firmado con otra clave). Verifica que responde `401 Unauthorized`. Valida que la capa de seguridad protege endpoints autenticados. |

---

## 4. Tests de Publicaciones de Animales (Animal Posts)

**Clase:** `AnimalPostControllerTest`
**Tipo:** Tests de integracion
**Ubicacion:** `src/test/java/.../animal_posts/integrations/AnimalPostControllerTest.java`
**Extiende:** `BaseAuthenticatedIntegrationTest`
**Anotaciones de clase:** `@Transactional` (cada test se ejecuta en una transaccion que se revierte al finalizar)

### 4.1 Creacion (`POST /animal-posts`) — Tests parametrizados de validacion

| # | Caso | Titulo | Descripcion |
|---|------|--------|-------------|
| 1 | `createTests` | Request LOST valida — 201 Created | Envia un payload completo y valido de tipo `LOST`. Verifica que responde `201 Created` y que el campo `type` en la respuesta es `"LOST"`. |
| 2 | `createTests` | Request ADOPTION valida — 201 Created | Envia un payload completo y valido de tipo `ADOPTION`. Verifica que responde `201 Created` y que el campo `type` es `"ADOPTION"`. |
| 3 | `createTests` | LOST sin `hasOwner` — 400 | Envia un payload de tipo `LOST` sin el campo obligatorio `hasOwner`. Verifica que responde `400 Bad Request`. |
| 4 | `createTests` | ADOPTION sin `inTransit` — 400 | Envia un payload de tipo `ADOPTION` sin el campo obligatorio `inTransit`. Verifica que responde `400 Bad Request`. |
| 5 | `createTests` | ADOPTION con `reward` — 400 | Envia un payload de tipo `ADOPTION` que incluye el campo `reward` (exclusivo de LOST). Verifica que responde `400 Bad Request`. |
| 6 | `createTests` | Sin `type` — 400 | Envia un payload sin el campo `type`. Verifica que responde `400 Bad Request`. |
| 7 | `createTests` | Sin `title` — 400 | Envia un payload sin titulo. Verifica que responde `400 Bad Request`. |
| 8 | `createTests` | Sin `description` — 400 | Envia un payload sin descripcion. Verifica que responde `400 Bad Request`. |
| 9 | `createTests` | Sin `imageId` — 400 | Envia un payload sin imagen. Verifica que responde `400 Bad Request`. |
| 10 | `createTests` | Sin `phoneNumber` — 400 | Envia un payload sin telefono de contacto. Verifica que responde `400 Bad Request`. |
| 11 | `createTests` | Sin `age` del animal — 400 | Envia un payload sin la edad del animal. Verifica que responde `400 Bad Request`. |
| 12 | `createTests` | Sin `animal` — 400 | Envia un payload sin el objeto animal completo. Verifica que responde `400 Bad Request`. |
| 13 | `createTests` | Sin `location` — 400 | Envia un payload sin ubicacion. Verifica que responde `400 Bad Request`. |

### 4.2 Creacion — Tests individuales de logica de negocio

| # | Metodo | Titulo | Descripcion |
|---|--------|--------|-------------|
| 14 | `create_persistsPostWithAuthenticatedOwnerAndInputData` | POST valido: persiste correctamente con owner del JWT | Crea un post LOST con datos validos. Verifica que: (a) la respuesta incluye todos los campos enviados (titulo, descripcion, imagen, animal, ubicacion, telefono, recompensa); (b) el `ownerId` corresponde al usuario autenticado via JWT (no al payload); (c) el post se persiste realmente en la base de datos y se puede recuperar via `GET /animal-posts`. |
| 15 | `create_lostWithoutHasOwner_returnsValidatorMessage` | LOST sin hasOwner: retorna mensaje del validador | Envia un post LOST sin el campo `hasOwner`. Verifica que el response body contiene el mensaje de error de validacion `"hasOwner es obligatorio"` generado por el validador `@ConditionalField`. |
| 16 | `create_withoutToken_returnsUnauthorized` | POST sin token — 401 | Intenta crear un post sin enviar header `Authorization`. Verifica que responde `401 Unauthorized`. |
| 17 | `create_withInvalidToken_returnsUnauthorized` | POST con token invalido — 401 | Intenta crear un post con un JWT firmado con clave incorrecta. Verifica que responde `401 Unauthorized`. |

### 4.3 Creacion — Tests de transicion de estado (ADOPTION + inTransit)

| # | Caso | Titulo | Descripcion |
|---|------|--------|-------------|
| 18 | `create_adoption_transitionsByInTransit` | Adopcion con `inTransit=true` — estado `SEARCHING_ADOPT` | Crea un post de tipo ADOPTION con `inTransit=true` (el transito ya esta resuelto). Verifica que: (a) responde `201` con status `SEARCHING_ADOPT`; (b) el historial de estados contiene exactamente dos entradas: `CREATED` (cerrado con `finishedAt` no nulo) y `SEARCHING_ADOPT` (abierto, `finishedAt` nulo); (c) el estado vigente actual es `SEARCHING_ADOPT`. |
| 19 | `create_adoption_transitionsByInTransit` | Adopcion con `inTransit=false` — estado `SEARCHING_ADOPT_AND_TRANSIT` | Crea un post de tipo ADOPTION con `inTransit=false` (aun necesita transito). Verifica que: (a) responde `201` con status `SEARCHING_ADOPT_AND_TRANSIT`; (b) el historial contiene `CREATED` (cerrado) y `SEARCHING_ADOPT_AND_TRANSIT` (abierto); (c) el estado vigente actual es `SEARCHING_ADOPT_AND_TRANSIT`. |

### 4.4 Listado y Filtrado (`GET /animal-posts`)

| # | Metodo / Caso | Titulo | Descripcion |
|---|---------------|--------|-------------|
| 20 | `list_returnsNewestFirst` | Listado ordenado del mas nuevo al mas viejo | Crea dos posts (uno LOST, luego uno ADOPTION). Verifica que `GET /animal-posts` los devuelve en orden descendente por fecha de creacion, con el mas reciente primero. Tambien verifica que el campo `status` refleja el estado correcto de cada subtipo. |
| 21 | `filterTests` | Sin filtros — devuelve todos los posts | Crea 4 posts (2 LOST, 2 ADOPTION con distintos estados). Verifica que sin parametros de filtro se devuelven los 4 posts. |
| 22 | `filterTests` | `type=LOST` — solo posts perdidos | Filtra por `type=LOST`. Verifica que se devuelven exactamente 2 resultados (los dos posts de tipo LOST). |
| 23 | `filterTests` | `type=ADOPTION` — solo adopciones | Filtra por `type=ADOPTION`. Verifica que se devuelven exactamente 2 resultados (los dos posts de tipo ADOPTION). |
| 24 | `filterTests` | `status=CREATED` — solo perdidos recien creados | Filtra por `status=CREATED`. Verifica que se devuelve exactamente 1 resultado (el post LOST en estado CREATED). |
| 25 | `filterTests` | `status=SEARCHING` — solo en busqueda | Filtra por `status=SEARCHING`. Verifica que se devuelve exactamente 1 resultado. |
| 26 | `filterTests` | `status=FOUND` — sin resultados | Filtra por `status=FOUND`. Verifica que no se devuelve ningun resultado (no hay posts con ese estado). |
| 27 | `filterTests` | `status=SEARCHING_ADOPT_AND_TRANSIT` — adopciones con transito | Filtra por `status=SEARCHING_ADOPT_AND_TRANSIT`. Verifica que se devuelve exactamente 1 resultado. |
| 28 | `filterTests` | `status=SEARCHING_ADOPT` — sin resultados | Filtra por `status=SEARCHING_ADOPT`. Verifica que no se devuelve ningun resultado. |
| 29 | `filterTests` | `status=ADOPTED` — adopciones concretadas | Filtra por `status=ADOPTED`. Verifica que se devuelve exactamente 1 resultado. |
| 30 | `filterTests` | `type=ADOPTION` + `status=ADOPTED` — adopciones adoptadas | Filtra combinando `type=ADOPTION` y `status=ADOPTED`. Verifica que se devuelve exactamente 1 resultado. |
| 31 | `filterTests` | `type=LOST` + `status=ADOPTED` — sin resultados | Filtra combinando `type=LOST` y `status=ADOPTED` (combinacion imposible). Verifica que no se devuelve ningun resultado. |
| 32 | `filterByStatus_returnsOnlyMatchingStatusAndSubtype` | `status=SEARCHING` devuelve solo perdidos en ese estado | Crea 3 posts (2 LOST con distinto estado, 1 ADOPTION). Filtra por `status=SEARCHING`. Verifica que devuelve exactamente 1 resultado, que es de tipo `LOST` y con titulo `"En busqueda"`. |
| 33 | `list_paginates` | Paginacion — respeta tamano de pagina y metadata | Crea 3 posts y solicita pagina 0 con tamano 2. Verifica que: (a) se devuelven 2 elementos en `content`; (b) la metadata incluye `size=2`, `number=0`, `totalElements=3`, `totalPages=2`. |
| 34 | `list_whenEmpty_returnsEmptyPage` | Sin publicaciones — pagina vacia | Realiza `GET /animal-posts` sin ningun post creado. Verifica que responde `200 OK` con `totalElements=0`. |
| 35 | `list_withInvalidType_returnsBadRequest` | `type=INVALIDO` — 400 | Envia un valor de `type` no reconocido. Verifica que responde `400 Bad Request`. |
| 36 | `list_withoutToken_returnsUnauthorized` | GET sin token — 401 | Intenta listar posts sin header de autorizacion. Verifica que responde `401 Unauthorized`. |

---

## 5. Tests de Campanas

**Clase:** `CampaignControllerTest`
**Tipo:** Tests de integracion
**Ubicacion:** `src/test/java/.../campaigns/integrations/CampaignControllerTest.java`
**Extiende:** `BaseAuthenticatedIntegrationTest`
**Anotaciones de clase:** `@Transactional`

### 5.1 Creacion (`POST /campaigns`) — Tests parametrizados de validacion

| # | Caso | Titulo | Descripcion |
|---|------|--------|-------------|
| 1 | `createCampaignTests` | Campana NEWS valida — 201 Created | Envia un `CreateCampaignRequest` de tipo `NEWS` con todos los campos validos. Verifica que responde `201 Created`, con `type="NEWS"` y `status="CREATED"`. |
| 2 | `createCampaignTests` | Campana NEWS con ubicacion opcional — 201 Created | Envia un request NEWS donde la ubicacion solo tiene los campos obligatorios (nombre, latitud, longitud), sin calle ni numero. Verifica que se crea exitosamente. |
| 3 | `createCampaignTests` | Campana DONATION completa — 201 Created | Envia un request de tipo `DONATION` con monto objetivo y fecha de fin. Verifica que responde `201 Created`, con `type="DONATION"` y `status="CREATED"`. |
| 4 | `createCampaignTests` | Campana DONATION abierta (sin monto ni fecha) — 201 Created | Envia un request de tipo `DONATION` sin especificar monto objetivo ni fecha limite. Verifica que se crea exitosamente como campana de donacion abierta. |
| 5 | `createCampaignTests` | NEWS con campos de donacion — 400 | Envia un request de tipo `NEWS` que incluye `amountToBeCollected` (campo exclusivo de DONATION). Verifica que la validacion lo rechaza con `400 Bad Request`. |
| 6 | `createCampaignTests` | Sin titulo — 400 | Envia un request sin el campo obligatorio `title`. Verifica que responde `400 Bad Request`. |
| 7 | `createCampaignTests` | DONATION con monto negativo — 400 | Envia un request de tipo `DONATION` con `amountToBeCollected` negativo. Verifica que la validacion lo rechaza con `400 Bad Request`. |
| 8 | `createCampaignTests` | DONATION con fecha de fin en el pasado — 400 | Envia un request de tipo `DONATION` con `endDate` anterior a la fecha actual. Verifica que responde `400 Bad Request`. |

### 5.2 Creacion — Tests individuales de logica de negocio

| # | Metodo | Titulo | Descripcion |
|---|--------|--------|-------------|
| 9 | `create_persistsCampaignWithAuthenticatedOwnerAndData` | POST valido: persiste con owner del JWT y datos correctos | Crea una campana DONATION con datos completos. Verifica que: (a) la respuesta contiene `id`, `type="DONATION"`, titulo correcto y `ownerId` del usuario autenticado; (b) la campana se persiste en base de datos con el titulo, owner y monto correctos (`150000`). |
| 10 | `create_withoutToken_returnsUnauthorized` | POST sin token — 401 | Intenta crear una campana sin header de autorizacion. Verifica que responde `401 Unauthorized`. |
| 11 | `create_withInvalidToken_returnsUnauthorized` | POST con token invalido — 401 | Intenta crear una campana con un JWT invalido. Verifica que responde `401 Unauthorized`. |

### 5.3 Listado y Filtrado (`GET /campaigns`)

| # | Metodo | Titulo | Descripcion |
|---|--------|--------|-------------|
| 12 | `getAll_returnsAllCampaignsWithDefaultPagination` | GET todas las campanas — paginacion por defecto | Guarda 2 campanas (1 NEWS + 1 DONATION) directamente en la base de datos. Realiza `GET /campaigns`. Verifica que responde `200 OK`, con `content` como array de longitud 2 y `totalElements=2`. |
| 13 | `getAll_withTypeDonation_returnsOnlyDonations` | `type=DONATION` — solo donaciones | Guarda 2 campanas (1 NEWS + 1 DONATION). Filtra por `type=DONATION`. Verifica que devuelve solo 1 resultado de tipo `"DONATION"`. |
| 14 | `getAll_withInvalidType_returnsBadRequest` | `type=HOLA` — 400 por tipo invalido | Envia un valor de `type` no reconocido (`"HOLA"`). Verifica que responde `400 Bad Request`. |

---

## Resumen

| Modulo | Clase de Test | Tipo | Cantidad de Tests |
|--------|--------------|------|:-----------------:|
| Aplicacion | `ManadaSolidariaBackendApplicationTests` | Smoke test | 1 |
| Autenticacion | `AuthControllerTest` | Integracion | 8 |
| Ping | `PingControlletTest` | Integracion | 2 |
| Publicaciones de Animales | `AnimalPostControllerTest` | Integracion | 36 |
| Campanas | `CampaignControllerTest` | Integracion | 14 |
| **Total** | | | **61** |
