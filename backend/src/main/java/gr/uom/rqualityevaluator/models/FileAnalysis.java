package gr.uom.rqualityevaluator.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@NoArgsConstructor
@Entity
@Table(name = "file_analysis")
public class FileAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;
    @Column(name = "owner")
    @Getter
    @Setter
    private String owner;
    @Column(name = "name")
    @Getter
    @Setter
    private String name;
    @ElementCollection
    @Getter
    @Setter
    private List<String> linterComments = new ArrayList<>();
    @Column(name = "linterCommentsCounter")
    @Getter
    @Setter
    private int cycloComplexity;

}
