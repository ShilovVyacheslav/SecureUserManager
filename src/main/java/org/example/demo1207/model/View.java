package org.example.demo1207.model;

import org.bson.types.ObjectId;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "View")
@NoArgsConstructor
@AllArgsConstructor
public class View {
    @Id
    private ObjectId id;
    private String userId;
    private String url;
    private Date viewTimestamp;
}
