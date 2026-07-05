package org.ups.citasalud.presentation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ErrorReserva
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-04T16:04:14.475931500-05:00[America/Guayaquil]", comments = "Generator version: 7.16.0")
public class ErrorReserva {

  /**
   * Gets or Sets codigo
   */
  public enum CodigoEnum {
    HORARIO_NO_DISPONIBLE("HORARIO_NO_DISPONIBLE"),
    
    CONFLICTO_HORARIO_PACIENTE("CONFLICTO_HORARIO_PACIENTE");

    private final String value;

    CodigoEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CodigoEnum fromValue(String value) {
      for (CodigoEnum b : CodigoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private CodigoEnum codigo;

  private String mensaje;

  public ErrorReserva() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ErrorReserva(CodigoEnum codigo, String mensaje) {
    this.codigo = codigo;
    this.mensaje = mensaje;
  }

  public ErrorReserva codigo(CodigoEnum codigo) {
    this.codigo = codigo;
    return this;
  }

  /**
   * Get codigo
   * @return codigo
   */
  @NotNull 
  @JsonProperty("codigo")
  public CodigoEnum getCodigo() {
    return codigo;
  }

  public void setCodigo(CodigoEnum codigo) {
    this.codigo = codigo;
  }

  public ErrorReserva mensaje(String mensaje) {
    this.mensaje = mensaje;
    return this;
  }

  /**
   * Get mensaje
   * @return mensaje
   */
  @NotNull 
  @JsonProperty("mensaje")
  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorReserva errorReserva = (ErrorReserva) o;
    return Objects.equals(this.codigo, errorReserva.codigo) &&
        Objects.equals(this.mensaje, errorReserva.mensaje);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigo, mensaje);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorReserva {\n");
    sb.append("    codigo: ").append(toIndentedString(codigo)).append("\n");
    sb.append("    mensaje: ").append(toIndentedString(mensaje)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

