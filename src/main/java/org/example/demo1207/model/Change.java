package org.example.demo1207.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Document(collection = "Change")
@NoArgsConstructor
@AllArgsConstructor
public class Change {
    @Id
    private ObjectId id;
    private String userId;
    private List<String> fieldsChanged;
    private Map<String, String> oldValues;
    private Map<String, String> newValues;
    private Date changeTimestamp;
}
