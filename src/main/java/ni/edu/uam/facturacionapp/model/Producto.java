package ni.edu.uam.facturacionapp.model;

import jdk.jfr.DataAmount;
import lombok.*;
import java.math.BigDecimal;

@DataAmount
@NoArgsContructor
@AllArgsContructor
@Setter


public class Producto {
    private Integer id;
    private String codigo;
    private String nombre;
    private String categoria;
    private BigDecimal precioventa;

    }
}
