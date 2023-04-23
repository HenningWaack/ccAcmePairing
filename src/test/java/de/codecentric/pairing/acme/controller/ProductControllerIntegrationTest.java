package de.codecentric.pairing.acme.controller;

import de.codecentric.pairing.acme.model.Product;
import de.codecentric.pairing.acme.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    private static final String USERNAME = "user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository.save(new Product(1L, "Test Product", "A test product", 10.0));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenProduct_whenGetProductById_thenReturnsProduct() throws Exception {
        // Arrange
        long productId = 1L;
        String productName = "Test Product";
        String productDescription = "A test product";

        // Act
        ResultActions resultActions = mockMvc.perform(get("/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value(productName))
                .andExpect(jsonPath("$.description").value(productDescription));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidProductId_whenGetProductById_thenReturnsNotFound() throws Exception {
        // Arrange
        long invalidProductId = 999L;

        // Act
        ResultActions resultActions = mockMvc.perform(get("/products/" + invalidProductId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenProduct_whenCreateProduct_thenReturnsCreatedProduct() throws Exception {
        // Arrange
        String productName = "New Product";
        String productDescription = "A new product";

        String productJson = "{ \"name\": \"" + productName + "\", \"description\": \"" + productDescription + "\", \"price\": 10.0 }";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson));

        // Assert
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(productName))
                .andExpect(jsonPath("$.description").value(productDescription));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidProduct_whenCreateProduct_thenReturnsBadRequest() throws Exception {
        // Arrange
        String invalidProductJson = "{ \"name\": \"\", \"description\": \"\" }";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductJson));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenProduct_whenUpdateProduct_thenReturnsUpdatedProduct() throws Exception {
        // Arrange
        long productId = 1L;
        String updatedProductName = "Updated Product";
        String updatedProductDescription = "An updated product";

        String updatedProductJson = "{ \"name\": \"" + updatedProductName + "\", \"description\": \"" + updatedProductDescription + "\", \"price\": 10.0 }";

        // Act
        ResultActions resultActions = mockMvc.perform(put("/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedProductJson));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value(updatedProductName))
                .andExpect(jsonPath("$.description").value(updatedProductDescription));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidProductId_whenUpdateProduct_thenReturnsNotFound() throws Exception {
        // Arrange
        long invalidProductId = 999L;
        String updatedProductName = "Updated Product";
        String updatedProductDescription = "An updated product";

        String updatedProductJson = "{ \"name\": \"" + updatedProductName + "\", \"description\": \"" + updatedProductDescription + "\" , \"price\": 10.0}";

        // Act
        ResultActions resultActions = mockMvc.perform(put("/products/" + invalidProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedProductJson));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }


    @Test
    void givenUnauthenticatedUser_whenGetProducts_thenReturnsUnauthorized() throws Exception {
        // Arrange
        // No additional arrangement needed, because this test requires an unauthenticated user

        // Act
        ResultActions resultActions = mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void givenUnauthenticatedUser_whenCreateProduct_thenReturnsUnauthorized() throws Exception {
        // Arrange
        String productName = "New Product";
        String productDescription = "A new product";

        String productJson = "{ \"name\": \"" + productName + "\", \"description\": \"" + productDescription + "\" }";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateProduct_thenReturnsUnauthorized() throws Exception {
        // Arrange
        long productId = 1L;
        String updatedProductName = "Updated Product";
        String updatedProductDescription = "An updated product";

        String updatedProductJson = "{ \"name\": \"" + updatedProductName + "\", \"description\": \"" + updatedProductDescription + "\" }";

        // Act
        ResultActions resultActions = mockMvc.perform(
                put("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedProductJson));
        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }


    @Test
    void givenInvalidCredentials_whenGetProducts_thenReturnsUnauthorized() throws Exception {
        // Arrange
        String invalidUsername = "invaliduser";
        String invalidPassword = "invalidpassword";
        String base64Credentials = Base64Utils.encodeToString((invalidUsername + ":" + invalidPassword).getBytes());

        // Act
        ResultActions resultActions = mockMvc.perform(get("/products")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidCredentials_whenCreateProduct_thenReturnsUnauthorized() throws Exception {
        // Arrange
        String invalidUsername = "invaliduser";
        String invalidPassword = "invalidpassword";
        String base64Credentials = Base64Utils.encodeToString((invalidUsername + ":" + invalidPassword).getBytes());

        String productName = "New Product";
        String productDescription = "A new product";

        String productJson = "{ \"name\": \"" + productName + "\", \"description\": \"" + productDescription + "\" }";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/products")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidCredentials_whenUpdateProduct_thenReturnsUnauthorized() throws Exception {
        // Arrange
        long productId = 1L;
        String updatedProductName = "Updated Product";
        String updatedProductDescription = "An updated product";
        String invalidUsername = "invaliduser";
        String invalidPassword = "invalidpassword";
        String base64Credentials = Base64Utils.encodeToString((invalidUsername + ":" + invalidPassword).getBytes());

        String updatedProductJson = "{ \"name\": \"" + updatedProductName + "\", \"description\": \"" + updatedProductDescription + "\" }";

        // Act
        ResultActions resultActions = mockMvc.perform(put("/products/" + productId)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedProductJson));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }
}
