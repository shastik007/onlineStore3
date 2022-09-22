package online.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class SecondCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String image;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "first_category_id")
    private FirstCategory firstCategory;


    @OneToMany(mappedBy = "secondCategory")
    private List<Products> products;
}
