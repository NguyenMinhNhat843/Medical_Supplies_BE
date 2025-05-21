package com.product.productservice.service.impl;

import com.product.productservice.dto.ProductDTO;
import com.product.productservice.entity.FavoriteProductEntity;
import com.product.productservice.repository.FavoriteProductRepository;
import com.product.productservice.service.IFavoriteService;
import com.product.productservice.service.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements IFavoriteService {

    @Autowired
    private FavoriteProductRepository favoriteRepo;

    @Autowired
    private IProductService productService;

    @Override
    public String addToFavorite(Long userId, Long productId) {
        if (!productService.existsById(productId)) {
            throw new RuntimeException("Sản phẩm không tồn tại.");
        }

        if (favoriteRepo.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Đã có trong danh sách yêu thích.");
        }

        FavoriteProductEntity entity = new FavoriteProductEntity();
        entity.setUserId(userId);
        entity.setProductId(productId);
        favoriteRepo.save(entity);

        return "Đã thêm vào danh sách yêu thích.";
    }

    @Override
    @Transactional
    public String removeFromFavorite(Long userId, Long productId) {
        if (!favoriteRepo.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Chưa có trong danh sách yêu thích.");
        }

        favoriteRepo.deleteByUserIdAndProductId(userId, productId);
        return "Đã xoá khỏi danh sách yêu thích.";
    }

    @Override
    public List<ProductDTO> getFavorites(Long userId) {
        List<Long> ids = favoriteRepo.findByUserId(userId).stream()
                .map(FavoriteProductEntity::getProductId)
                .toList();
        List<ProductDTO> products = productService.getProductsByIds(ids);
        products.forEach(p -> p.setIsFavorite(true));
        return products;
    }
}