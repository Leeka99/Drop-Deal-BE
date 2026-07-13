package com.dropdeal.api.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByOrderByIdAsc();

    List<ProductEntity> findByTypeOrderByIdAsc(ProductType type);
}
