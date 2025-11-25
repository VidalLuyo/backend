package pe.edu.vallegrande.vg_ms_assistance.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private JsonNode data; // Cambiado a JsonNode para manejar tanto objetos como arrays
    
    // Método helper para obtener los datos como lista
    public List<T> getDataAsList(Class<T> clazz) {
        if (data == null) {
            return new ArrayList<>();
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            
            if (data.isArray()) {
                // Si es un array, convertir directamente
                return mapper.convertValue(data, 
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } else {
                // Si es un objeto único, crear una lista con un elemento
                T item = mapper.convertValue(data, clazz);
                List<T> list = new ArrayList<>();
                list.add(item);
                return list;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    // Mantener compatibilidad con código existente
    public List<T> getData() {
        return new ArrayList<>(); // Deprecated, usar getDataAsList
    }
}
