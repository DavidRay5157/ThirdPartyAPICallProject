package com.example.assignmentpractice.services;

import com.example.assignmentpractice.dtos.FakeStoreProductDto;
import com.example.assignmentpractice.models.Category;
import com.example.assignmentpractice.models.Product;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@Service("fkps")
@Primary
public class FakeStoreProductService implements IProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private final String BASE_URL = "https://fakestoreapi.com/products";

    private <T> ResponseEntity<T> requestForEntity(String url, @Nullable Object request, HttpMethod httpMethod,
                                                   Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }

    @Override
    public List<Product> getAllProducts() {
        System.out.println("Getting all products from fake store");

        // 1. Call the external API.
        // We pass 'null' for the body because it's a GET request.
        // We use FakeStoreProductDto[].class because the API returns a JSON array.
        ResponseEntity<FakeStoreProductDto[]> responseEntity = requestForEntity(
                BASE_URL,
                null,
                HttpMethod.GET,
                FakeStoreProductDto[].class
        );

        List<Product> products = new ArrayList<>();

        // 2. Check if the body exists and convert the array to your internal Product model
        if (responseEntity.getBody() != null) {
            for (FakeStoreProductDto fakeStoreProductDto : responseEntity.getBody()) {
                products.add(from(fakeStoreProductDto));
            }
        }

        return products;
    }

    @Override
    public Product getProductById(Long id) {
        //
        FakeStoreProductDto fakeStoreProductDto = null;
        fakeStoreProductDto = (FakeStoreProductDto) redisTemplate.opsForHash().get("PRODUCTS",id);

        if(fakeStoreProductDto == null) {
            fakeStoreProductDto =
                    requestForEntity("https://fakestoreapi.com/products/{id}", null, HttpMethod.GET,
                            FakeStoreProductDto.class, id).getBody();

            if (fakeStoreProductDto != null) {
                System.out.println("Found by calling fakestore");
                redisTemplate.opsForHash().put("PRODUCTS", id, fakeStoreProductDto);
                return from(fakeStoreProductDto);
            }
        } else {
            System.out.println("Found in cache");
            return from(fakeStoreProductDto);
        }

        return null;





//        ResponseEntity<FakeStoreProductDto> fakeStoreProductDtoResponseEntity =
//                requestForEntity(BASE_URL + "/{id}", null, HttpMethod.GET,
//                        FakeStoreProductDto.class, id);
//
//        if (fakeStoreProductDtoResponseEntity.getStatusCode().equals(HttpStatusCode.valueOf(200)) &&
//                fakeStoreProductDtoResponseEntity.getBody() != null) {
//            return from(fakeStoreProductDtoResponseEntity.getBody());
//        }
//
//        return null;
    }
    @Override
    public Product createProduct(Product product) {
        // 1. Convert our internal Product model to the DTO FakeStore expects
        FakeStoreProductDto requestDto = from(product);

        // 2. Make the POST request
        ResponseEntity<FakeStoreProductDto> response = requestForEntity(
                BASE_URL,
                requestDto,
                HttpMethod.POST,
                FakeStoreProductDto.class
        );

        // 3. Convert the response back to our internal Product model
        return from(response.getBody());
    }

    private Product from(FakeStoreProductDto fakeStoreProductDto) {
        Product product = new Product();
        product.setId(fakeStoreProductDto.getId());
        product.setName(fakeStoreProductDto.getTitle());
        product.setDescription(fakeStoreProductDto.getDescription());
        product.setPrice(fakeStoreProductDto.getPrice());
        product.setImageUrl(fakeStoreProductDto.getImage());
        Category category = new Category();
        category.setName(fakeStoreProductDto.getCategory());
        product.setCategory(category);
        return product;
    }

    private FakeStoreProductDto from(Product product) {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setId(product.getId());
        fakeStoreProductDto.setTitle(product.getName());
        fakeStoreProductDto.setPrice(product.getPrice());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setImage(product.getImageUrl());
        if(product.getCategory() != null) {
            fakeStoreProductDto.setCategory(product.getCategory().getName());
        }
        return fakeStoreProductDto;
    }
}
