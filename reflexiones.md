# Reflexión

## ¿Qué cambió en tu forma de "dar por terminado" el código cuando el veredicto lo decidió un gate determinista en vez de tu propio criterio?

Ya no bastó con “sentir” que el código estaba listo: tuve que demostrarlo con evidencia objetiva. El gate determinista obligó a cerrar el trabajo solo cuando las pruebas pasaron, no hubo hallazgos críticos de seguridad y los criterios de aceptación quedaron cumplidos.

---

## ¿Qué pilar te costó más dejar en verde —pruebas, seguridad o criterios—, y por qué?

El pilar más difícil de dejar en verde fue **criterios**, porque exige interpretar correctamente lo que se pidió y verificar que el resultado responda exactamente al valor esperado, no solo que compile o pase pruebas técnicas.

---

## ¿Para qué te serviría un gate de Definition of Done (y el escaneo automático de seguridad vía MCP) en tu equipo real?

Finalmente, un gate de **Definition of Done** serviría para reducir entregas incompletas, evitar retrabajo y estandarizar la calidad del equipo. El escaneo automático de seguridad vía MCP ayudaría a detectar vulnerabilidades temprano, antes de integrar o desplegar código.