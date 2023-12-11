package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(()-> new DataNotFoundException(
                        "Can not createProduct cause can not find category with Id: " + productDTO.getCategoryId()));
        // convert ProductDTO -> Object Product
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception { // dung exception lop cha,auto them throw vào service
        return productRepository.findById(productId)
                .orElseThrow(()-> new DataNotFoundException(
                        "cannot find product with id = " + productId
                ));
    }
    @Override
    public Page<Product> getAllProduct(PageRequest pageRequest) {
        // get listProduct by page and limit
        productRepository.findAll(pageRequest); // dien pageRequest trong controller
        return null;
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id); // da check tren getProductById function
        //check isCategory
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(()-> new DataNotFoundException(
                        "Can not update cause dont find categoryId: " + productDTO.getCategoryId()));

        if(existingProduct != null){
            // convert DTO -> Object
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());

            ResponseEntity.ok("update product successfully");
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id); // dung optional -> dung isPresent ?
        optionalProduct.ifPresent(productRepository::delete); //dùng bieu thuc lambda tham chieu den ham delete
    }
    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage( Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()->
                        new DataNotFoundException(
                            "Cannot find productId in productImage, productIdId: " + productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage
                .builder()
                .imageUrl(productImageDTO.getImageUrl())
                .product(existingProduct)
                .build();
        //so luong insert  size < 5
        int size = productImageRepository.findByProductId(productId).size();
        if(size > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException("Number of images is fulled, maximum: " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }
}
