package com.jsbs.finanzas_api.category;

import com.jsbs.finanzas_api.common.exception.CategoryInUseException;
import com.jsbs.finanzas_api.common.exception.CategoryNotFoundException;
import com.jsbs.finanzas_api.security.CurrentUserService;
import com.jsbs.finanzas_api.transaction.TransactionRepository;
import com.jsbs.finanzas_api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        User currentUser = currentUserService.getCurrentUser();

        return categoryRepository.findByUser(currentUser)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Category category = Category.builder()
                .name(request.name())
                .type(request.type())
                .user(currentUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.setName(request.name());
        category.setType(request.type());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (transactionRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException(
                    "Cannot delete category because it has associated transactions"
            );
        }

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(category);
    }

}