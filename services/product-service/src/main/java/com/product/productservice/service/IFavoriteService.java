package com.product.productservice.service;

import com.product.productservice.dto.ProductDTO;

import java.util.List;

public interface IFavoriteService {
    String addToFavorite(Long userId, Long productId);
    String removeFromFavorite(Long userId, Long productId);
    List<ProductDTO> getFavorites(Long userId);
}
