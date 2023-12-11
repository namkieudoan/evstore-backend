package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult bindingResult
            ) throws IOException {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct =  productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long id,
            @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product existingProduct = productService.getProductById(id);
            files = files == null ? new ArrayList<MultipartFile>() : files;

            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body("You can only upload maximum"+ ProductImage.MAXIMUM_IMAGES_PER_PRODUCT +"images");
            }
            List<ProductImage> productImages = new ArrayList<>();

            for(MultipartFile file: files){
                if(file.getSize() ==0){
                    continue;
                }
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large ! Maximum size is 10 MB");
                }
                String contentType = file.getContentType();
                if (contentType == null ||!contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                //luu file va update thumbnail trong productDTO
                String filename = storeFile(file);
                // luu vao doi tuong product trong DB
                ProductImage productImage = productService.createProductImage(
                        (long) existingProduct.getId(), //wrap int to long
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                // service da luu vao bang product_images
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private String storeFile(MultipartFile file) throws IOException {
        if(file.getOriginalFilename() == null){
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //  add UUID before file's name to unique
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        // path dir you want to save
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // check is there dir exist or create a new dir
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);


        return uniqueFileName;
    }
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        // Tạo pageable tu page and limit
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("createdAt").descending());
        Page<ProductResponse> productPage = productService.getAllProduct(pageRequest);
        // Lấy total page
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId){
        try {
            Product existingProduct = productService.getProductById(productId);
//            return ResponseEntity.ok(existingProduct);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductDTO productDTO){
        try {
            ProductResponse productResponse = ProductResponse.fromProduct(productService.updateProduct(productId,productDTO));
            return ResponseEntity.ok(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") long productId){
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", productId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //faker db
//    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts(){
        Faker faker =  new Faker();
        for(int i =0 ; i < 5_000; i++){
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10,9_000_000))
                    .description(faker.lorem().sentence())
                    .categoryId(faker.number().numberBetween(1,4))
                    .thumbnail("")
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (DataNotFoundException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created successfully");
    }
}
