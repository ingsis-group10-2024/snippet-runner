# Runner Service

#### Encargado de ejecutar, formatear y lintear snippets. Proporciona funciones para asegurar que los snippets cumplan con las reglas de estilo y puedan ejecutarse correctamente.

### Endpoints del Runner Service

* POST `/runner/execute`
Ejecuta un snippet.
* POST `/runner/lint`
Valida un snippet mediante linteo.
* POST `/runner/format`
Aplica formato a un snippet.
* GET `/runner/rules/format`
Obtiene las reglas de formateo habilitadas.
* GET `/runner/rules/lint`
Obtiene las reglas de linteo habilitadas.
* PUT `/runner/rules/format`
Modifica las reglas de formateo.
* PUT `/runner/rules/lint`
Modifica las reglas de linteo.
* DELETE `/runner/rules/{ruleId}`
Elimina una regla existente.