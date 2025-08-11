package com.example.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.inventory.dto.PagedResponse;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Product;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private PagedResponse<Product> readProductPage(String json) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PagedResponse.class, Product.class);
        return objectMapper.readValue(json, type);
    }

    @Test
    void list_products_filters_by_name_case_insensitive() throws Exception {
        MvcResult res = mockMvc.perform(get("/api/products")
                .queryParam("name", "an")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> page = readProductPage(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent())
                .allSatisfy(p -> assertThat(p.getName().toLowerCase()).contains("an"));
    }

    @Test
    void list_products_filters_by_categories() throws Exception {
        // Category 2 = Electronics (from seed data)
        MvcResult res = mockMvc.perform(get("/api/products")
                .queryParam("categories", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> page = readProductPage(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).allSatisfy(p -> {
            assertThat(p.getCategory()).isNotNull();
            assertThat(p.getCategory().getId()).isEqualTo(2L);
        });
    }

    @Test
    void list_products_filters_by_availability_instock_and_outofstock() throws Exception {
        // instock > 0
        MvcResult inRes = mockMvc.perform(get("/api/products")
                .queryParam("available", "instock")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> inPage = readProductPage(inRes.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(inPage.getContent()).isNotEmpty();
        assertThat(inPage.getContent()).allSatisfy(p -> assertThat(p.getStock()).isGreaterThan(0));

        // outofstock == 0
        MvcResult outRes = mockMvc.perform(get("/api/products")
                .queryParam("available", "outofstock")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> outPage = readProductPage(
                outRes.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(outPage.getContent()).isNotEmpty();
        assertThat(outPage.getContent()).allSatisfy(p -> assertThat(p.getStock()).isEqualTo(0));
    }

    @Test
    void list_products_sorts_by_price_and_paginates() throws Exception {
        // sort by price asc, first page size 5
        MvcResult res = mockMvc.perform(get("/api/products")
                .queryParam("primarySortBy", "price")
                .queryParam("primarySortDirection", "asc")
                .queryParam("page", "0")
                .queryParam("size", "5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> page = readProductPage(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
        List<Product> list = page.getContent();
        assertThat(list).hasSize(5);
        for (int i = 1; i < list.size(); i++) {
            assertThat(list.get(i).getPrice()).isGreaterThanOrEqualTo(list.get(i - 1).getPrice());
        }

        // second page
        MvcResult res2 = mockMvc.perform(get("/api/products")
                .queryParam("primarySortBy", "price")
                .queryParam("primarySortDirection", "asc")
                .queryParam("page", "1")
                .queryParam("size", "5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> page2 = readProductPage(res2.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(page2.getContent()).hasSize(5);
        assertThat(page2.getTotalElements()).isGreaterThanOrEqualTo(10);
    }

    @Test
    void list_products_secondary_sort_applies_on_ties() throws Exception {
        // Create two products with same stock to enforce tie, different names to test
        // secondary sort
        ProductDTO dtoA = ProductDTO.builder().name("Alpha Tie").price(1.0).stock(123).categoryId(1L).build();
        ProductDTO dtoZ = ProductDTO.builder().name("Zed Tie").price(1.0).stock(123).categoryId(1L).build();

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoZ)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoA)))
                .andExpect(status().isOk());

        MvcResult res = mockMvc.perform(get("/api/products")
                .queryParam("primarySortBy", "stock")
                .queryParam("primarySortDirection", "asc")
                .queryParam("secondarySortBy", "name")
                .queryParam("secondarySortDirection", "asc")
                .queryParam("size", "1000")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PagedResponse<Product> page = readProductPage(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
        List<Product> list = page.getContent();
        int idxAlpha = -1, idxZed = -1;
        for (int i = 0; i < list.size(); i++) {
            if ("Alpha Tie".equals(list.get(i).getName()))
                idxAlpha = i;
            if ("Zed Tie".equals(list.get(i).getName()))
                idxZed = i;
        }
        assertThat(idxAlpha).isGreaterThanOrEqualTo(0);
        assertThat(idxZed).isGreaterThanOrEqualTo(0);
        assertThat(idxAlpha).isLessThan(idxZed); // Alpha should come before Zed when stock ties
    }

    @Test
    void product_crud_happy_path_and_stock_toggles() throws Exception {
        // Create
        ProductDTO dto = ProductDTO.builder().name("Widget").price(9.99).stock(2).categoryId(1L).build();
        MvcResult createRes = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();
        Product created = objectMapper.readValue(createRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Product.class);
        assertThat(created.getId()).isNotNull();

        // Update
        ProductDTO update = ProductDTO.builder().name("Widget+1").price(19.99).stock(5).categoryId(1L).build();
        MvcResult updateRes = mockMvc.perform(put("/api/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andReturn();
        Product updated = objectMapper.readValue(updateRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Product.class);
        assertThat(updated.getName()).isEqualTo("Widget+1");
        assertThat(updated.getPrice()).isEqualTo(19.99);

        // Out of stock
        MvcResult outRes = mockMvc.perform(put("/api/products/" + created.getId() + "/outofstock"))
                .andExpect(status().isOk())
                .andReturn();
        Product out = objectMapper.readValue(outRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Product.class);
        assertThat(out.getStock()).isEqualTo(0);

        // In stock (restock to default 10)
        MvcResult inRes = mockMvc.perform(put("/api/products/" + created.getId() + "/instock"))
                .andExpect(status().isOk())
                .andReturn();
        Product in = objectMapper.readValue(inRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Product.class);
        assertThat(in.getStock()).isEqualTo(10);

        // Delete
        mockMvc.perform(delete("/api/products/" + created.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void product_crud_404_and_400_cases() throws Exception {
        // 400 on invalid category during create
        ProductDTO badCat = ProductDTO.builder().name("Bad").price(1).stock(1).categoryId(99999L).build();
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badCat)))
                .andExpect(status().isBadRequest());

        // 400 on invalid update category
        ProductDTO good = ProductDTO.builder().name("Tmp").price(1).stock(1).categoryId(1L).build();
        MvcResult createRes = mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(good)))
                .andExpect(status().isOk()).andReturn();
        Product created = objectMapper.readValue(createRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Product.class);

        ProductDTO badUpdate = ProductDTO.builder().name("Tmp2").price(1).stock(1).categoryId(99999L).build();
        mockMvc.perform(put("/api/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badUpdate)))
                .andExpect(status().isBadRequest());

        // 404 on non-existing update/delete/stock toggles
        mockMvc.perform(put("/api/products/999999").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(good)))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/products/999999/instock"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/products/999999/outofstock"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/products/999999"))
                .andExpect(status().isNotFound());

        // 400 on validation failure (negative price)
        ProductDTO invalidPayload = ProductDTO.builder().name("Invalid").price(-1).stock(0).categoryId(1L).build();
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest());
    }
}
