/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.BreedMapper;
import org.springframework.samples.petclinic.model.Breed;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for {@link BreedRestController}
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
@ContextConfiguration(classes=ApplicationTestConfig.class)
@WebAppConfiguration
class BreedRestControllerTests {

    @Autowired
    private BreedRestController breedRestController;

    @Autowired
    private BreedMapper breedMapper;

    @MockBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private List<Breed> breeds;

    @BeforeEach
    void initBreeds(){
    	this.mockMvc = MockMvcBuilders.standaloneSetup(breedRestController)
    			.setControllerAdvice(new ExceptionControllerAdvice())
    			.build();
    	breeds = new ArrayList<>();

    	Breed breed = new Breed();
    	breed.setId(1);
    	breed.setName("siamese");
    	breeds.add(breed);

    	breed = new Breed();
    	breed.setId(2);
    	breed.setName("deuchsland");
    	breeds.add(breed);

    	breed = new Breed();
    	breed.setId(3);
    	breed.setName("chameleon");
    	breeds.add(breed);

    	breed = new Breed();
    	breed.setId(4);
    	breed.setName("anaconda");
    	breeds.add(breed);
    }

    @Test
    @WithMockUser(roles="OWNER_ADMIN")
    void testGetBreedSuccessAsOwnerAdmin() throws Exception {
    	given(this.clinicService.findBreedById(1)).willReturn(breeds.get(0));
        this.mockMvc.perform(get("/api/breeds/1")
        	.accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("siamese"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetBreedSuccessAsVetAdmin() throws Exception {
        given(this.clinicService.findBreedById(1)).willReturn(breeds.get(0));
        this.mockMvc.perform(get("/api/breeds/1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("siamese"));
    }

    @Test
    @WithMockUser(roles="OWNER_ADMIN")
    void testGetBreedNotFound() throws Exception {
    	given(this.clinicService.findBreedById(999)).willReturn(null);
        this.mockMvc.perform(get("/api/breeds/999")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles="OWNER_ADMIN")
    void testGetAllBreedsSuccessAsOwnerAdmin() throws Exception {
    	breeds.remove(0);
    	breeds.remove(1);
    	given(this.clinicService.findAllBreeds()).willReturn(breeds);
        this.mockMvc.perform(get("/api/breeds/")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
        	.andExpect(jsonPath("$.[0].id").value(2))
        	.andExpect(jsonPath("$.[0].name").value("deuchsland"))
        	.andExpect(jsonPath("$.[1].id").value(4))
        	.andExpect(jsonPath("$.[1].name").value("anaconda"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetAllBreedsSuccessAsVetAdmin() throws Exception {
        breeds.remove(0);
        breeds.remove(1);
        given(this.clinicService.findAllBreeds()).willReturn(breeds);
        this.mockMvc.perform(get("/api/breeds/")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].name").value("deuchsland"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].name").value("anaconda"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetAllBreedsNotFound() throws Exception {
    	breeds.clear();
    	given(this.clinicService.findAllBreeds()).willReturn(breeds);
        this.mockMvc.perform(get("/api/breeds/")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testCreateBreedSuccess() throws Exception {
    	Breed newBreed = breeds.get(0);
    	newBreed.setId(null);
    	ObjectMapper mapper = new ObjectMapper();
        String newBreedAsJSON = mapper.writeValueAsString(breedMapper.toBreedFieldsDto(newBreed));
    	this.mockMvc.perform(post("/api/breeds/")
    		.content(newBreedAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
    		.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testCreateBreedError() throws Exception {
    	Breed newBreed = breeds.get(0);
    	newBreed.setId(null);
    	newBreed.setName(null);
    	ObjectMapper mapper = new ObjectMapper();
        String newBreedAsJSON = mapper.writeValueAsString(breedMapper.toBreedDto(newBreed));
    	this.mockMvc.perform(post("/api/breeds/")
        		.content(newBreedAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        		.andExpect(status().isBadRequest());
     }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testUpdateBreedSuccess() throws Exception {
    	given(this.clinicService.findBreedById(2)).willReturn(breeds.get(1));
    	Breed newBreed = breeds.get(1);
    	newBreed.setName("deuchsland");
    	ObjectMapper mapper = new ObjectMapper();
        String newBreedAsJSON = mapper.writeValueAsString(breedMapper.toBreedDto(newBreed));
    	this.mockMvc.perform(put("/api/breeds/2")
    		.content(newBreedAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(content().contentType("application/json"))
        	.andExpect(status().isNoContent());

    	this.mockMvc.perform(get("/api/breeds/2")
           	.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("deuchsland"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testUpdateBreedError() throws Exception {
    	Breed newBreed = breeds.get(0);
    	newBreed.setName("");
    	ObjectMapper mapper = new ObjectMapper();
        String newBreedAsJSON = mapper.writeValueAsString(breedMapper.toBreedDto(newBreed));
    	this.mockMvc.perform(put("/api/breeds/1")
    		.content(newBreedAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isBadRequest());
     }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testDeleteBreedSuccess() throws Exception {
    	Breed newBreed = breeds.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newBreedAsJSON = mapper.writeValueAsString(newBreed);
    	given(this.clinicService.findBreedById(1)).willReturn(breeds.get(0));
    	this.mockMvc.perform(delete("/api/breeds/1")
    		.content(newBreedAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testDeleteBreedError() throws Exception {
    	Breed newBreed = breeds.get(0);
    	ObjectMapper mapper = new ObjectMapper();
        String newBreedAsJSON = mapper.writeValueAsString(breedMapper.toBreedDto(newBreed));
    	given(this.clinicService.findBreedById(999)).willReturn(null);
    	this.mockMvc.perform(delete("/api/breeds/999")
    		.content(newBreedAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isNotFound());
    }

}
