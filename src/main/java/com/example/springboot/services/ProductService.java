package com.example.springboot.services;

import com.example.springboot.controllers.ProductController;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.exceptions.ResourceNotFoundException;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductModel saveProduct(ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return productRepository.save(productModel);
    }

    public List<ProductModel> getAllProducts() {
        List<ProductModel> productsList = productRepository.findAll();
        if (!productsList.isEmpty()) {
            productsList.forEach(productModel -> productModel.add(
                    linkTo(methodOn(ProductController.class).getOneProduct(productModel.getIdProduct()))
                            .withSelfRel()));
        }
        return productsList;
    }

    public ProductModel getOneProduct(UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);

        return product.map(value -> {
            value.add(linkTo(methodOn(ProductController.class)
                    .getOneProduct(id)).withSelfRel());
            return value;
        }).orElse(null);
    }

    public ProductModel updateProduct(UUID id, ProductRecordDto productRecordDto) {
        Optional<ProductModel> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found");
        }

        ProductModel productModel = optionalProduct.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return productRepository.save(productModel);
    }

    public void deleteProduct(UUID id) {
        Optional<ProductModel> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found");
        }
        optionalProduct.ifPresent(productRepository::delete);
    }
}