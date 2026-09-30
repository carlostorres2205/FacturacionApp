package ni.edu.uam.facturacionapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private Integer id;
    private String nombreUsuario;
    private String contrasena;
    private boolean activo;
}
