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

    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        User currentUser = currentUserService.getCurrentUser();
        return categoryRepository.findByUser(currentUser)
                .stream()
                .map(categoryMapper::toResponse)
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

        return categoryMapper.toResponse(savedCategory);
    }

    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->new CategoryNotFoundException(id));

        return categoryMapper.toResponse(category);
    }
}