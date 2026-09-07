package ni.edu.uam.facturacionapp.model;

import java.time.LocalDate;

import lombok.*;
@Getter
@Setter
@NoArgsContructor
@AllArgsContructor

public class Empleado {
    private Integer id;
    private String nombre;
    private String apellido;
    private Cargo cargo;
    private LocalDate fechaContrato;



    }


