package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    //    @Value("${project.images.path}")
    private String path = System.getProperty("user.dir") + "/images";

    @Override
    public ProductDTO addProductToCategory(Long categoryId, ProductDTO productDTO) {
        // if product already exists - throw error
        Product product = modelMapper.map(productDTO, Product.class);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductPresent = false;

        List<Product> productsInCategory = category.getProducts();

        for (Product productInCategory:productsInCategory){
            if (productInCategory.getProductName().equals(productDTO.getProductName())){
                isProductPresent = true;
                break;
            }
        }

        if (!isProductPresent) {
            product.setCategory(category);
            product.setImage("default.png");
            product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount(), product.getQuantity()));
        }
        else{
            throw new APIException("Product already exists in Category: "+category.getCategoryName()+"!!!!");
        }
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> productsPage = productRepository.findAll(pageDetails);

        List<Product> products = productsPage.getContent() ;

        if (products.isEmpty()) throw new APIException("No products available !!!!");

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> productsPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> products = productsPage.getContent();

        if (products.isEmpty()) throw new APIException("No products available !!!!");

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> productsPage = productRepository.findByProductNameContainingIgnoreCase(keyword,pageDetails);

        List<Product> products = productsPage.getContent();

        if (products.isEmpty()) throw new APIException("No products available !!!!");

        List<ProductDTO> productDTOList = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product productFromUser = modelMapper.map(productDTO, Product.class);

        productFromDB.setProductName(productFromUser.getProductName());
        productFromDB.setDescription(productFromUser.getDescription());
        productFromDB.setQuantity(productFromUser.getQuantity());
        productFromDB.setPrice(productFromUser.getPrice());
        productFromDB.setDiscount(productFromUser.getDiscount());
        productFromDB.setSpecialPrice(calculateSpecialPrice(productFromUser.getPrice(), productFromUser.getDiscount(), productFromUser.getQuantity()));

        productRepository.save(productFromDB);

        return modelMapper.map(productFromDB, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product toBeDeletedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.deleteById(productId);
        return modelMapper.map(toBeDeletedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile file) throws IOException {
        //fetch product from DB
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Upload/Save image to path and retrieve uploaded file name in fileName variable
        String fileName = fileService.uploadImage(path, file);

        // Updating the new fileName as the Image name for product retrieved from DB
        productFromDB.setImage(fileName);

        // Saving the updated product back to DB
        Product updatedProduct = productRepository.save(productFromDB);

        // Returning DTO to client
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private double calculateSpecialPrice(double price, double discount, int quantity) {
        return ((1 - discount * 0.01) * price) * quantity;
    }
}
