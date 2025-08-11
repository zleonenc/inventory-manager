package com.example.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.inventory.model.Category;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_categories_returns_active() throws Exception {
        MvcResult res = mockMvc.perform(get("/api/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Category> list = objectMapper.readValue(
                res.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<Category>>() {
                });
        assertThat(list).isNotEmpty();
        assertThat(list).allSatisfy(c -> assertThat(c.isActive()).isTrue());
    }

    @Test
    void category_crud_happy_path_and_error_cases() throws Exception {
        // Create
        Category toCreate = Category.builder().name("TempCat-INT").build();
        MvcResult createRes = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andReturn();
        Category created = objectMapper.readValue(createRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Category.class);
        assertThat(created.getId()).isNotNull();

        // Update
        Category update = Category.builder().name("TempCat-INT-2").build();
        MvcResult updateRes = mockMvc.perform(put("/api/categories/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andReturn();
        Category updated = objectMapper.readValue(updateRes.getResponse().getContentAsString(StandardCharsets.UTF_8),
                Category.class);
        assertThat(updated.getName()).isEqualTo("TempCat-INT-2");

        // Duplicate name -> 400
        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Category.builder().name("TempCat-INT-2").build())))
                .andExpect(status().isBadRequest());

        // 404 update non-existing
        mockMvc.perform(put("/api/categories/999999").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Category.builder().name("X").build())))
                .andExpect(status().isNotFound());

        // Delete existing
        mockMvc.perform(delete("/api/categories/" + created.getId()))
                .andExpect(status().isNoContent());

        // 404 delete non-existing
        mockMvc.perform(delete("/api/categories/999999"))
                .andExpect(status().isNotFound());

        // 400 validation: blank name
        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Category.builder().name("").build())))
                .andExpect(status().isBadRequest());
    }
}
