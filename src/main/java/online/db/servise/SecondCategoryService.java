package online.db.servise;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.db.model.FirstCategory;
import online.db.model.SecondCategory;
import online.db.repository.FirstCategoryRepository;
import online.db.repository.SecondCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecondCategoryService {

    SecondCategoryRepository nextCategoryRepository;
    FirstCategoryRepository fourCategoryRepository;

    public SecondCategory save(SecondCategory category, Long id) {

        FirstCategory fourCategory = fourCategoryRepository.findById(id).get();

        SecondCategory secondCategory = new SecondCategory();
                secondCategory.setFirstCategory(fourCategory);
                secondCategory.setImage(category.getImage());
                secondCategory.setName(category.getName());

        return nextCategoryRepository.save(secondCategory);
    }

    public List<SecondCategory> getAllNextCategory(Long id) {
        return nextCategoryRepository.findAllNext(id);
    }

    @Transactional
    public SecondCategory updateNextCategory(SecondCategory nextCategory, Long id) {

        SecondCategory secondCategory = nextCategoryRepository.findById(id).get();
        String oldName = secondCategory.getName();
        String newName = nextCategory.getName();

        if (!oldName.equals(newName)) {
            secondCategory.setName(newName);
        }

        if (Objects.nonNull(nextCategory.getImage()))
            secondCategory.setImage(nextCategory.getImage());

        nextCategoryRepository.save(secondCategory);

        return secondCategory;
    }

    public String deleteById(Long id) {
        nextCategoryRepository.deleteById(id);
        return "Delete Category successfully";
    }

    public SecondCategory findById(Long id) {
        return nextCategoryRepository.findById(id).get();
    }
}
