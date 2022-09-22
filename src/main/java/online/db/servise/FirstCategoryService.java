package online.db.servise;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.db.model.FirstCategory;
import online.db.repository.FirstCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FirstCategoryService {

    FirstCategoryRepository repository;

    public FirstCategory save(FirstCategory category) {
        return repository.save(category);
    }

    public List<FirstCategory> getAllFourCategory() {
        return repository.findAll();
    }

    @Transactional
    public FirstCategory updateCategory(FirstCategory products, Long id) {

        FirstCategory fourCategory = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(
                    String.format("not-found id: ", id)
            );
        });

        String oldName = fourCategory.getNameCategory();
        String newName = products.getNameCategory();

        if (!oldName.equals(newName)) {
            fourCategory.setNameCategory(newName);
        }
        if (Objects.nonNull(products.getImage()))
            fourCategory.setImage(products.getImage());

        repository.save(fourCategory);

        return fourCategory;
    }

    public String delete(Long id) {
        repository.deleteById(id);
        return "Successfully delete Category";
    }

    public FirstCategory findById(Long id) {
        return repository.findById(id).get();
    }
}
