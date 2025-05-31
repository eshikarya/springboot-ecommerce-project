package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${product.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductName().equals(productDTO.getProductName())){
                isProductNotPresent = false;
                break;
            }
        }


        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);

            product.setCategory(category);
            product.setImage("default.png");

//        price - (x% * price) = price*(1 - x%)
//        price = 100;
//        discount = 25% of price; which is 0.25*100 = 25
//        special price is? price - discount price (100-25) = 75
//        price - x% * price = price*(1 - x%)
            double specialPrice = product.getPrice() * (1 - (product.getDiscount() * 0.01));
            product.setSpecialPrice(specialPrice);

            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else{
            throw new APIException("Product already exists!");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {


        // Sorting implemented
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pagination implemented
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> productList = pageProducts.getContent();
        List<ProductDTO> productDTOList = productList.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        if (productList.isEmpty())
            throw new APIException("No Products found");

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductByCategoryId(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","categoryId",categoryId));

        // Sorting implemented
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pagination implemented
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> productList = pageProducts.getContent();


        List<ProductDTO> productDTOList = productList.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        if(productList.isEmpty()){
            throw new APIException(category.getCategoryName()+" does not have any products");
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);


        List<Product> productList = pageProducts.getContent();
        List<ProductDTO> productDTOList = productList.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        if(productList.isEmpty()){
            throw new APIException("Products not found with keyword "+keyword);
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        // Get existing product from DB
        Product productDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        Product product = modelMapper.map(productDTO,Product.class);

        // Update product information with one shared in request body
        productDB.setProductName(product.getProductName());
        productDB.setDescription(product.getDescription());
        productDB.setQuantity(product.getQuantity());
        productDB.setPrice(product.getPrice());
        productDB.setDiscount(product.getDiscount());
        productDB.setSpecialPrice(product.getSpecialPrice());

        // save to DB
        Product savedProduct = productRepository.save(productDB);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct,ProductDTO.class);

        return savedProductDTO;
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        ProductDTO deletedProduct = modelMapper.map(productDB,ProductDTO.class);
        productRepository.delete(productDB);

        return deletedProduct;
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get the product from DB
        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        // Upload image to server
        // Get the filename of uploaded image
        String fileName = fileService.uploadImage(path,image);

        // Updating the new file name to the product
        productDB.setImage(fileName);

        // Save the updated product
        Product updatedProduct = productRepository.save(productDB);

        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);

    }

}
