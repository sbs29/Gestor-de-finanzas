package com.jsbs.finanzas_api.category;

import com.jsbs.finanzas_api.common.exception.CategoryNotFoundException;
import com.jsbs.finanzas_api.security.CurrentUserService;
import com.jsbs.finanzas_api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CurrentUserService currentUserService;

    public List<CategoryResponse> getAllCategories() {
        User currentUser = currentUserService.getCurrentUser();
        return categoryRepository.findByUser(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Category category = Category.builder()
                .name(request.name())
                .type(request.type())
                .user(currentUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType()
        );
    }

    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->new CategoryNotFoundException(id));

        return toResponse(category);
    }
}