package org.example.coffee.mapper;

import org.example.coffee.dto.category.CategoryInput;
import org.example.coffee.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface CategoryMapper {
    CategoryEntity getEntityFromInput(CategoryInput categoryInput);

    void updateEntityFromInput(@MappingTarget CategoryEntity categoryEntity, CategoryInput categoryInput);
}
